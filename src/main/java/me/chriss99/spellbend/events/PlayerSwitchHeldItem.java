package me.chriss99.spellbend.events;

import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.playerdata.PlayerDataBoard;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerSwitchHeldItem implements Listener {
    public PlayerSwitchHeldItem() {
        GeneralRegisterUtil.registerEvent(this);
    }

    @EventHandler
    public void onPlayerSwitchHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        if (SpellHandler.itemIsSpell(player.getInventory().getItemInMainHand()) || SpellHandler.itemIsSpell(newItem)) {
            String spellType = ItemData.getSpellType(newItem);
            //noinspection ConstantConditions
            if (spellType != null && CoolDowns.getCoolDownEntry(player, spellType) != null && CoolDowns.getCoolDownEntry(player, spellType).getRemainingCoolDownStageTimeInS() > 0.1f) //TODO redo this in context of the CoolDownEntry change
                PlayerDataBoard.registerPlayer(player, spellType);
            else PlayerDataBoard.deRegisterPlayer(player, null); //have to pass null here as the event isn't finished yet. therefore the item the player is currently holding is still the old one
        }
    }
}
