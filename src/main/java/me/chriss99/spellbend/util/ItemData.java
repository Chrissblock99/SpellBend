package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.harddata.SpellEnum;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
        if (!data.has(PersistentDataKeys.SPELL_TYPE_KEY, PersistentDataType.STRING))
            return null;

        String spellType = data.get(PersistentDataKeys.SPELL_TYPE_KEY, PersistentDataType.STRING);
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
        if (!item.hasItemMeta()) //TODO //HACK this does not seem like the correct way to do it
            item.setItemMeta(new ItemStack(Material.STONE).getItemMeta());

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(PersistentDataKeys.SPELL_TYPE_KEY, PersistentDataType.STRING, spellType);
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
        if (!data.has(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING))
            return null;

        String spellName = data.get(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING);
        if (spellName != null)
            spellName = spellName.toUpperCase();

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
        if (!item.hasItemMeta()) //TODO //HACK this does not seem like the correct way to do it
            item.setItemMeta(new ItemStack(Material.STONE).getItemMeta());

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING, spellName);
        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     * @param item The item to get the PersistentData of
     * @param key The key to use
     * @param persistentDataType The type to get
     * @return The stored value
     * @param <T> the primary object type that is stored in the given tag (no clue what this means I copied it from the PersistentDataType doc)
     * @param <Z> The type of the value
     */
    public static <T, Z> @Nullable Z getPersistentDataValue(@Nullable ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> persistentDataType) {
        if (item == null)
            return null;
        if (!item.hasItemMeta())
            return null;
        ItemMeta meta = item.getItemMeta();

        if (meta == null)
            return null;
        if (!meta.getPersistentDataContainer().has(key, persistentDataType))
            return null;
        return meta.getPersistentDataContainer().get(key, persistentDataType);
    }

    /**
     * @param item The item to set the PersistentData of
     * @param key The key to use
     * @param persistentDataType The type to set
     * @param <T> the primary object type that is stored in the given tag (no clue what this means I copied it from the PersistentDataType doc)
     * @param <Z> The type of the value
     */
    public static <T, Z> void setPersistentDataValue(@NotNull ItemStack item, @NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> persistentDataType, Z value) {
        if (!item.hasItemMeta()) //TODO //HACK this does not seem like the correct way to do it
            item.setItemMeta(new ItemStack(Material.STONE).getItemMeta());

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, persistentDataType, value);
        item.setItemMeta(meta);
    }

    /**
     * Checks if the item has a spellName and spellType argument, both not being null
     *
     * @param item The item to be checked
     * @return If it is a spell
     */
    public static boolean itemIsSpell(@Nullable ItemStack item) {
        if (item == null)
            return false;

        if (item.hasItemMeta()) {
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if (data.has(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING))
                return data.get(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING) != null;
        }
        return false;
    }

    /**
     * Checks itemIsSpell() and if the name is contained in SpellEnum
     *
     * @param item The item to be checked
     * @return If it is a registered spell
     */
    public static boolean itemIsRegisteredSpell(@Nullable ItemStack item) {
        if (item == null)
            return false;

        //noinspection ConstantConditions
        return itemIsSpell(item) && SpellEnum.spellExists(item.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING));
    }

    /**
     * Checks itemIsSpell() and if the name is contained in the nameToSpellBuilderMap
     *
     * @param item The item to be checked
     * @return If it is a registered spell
     */
    public static boolean itemIsExecutableSpell(@Nullable ItemStack item) {
        if (item == null)
            return false;

        //noinspection ConstantConditions
        return itemIsRegisteredSpell(item) &&
                SpellEnum.spellEnumOf(item.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING)).getSpellBuilder() != null;
    }
}
