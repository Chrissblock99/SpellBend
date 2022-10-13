package me.chriss99.spellbend.playerdata;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PlayerDataUtil {
    /**Sets up all the PersistentData of the player
     *
     * @param player The player who's PersistentData to set up
     */
    public static void setupPlayerData(@NotNull Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        //data.set(PersistentDataKeys.gemsKey, PersistentDataType.INTEGER, 150);
        //data.set(PersistentDataKeys.goldKey, PersistentDataType.INTEGER, 650);
        //data.set(PersistentDataKeys.spellsOwnedKey, PersistentDataType.INTEGER_ARRAY, new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
        data.set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, "");
        data.set(PersistentDataKeys.dmgModsKey, PersistentDataType.STRING, "1, 1, 1");
        //data.set(PersistentDataKeys.crystalsKey, PersistentDataType.INTEGER, 0);
        //data.set(PersistentDataKeys.crystalShardsKey, PersistentDataType.INTEGER, 0);
    }

    /**Loads all the persistentData of the player
     *
     * @param player The player to load the PersistentData of
     */
    public static void loadAll(@NotNull Player player) {
        /*try {
            Gems.loadGems(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s Gems generated an exception!");
            e.printStackTrace();
        }
        try {
            Gold.loadGold(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s Gold generated an exception!");
            e.printStackTrace();
        }
        try {
            SpellsOwned.loadSpellsOwned(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s SpellsOwned generated an exception!");
            e.printStackTrace();
        }
        try {
            Health.registerPlayer(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s Health generated an exception!");
            e.printStackTrace();
        }*/
        try {
            CoolDowns.loadCoolDowns(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s Cooldowns generated an exception!");
            e.printStackTrace();
        }
        try {
            DmgMods.loadDmgMods(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s DmgMods generated an exception!");
            e.printStackTrace();
        }
        /*try {
            Crystals.loadCrystals(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s Crystals generated an exception!");
            e.printStackTrace();
        }
        try {
            CrystalShards.loadCrystalShards(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The loading of " + player.getDisplayName() + "'s CrystalShards generated an exception!");
            e.printStackTrace();
        }*/
    }

    /**Saves all PersistentData of the player
     *
     * @param player The player to save the PersistentData of
     */
    public static void saveAll(@NotNull Player player) {
        /*try{
            Gems.saveGems(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s Gems generated an exception!");
            e.printStackTrace();
        }
        try {
            Gold.saveGold(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s Gold generated an exception!");
            e.printStackTrace();
        }
        try {
            SpellsOwned.saveSpellsOwned(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s SpellsOwned generated an exception!");
            e.printStackTrace();
        }
        try {
            Health.deRegisterPlayer(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s Health generated an exception!");
            e.printStackTrace();
        }*/
        try {
            CoolDowns.saveCoolDowns(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s CoolDowns generated an exception!");
            e.printStackTrace();
        }
        try {
            DmgMods.saveDmgMods(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s DmgMods generated an exception!");
            e.printStackTrace();
        }
        /*try {
            Crystals.saveCrystals(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s Crystals generated an exception!");
            e.printStackTrace();
        }
        try {
            CrystalShards.saveCrystalShards(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("The saving of " + player.getDisplayName() + "'s CrystalShards generated an exception!");
            e.printStackTrace();
        }*/
    }
}
