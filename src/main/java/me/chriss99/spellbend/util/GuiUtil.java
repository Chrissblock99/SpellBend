package me.chriss99.spellbend.util;

import me.chriss99.spellbend.guiframework.GuiInventory;
import me.chriss99.spellbend.guiframework.GuiButton;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class GuiUtil {
    public static int convertNumToOutlinedGUISlot(int number) {
        int x = number%7;
        int y = number/7;
        return 1+9+(9*y)+x;
    }

    /**
     * Returns a list of all slots that are on the edge of a gui
     *
     * @param rows How many rows its big
     * @return The slot numbers
     */
    public static @NotNull List<Integer> guiEdgeSlots(int rows) {
        int slots = rows*9;
        List<Integer> slotNumbers = new LinkedList<>(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8));
        for (int i=1; i<=(slots+1)/9-2;i++) {
            slotNumbers.add(i*9);
            slotNumbers.add(i*9+8);
        }
        for (int i=slots-9; i<slots;i++)
            slotNumbers.add(i);
        return slotNumbers;
    }

    public static void outLineGui(@NotNull GuiInventory guiInventory, @NotNull ItemStack item, int rows) {
        new GuiButton(item).registerIn(guiInventory, GuiUtil.guiEdgeSlots(rows));
    }
}