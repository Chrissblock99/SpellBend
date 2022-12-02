package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemData {
    /**
     * Gets the item the player is currently holding and returns it
     *
     * @param player The player to get the held item from
     * @return The held item
     */
    public static @NotNull ItemStack getHeldItem(@NotNull Player player) {
        return player.getInventory().getItemInMainHand();
    }

    /**
     * Gets the spellType of the item the player is currently holding.
     *
     * @param player The player whose hold item to get the spellType from
     * @return The spellType
     */
    public static @Nullable String getHeldSpellType(@NotNull Player player) {
        return getSpellType(player.getInventory().getItemInMainHand());
    }

    /**
     * Gets the spellType of the item.
     *
     * @param item The item to get the spellType from
     * @return The spellType
     */
    public static @Nullable String getSpellType(@Nullable ItemStack item) {
        if (item == null)
            return null;

        if (!item.hasItemMeta())
            return null;

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        if (!data.has(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING))
            return null;

        String spellType = data.get(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING);
        if (spellType != null)
            spellType = spellType.toUpperCase();

        return spellType;
    }

    /**
     * Sets the spellType of the item to the given String
     *
     * @param item The item to set the spellType of
     * @param spellType The spellType to set to
     * @return The modified item
     */
    public static @NotNull ItemStack setSpellType(@NotNull ItemStack item, @NotNull String spellType) {
        if (!item.hasItemMeta()) //TODO THIS BYPASS IS GARBAGE!!!! FIX AT SOME POINT
            item.setItemMeta(new ItemStack(Material.STONE).getItemMeta());

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING, spellType);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Gets the spellName of the item the player is currently holding.
     *
     * @param player The player whose hold item to get the spellName from
     * @return The spellName
     */
    public static @Nullable String getHeldSpellName(@NotNull Player player) {
        return getSpellName(player.getInventory().getItemInMainHand());
    }

    /**
     * Gets the spellName of the item.
     *
     * @param item The item to get the spellName from
     * @return The spellName
     */
    public static @Nullable String getSpellName(@Nullable ItemStack item) {
        if (item == null)
            return null;

        if (!item.hasItemMeta())
            return null;

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        if (!data.has(PersistentDataKeys.spellNameKey, PersistentDataType.STRING))
            return null;

        String spellName = data.get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING);
        if (spellName != null)
            spellName = spellType.toLowerCase();

        return spellName;
    }

    /**
     * Sets the spellName of the item to the given String
     *
     * @param item The item to set the spellName of
     * @param spellName The spellName to set to
     * @return The modified item
     */
    public static @NotNull ItemStack setSpellName(@NotNull ItemStack item, @NotNull String spellName) {
        if (!item.hasItemMeta()) //TODO THIS BYPASS IS GARBAGE!!!! FIX AT SOME POINT
            item.setItemMeta(new ItemStack(Material.STONE).getItemMeta());

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(PersistentDataKeys.spellNameKey, PersistentDataType.STRING, spellName);
        item.setItemMeta(meta);
        return item;
    }
}
