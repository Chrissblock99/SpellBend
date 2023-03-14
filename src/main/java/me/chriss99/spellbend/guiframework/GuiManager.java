package me.chriss99.spellbend.guiframework;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GuiManager {
    private static final HashMap<Inventory, GuiInventory> guiInventories = new HashMap<>();

    public static void registerGuiInventory(@NotNull GuiInventory guiInventory) {
        guiInventories.put(guiInventory.inventory, guiInventory);
    }

    public static void click(@NotNull InventoryClickEvent clickEvent) {
        GuiInventory guiInventory = guiInventories.get(clickEvent.getClickedInventory());
        if (guiInventory == null) {
            nonGuiClicked(clickEvent);
            return;
        }

        guiInventory.click(clickEvent);
    }

    private static void nonGuiClicked(@NotNull InventoryClickEvent clickEvent) {
        GuiInventory guiInventory = guiInventories.get(getNotClickedInventory(clickEvent));
        if (guiInventory == null)
            return;

        guiInventory.clickInOtherInventory(clickEvent);
    }

    private static Inventory getNotClickedInventory(@NotNull InventoryClickEvent clickEvent) {
        InventoryView inventoryView = clickEvent.getView();
        if (inventoryView.getTopInventory().equals(clickEvent.getClickedInventory()))
            return inventoryView.getBottomInventory();
        return inventoryView.getTopInventory();
    }
}
