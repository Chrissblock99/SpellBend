package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.math.Percentage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PercentageModifier {
    private static final Gson gson = SpellBend.getGson();

    private final LivingEntity livingEntity;
    private final NamespacedKey key;
    private final Percentage percentage;

    public PercentageModifier(@NotNull LivingEntity livingEntity, @NotNull NamespacedKey key, @NotNull String name) {
        this.livingEntity = livingEntity;
        this.key = key;

        String gsonString = livingEntity.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(livingEntity.getName() + "'s " + name + " were not setup when loading, fixing now!");
            percentage = new Percentage(1, 0);
            livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(percentage));
            return;
        }

        percentage = gson.fromJson(gsonString, Percentage.class);
        if (percentage.getRealPart() == 0)
            Bukkit.getLogger().warning("PercentageModifier for livingEntity " + livingEntity.getName() + " with key " + key.getKey() +
                    " and name \"" + name + "\" loaded with a real part of 0! This presents a problem as this value can never change!");
    }

    /**
     * Gets the modifier
     *
     * @return The modifier
     */
    public double getModifier() {
        return percentage.getPercentage();
    }

    /**
     * Adds the Modifier to the livingEntity
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @param modifier The modifier
     */
    public void addModifier(double modifier) {
        percentage.multiply(modifier);
    }

    /**
     * Removes the modifier from the livingEntity
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @param modifier The modifier
     */
    public void removeModifier(double modifier) {
        percentage.divide(modifier);
    }

    /**
     * Gets the internal percentage instance
     *
     * @return The internal percentage instance
     */
    public @NotNull Percentage getPercentage() {
        return percentage;
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    /**
     * Saves the livingEntities modifiers to their PersistentDataContainer
     */
    public void saveModifiers() {
        livingEntity.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(percentage));
    }
}
