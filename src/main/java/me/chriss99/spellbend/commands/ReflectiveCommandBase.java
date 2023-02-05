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
    private final LinkedHashMap<String, ArrayList<SubCommand>> pathToSubCommandsMap = new LinkedHashMap<>();
    private int maxPathLength = 0;
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
            SubCommand subCommand = new SubCommand(method, commandName);

            if (maxPathLength < subCommand.getPathLength())
                maxPathLength = subCommand.getPathLength();

            String path = subCommand.getPath().toUpperCase();
            ArrayList<SubCommand> samePathSubCommands = pathToSubCommandsMap.get(path);

            if (samePathSubCommands == null) {
                pathToSubCommandsMap.put(path, new ArrayList<>(List.of(subCommand)));
                continue;
            }

            for (SubCommand listSubCommand : samePathSubCommands)
                if (Arrays.equals(subCommand.getParsingParameterTypes(), listSubCommand.getParsingParameterTypes()))
                    throw new IllegalStateException("Methods \"" + method.getName() + "\" and " + listSubCommand.getMethod().getName() + " have the same path and parsingParameterTypes!");

            samePathSubCommands.add(subCommand);
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
        LinkedList<SubCommand> subCommands = getPathMatchingSubCommands(arguments, diagnostics);

        if (subCommands.isEmpty()) {
            sender.sendMessage(noPathMatchingSubCommandsMessage(diagnostics.getPossiblePaths()));
            return true;
        }
        if (subCommandsMatchingParameterCount(subCommands, arguments.length).isEmpty()) {
            sender.sendMessage(noSubCommandsMatchingParameterCountMessage(arguments, diagnostics));
            return true;
        }
        LinkedList<ParsedSubCommand> parsedSubCommands = successfullyParsedSubCommands(subCommands, arguments, diagnostics);

        if (parsedSubCommands.isEmpty()) {
            sender.sendMessage(noSubCommandsParsedMessage(diagnostics));
            return true;
        }

        if (parsedSubCommands.size() == 1) {
            ParsedSubCommand parsedSubCommand = parsedSubCommands.get(0);
            if (Player.class.equals(parsedSubCommand.subCommand().getSenderParameterType()))
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cOnly players can use this subCommand!");
                    return true;
                }

            Object[] parameters = new Object[parsedSubCommand.subCommand().getMethod().getParameterCount()];
            boolean methodHasSenderParameter = parsedSubCommand.subCommand().getSenderParameterType() != null;
            if (methodHasSenderParameter)
                parameters[0] = sender;

            System.arraycopy(parsedSubCommand.parameters(), 0, parameters, (methodHasSenderParameter) ? 1 : 0, parsedSubCommand.parameters().length);

            try {
                parsedSubCommand.subCommand().getMethod().invoke(this, parameters);
            } catch (IllegalAccessException iae) {
                sender.sendMessage("§cThe programmer of this subCommand used the ReflectiveCommandBase incorrectly!\n§4IllegalAccessException: §c" + iae.getMessage());
                iae.printStackTrace();
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                sender.sendMessage("§cThe subCommand method \"" + parsedSubCommand.subCommand().getMethod().getName() + "\" threw an exception!\n§4" +
                        cause.getClass().getSimpleName() + ": §c" + cause.getMessage());
                e.printStackTrace();
            }
            return true;
        }

        sender.sendMessage(multipleSubCommandsParsedMessage(diagnostics));
        return true;
    }

    private @NotNull LinkedList<SubCommand> getPathMatchingSubCommands(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        LinkedList<SubCommand> methods = new LinkedList<>();
        ArrayList<String> possiblePaths = new ArrayList<>(maxPathLength);

        for (int i = 0; i < arguments.length && i < maxPathLength; i++) {
            String path = String.join(" ", Arrays.copyOfRange(arguments, 0, i+1)).toUpperCase();
            possiblePaths.add(path);

            ArrayList<SubCommand> methodsMatchingPath = pathToSubCommandsMap.get(path);
            if (methodsMatchingPath != null)
                methods.addAll(methodsMatchingPath);
        }

        diagnostics.setPossiblePaths(possiblePaths);
        return methods;
    }

    private @NotNull String noPathMatchingSubCommandsMessage(final @NotNull ArrayList<String> possiblePaths) {
        HashSet<Map.Entry<String, ArrayList<SubCommand>>> matchingPathToSubCommandsEntrys = new HashSet<>(pathToSubCommandsMap.entrySet());
        //noinspection unchecked
        HashSet<Map.Entry<String, ArrayList<SubCommand>>> newMatchingPathToSubCommandsEntrys = (HashSet<Map.Entry<String, ArrayList<SubCommand>>>) matchingPathToSubCommandsEntrys.clone();
        for (String potentialPath : possiblePaths) {
            newMatchingPathToSubCommandsEntrys.removeIf(pathToSubCommands -> {
                try {
                    return potentialPath.equals(pathToSubCommands.getKey().substring(1, potentialPath.length()));
                } catch (StringIndexOutOfBoundsException sioobe) {
                    return false;
                }
            });
            if (newMatchingPathToSubCommandsEntrys.isEmpty())
                break;
            //noinspection unchecked
            matchingPathToSubCommandsEntrys = (HashSet<Map.Entry<String, ArrayList<SubCommand>>>) newMatchingPathToSubCommandsEntrys.clone();
        }

        List<SubCommand> sortedMostPathMatchingMethods = new LinkedList<>();
        for (Map.Entry<String, ArrayList<SubCommand>> pathToMethods : matchingPathToSubCommandsEntrys)
            sortedMostPathMatchingMethods.addAll(pathToMethods.getValue());
        sortedMostPathMatchingMethods.sort(Comparator.comparing(SubCommand::getArguments));

        StringBuilder stringBuilder = new StringBuilder("§cNo subCommands matched the given path! Most matching subCommands:§r");
        for (SubCommand subCommand : sortedMostPathMatchingMethods)
            stringBuilder.append("\n").append(subCommand.getArguments());

        return stringBuilder.toString();
    }

    private @NotNull LinkedList<SubCommand> subCommandsMatchingParameterCount(final @NotNull LinkedList<SubCommand> SubCommands, final int argumentCount) {
        SubCommands.removeIf(subCommand -> argumentCount !=  subCommand.getPathLength() + subCommand.getParsingParameterTypes().length);
        return SubCommands;
    }

    private @NotNull String noSubCommandsMatchingParameterCountMessage(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        String path = diagnostics.getPossiblePaths().get(diagnostics.getPossiblePaths().size()-1);
        //if anyone wants to use the arguments to find out which of the possible parameter lists fit the best, here is the place to start!

        StringBuilder stringBuilder = new StringBuilder("§cWrong parameter count! Possible subcommand parameters:§r");
        for (SubCommand subCommand : pathToSubCommandsMap.get(path))
            stringBuilder.append("\n").append(subCommand.getArguments());

        return stringBuilder.toString();
    }

    private @NotNull LinkedList<ParsedSubCommand> successfullyParsedSubCommands(@NotNull LinkedList<SubCommand> subCommands, @NotNull String[] arguments, @NotNull Diagnostics diagnostics) {
        LinkedList<ParsedSubCommand> parsedSubCommands = new LinkedList<>();
        diagnostics.setSubCommandParsingLog(new LinkedList<>());

        for (SubCommand subCommand : subCommands) {
            String[] parameterStrings = new String[subCommand.getParsingParameterTypes().length];
            System.arraycopy(arguments, arguments.length-parameterStrings.length, parameterStrings, 0, parameterStrings.length);

            Object[] parameters = parseSubCommandParameters(subCommand, parameterStrings, diagnostics.getSubCommandParsingLog());
            if (parameters != null)
                parsedSubCommands.add(new ParsedSubCommand(subCommand, parameters));
        }

        return parsedSubCommands;
    }

    private @Nullable Object[] parseSubCommandParameters(@NotNull SubCommand subCommand, final @NotNull String[] parameterStrings, final @NotNull LinkedList<ParsingLog> SubCommandParsingLog) {
        Class<?>[] parameterTypes = subCommand.getParsingParameterTypes().clone();
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
            SubCommandParsingLog.add(new ParsingLog(subCommand, parameterStrings, parameterTypes, parameters));
            return null;
        }

        SubCommandParsingLog.add(new ParsingLog(subCommand, parameterStrings, null, parameters));
        return parameters;
    }

    private @NotNull String noSubCommandsParsedMessage(@NotNull Diagnostics diagnostics) {
        return "§cno methods parsed!";
    }

    private @NotNull String multipleSubCommandsParsedMessage(@NotNull Diagnostics diagnostics) {
        //noinspection SpellCheckingInspection
        return "§cmultiple methods parsed!";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {
        return execute(sender, alias, arguments);
    }
}
