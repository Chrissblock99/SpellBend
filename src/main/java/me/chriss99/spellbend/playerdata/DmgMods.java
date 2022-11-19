package me.chriss99.spellbend.playerdata;

import com.google.gson.Gson;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.Maps;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class DmgMods {
    private static final Gson gson = SpellBend.getGson();
    private static HashMap<Player, float[]> currentMap = PlayerSessionStorage.dmgDealtMods;
    private static NamespacedKey currentKey = PersistentDataKeys.dmgDealtModsKey;
    private static String currentName = "DmgDealtMods";
    private static Enums.DmgMod dmgMod = Enums.DmgMod.DEALT;

    public static void setDmgMod(Enums.DmgMod newDmgMod) {
        dmgMod = newDmgMod;
        switch (dmgMod) {
            case DEALT -> {
                currentMap = PlayerSessionStorage.dmgDealtMods;
                currentKey = PersistentDataKeys.dmgDealtModsKey;
                currentName = "DmgDealtMods";
            }
            case TAKEN -> {
                currentMap = PlayerSessionStorage.dmgTakenMods;
                currentKey = PersistentDataKeys.dmgTakenModsKey;
                currentName = "DmgTakenMods";
            }
        }
    }

    public static Enums.DmgMod getDmgMod() {
        return dmgMod;
    }

    public static String getCurrentName() {
        return currentName;
    }

    /**
     * Loads the players DmgMods from its PersistentDataContainer into the PlayerSessionStorage.
     * <b>Intended to be used when the player joins.</b>
     *
     * @param player The player whose DmgMods to load
     * @return The values loaded, null if loading failed
     */
    public static float[] loadDmgMods(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load " + currentName + ", skipping loading!");
            return null;
        }
        float[] dmgMods = currentMap.get(player);
        if (dmgMods != null) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading " + currentName + ", skipping loading!");
            return dmgMods;
        }

        dmgMods = gson.fromJson(player.getPersistentDataContainer().get(currentKey, PersistentDataType.STRING), float[].class);
        currentMap.put(player, dmgMods);
        return dmgMods;
    }

    /**
     * Gets the specified Damage modifier of the player
     * returns all of them multiplied together if given null
     *
     * @param player The player to get DamageMods from
     * @param modType The damageMod name, null returns all of them
     * @return The dmgMod
     */
    public static float getDmgMod(@NotNull Player player, @Nullable Enums.DmgModType modType) {
        float[] dmgMods = currentMap.get(player);
        if (dmgMods == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            dmgMods = Objects.requireNonNull(loadDmgMods(player));
        }

        if (modType == null) {
            float result = 1;
            for (float num : dmgMods)
                result *= num;
            return result;
        }
        return dmgMods[Maps.dmgModToIndexMap.get(modType)];
    }

    /**
     * Adds the DmgMod to the specified type of the player
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeDmgMod()
     *
     * @param player The player to add the DmgMod to
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void addDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        float[] dmgMods = currentMap.get(player);
        if (dmgMods == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            dmgMods = Objects.requireNonNull(loadDmgMods(player));
        }

        dmgMods[Maps.dmgModToIndexMap.get(modType)] *= modifier;
    }

    /**
     * Removes the DmgMod from the specified type of the player
     * It does this by dividing the number with the modifier, undoing addDmgMod() in the process
     *
     * @param player The player to remove the DmgMod from
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void removeDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        float[] dmgMods = currentMap.get(player);
        if (dmgMods == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            dmgMods = Objects.requireNonNull(loadDmgMods(player));
        }

        dmgMods[Maps.dmgModToIndexMap.get(modType)] /= modifier;
    }

    /**
     * Sets the DmgMod from the specified type of the player if it is larger
     * <b>this can mathematically not be undone and will break things if added modifiers are still present
     * therefore it should only be used rarely</b>
     *
     * @param player The player to set the DmgMod of
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void extendDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        float[] dmgMods = currentMap.get(player);
        if (dmgMods == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            dmgMods = Objects.requireNonNull(loadDmgMods(player));
        }

        int index = Maps.dmgModToIndexMap.get(modType);
        if (dmgMods[index]<modifier)
            dmgMods[index] = modifier;
    }

    /**
     * Sets the DmgMod from the specified type of the player
     * <b>this can mathematically not be undone and will break things if added modifiers are still present
     * therefore it should only be used rarely</b>
     *
     * @param player The player to set the DmgMod of
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void setDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        float[] dmgMods = currentMap.get(player);
        if (dmgMods == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            dmgMods = Objects.requireNonNull(loadDmgMods(player));
        }

        dmgMods[Maps.dmgModToIndexMap.get(modType)] = modifier;
    }

    /**
     * Saves the players DmgMods to its PersistentDataContainer and removes it from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose DmgMods to save
     */
    public static void saveDmgMods(@NotNull Player player) {
        float[] dmgMods = currentMap.get(player);
        if (dmgMods == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map when saving, saving skipped!");
            return;
        }

        player.getPersistentDataContainer().set(currentKey, PersistentDataType.STRING, gson.toJson(dmgMods));
        currentMap.remove(player);
    }
}
