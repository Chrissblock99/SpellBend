package me.chriss99.spellbend.gui;

import me.chriss99.spellbend.SpellBend;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GUIUtil {
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();

    public static int convertNumToOutlinedGUISlot(int number) {
        int x = number%7;
        int y = number/7;
        return 1+9+(9*y)+x;
    }

    /**
     * Creates a chest inventory with n rows which is outlined by the given ItemStack and has the given name
     *
     * @param item The item to outline with
     * @param rows How many rows it should be big
     * @param miniMessageName The name in miniMessage format
     * @return The inventory
     */
    public static @NotNull Inventory createOutlinedGUI(@NotNull ItemStack item, int rows, String miniMessageName) {
        int slots = rows*9;
        Inventory inv = Bukkit.createInventory(null, slots, miniMessage.deserialize(miniMessageName));
        for (int i=0; i<9;i++)
            inv.setItem(i, item);
        for (int i=1; i<=(slots+1)/9-2;i++) {
            inv.setItem(i*9, item);
            inv.setItem(i*9+8, item);
        }
        for (int i=slots-9; i<slots;i++)
            inv.setItem(i, item);
        return inv;
    }
}
