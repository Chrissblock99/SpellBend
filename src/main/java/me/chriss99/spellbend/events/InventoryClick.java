package me.chriss99.spellbend.events;

import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.playerdata.PlayerDataBoard;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {
    public InventoryClick() {
        GeneralRegisterUtil.registerEvent(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInv = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (!player.getInventory().equals(clickedInv)) //this includes the case of clickedInv being null
            return;
        if (player.getInventory().getHeldItemSlot() != event.getSlot())
            return;
        ItemStack itemToBeInSlot = event.getCursor();

        if (SpellHandler.itemIsSpell(clickedInv.getItem(event.getSlot())) || SpellHandler.itemIsSpell(itemToBeInSlot)) {
            String spellType = ItemData.getSpellType(itemToBeInSlot);
            //noinspection ConstantConditions
            if (spellType != null && CoolDowns.getCoolDownEntry(player, spellType) != null && CoolDowns.getCoolDownEntry(player, spellType).getRemainingCoolDownTime() > 0.1f)
                PlayerDataBoard.registerPlayer(player, spellType);
            else PlayerDataBoard.deRegisterPlayer(player, null); //have to pass null here as the event isn't finished yet. therefore the item the player is currently holding is still the old one
        }
    }
}
