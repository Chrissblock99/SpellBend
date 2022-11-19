package me.chriss99.spellbend.playerdata;

import com.google.gson.Gson;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.Enums;
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
 * NOT THREAD SAVE
 */
public class Currency {
    private static final Gson gson = SpellBend.getGson();

    private static HashMap<Player, Float> currentMap = PlayerSessionStorage.gems;
    private static NamespacedKey currentKey = PersistentDataKeys.gemsKey;
    private static String currentName = "Gems";
    private static Enums.Currency currentCurrency = Enums.Currency.GEMS;

    public static void setCurrency(Enums.Currency newCurrency) {
        currentCurrency = newCurrency;
        switch (currentCurrency) {
            case GEMS -> {
                currentMap = PlayerSessionStorage.gems;
                currentKey = PersistentDataKeys.gemsKey;
                currentName = "Gems";
            }
            case GOLD -> {
                currentMap = PlayerSessionStorage.gold;
                currentKey = PersistentDataKeys.goldKey;
                currentName = "Gold";
            }
            case CRYSTALS -> {
                currentMap = PlayerSessionStorage.crystals;
                currentKey = PersistentDataKeys.crystalsKey;
                currentName = "Crystals";
            }
        }
    }

    public static Enums.Currency getCurrency() {
        return currentCurrency;
    }

    public static String getCurrentName() {
        return currentName;
    }

    /**
     * Loads the currently set currency from the players PersistentDataContainer into the PlayerSessionStorage.
     * <b>Intended to be used when the player joins.</b>
     *
     * @param player The player whose currency to load
     */
    public static @Nullable Float loadCurrency(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load " + currentName + ", skipping loading!");
            return null;
        }
        Float currency = currentMap.get(player);
        if (currency != null) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading " + currentName + ", skipping loading!");
            return currency;
        }

        currency = gson.fromJson(player.getPersistentDataContainer().get(currentKey, PersistentDataType.STRING), Float.class);
        currentMap.put(player, currency);
        return currency;
    }

    public static void addCurrency(@NotNull Player player, int currency) {
        setCurrency(player, getCurrency(player) + currency);
    }

    public static void setCurrency(@NotNull Player player, float currency) {
        currentMap.put(player, currency);
        PlayerDataBoard.updateBoard(player);
    }

    public static float getCurrency(@NotNull Player player) {
        Float currency = currentMap.get(player);
        if (currency == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map, now fixing!");
            currency = Objects.requireNonNull(loadCurrency(player));
        }

        return currency;
    }

    /**
     * Saves the currently set currency to the players PersistentDataContainer and removes it from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose currency to save
     */
    public static void saveCurrency(@NotNull Player player) {
        Float currency = currentMap.get(player);
        if (currency == null) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerTo" + currentName + " map when saving, saving skipped!");
            return;
        }

        player.getPersistentDataContainer().set(currentKey, PersistentDataType.STRING, gson.toJson(currency));
        currentMap.remove(player);
    }
}
