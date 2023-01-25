package me.chriss99.spellbend.data;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ValueTracker {
    protected final Player player;
    private final NamespacedKey key;
    private Integer value;

    public ValueTracker(@NotNull Player player, @NotNull NamespacedKey key, @NotNull String name, int defaultValue) {
        this.player = player;
        this.key = key;

        value = player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if (value == null) {
            Bukkit.getLogger().warning(player.getName() + " did not have " + name + " set up when loading, fixing now!");
            player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, defaultValue);
            value = defaultValue;
        }
    }

    public void displaceValue(int value) {
        this.value += value;
    }

    public int getValue() {
        return value;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public boolean valueIsLargerZero() {
        return value>0;
    }

    public void saveValue() {
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
    }
}
