package me.chriss99.spellbend.events;

import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.temporary.InteractableEntityHandler;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntity implements Listener {
    public PlayerInteractEntity() {
        GeneralRegisterUtil.registerEvent(this);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (GameMode.ADVENTURE.equals(event.getPlayer().getGameMode())) {
            event.setCancelled(true);  //basically noInteract for normal players
            //noinspection ConstantConditions
            if (!InteractableEntityHandler.isInteractableEntity(event.getRightClicked()))
                if (SpellHandler.itemIsRegisteredSpell(event.getPlayer().getInventory().getItemInMainHand()))
                    SpellHandler.letPlayerCastSpell(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
        }
    }
}
