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
    private final boolean updateBoard;
    private final boolean updateActionBar;

    public Currency(@NotNull Player player, @NotNull NamespacedKey key, @NotNull String name, float defaultValue, boolean updateBoard, boolean updateActionBar) {
        this.player = player;
        this.key = key;
        this.updateBoard = updateBoard;
        this.updateActionBar = updateActionBar;

        currency = player.getPersistentDataContainer().get(key, PersistentDataType.FLOAT);
        if (currency == null) {
            Bukkit.getLogger().warning(player.getName() + " did not have " + name + " set up, fixing!");
            currency = defaultValue;
        }
    }

    public Currency(@NotNull Player player, float defaultValue, boolean updateBoard, boolean updateActionBar) {
        this.player = player;
        this.key = null;
        this.updateBoard = updateBoard;
        this.updateActionBar = updateActionBar;

        currency = defaultValue;
    }

    public void addCurrency(float currency) {
        this.currency += currency;
        updateDisplay();
    }

    public void setCurrency(float currency) {
        this.currency = currency;
        updateDisplay();
    }

    private void updateDisplay() {
        if (updateBoard)
            PlayerSessionData.getPlayerSession(player).getPlayerDataBoard().updateBoard();
        if (updateActionBar)
            PlayerSessionData.getPlayerSession(player).getActionBarController().updateBar();
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
        if (key == null) {
            Bukkit.getLogger().warning("An attempt to save a temporary Currency was made, skipping!");
            return;
        }

        player.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, currency);
    }
}
