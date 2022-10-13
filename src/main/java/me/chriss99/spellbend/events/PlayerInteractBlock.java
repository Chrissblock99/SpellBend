package me.chriss99.spellbend.events;

import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractBlock implements Listener {
    public PlayerInteractBlock() {
        GeneralRegisterUtil.registerEvent(this);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Bukkit.getLogger().info("player interacted!");
        if (GameMode.ADVENTURE.equals(event.getPlayer().getGameMode()))  {
            Bukkit.getLogger().info("player is adventure game mode");
            event.setCancelled(true);  //basically noInteract for normal players
            //spellHandling
            if (event.hasItem()) {
                Bukkit.getLogger().info("event has item");
                if (Action.RIGHT_CLICK_AIR.equals(event.getAction()) || Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
                    Bukkit.getLogger().info("right click air or right click block was true!");
                    if (SpellHandler.itemIsRegisteredSpell(event.getItem())) {
                        Bukkit.getLogger().info("item is registered spell, casting it!");
                        SpellHandler.letPlayerCastSpell(event.getPlayer(), event.getItem());
                    }
                }
            }
        }
    }
}
