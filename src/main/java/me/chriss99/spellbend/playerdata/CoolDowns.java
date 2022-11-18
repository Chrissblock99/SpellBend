package me.chriss99.spellbend.playerdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class CoolDowns {
    private static final Gson gson = SpellBend.getGson();

    /**
     * Loads the players CoolDowns from its PersistentDataContainer into the PlayerSessionStorage.
     * <b>Intended to be used when the player joins.</b>
     *
     * @param player The player whose CoolDowns to load
     */
    public static void loadCoolDowns(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load CoolDowns, skipping loading!");
            return;
        }
        if (PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading coolDowns, skipping loading!");
            return;
        }

        Type type = new TypeToken<HashMap<String, CoolDownEntry>>(){}.getType();
        PlayerSessionStorage.coolDowns.put(player, gson.fromJson(player.getPersistentDataContainer().get(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING), type));
    }

    /**
     * Removes all coolDowns from the list that have almost no time left
     *
     * @param player The player to clear coolDowns of
     */
    public static void removeExpiredCoolDowns(@NotNull Player player) {
        PlayerSessionStorage.coolDowns.get(player).entrySet().removeIf(entry -> entry.getValue().getRemainingCoolDownTimeInS() <= 0.01f);
    }

    /**
     * Sets the cooldown of the player regardless of present cooldown using WINDUP as the starting stage
     *
     * @param player The player to add the cooldown to
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     */
    public static void setCoolDown(@NotNull Player player, @NotNull String spellType, float[] timeInSeconds) {
        setCoolDown(player, spellType, timeInSeconds, Enums.CoolDownStage.WINDUP);
    }

    /**
     * Sets the cooldown of the player regardless of present cooldown
     *
     * @param player The player to add the cooldown to
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownStage
     */
    public static void setCoolDown(@NotNull Player player, @NotNull String spellType, float[] timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }
        spellType = spellType.toUpperCase();

        PlayerSessionStorage.coolDowns.get(player).put(spellType, new CoolDownEntry(spellType, timeInSeconds, coolDownStage));
        if (spellType.equals(ItemData.getHeldSpellType(player)))
            PlayerDataBoard.registerPlayer(player, spellType);
    }

    /**
     * Removes the cooldown from the player
     *
     * @param player The player to remove a cooldown from
     * @param spellType The spellType to remove
     */
    public static void removeCoolDown(@NotNull Player player, @NotNull String spellType) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        PlayerSessionStorage.coolDowns.get(player).remove(spellType);
    }

    /**
     * Adds a cooldown to the player
     * if a cooldown is already present the larger one is assigned
     *
     * @param player The player to add the cooldown to
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownStage to start in
     */
    public static void extendCoolDown(@NotNull Player player, @NotNull String spellType, float[] timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        CoolDownEntry oldCoolDown = coolDowns.get(spellType);
        CoolDownEntry newCoolDown = new CoolDownEntry(spellType, timeInSeconds, coolDownStage);

        if (coolDowns.containsKey(spellType))
            if (oldCoolDown.getRemainingCoolDownTimeInS()<newCoolDown.getRemainingCoolDownTimeInS())
                coolDowns.put(spellType, newCoolDown);
        else PlayerSessionStorage.coolDowns.get(player).put(spellType, newCoolDown);

        if (spellType.equals(ItemData.getHeldSpellType(player)))
            PlayerDataBoard.registerPlayer(player, spellType);
    }

    /**
     * Adds a cooldown to the player but warns console if one is already present
     * if already present the larger cooldown is assigned
     *
     * @param player The player to add the cooldown to
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownType
     */
    public static void addCoolDown(@NotNull Player player, @NotNull String spellType, float[] timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        if (PlayerSessionStorage.coolDowns.get(player).containsKey(spellType))
            Bukkit.getLogger().warning("CoolDown " + spellType + " is already present (" + PlayerSessionStorage.coolDowns.get(player).get(spellType) + ") " +
                    "when trying to add (" + Arrays.toString(timeInSeconds) + ", " + coolDownStage + ") to " + player.getName() + ", assigning larger coolDown!");
        extendCoolDown(player, spellType, timeInSeconds, coolDownStage);
    }

    /**
     * @param player The player to get the CoolDown from
     * @param spellType The CoolDown to get
     * @return A CoolDownEntry containing {TimeInS, startDate, CoolDownType}
     */
    public static @Nullable CoolDownEntry getCoolDownEntry(@NotNull Player player, @NotNull String spellType) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        removeExpiredCoolDowns(player);
        return PlayerSessionStorage.coolDowns.get(player).get(spellType);
    }

    /**
     * @param player The player to get the CoolDown from
     * @return All CoolDownEntries containing {TimeInS, startDate, CoolDownType}
     */
    public static HashMap<String, CoolDownEntry> getCoolDowns(@NotNull Player player) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        removeExpiredCoolDowns(player);
        return PlayerSessionStorage.coolDowns.get(player);
    }

    /**
     * Saves the players CoolDowns to its PersistentDataContainer and removes it from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose CoolDowns to save
     */
    public static void saveCoolDowns(@NotNull Player player) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToCoolDowns map when saving, saving skipped!");
            return;
        }

        player.getPersistentDataContainer().set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, gson.toJson(PlayerSessionStorage.coolDowns.get(player)));
        PlayerSessionStorage.coolDowns.remove(player);
    }
}
