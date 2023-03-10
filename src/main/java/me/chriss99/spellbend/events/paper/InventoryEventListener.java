package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.gui.GUIManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;

public class InventoryEventListener implements Listener {
    public InventoryEventListener() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onInventoryEvent(InventoryEvent event) {
        GUIManager.onInventoryEvent(event);
    }
}
