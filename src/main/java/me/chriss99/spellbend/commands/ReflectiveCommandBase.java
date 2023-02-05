package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.CustomClassParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public abstract class ReflectiveCommandBase extends BukkitCommand implements CommandExecutor {
    private final LinkedHashMap<String, ArrayList<PreParsingMethod>> pathToPreParsingMethodsMap = new LinkedHashMap<>();
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
            PreParsingMethod preParsingMethod = new PreParsingMethod(method, commandName);

            if (longestPath < preParsingMethod.getPathSize())
                longestPath = preParsingMethod.getPathSize();

            String path = preParsingMethod.getPath().toUpperCase();
            ArrayList<PreParsingMethod> samePathMethods = pathToPreParsingMethodsMap.get(path);

            if (samePathMethods == null) {
                pathToPreParsingMethodsMap.put(path, new ArrayList<>(List.of(preParsingMethod)));
                continue;
            }

            for (PreParsingMethod listMethod : samePathMethods)
                if (Arrays.equals(preParsingMethod.getParsingParameterTypes(), listMethod.getParsingParameterTypes()))
                    throw new IllegalStateException("Methods \"" + method.getName() + "\" and " + listMethod.getMethod().getName() + " have the same path and parsingParameterTypes!");

            samePathMethods.add(preParsingMethod);
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
        LinkedList<PreParsingMethod> preParsingMethods = getPathMatchingMethods(arguments, diagnostics);

        if (preParsingMethods.isEmpty()) {
            sender.sendMessage(noPathMatchingMethodsMessage(diagnostics.getPotentialPaths()));
            return true;
        }
        if (methodsMatchingParameterCount(preParsingMethods, arguments.length, diagnostics).isEmpty()) {
            sender.sendMessage(noMethodsMatchingParameterCountMessage(arguments, diagnostics));
            return true;
        }
        LinkedList<ParsedMethod> parsedMethods = successfullyParsedMethods(preParsingMethods, arguments, diagnostics);

        if (parsedMethods.isEmpty()) {
            sender.sendMessage(noMethodsParsedMessage(diagnostics));
            return true;
        }

        if (parsedMethods.size() == 1) {
            ParsedMethod parsedMethod = parsedMethods.get(0);
            if (Player.class.equals(parsedMethod.preParsedMethod().getSenderParameterType()))
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cOnly players can use this subCommand!");
                    return true;
                }

            Object[] parameters = new Object[parsedMethod.preParsedMethod().getMethod().getParameterCount()];
            boolean methodHasSenderParameter = parsedMethod.preParsedMethod().getSenderParameterType() != null;
            if (methodHasSenderParameter)
                parameters[0] = sender;

            System.arraycopy(parsedMethod.parameters(), 0, parameters, (methodHasSenderParameter) ? 1 : 0, parsedMethod.parameters().length);

            try {
                parsedMethod.preParsedMethod().getMethod().invoke(this, parameters);
            } catch (IllegalAccessException e) {
                sender.sendMessage("§cThe programmer of this subCommand used the ReflectiveCommandBase incorrectly!\n§4IllegalAccessException: §c" + e.getMessage());
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                sender.sendMessage("§cThe subCommand method \"" + parsedMethod.preParsedMethod().getMethod().getName() + "\" threw an exception!\n§4" +
                        cause.getClass().getSimpleName() + ": §c" + cause.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        sender.sendMessage(multipleMethodsParsedMessage(diagnostics));
        return true;
    }

    private @NotNull LinkedList<PreParsingMethod> getPathMatchingMethods(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        LinkedList<PreParsingMethod> methods = new LinkedList<>();
        ArrayList<String> potentialPaths = new ArrayList<>(longestPath);

        for (int i = 0; i < arguments.length && i < longestPath; i++) {
            String path = String.join(" ", Arrays.copyOfRange(arguments, 0, i+1)).toUpperCase();
            potentialPaths.add(path);

            ArrayList<PreParsingMethod> methodsMatchingPath = pathToPreParsingMethodsMap.get(path);
            if (methodsMatchingPath != null)
                methods.addAll(methodsMatchingPath);
        }

        diagnostics.setPotentialPaths(potentialPaths);
        return methods;
    }

    private @NotNull String noPathMatchingMethodsMessage(final @NotNull ArrayList<String> potentialPaths) {
        HashSet<Map.Entry<String, ArrayList<PreParsingMethod>>> matchingMethods = new HashSet<>(pathToPreParsingMethodsMap.entrySet());
        //noinspection unchecked
        HashSet<Map.Entry<String, ArrayList<PreParsingMethod>>> newMatchingMethods = (HashSet<Map.Entry<String, ArrayList<PreParsingMethod>>>) matchingMethods.clone();
        for (String potentialPath : potentialPaths) {
            newMatchingMethods.removeIf(pathToMethod -> {
                try {
                    return potentialPath.equals(pathToMethod.getKey().substring(1, potentialPath.length()));
                } catch (StringIndexOutOfBoundsException sioobe) {
                    return false;
                }
            });
            if (newMatchingMethods.isEmpty())
                break;
            //noinspection unchecked
            matchingMethods = (HashSet<Map.Entry<String, ArrayList<PreParsingMethod>>>) newMatchingMethods.clone();
        }

        List<PreParsingMethod> sortedMostPathMatchingMethods = new LinkedList<>();
        for (Map.Entry<String, ArrayList<PreParsingMethod>> pathToMethods : matchingMethods)
            sortedMostPathMatchingMethods.addAll(pathToMethods.getValue());
        sortedMostPathMatchingMethods.sort(Comparator.comparing(PreParsingMethod::getArguments));

        StringBuilder stringBuilder = new StringBuilder("§cNo subCommands matched the given path! Most matching subCommands:§r");
        for (PreParsingMethod preParsingMethod : sortedMostPathMatchingMethods)
            stringBuilder.append("\n").append(preParsingMethod.getArguments());

        return stringBuilder.toString();
    }

    private @NotNull LinkedList<PreParsingMethod> methodsMatchingParameterCount(final @NotNull LinkedList<PreParsingMethod> methodParsingPresets, final int argumentCount, final @NotNull Diagnostics diagnostics) {
        methodParsingPresets.removeIf(preParsingMethod -> preParsingMethod.getParsingParameterTypes().length != argumentCount - preParsingMethod.getPathSize());
        return methodParsingPresets;
    }

    private @NotNull String noMethodsMatchingParameterCountMessage(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        String path = diagnostics.getPotentialPaths().get(diagnostics.getPotentialPaths().size()-1);
        //if anyone wants to use the arguments to find out which of the possible parameter lists fit the best, here is the place to start!

        StringBuilder stringBuilder = new StringBuilder("§cWrong parameter count! Possible subcommand parameters:§r");
        for (PreParsingMethod preParsingMethod : pathToPreParsingMethodsMap.get(path))
            stringBuilder.append("\n").append(preParsingMethod.getArguments());

        return stringBuilder.toString();
    }

    private @NotNull LinkedList<ParsedMethod> successfullyParsedMethods(@NotNull LinkedList<PreParsingMethod> preParsingMethods, @NotNull String[] arguments, @NotNull Diagnostics diagnostics) {
        LinkedList<ParsedMethod> parsedMethods = new LinkedList<>();
        diagnostics.setMethodParsingLog(new LinkedList<>());

        for (PreParsingMethod preParsingMethod : preParsingMethods) {
            String[] parameterStrings = new String[preParsingMethod.getParsingParameterTypes().length];
            System.arraycopy(arguments, arguments.length-parameterStrings.length, parameterStrings, 0, parameterStrings.length);

            Object[] parameters = parseMethodParameters(preParsingMethod, parameterStrings, diagnostics.getMethodParsingLog());
            if (parameters != null)
                parsedMethods.add(new ParsedMethod(preParsingMethod, parameters));
        }

        return parsedMethods;
    }

    private @Nullable Object[] parseMethodParameters(@NotNull PreParsingMethod preParsingMethod, final @NotNull String[] parameterStrings, final @NotNull LinkedList<ParsingLog> methodParsingLog) {
        Class<?>[] parameterTypes = preParsingMethod.getParsingParameterTypes().clone();
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
            methodParsingLog.add(new ParsingLog(preParsingMethod, parameterStrings, parameterTypes, parameters));
            return null;
        }

        methodParsingLog.add(new ParsingLog(preParsingMethod, parameterStrings, null, parameters));
        return parameters;
    }

    private @NotNull String noMethodsParsedMessage(@NotNull Diagnostics diagnostics) {
        return "§cno methods parsed!";
    }

    private @NotNull String multipleMethodsParsedMessage(@NotNull Diagnostics diagnostics) {
        //noinspection SpellCheckingInspection
        return "§cmultiple methods parsed!";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {
        return execute(sender, alias, arguments);
    }
}
