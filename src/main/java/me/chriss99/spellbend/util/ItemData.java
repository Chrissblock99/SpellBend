package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemData {

    /**
     * Gets the SpellType of the item the player is currently holding.
     *
     * @param player The player to get the spellType from
     * @return The SpellType
     */
    public static @Nullable String getHeldSpellType(@NotNull Player player) {
        return getSpellType(player.getInventory().getItemInMainHand());
    }

    /**
     * Gets the SpellType of the item.
     *
     * @param item The item to get the SpellType from
     * @return The SpellType
     */
    public static @Nullable String getSpellType(@Nullable ItemStack item) {
        if (item != null)
            if (item.hasItemMeta()) {
                PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                if (data.has(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING))
                    return data.get(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING);
            }
        return null;
    }
}
