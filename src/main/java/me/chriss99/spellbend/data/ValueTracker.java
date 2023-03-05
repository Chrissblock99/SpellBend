package me.chriss99.spellbend.data;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ValueTracker {
    protected final LivingEntity livingEntity;
    private final NamespacedKey key;
    private Integer value;

    public ValueTracker(@NotNull LivingEntity livingEntity, @NotNull NamespacedKey key, @NotNull String name, int defaultValue) {
        this.livingEntity = livingEntity;
        this.key = key;

        value = livingEntity.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if (value == null) {
            Bukkit.getLogger().warning(livingEntity.getName() + " did not have " + name + " set up when loading, fixing now!");
            livingEntity.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, defaultValue);
            value = defaultValue;
        }
    }

    public void displaceValue(int value) {
        this.value += value;
    }

    public int getValue() {
        return value;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public boolean valueIsLargerZero() {
        return value>0;
    }

    public void saveValue() {
        livingEntity.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
    }
}
