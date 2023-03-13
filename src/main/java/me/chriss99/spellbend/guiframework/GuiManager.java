package me.chriss99.spellbend.guiframework;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GuiManager {
    private static final HashMap<Inventory, GuiInventory> guiInventories = new HashMap<>();

    public static void registerGuiInventory(@NotNull GuiInventory guiInventory) {
        guiInventories.put(guiInventory.inventory, guiInventory);
    }

    public static void click(@NotNull InventoryClickEvent clickEvent) {
        GuiInventory guiInventory = guiInventories.get(clickEvent.getClickedInventory());
        if (guiInventory == null)
            return;

        clickEvent.setCancelled(true);
        guiInventory.click(clickEvent);
    }
}
