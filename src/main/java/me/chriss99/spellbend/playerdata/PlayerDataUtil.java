package me.chriss99.spellbend.playerdata;

import com.google.gson.Gson;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PlayerDataUtil {
    private static final Gson gson = SpellBend.getGson();

    /**
     * Sets up all the PersistentData of the player
     *
     * @param player The player who's PersistentData to set up
     */
    public static void setupPlayerData(@NotNull Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(PersistentDataKeys.gemsKey, PersistentDataType.FLOAT, 150f);
        data.set(PersistentDataKeys.goldKey, PersistentDataType.FLOAT, 650f);
        data.set(PersistentDataKeys.crystalsKey, PersistentDataType.FLOAT, 0f);
        //data.set(PersistentDataKeys.crystalShardsKey, PersistentDataType.INTEGER, 0);
        //data.set(PersistentDataKeys.spellsOwnedKey, PersistentDataType.INTEGER_ARRAY, new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
        data.set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, gson.toJson(new HashMap<String, CoolDownEntry>()));
        data.set(PersistentDataKeys.dmgDealtModsKey, PersistentDataType.STRING, gson.toJson(new float[]{1, 1, 1}));
        data.set(PersistentDataKeys.dmgTakenModsKey, PersistentDataType.STRING, gson.toJson(new float[]{1, 1, 1}));
    }

    /**
     * Loads all the persistentData of the player
     *
     * @param player The player to load the PersistentData of
     */
    public static void loadAll(@NotNull Player player) {
        try {
            Currency.setCurrency(Enums.Currency.GEMS);
            Currency.loadCurrency(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s Gems generated an exception!");
            e.printStackTrace();
        }
        try {
            Currency.setCurrency(Enums.Currency.GOLD);
            Currency.loadCurrency(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s Gold generated an exception!");
            e.printStackTrace();
        }
        try {
            Currency.setCurrency(Enums.Currency.CRYSTALS);
            Currency.loadCurrency(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s Crystals generated an exception!");
            e.printStackTrace();
        }
        /*try {
            SpellsOwned.loadSpellsOwned(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s SpellsOwned generated an exception!");
            e.printStackTrace();
        }*/
        try {
            Health.registerPlayer(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s Health generated an exception!");
            e.printStackTrace();
        }
        try {
            CoolDowns.loadCoolDowns(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s CoolDowns generated an exception!");
            e.printStackTrace();
        }
        try {
            DmgMods.setDmgMod(Enums.DmgMod.DEALT);
            DmgMods.loadDmgMods(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s DmgDealtMods generated an exception!");
            e.printStackTrace();
        }
        try {
            DmgMods.setDmgMod(Enums.DmgMod.TAKEN);
            DmgMods.loadDmgMods(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s DmgTakenMods generated an exception!");
            e.printStackTrace();
        }
        /*try {
            CrystalShards.loadCrystalShards(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getName() + "'s CrystalShards generated an exception!");
            e.printStackTrace();
        }*/
    }

    /**
     * Saves all PersistentData of the player
     *
     * @param player The player to save the PersistentData of
     */
    public static void saveAll(@NotNull Player player) {
        try{
            Currency.setCurrency(Enums.Currency.GEMS);
            Currency.saveCurrency(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s Gems generated an exception!");
            e.printStackTrace();
        }
        try{
            Currency.setCurrency(Enums.Currency.GOLD);
            Currency.saveCurrency(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s Gold generated an exception!");
            e.printStackTrace();
        }
        try{
            Currency.setCurrency(Enums.Currency.CRYSTALS);
            Currency.saveCurrency(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s Crystals generated an exception!");
            e.printStackTrace();
        }
        /*try {
            SpellsOwned.saveSpellsOwned(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s SpellsOwned generated an exception!");
            e.printStackTrace();
        }*/
        try {
            Health.deRegisterPlayer(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s Health generated an exception!");
            e.printStackTrace();
        }
        try {
            CoolDowns.saveCoolDowns(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s CoolDowns generated an exception!");
            e.printStackTrace();
        }
        try {
            DmgMods.setDmgMod(Enums.DmgMod.DEALT);
            DmgMods.saveDmgMods(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s DmgDealtMods generated an exception!");
            e.printStackTrace();
        }
        try {
            DmgMods.setDmgMod(Enums.DmgMod.TAKEN);
            DmgMods.saveDmgMods(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s DmgTakenMods generated an exception!");
            e.printStackTrace();
        }

        /*try {
            CrystalShards.saveCrystalShards(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getName() + "'s CrystalShards generated an exception!");
            e.printStackTrace();
        }*/
    }
}
