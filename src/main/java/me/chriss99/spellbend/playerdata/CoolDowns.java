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
    public static HashMap<String, CoolDownEntry> loadCoolDowns(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load CoolDowns, skipping loading!");
            return null;
        }
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns != null) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading coolDowns, skipping loading!");
            return coolDowns;
        }

        Type type = new TypeToken<HashMap<String, CoolDownEntry>>(){}.getType();
        coolDowns = gson.fromJson(player.getPersistentDataContainer().get(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING), type);
        PlayerSessionStorage.coolDowns.put(player, coolDowns);
        return coolDowns;
    }

    /**
     * Removes all coolDowns from the list that have almost no time left
     *
     * @param player The player to clear coolDowns of
     */
    public static void removeExpiredCoolDowns(@NotNull Player player) {
        PlayerSessionStorage.coolDowns.get(player).entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Checks if the type is cooled down <br>
     * returns false if expired
     *
     * @param player The player to check the coolDowns of
     * @param spellType The spellType to check if cooled down
     * @return If the type is cooled down
     */
    public static boolean typeIsCooledDown(@NotNull Player player, @Nullable String spellType) {
        if (spellType == null)
            return false;
        CoolDownEntry coolDownEntry = getCoolDownEntry(player, spellType);
        if (coolDownEntry == null)
            return false;

        if (coolDownEntry.isExpired()) {
            removeCoolDown(player, spellType);
            return false;
        }
        return true;
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
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns == null) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            coolDowns = Objects.requireNonNull(loadCoolDowns(player));
        }
        spellType = spellType.toUpperCase();

        coolDowns.put(spellType, new CoolDownEntry(spellType, timeInSeconds, coolDownStage));
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
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns == null) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            coolDowns = Objects.requireNonNull(loadCoolDowns(player));
        }

        coolDowns.remove(spellType);
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
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns == null) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            coolDowns = Objects.requireNonNull(loadCoolDowns(player));
        }
        CoolDownEntry oldCoolDown = coolDowns.get(spellType);
        CoolDownEntry newCoolDown = new CoolDownEntry(spellType, timeInSeconds, coolDownStage);

        if (coolDowns.containsKey(spellType))
            if (oldCoolDown.getRemainingCoolDownTimeInS()<newCoolDown.getRemainingCoolDownTimeInS())
                coolDowns.put(spellType, newCoolDown);
        else coolDowns.put(spellType, newCoolDown);

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
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns == null) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            coolDowns = Objects.requireNonNull(loadCoolDowns(player));
        }

        if (coolDowns.containsKey(spellType))
            Bukkit.getLogger().warning("CoolDown " + spellType + " is already present (" + coolDowns.get(spellType) + ") " +
                    "when trying to add (" + Arrays.toString(timeInSeconds) + ", " + coolDownStage + ") to " + player.getName() + ", assigning larger coolDown!");
        extendCoolDown(player, spellType, timeInSeconds, coolDownStage);
    }

    /**
     * @param player The player to get the CoolDown from
     * @param spellType The CoolDown to get
     * @return A CoolDownEntry containing {TimeInS, startDate, CoolDownType}
     */
    public static @Nullable CoolDownEntry getCoolDownEntry(@NotNull Player player, @NotNull String spellType) {
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns == null) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            coolDowns = Objects.requireNonNull(loadCoolDowns(player));
        }

        removeExpiredCoolDowns(player);
        return coolDowns.get(spellType);
    }

    /**
     * @param player The player to get the CoolDown from
     * @return All CoolDownEntries containing {TimeInS, startDate, CoolDownType}
     */
    public static HashMap<String, CoolDownEntry> getCoolDowns(@NotNull Player player) {
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns == null) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            coolDowns = Objects.requireNonNull(loadCoolDowns(player));
        }

        removeExpiredCoolDowns(player);
        return coolDowns;
    }

    /**
     * Saves the players CoolDowns to its PersistentDataContainer and removes it from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose CoolDowns to save
     */
    public static void saveCoolDowns(@NotNull Player player) {
        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToCoolDowns map when saving, saving skipped!");
            coolDowns = Objects.requireNonNull(loadCoolDowns(player));
        }

        player.getPersistentDataContainer().set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, gson.toJson(coolDowns));
        PlayerSessionStorage.coolDowns.remove(player);
    }
}
