package me.chriss99.spellbend;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.google.gson.Gson;
import me.chriss99.spellbend.commands.Test;
import me.chriss99.spellbend.data.ActionBarController;
import me.chriss99.spellbend.data.PlayerDataBoard;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.events.paper.*;
import me.chriss99.spellbend.events.protocollib.*;
import me.chriss99.spellbend.spells.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class SpellBend extends JavaPlugin {
    private static SpellBend instance;
    private static final Gson gson = new Gson();
    private static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

        registerAllPaperEvents();
        registerAllPacketListeners();
        registerAllSpells();
        registerAllCommands();
        PlayerDataBoard.startUpdater();
        ActionBarController.startUpdater();
        PlayerSessionData.startManaRegenerator();

        Bukkit.getLogger().info("SpellBend enabled!");
    }

    @Override
    public void onDisable() {
        PlayerSessionData.endAllSessions();

        Bukkit.getLogger().info("SpellBend disabled!");
    }

    public static void registerAllPaperEvents() {
        new PlayerInteractBlock();
        new PlayerInteractEntity();
        new PlayerJoin();
        new PlayerQuit();
        new FoodLevelChange();
        new PlayerSwitchHeldItem();
        new InventoryClick();
        new PlayerJump();
    }

    public static void registerAllPacketListeners() {
        new PlayClientPosition();
        new PlayClientPositionLook();
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

    public static void registerPaperEvent(@NotNull Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void registerPacketListener(@NotNull PacketAdapter adapter) {
        protocolManager.addPacketListener(adapter);
    }

    public static SpellBend getInstance() {
        return instance;
    }

    public static Gson getGson() {
        return gson;
    }
}
