package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryUtil {
    /**
     *
     * @param inv The inventory to search for Spells inside
     * @return The amount of Spells inside the inventory
     */
    public static int spellsInsideInventory(@NotNull Inventory inv) {
        int num = 0;
        for (ItemStack item : inv.getContents())
            if (ItemData.itemIsSpell(item))
                num++;
        return num;
    }

    /**
     *
     * @param inv The inventory to search for SpellName
     * @param spellName The spellName to check for
     * @return The amount of Spells inside the inventory
     */
    public static boolean inventoryContainsSpellName(@NotNull Inventory inv, @Nullable String spellName) {
        if (spellName == null)
            return false;

        for (ItemStack item : inv.getContents())
            if (ItemData.itemIsSpell(item) && spellName.equals(ItemData.getPersistentDataValue(item, PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING)))
                return true;
        return false;
    }
}
