package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemData {
    public static @Nullable String getHeldSpellType(@NotNull Player player) {
        ItemStack item =  player.getInventory().getItemInMainHand();
        if (item.hasItemMeta())
            if (item.getItemMeta().getPersistentDataContainer().has(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING))
                return item.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING);
        return null;
    }
}
