package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClick implements Listener {
    public InventoryClick() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInv = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (!player.getInventory().equals(clickedInv)) //this includes the case of clickedInv being null
            return;
        if (player.getInventory().getHeldItemSlot() != event.getSlot())
            return;

        PlayerSwitchHeldItem.processPlayerSwitchHeldItem(player, clickedInv.getItem(event.getSlot()), event.getCursor());
    }
}
