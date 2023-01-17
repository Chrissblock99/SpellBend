package me.chriss99.spellbend;

import com.google.gson.Gson;
import me.chriss99.spellbend.commands.Test;
import me.chriss99.spellbend.data.ActionBarController;
import me.chriss99.spellbend.data.PlayerDataBoard;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.events.paper.*;
import me.chriss99.spellbend.spells.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpellBend extends JavaPlugin {
    private static SpellBend instance;
    private static final Gson gson = new Gson();

    @Override
    public void onEnable() {
        instance = this;

        registerAllEvents();
        registerAllSpells();
        registerAllCommands();
        PlayerDataBoard.startUpdater();
        ActionBarController.startUpdater();
        PlayerSessionData.startManaRegenerator();
    }

    @Override
    public void onDisable() {
        PlayerSessionData.endAllSessions();
    }

    public static void registerAllEvents() {
        new PlayerInteractBlock();
        new PlayerInteractEntity();
        new PlayerJoin();
        new PlayerQuit();
        new FoodLevelChange();
        new PlayerSwitchHeldItem();
        new InventoryClick();
        new PlayerJump();
    }

    public static void registerAllSpells() {
        Test_Spell.register();

        Fiery_Rage.register();
        Ember_Blast.register();
        Escape_Through_Time.register();
        Seismic_Shock.register();
    }

    public static void registerAllCommands() {
        new Test();
    }

    public static void registerEvent(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static SpellBend getInstance() {
        return instance;
    }

    public static Gson getGson() {
        return gson;
    }
}
