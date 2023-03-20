package me.chriss99.spellbend.guiframework;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GuiItem {
    protected final ItemStack item;

    public GuiItem(@NotNull ItemStack item) {
        this.item = item;
    }

    public void registerIn(@NotNull GuiInventory guiInventory, @NotNull List<Integer> slots) {
        guiInventory.registerItem(this, slots);
    }

    public void registerIn(@NotNull GuiInventory guiInventory, int slot) {
        guiInventory.registerItem(this, slot);
    }
}
