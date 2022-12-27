package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.Maps;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class PercentageModifier {
    private static final Gson gson = SpellBend.getGson();

    private final Player player;
    private final float[] modifiers;
    private final NamespacedKey key;

    public PercentageModifier(@NotNull Player player, @NotNull NamespacedKey key, @NotNull String name) {
        this.player = player;
        this.key = key;

        String gsonString = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(player.getName() + "'s " + name + " were not setup when loading, fixing now!");
            modifiers = new float[]{1, 1, 1};
            player.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(modifiers));
            return;
        }

        Type type = new TypeToken<float[]>(){}.getType();
        modifiers = gson.fromJson(gsonString, type);
    }

    /**
     * Gets the specified modifier of the player
     * returns all of them multiplied together if given null
     *
     * @param modType The modifiers name, null returns all of them
     * @return The modifier
     */
    public float getModifier(@Nullable Enums.DmgModType modType) {
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
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     */
    public void addModifier(@NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

        modifiers[Maps.modifierToIndexMap.get(modType)] *= modifier;
    }

    /**
     * Removes the modifier from the specified type of the player
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     */
    public void removeModifier(@NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

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
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     * @return An undo factor usable to undo the change later
     */
    public float extendModifier(@NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

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
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     * @return An undo factor usable to undo the change later
     */
    public float setModifier(@NotNull Enums.DmgModType modType, float modifier) {
        if (modifier <= 0)
            throw new IllegalArgumentException("Modifier cannot be smaller or equal to 0!");

        int index = Maps.modifierToIndexMap.get(modType);
        float oldModifiers = modifiers[index];
        modifiers[index] = modifier;
        return modifiers[index]/oldModifiers;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Saves the players modifiers to their PersistentDataContainer
     */
    public void saveModifiers() {
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(modifiers));
    }
}
