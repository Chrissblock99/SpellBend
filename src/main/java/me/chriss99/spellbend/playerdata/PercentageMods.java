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

/**
 * This class has three enum states: DMGTAKEN, DMGDEALT and WALKSPEED <br>
 * depending on those either the damage taken or dealt or the walkSpeed will be modified <br>
 * The state can be set with setModifier() and the appropriate enum: Enums.Modifier <br>
 * <b>THIS IS NOT THREAD SAVE</b>
 */
public class PercentageMods {
    private static final Gson gson = SpellBend.getGson();
    private static HashMap<Player, float[]> currentMap = PlayerSessionStorage.dmgDealtMods;
    private static NamespacedKey currentKey = PersistentDataKeys.damageDealtModifiersKey;
    private static String currentName = "DmgDealtMods";
    private static Enums.Modifier modifier = Enums.Modifier.DMGDEALT;

    public static void setModifier(Enums.Modifier newDmgMod) {
        modifier = newDmgMod;
        switch (modifier) {
            case DMGDEALT -> {
                currentMap = PlayerSessionStorage.dmgDealtMods;
                currentKey = PersistentDataKeys.damageDealtModifiersKey;
                currentName = "DmgDealtMods";
            }
            case DMGTAKEN -> {
                currentMap = PlayerSessionStorage.dmgTakenMods;
                currentKey = PersistentDataKeys.damageTakenModifiersKey;
                currentName = "DmgTakenMods";
            }
            case WALKSPEED -> {
                currentMap = new HashMap<>();
                currentKey = new NamespacedKey(SpellBend.getInstance(), "walkSpeed");
                currentName = "WalkSpeedMods";
            }
        }
    }

    public static Enums.Modifier getModifier() {
        return modifier;
    }

    public static String getCurrentName() {
        return currentName;
    }

    /**
     * Loads the players Modifiers from its PersistentDataContainer into the PlayerSessionStorage.
     * <b>Intended to be used when the player joins.</b>
     *
     * @param player The player whose Modifiers to load
     * @return The values loaded, null if loading failed
     */
    public static float[] loadDmgMods(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load " + currentName + ", skipping loading!");
            return null;
        }
        float[] modifiers = currentMap.get(player);
        if (modifiers != null) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading " + currentName + ", skipping loading!");
            return modifiers;
        }

        modifiers = gson.fromJson(player.getPersistentDataContainer().get(currentKey, PersistentDataType.STRING), float[].class);
        currentMap.put(player, modifiers);
        return modifiers;
    }

    /**
     * Gets the specified modifier of the player
     * returns all of them multiplied together if given null
     *
     * @param player The player to get modifiers from
     * @param modType The modifiers name, null returns all of them
     * @return The modifier
     */
    public static float getModifier(@NotNull Player player, @Nullable Enums.DmgModType modType) {
        float[] modifiers = currentMap.get(player);
        if (modifiers == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            modifiers = Objects.requireNonNull(loadDmgMods(player));
        }

        if (modType == null) {
            float result = 1;
            for (float num : modifiers)
                result *= num;
            return result;
        }
        return modifiers[Maps.modifierToIndexMap.get(modType)];
    }

    /**
     * Adds the Modifier to the specified type of the player
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param player The player to add the modifier to
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     */
    public static void addModifier(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

        float[] modifiers = currentMap.get(player);
        if (modifiers == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            modifiers = Objects.requireNonNull(loadDmgMods(player));
        }

        modifiers[Maps.modifierToIndexMap.get(modType)] *= modifier;
    }

    /**
     * Removes the modifier from the specified type of the player
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param player The player to remove the modifier from
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     */
    public static void removeModifier(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

        float[] modifiers = currentMap.get(player);
        if (modifiers == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            modifiers = Objects.requireNonNull(loadDmgMods(player));
        }

        modifiers[Maps.modifierToIndexMap.get(modType)] /= modifier;
    }

    /**
     * Sets the modifier from the specified type of the player if it is larger <br>
     * <b>Because this can mathematically not be undone an undo factor will be returned <br>
     * which should be used to undo this modifier with removeModifier() later in the process</b> <br>
     * If the extending action didn't change anything, 1 will be returned
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param player The player to set the modifier of
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     * @return An undo factor usable to undo the change later
     */
    public static float extendModifier(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

        float[] modifiers = currentMap.get(player);
        if (modifiers == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            modifiers = Objects.requireNonNull(loadDmgMods(player));
        }

        int index = Maps.modifierToIndexMap.get(modType);
        if (modifiers[index]<modifier) {
            float oldModifiers = modifiers[index];
            modifiers[index] = modifier;
            return modifiers[index]/oldModifiers;
        }
        return 1;
    }

    /**
     * Sets the Modifier from the specified type of the player <br>
     * <b>Because this can mathematically not be undone an undo factor will be returned <br>
     * which should be used to undo this modifier with removeModifier() later in the process</b>
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param player The player to set the modifier of
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     * @return An undo factor usable to undo the change later
     */
    public static float setDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

        float[] modifiers = currentMap.get(player);
        if (modifiers == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            modifiers = Objects.requireNonNull(loadDmgMods(player));
        }

        int index = Maps.modifierToIndexMap.get(modType);
        float oldModifiers = modifiers[index];
        modifiers[index] = modifier;
        return modifiers[index]/oldModifiers;
    }

    /**
     * Saves the players modifiers to its PersistentDataContainer and removes it from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose modifiers to save
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
