package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemData {
    public static @Nullable String getHeldSpellType(@NotNull Player player) {
        return getSpellType(player.getInventory().getItemInMainHand());
    }

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
