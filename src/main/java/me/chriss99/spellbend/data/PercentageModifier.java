package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class PercentageModifier {
    private static final Gson gson = SpellBend.getGson();

    private final LivingEntity livingEntity;
    private double modifier;
    private int isZero;
    private final NamespacedKey key;

    public PercentageModifier(@NotNull LivingEntity livingEntity, @NotNull NamespacedKey key, @NotNull String name) {
        this.livingEntity = livingEntity;
        this.key = key;

        String gsonString = livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(livingEntity.getName() + "'s " + name + " were not setup when loading, fixing now!");
            modifier = 1;
            isZero = 0;
            livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(getDefaultData()));
            return;
        }

        Type type = new TypeToken<Data>(){}.getType();
        Data data = gson.fromJson(gsonString, type);
        modifier = data.modifier;
        if (modifier == 0)
            Bukkit.getLogger().warning("PercentageModifier for livingEntity " + livingEntity.getName() + " with key " + key.getKey() +
                    " and name \"" + name + "\" loaded with a modifier of 0! This presents a problem as this value can never change!");
        isZero = data.isZero;
    }

    /**
     * Gets the modifier
     *
     * @return The modifier
     */
    public double getModifier() {
        if (isZero > 0)
            return 0;
        else if (isZero < 0)
            return modifier / 0;

        return modifier;
    }

    /**
     * Adds the Modifier to the livingEntity
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    public void addModifier(double modifier) {
        if (modifier < 0)
            throw new IllegalArgumentException("Modifier cannot be negative!");
        if (modifier == 1)
            return;

        if (this.modifier * modifier == 0) {
            isZero++;
            return;
        }

        this.modifier *= modifier;
    }

    /**
     * Removes the modifier from the livingEntity
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    public void removeModifier(double modifier) {
        if (modifier < 0)
            throw new IllegalArgumentException("Modifier cannot be negative!");
        if (modifier == 1)
            return;

        if (modifier == 0) {
            isZero--;
            return;
        }

        if (this.modifier / modifier == 0) {
            isZero++;
            return;
        }

        this.modifier /= modifier;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    /**
     * Saves the livingEntities modifiers to their PersistentDataContainer
     */
    public void saveModifiers() {
        livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING,
                gson.toJson(new Data(modifier, isZero)));
    }

    public static Data getDefaultData() {
        return new Data(1, 0);
    }

    @SuppressWarnings("ClassCanBeRecord") //GSON will break otherwise
    private static class Data {
        public final double modifier;
        public final int isZero;

        public Data(double modifier, int isZero) {
            this.modifier = modifier;
            this.isZero = isZero;
        }
    }
}
