package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.CustomClassParser;
import me.chriss99.spellbend.util.ParameterTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Level;

public abstract class ReflectiveCommandBase extends BukkitCommand implements CommandExecutor, TabCompleter {
    private final LinkedHashMap<String, ArrayList<SubCommand>> pathToSubCommandsMap = new LinkedHashMap<>();
    private int maxPathLength = 0;
    private final CustomClassParser classParser;
    private final ParameterTabCompleter tabCompleter;

    /**
     * Initializes the CommandBase with the default classParser and tabCompleter
     *
     * @param commandName The name of the command
     * @param description The description of the command
     * @param aliases The possible aliases (doesn't matter, only the ones in plugin.yml are usable, regardless of their presence here)
     *
     * @throws IllegalStateException When two methods have the same annotated path and arguments
     */
    public ReflectiveCommandBase(@NotNull String commandName, @NotNull String description, @NotNull List<String> aliases) {
        this(commandName, description, aliases, new CustomClassParser(), new ParameterTabCompleter());
    }

    /**
     * Initializes the CommandBase with the default tabCompleter
     *
     * @param commandName The name of the command
     * @param description The description of the command
     * @param aliases The possible aliases (doesn't matter, only the ones in plugin.yml are usable, regardless of their presence here)
     * @param classParser The classParser to parse the arguments (needed if arguments contain non-standard classes)
     *
     * @throws IllegalStateException When two methods have the same annotated path and arguments
     */
    public ReflectiveCommandBase(@NotNull String commandName, @NotNull String description, @NotNull List<String> aliases, @NotNull CustomClassParser classParser) {
        this(commandName, description, aliases, classParser, new ParameterTabCompleter());
    }

    /**
     * Initializes the CommandBase with the default tabCompleter
     *
     * @param commandName The name of the command
     * @param description The description of the command
     * @param aliases The possible aliases (doesn't matter, only the ones in plugin.yml are usable, regardless of their presence here)
     * @param tabCompleter The tabCompleter to use (useful for tab completion of non-standard classes)
     *
     * @throws IllegalStateException When two methods have the same annotated path and arguments
     */
    public ReflectiveCommandBase(@NotNull String commandName, @NotNull String description, @NotNull List<String> aliases, @NotNull ParameterTabCompleter tabCompleter) {
        this(commandName, description, aliases, new CustomClassParser(), tabCompleter);
    }

    /**
     * Initializes the CommandBase with a custom classParser and tabCompleter
     *
     * @param commandName The name of the command
     * @param description The description of the command
     * @param aliases The possible aliases (doesn't matter, only the ones in plugin.yml are usable, regardless of their presence here)
     * @param classParser The classParser to parse the arguments (needed if arguments contain non-standard classes)
     * @param tabCompleter The tabCompleter to use (useful for tab completion of non-standard classes)
     *
     * @throws IllegalStateException When two methods have the same annotated path and arguments
     */
    public ReflectiveCommandBase(@NotNull String commandName, @NotNull String description, @NotNull List<String> aliases, @NotNull CustomClassParser classParser, @NotNull ParameterTabCompleter tabCompleter) {
        super(commandName, description,
                "If this shows up, someone has not updated the usageMessage in ReflectiveCommandBase yet. Please notify a developer immediately!", aliases);
        this.classParser = classParser;
        this.tabCompleter = tabCompleter;

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

    /**
     * Gets called whenever the command is called <br>
     * Handles the individual methods to find which subCommand is used, parse the arguments to and execute it and give error feedback
     *
     * @param sender Source object which is executing this command
     * @param alias The alias of the command used
     * @param arguments All arguments passed to the command, split via ' '
     * @return true, always, error feedback is handled here
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] arguments) {
        Diagnostics diagnostics = new Diagnostics(arguments, maxPathLength);
        //these can have the same parameter count, but will have different parameter types, though those types might not be distinguishable in string from
        LinkedList<SubCommand> subCommands = getPathMatchingSubCommands(arguments, diagnostics);

        if (subCommands.isEmpty()) {
            sender.sendMessage(noPathMatchingSubCommandsMessage(diagnostics.getPossiblePaths()));
            return true;
        }
        if (subCommandsMatchingParameterCount(subCommands, arguments.length).isEmpty()) {
            sender.sendMessage(noSubCommandsMatchingParameterCountMessage(diagnostics.getPossiblePaths()));
            return true;
        }
        LinkedList<ParsedSubCommand> parsedSubCommands = successfullyParsedSubCommands(subCommands, arguments, diagnostics);

        if (parsedSubCommands.isEmpty()) {
            sender.sendMessage(noSubCommandsParsedMessage(diagnostics.getSubCommandParsingLog()));
            return true;
        }
        if (parsedSubCommands.size() > 1) {
            sender.sendMessage(multipleSubCommandsParsedMessage(diagnostics.getSubCommandParsingLog()));
            return true;
        }

        ParsedSubCommand parsedSubCommand = parsedSubCommands.get(0);
        if (Player.class.equals(parsedSubCommand.subCommand().getSenderParameterType()) && !(sender instanceof Player)) {
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

    /**
     * Generates all possible paths and returns a list of all subCommands with any of those paths
     *
     * @param arguments The arguments given
     * @param diagnostics The diagnostics object
     * @return All subCommands matching with a possible path
     */
    private @NotNull LinkedList<SubCommand> getPathMatchingSubCommands(final @NotNull String[] arguments, final @NotNull Diagnostics diagnostics) {
        LinkedList<SubCommand> subCommands = new LinkedList<>();

        for (String path : diagnostics.getPossiblePaths()) {
            ArrayList<SubCommand> SubCommandsMatchingPath = pathToSubCommandsMap.get(path);
            if (SubCommandsMatchingPath != null)
                subCommands.addAll(SubCommandsMatchingPath);
        }

        return subCommands;
    }

    /**
     * Formats and creates the error feedback message for the case that no subCommands match the given arguments
     *
     * @param possiblePaths The pre-generated possible paths, from shortest to longest
     * @return The error feedback
     */
    private @NotNull String noPathMatchingSubCommandsMessage(final @NotNull ArrayList<String> possiblePaths) {
        StringBuilder stringBuilder = new StringBuilder("§cInvalid path! Most matching subCommands:§r");
        for (SubCommand subCommand : mostPathMatchingSubCommands(possiblePaths))
            stringBuilder.append("\n").append(subCommand.getArguments());

        return stringBuilder.toString();
    }

    /**
     * Creates a copy of the subCommands and iterates through all possible paths from shortest (most general) to longest (most specific)<br>
     * On each iteration it removes all subCommands from the copy, which don't match the current path <br>
     * If the copy is left empty, it continues with the copy of the last iteration <br>
     * Therefore it always uses the most specific subCommand collection
     *
     * @param possiblePaths The pre-generated possible paths, from shortest to longest
     * @return An alphabetically sorted list of the most matching subCommands
     */
    private @NotNull LinkedList<SubCommand> mostPathMatchingSubCommands(final @NotNull ArrayList<String> possiblePaths) {
        HashSet<Map.Entry<String, ArrayList<SubCommand>>> matchingPathToSubCommandsEntries = new HashSet<>(pathToSubCommandsMap.entrySet());
        //noinspection unchecked
        HashSet<Map.Entry<String, ArrayList<SubCommand>>> newMatchingPathToSubCommandsEntries = (HashSet<Map.Entry<String, ArrayList<SubCommand>>>) matchingPathToSubCommandsEntries.clone();
        for (String potentialPath : possiblePaths) {
            newMatchingPathToSubCommandsEntries.removeIf(pathToSubCommands -> {
                try {
                    return !potentialPath.equals(pathToSubCommands.getKey().substring(0, potentialPath.length()));
                } catch (StringIndexOutOfBoundsException sioobe) {
                    return true;
                }
            });
            if (newMatchingPathToSubCommandsEntries.isEmpty())
                break;
            //noinspection unchecked
            matchingPathToSubCommandsEntries = (HashSet<Map.Entry<String, ArrayList<SubCommand>>>) newMatchingPathToSubCommandsEntries.clone();
        }

        LinkedList<SubCommand> sortedMostPathMatchingSubCommands = new LinkedList<>();
        for (Map.Entry<String, ArrayList<SubCommand>> pathToMethods : matchingPathToSubCommandsEntries)
            sortedMostPathMatchingSubCommands.addAll(pathToMethods.getValue());
        sortedMostPathMatchingSubCommands.sort(Comparator.comparing(SubCommand::getArguments));
        return sortedMostPathMatchingSubCommands;
    }

    /**
     * Removes all subCommands from the list which path length plus parameter count don't match the argument count
     *
     * @param SubCommands The subCommands to filter through
     * @param argumentCount The argument count
     * @return The filtered list
     */
    private @NotNull LinkedList<SubCommand> subCommandsMatchingParameterCount(final @NotNull LinkedList<SubCommand> SubCommands, final int argumentCount) {
        SubCommands.removeIf(subCommand -> argumentCount !=  subCommand.getPathLength() + subCommand.getParsingParameters().length);
        return SubCommands;
    }

    /**
     * Generates the error feedback for the case that no methods match the parameter count. <br>
     * This does the same thing as noPathMatchingSubCommandsMessage()
     *
     * @param possiblePaths The pre-generated possible paths, from shortest to longest
     * @return The error feedback
     */
    private @NotNull String noSubCommandsMatchingParameterCountMessage(final @NotNull ArrayList<String> possiblePaths) {
        //if anyone wants to use the arguments to find out which of the possible parameter lists fit the best, here is the place to start!
        StringBuilder stringBuilder = new StringBuilder("§cWrong parameter count! Possible subCommands:§r");
        for (SubCommand subCommand : mostPathMatchingSubCommands(possiblePaths))
            stringBuilder.append("\n").append(subCommand.getArguments());

        return stringBuilder.toString();
    }

    /**
     * Converts a subCommand list to a list of parsedSubCommands<br>
     * This uses parseSubCommandParameters() and only includes subCommands that passed this function successfully
     *
     * @param subCommands The subcommands to parse
     * @param arguments The given arguments
     * @param diagnostics The diagnostics object
     * @return A list of all successfully parsed SubCommands including parameters
     */
    private @NotNull LinkedList<ParsedSubCommand> successfullyParsedSubCommands(@NotNull LinkedList<SubCommand> subCommands, @NotNull String[] arguments, @NotNull Diagnostics diagnostics) {
        LinkedList<ParsedSubCommand> parsedSubCommands = new LinkedList<>();
        diagnostics.setSubCommandParsingLog(new LinkedList<>());

        for (SubCommand subCommand : subCommands) {
            String[] parameterStrings = new String[subCommand.getParsingParameters().length];
            System.arraycopy(arguments, arguments.length-parameterStrings.length, parameterStrings, 0, parameterStrings.length);

            Object[] parameters = parseSubCommandParameters(subCommand, parameterStrings, diagnostics.getSubCommandParsingLog());
            if (parameters != null)
                parsedSubCommands.add(new ParsedSubCommand(subCommand, parameters));
        }

        return parsedSubCommands;
    }

    /**
     * Parses the given parameter strings into the types of the subCommand and returns it<br>
     * If an exception is encountered during the process it returns null
     *
     * @param subCommand The subCommand to parse
     * @param parameterStrings The parameters in string form
     * @param SubCommandParsingLog The parsingLog list
     * @return The parsed parameters or null if not successful
     */
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

    /**
     * Generates the error feedback for the case that no subCommands could be parsed
     *
     * @param subCommandParsingLog The list of parsingLogs
     * @return The error feedback
     */
    private @NotNull String noSubCommandsParsedMessage(@NotNull LinkedList<ParsingLog> subCommandParsingLog) {
        StringBuilder stringBuilder = new StringBuilder("§cIncorrect parameters!§r");
        for (ParsingLog parsingLog : subCommandParsingLog)
            addParsingFailures(stringBuilder, parsingLog);
        return stringBuilder.toString();
    }

    /**
     * Appends the error information of the parsingLog to the given stringBuilder
     *
     * @param stringBuilder The stringBuilder to add to
     * @param parsingLog The parsingLog object
     */
    private void addParsingFailures(@NotNull StringBuilder stringBuilder, @NotNull ParsingLog parsingLog) {
        if (parsingLog.parameterTypes() == null)
            return;

        stringBuilder.append("\n").append(parsingLog.subCommand().getArguments());
        for (int i = 0; i < parsingLog.parameters().length; i++) {
            if (!(parsingLog.parameters()[i] instanceof Exception) || parsingLog.parameterTypes()[i] == null)
                continue;

            stringBuilder.append("\n  \"").append(parsingLog.parameterStrings()[i]).append("\" -> ")
                    .append(parsingLog.subCommand().getParsingParameters()[i].getType().getSimpleName()).append("\n  §4")
                    .append(parsingLog.parameterTypes()[i].getSimpleName()).append(": §c")
                    .append(((Exception) parsingLog.parameters()[i]).getMessage()).append("§r");
        }
    }

    /**
     * Generates the error feedback for the case that multiple subCommands could be parsed
     *
     * @param subCommandParsingLog The list of parsingLogs
     * @return The error feedback
     */
    private @NotNull String multipleSubCommandsParsedMessage(@NotNull LinkedList<ParsingLog> subCommandParsingLog) {
        StringBuilder stringBuilder = new StringBuilder("§cThe parameters were parsable to multiple subCommands! There is no way to fix this currently, sorry.\n" +
                "Tip: floats can be differentiated from integers with a \".0\" at the end.§r");
        for (ParsingLog parsingLog : subCommandParsingLog)
            stringBuilder.append("\n").append(parsingLog.subCommand().getArguments());
        return stringBuilder.toString();
    }

    /**
     * Generates possible tab completions for the subCommands returned by mostMatchingSubCommands()
     *
     * @param sender Source of the command.  For players tab-completing a
     *     command inside a command block, this will be the player, not
     *     the command block.
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param arguments The arguments passed to the command, including final
     *     partial argument to be completed
     * @return The possible tab completions
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] arguments) {
        Diagnostics diagnostics = new Diagnostics(
                (arguments.length > 1 && arguments[arguments.length-1].equals("")) ?
                        Arrays.copyOfRange(arguments, 0, arguments.length-2) :
                        arguments,
                maxPathLength);
        int argLength = arguments.length-1; //arguments contains "" when nothing has been typed for an argument

        List<String> completions = new LinkedList<>();
        for (SubCommand subCommand : mostPathMatchingSubCommands(diagnostics.getPossiblePaths())) {
            String[] path = subCommand.getCleanPath();
            if (path.length > argLength) {
                completions.add(path[argLength]);
                continue;
            }

            int parameterLength = argLength-path.length;
            Parameter[] parameters = subCommand.getParsingParameters();
            if (parameters.length > parameterLength) {
                Parameter parameter = parameters[parameterLength];
                if (!completions.addAll(tabCompleter.enumerateOptions(parameter, arguments[argLength])))
                    completions.add(parameter.getType().getSimpleName() + "<" + parameter.getName() + ">");
            }
        }

        return completions;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {
        return execute(sender, alias, arguments);
    }
}
