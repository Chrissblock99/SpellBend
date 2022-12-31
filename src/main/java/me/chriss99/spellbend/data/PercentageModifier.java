package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.Maps;
import me.chriss99.spellbend.util.math.MathUtil;
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
    private final int[] activeModifiers;
    private final int[] isZero;
    private final NamespacedKey key;

    public PercentageModifier(@NotNull Player player, @NotNull NamespacedKey key, @NotNull String name) {
        this.player = player;
        this.key = key;

        String gsonString = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(player.getName() + "'s " + name + " were not setup when loading, fixing now!");
            modifiers = new float[]{1, 1, 1};
            activeModifiers = new int[]{0, 0, 0};
            isZero = new int[]{0, 0, 0};
            player.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(modifiers));
            return;
        }

        Type type = new TypeToken<Data>(){}.getType();
        Data data = gson.fromJson(gsonString, type);
        modifiers = data.modifiers;
        activeModifiers = data.activeModifiers;
        isZero = data.isZero;
    }

    /**
     * Gets the specified modifier <br>
     * returns all of them multiplied together if given null <br>
     *
     * @param modType The modifiers name, null returns all of them
     * @return The modifier
     */
    public float getModifier(@Nullable Enums.DmgModType modType) {
        Integer index = null;
        if (modType != null)
            index = Maps.modifierToIndexMap.get(modType);

        if (index != null) {
            if (isZero[index] == 0)
                return 0;

            return modifiers[index];
        }

        if (MathUtil.additiveArrayValue(isZero) != 0)
            return 0;

        float result = 1;
        for (float num : modifiers)
            result *= num;
        return result;
    }

    /**
     * Adds the Modifier to the specified type of the player
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modType The type of the modifier
     * @param modifier The modifier not negative
     */
    public void addModifier(@NotNull Enums.DmgModType modType, float modifier) {
        if (modifier < 0)
            throw new IllegalArgumentException("Modifier cannot be negative!");
        int index = Maps.modifierToIndexMap.get(modType);

        if (modifier != 1)
            activeModifiers[index]++;

        if (modifier == 0) {
            isZero[index]++;
            return;
        }

        modifiers[index] *= modifier;
    }

    /**
     * Removes the modifier from the specified type of the player
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modType The type of the modifier
     * @param modifier The modifier not negative
     */
    public void removeModifier(@NotNull Enums.DmgModType modType, float modifier) {
        if (modifier < 0)
            throw new IllegalArgumentException("Modifier cannot be negative!");
        int index = Maps.modifierToIndexMap.get(modType);

        if (modifier != 1) {
            activeModifiers[index]--;

            if (activeModifiers[index] == 0)
                modifiers[index] = 1;
        }

        if (modifier == 0) {
            isZero[index]--;
            return;
        }

        modifiers[index] /= modifier;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Saves the players modifiers to their PersistentDataContainer
     */
    public void saveModifiers() {
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING,
                gson.toJson(new Data(modifiers, activeModifiers, isZero)));
    }

    public static Data getDefaultData() {
        return new Data(new float[]{1, 1, 1}, new int[]{0, 0, 0}, new int[]{0, 0, 0});
    }

    private static class Data {
        public float[] modifiers;
        public int[] activeModifiers;
        public int[] isZero;

        public Data(float[] modifiers, int[] activeModifiers, int[] isZero) {
            this.modifiers = modifiers;
            this.activeModifiers = activeModifiers;
            this.isZero = isZero;
        }
    }
}
