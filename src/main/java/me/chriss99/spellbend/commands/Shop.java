package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.gui.ShopGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class Shop extends BukkitCommand implements CommandExecutor {
    public Shop() {
        super("shop");

        PluginCommand pluginCommand = SpellBend.getInstance().getCommand("shop");
        if (pluginCommand == null) {
            Bukkit.getLogger().log(Level.SEVERE, """
                    /shop could not be registered!
                    This happened because the PluginCommand returned by the main class was null.
                    (This won't break anything, except that the command cannot be executed.)""");
            return;
        }
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }

        new ShopGui(player);
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return execute(sender, label, args);
    }
}
