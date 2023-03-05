package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.spells.Spell;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public class ReflectTest extends ReflectiveCommandBase {
    public ReflectTest() {
        super("reflectTest", "description", new ArrayList<>());
    }

    @ReflectCommand(path = "noParams")
    public void noParams() {
        for (World world : Bukkit.getWorlds())
            for (Player player : world.getPlayers())
                player.sendMessage(SpellBend.getMiniMessage().deserialize("\"/reflectTest noParams\" executed!"));
    }

    @ReflectCommand(path = "value")
    public void value_double(double value) {
        Bukkit.getLogger().info("\"/reflectTest value\" executed with double " + value + "!");
    }

    @ReflectCommand(path = "value")
    public void value_double(int value) {
        Bukkit.getLogger().info("\"/reflectTest value\" executed with int " + value + "!");
    }

    @ReflectCommand(path = "")
    public void noPath() {
        for (World world : Bukkit.getWorlds())
            for (Player player : world.getPlayers())
                player.sendMessage(SpellBend.getMiniMessage().deserialize("\"/reflectTest\" executed!"));
    }

    @ReflectCommand(path = "senderOnly")
    public void senderOnly(CommandSender commandSender) {
        for (World world : Bukkit.getWorlds())
            for (Player player : world.getPlayers())
                player.sendMessage(SpellBend.getMiniMessage().deserialize("\"/reflectTest senderOnly\" executed by " + commandSender.getName() + "!"));
    }

    @ReflectCommand(path = "playerOnly")
    public void playerOnly(Player commandSender) {
        for (World world : Bukkit.getWorlds())
            for (Player player : world.getPlayers())
                player.sendMessage(SpellBend.getMiniMessage().deserialize("\"/reflectTest playerOnly\" executed by " + commandSender.getName() + "!"));
    }

    @ReflectCommand(path = "playerOnly")
    public void playerOnly(Player commandSender, int value) {
        for (World world : Bukkit.getWorlds())
            for (Player player : world.getPlayers())
                player.sendMessage(SpellBend.getMiniMessage().deserialize("\"/reflectTest playerOnly\" executed by " + commandSender.getName() + " with value " + value + "!"));
    }

    @ReflectCommand(path = "memory")
    public void memory(CommandSender commandSender, Player player) {
        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("Spells:"));
        Set<Spell> playerSpells = PlayerSessionData.getPlayerSession(player).getSpellHandler().getActivePlayerSpells();
        if (playerSpells.size() == 0) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
            return;
        }

        for (Spell spell : playerSpells) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(spell.getClass().getName()));
        }
    }

    @ReflectCommand(path = "memory")
    public void memory(Player sendTo, Player player) {
        sendTo.sendMessage(SpellBend.getMiniMessage().deserialize("Spells:"));
        Set<Spell> playerSpells = PlayerSessionData.getPlayerSession(player).getSpellHandler().getActivePlayerSpells();
        if (playerSpells.size() == 0) {
            sendTo.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
            return;
        }

        for (Spell spell : playerSpells) {
            sendTo.sendMessage(SpellBend.getMiniMessage().deserialize(spell.getClass().getName()));
        }
    }

    @ReflectCommand(path = "memory spell")
    public void memory_spell(Player player) {
        player.sendMessage(SpellBend.getMiniMessage().deserialize("Spells:"));
        Set<Spell> playerSpells = PlayerSessionData.getPlayerSession(player).getSpellHandler().getActivePlayerSpells();
        if (playerSpells.size() == 0) {
            player.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
            return;
        }

        for (Spell spell : playerSpells) {
            player.sendMessage(SpellBend.getMiniMessage().deserialize(spell.getClass().getName()));
        }
    }
}
