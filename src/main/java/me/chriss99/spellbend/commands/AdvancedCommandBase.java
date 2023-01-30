package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.CustomizableStringToClassParser;
import me.chriss99.spellbend.util.NoSuchParserException;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public abstract class AdvancedCommandBase extends BukkitCommand implements CommandExecutor {
    private final Map<String, AdvancedSubCommand> subCommands;
    private static final CustomizableStringToClassParser parser = new CustomizableStringToClassParser();
    private final String command;
    private final String usage;

    public AdvancedCommandBase(@NotNull String command, @NotNull String usage, @NotNull Map<String, AdvancedSubCommand> subCommands) {
        super(command);
        this.subCommands = subCommands;
        this.command = command;
        this.usage = usage;

        PluginCommand pluginCommand = SpellBend.getInstance().getCommand(command);
        if (pluginCommand == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Command \"" + command + "\" could not be registered!\n" +
                    "This happened because the PluginCommand returned by the main class was null.\n" +
                    "(This won't break anything, except that the command cannot be executed.)");
            return;
        }
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] arguments) {
        final List<AdvancedSubCommand> validSubCommands = new ArrayList<>();
        String path = "";
        String latestValidPath = "";
        String potentialErrorMessage = "§4No Error was found.";
        List<String> argumentList = new ArrayList<>(Arrays.asList(arguments));

        if (subCommands.containsKey(path)) validSubCommands.add(subCommands.get(path));
        for (String argument : arguments) {
            if (!path.equals("")) path = (path + " " + argument);
            else path = arguments[0];

            if (subCommands.containsKey(path)) {
                AdvancedSubCommand cmd = subCommands.get(path);
                if (((path.equals("")) ? (0) : (path.split(" ").length)) + cmd.arguments.length == argumentList.size()) {
                    latestValidPath = path;
                    validSubCommands.add(cmd);
                } else potentialErrorMessage = ("§4Wrong amount of arguments, this subCommand requires " + cmd.arguments.length + " arguments!");
            }
        }
        for (String string : latestValidPath.split(" ")) argumentList.remove(string);

        if (validSubCommands.size() == 0) {
            sender.sendMessage("§4Wrong command usage, no valid subCommand found! This is a potential error:");
            sender.sendMessage(potentialErrorMessage);

            if (potentialErrorMessage.equals("§4No Error was found.")) {
                sender.sendMessage("Valid subCommands are:");
                for (Map.Entry<String, AdvancedSubCommand> entry: subCommands.entrySet()) {
                    sender.sendMessage("/" + command + " " + entry.getKey() + " " + entry.getValue().argumentString);
                }
                return true;
            }

            sender.sendMessage(usage);
            return true;
        }

        AdvancedSubCommand subCommand = validSubCommands.get(validSubCommands.size()-1);
        List<Object> parsedArguments = new ArrayList<>();
        for (int i = 0;i<subCommand.arguments.length;i++) {
            String parseFrom = argumentList.get(i);
            Class<?> parseTo = subCommand.arguments[i];

            try {
                parsedArguments.add(parser.parseStringToClass(parseFrom, parseTo));
            } catch (NoSuchParserException nspe) {
                sender.sendMessage("§cWrong command setUp, type \"" + parseTo.getSimpleName() + "\" does not have a parser!\n§4NoSuchParserException: §c" + nspe.getMessage());
                return true;
            } catch (Exception e) {
                sender.sendMessage("§cWrong command usage, subCommand argument \"" + parseFrom + "\" is supposed to be of type \"" + parseTo.getSimpleName() + "\"!\n§4" +
                        e.getClass().getSimpleName() + ": §c" + e.getMessage());
                return true;
            }
        }

        return subCommand.onCommand(sender, parsedArguments);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {
        return execute(sender, alias, arguments);
    }

    @Override
    public @NotNull String getUsage() {return usage;}
}
