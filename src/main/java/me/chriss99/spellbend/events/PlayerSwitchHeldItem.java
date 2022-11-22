package me.chriss99.spellbend.events;

import me.chriss99.spellbend.util.GeneralRegisterUtil;
import me.chriss99.spellbend.temporary.Temporary;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerSwitchHeldItem implements Listener {
    public PlayerSwitchHeldItem() {
        GeneralRegisterUtil.registerEvent(this);
    }

    @EventHandler
    public void onPlayerSwitchHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        Temporary.playerSwitchHeldItem(player, player.getInventory().getItemInMainHand(), player.getInventory().getItem(event.getNewSlot()));
    }
}
