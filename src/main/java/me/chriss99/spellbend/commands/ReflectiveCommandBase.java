package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.CustomClassParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public abstract class ReflectiveCommandBase extends BukkitCommand implements CommandExecutor {
    private final HashMap<String, ArrayList<Method>> pathToMethodsMap = new HashMap<>();
    private int longestPath = 0;
    private final CustomClassParser classParser;

    public ReflectiveCommandBase(@NotNull String commandName, @NotNull String description, @NotNull List<String> aliases) {
        this(commandName, description, aliases, new CustomClassParser());
    }

    public ReflectiveCommandBase(@NotNull String commandName, @NotNull String description, @NotNull List<String> aliases, @NotNull CustomClassParser classParser) {
        super(commandName, description,
                "If this shows up, someone has not updated the usageMessage in ReflectiveCommandBase yet. Please notify a developer immediately!", aliases);
        this.classParser = classParser;

        for (Method method : getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ReflectCommand.class))
                continue;

            String path = method.getAnnotation(ReflectCommand.class).path();
            int pathLength = path.split(" ").length;
            if (longestPath < pathLength)
                longestPath = pathLength;

            ArrayList<Method> methods = pathToMethodsMap.get(path);

            if (methods == null) {
                pathToMethodsMap.put(path, new ArrayList<>(List.of(method)));
                continue;
            }

            for (Method listMethod : methods)
                if (Arrays.equals(method.getParameterTypes(), listMethod.getParameterTypes()))
                    throw new IllegalStateException("Methods \"" + method.getName() + "\" and " + listMethod.getName() + " have the same path and parameterTypes!");

            methods.add(method);
        }

        PluginCommand pluginCommand = SpellBend.getInstance().getCommand(commandName);
        if (pluginCommand == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Command \"" + commandName + "\" could not be registered!\n" +
                    "This happened because the PluginCommand returned by the main class was null.\n" +
                    "(This won't break anything, except that the command cannot be executed.)");
            return;
        }
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] arguments) {
        Diagnostics diagnostics = new Diagnostics();
        //these can have the same parameter count, but will have different parameter types, though those types might not be distinguishable in string from
        LinkedList<Method> methods = getPathMatchingMethods(arguments, diagnostics);

        if (methods.isEmpty()) {
            sender.sendMessage(noPathMatchingMethodsMessage(arguments, diagnostics));
            return true;
        }
        if (methodsMatchingParameterCount(methods, arguments.length, diagnostics).isEmpty()) {
            sender.sendMessage(noMethodsMatchingParameterCountMessage(arguments, diagnostics));
            return true;
        }
        LinkedList<ParsedMethod> parsedMethods = successfullyParsedMethods(methods, arguments, diagnostics);

        if (parsedMethods.isEmpty()) {
            sender.sendMessage(noMethodsParsedMessage(diagnostics));
            return true;
        }

        if (parsedMethods.size() == 1) {
            ParsedMethod parsedMethod = parsedMethods.get(0);
            try {
                parsedMethod.method().invoke(this, parsedMethod.parameters());
            } catch (IllegalAccessException e) {
                sender.sendMessage("§cThe programmer of this subCommand used the ReflectiveCommandBase incorrectly!\n§4IllegalAccessException: §c" + e.getMessage());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                sender.sendMessage("§cThe method \"" + parsedMethod.method().getName() + "\" threw an exception!\n§4" +
                        cause.getClass().getSimpleName() + ": §c" + cause.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        sender.sendMessage(multipleMethodsParsedMessage(diagnostics));
        return true;
    }

    private @NotNull LinkedList<ParsedMethod> successfullyParsedMethods(@NotNull LinkedList<Method> methods, @NotNull String[] arguments, @NotNull Diagnostics diagnostics) {
        LinkedList<ParsedMethod> parsedMethods = new LinkedList<>();
        diagnostics.setMethodParsingLog(new LinkedList<>());

        for (Method method : methods) {
            String[] parameterStrings = new String[method.getParameterCount()];
            System.arraycopy(arguments, arguments.length-parameterStrings.length, parameterStrings, 0, parameterStrings.length);

            Object[] parameters = parseMethodParameters(method, parameterStrings, diagnostics.getMethodParsingLog());
            if (parameters != null)
                parsedMethods.add(new ParsedMethod(method, parameters));
        }

        return parsedMethods;
    }

    private @Nullable Object[] parseMethodParameters(@NotNull Method method, final @NotNull String[] parameterStrings, final @NotNull LinkedList<ParsingLog> methodParsingLog) {
        Class<?>[] parameterTypes = method.getParameterTypes().clone();
        Object[] parameters = new Object[parameterTypes.length];
        boolean encounteredException = false;

        for (int i = 0; i < parameters.length; i++)
            try {
                parameters[i] = classParser.parseStringToClass(parameterStrings[i], parameterTypes[i]);
            } catch (Exception e) {
                encounteredException = true;
                parameters[i] = e;
                parameterTypes[i] = e.getClass();
            }

        if (encounteredException) {
            methodParsingLog.add(new ParsingLog(method, parameterStrings, parameterTypes, parameters));
            return null;
        }

        methodParsingLog.add(new ParsingLog(method, parameterStrings, null, parameters));
        return parameters;
    }

    private @NotNull String noMethodsParsedMessage(@NotNull Diagnostics diagnostics) {
        return "§cno methods parsed!";
    }

    private @NotNull String multipleMethodsParsedMessage(@NotNull Diagnostics diagnostics) {
        //noinspection SpellCheckingInspection
        return "§cmultiple methods parsed!";
    }

    private @NotNull LinkedList<Method> methodsMatchingParameterCount(final @NotNull LinkedList<Method> methods, final int argumentCount, final @NotNull Diagnostics diagnostics) {
        methods.removeIf(method -> {
            //noinspection ResultOfMethodCallIgnored
            diagnostics.getMethodParsingLog();
            return method.getParameterCount() != argumentCount - method.getAnnotation(ReflectCommand.class).path().split(" ").length;
        });
        return methods;
    }

    private @NotNull String noMethodsMatchingParameterCountMessage(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        return "§cno methods matching parameter count!";
    }

    private @NotNull LinkedList<Method> getPathMatchingMethods(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        LinkedList<Method> methods = new LinkedList<>();

        for (int i = 0; i < arguments.length && i < longestPath; i++) {
            String[] path = new String[i+1];
            System.arraycopy(arguments, 0, path, 0, path.length);

            ArrayList<Method> methodsMatchingPath = pathToMethodsMap.get(String.join(" ", path));
            if (methodsMatchingPath != null)
                methods.addAll(methodsMatchingPath);
        }

        diagnostics.setMatchingPaths(new ArrayList<>(methods));
        return methods;
    }

    private @NotNull String noPathMatchingMethodsMessage(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        return "§cno methods matching path!";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {
        return execute(sender, alias, arguments);
    }
}
