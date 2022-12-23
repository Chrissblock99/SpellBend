package me.chriss99.spellbend.data;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class Currency {
    private final Player player;
    private Float currency;
    private final NamespacedKey key;

    public Currency(@NotNull Player player, @NotNull NamespacedKey key, @NotNull String name, float defaultValue) {
        this.player = player;
        this.key = key;

        currency = player.getPersistentDataContainer().get(key, PersistentDataType.FLOAT);
        if (currency == null) {
            Bukkit.getLogger().warning(player.getName() + " did not have " + name + " set up, fixing!");
            currency = defaultValue;
        }
    }

    public void addCurrency(float currency) {
        this.currency += currency;
        PlayerDataBoard.updateBoard(player);
    }

    public void setCurrency(float currency) {
        this.currency = currency;
        PlayerDataBoard.updateBoard(player);
    }

    public float getCurrency() {
        return currency;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Saves the currency to the players PersistentDataContainer
     */
    public void saveCurrency() {
        player.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, currency);
    }
}
