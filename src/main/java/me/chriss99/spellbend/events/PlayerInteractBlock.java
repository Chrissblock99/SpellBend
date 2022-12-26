package me.chriss99.spellbend.events;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
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
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        if (GameMode.ADVENTURE.equals(event.getPlayer().getGameMode()))  {
            event.setCancelled(true);  //basically noInteract for normal players
            //spellHandling
            if (event.hasItem())
                if (Action.RIGHT_CLICK_AIR.equals(event.getAction()) || Action.RIGHT_CLICK_BLOCK.equals(event.getAction()))
                    if (SpellHandler.itemIsRegisteredSpell(event.getItem()))
                        PlayerSessionData.getPlayerSession(event.getPlayer()).getSpellHandler().playerClickedSpellItem(event.getItem());
        }
    }
}
