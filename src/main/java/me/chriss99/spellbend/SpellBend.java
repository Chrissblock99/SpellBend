package me.chriss99.spellbend;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.google.gson.Gson;
import me.chriss99.spellbend.commands.ReflectTest;
import me.chriss99.spellbend.commands.Shop;
import me.chriss99.spellbend.commands.Test;
import me.chriss99.spellbend.data.*;
import me.chriss99.spellbend.events.paper.*;
//import me.chriss99.spellbend.events.protocollib.*;
import me.chriss99.spellbend.manager.BlockManager;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class SpellBend extends JavaPlugin {
    private static SpellBend instance;
    private static ProtocolManager protocolManager;
    private static final Gson gson = new Gson();
    private static final MiniMessage miniMsg = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();

        registerAllPaperEvents();
        registerAllPacketListeners();
        registerAllCommands();

        PlayerDataBoard.startUpdater();
        ActionBarController.startUpdater();
        PlayerSessionData.startManaRegenerator();

        Bukkit.getLogger().info("SpellBend enabled!");
    }

    @Override
    public void onDisable() {
        SpellHandler.endHeadLessSpellActivity();
        LivingEntitySessionData.endAllSessions(true);
        BlockManager.clearOverrides();

        Bukkit.getLogger().info("SpellBend disabled!");
    }

    private static void registerAllPaperEvents() {
        new PlayerInteractBlock();
        new PlayerInteractEntity();
        new PlayerInteractAtEntity();
        new EntityChangeBlock();
        new PlayerJoin();
        new PlayerQuit();
        new FoodLevelChange();
        new PlayerSwitchHeldItem();
        new InventoryClick();
        new InventoryDrag();
        new ProjectileHit();
        new EntityDamageByEntity();
    }

    private static void registerAllPacketListeners() {
        //new PlayClientPosition();
        //new PlayClientPositionLook();
    }

    private static void registerAllCommands() {
        new Test();
        new ReflectTest();
        new Shop();
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

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static Gson getGson() {
        return gson;
    }

    public static MiniMessage getMiniMessage(){
        return miniMsg;
    }
}
