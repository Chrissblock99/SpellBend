package me.chriss99.spellbend.util;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.commands.Test;
import me.chriss99.spellbend.events.*;
import me.chriss99.spellbend.spell.spellsubclassbuilder.Ember_BlastBuilder;
import me.chriss99.spellbend.spell.spellsubclassbuilder.Fiery_RageBuilder;
import me.chriss99.spellbend.spell.spellsubclassbuilder.Test_SpellBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class GeneralRegisterUtil {
    public static void registerEvent(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, SpellBend.getInstance());
    }

    public static void registerAllEvents() {
        new PlayerInteractBlock();
        new PlayerInteractEntity();
        new PlayerJoin();
        new PlayerQuit();
        new FoodLevelChange();
        new PlayerSwitchHeldItem();
        new InventoryClick();
    }

    public static void registerAllSpells() {
        new Test_SpellBuilder();

        new Fiery_RageBuilder();
        new Ember_BlastBuilder();
    }

    public static void registerAllCommands() {
        new Test();
    }
}
