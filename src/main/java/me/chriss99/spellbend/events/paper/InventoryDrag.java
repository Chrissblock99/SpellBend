package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.guiframework.GuiManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class InventoryDrag implements Listener {
    public InventoryDrag() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        GuiManager.dragEvent(event);
    }
}
