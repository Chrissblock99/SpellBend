package me.chriss99.spellbend;

import com.google.gson.Gson;
import me.chriss99.spellbend.data.PlayerDataBoard;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpellBend extends JavaPlugin {
    private static SpellBend instance;
    private static final Gson gson = new Gson();

    @Override
    public void onEnable() {
        instance = this;

        GeneralRegisterUtil.registerAllEvents();
        GeneralRegisterUtil.registerAllSpells();
        GeneralRegisterUtil.registerAllCommands();
        PlayerDataBoard.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SpellBend getInstance() {
        return instance;
    }

    public static Gson getGson() {
        return gson;
    }
}
