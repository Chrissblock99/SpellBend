package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.temporary.InteractableEntityHandler;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntity implements Listener {
    public PlayerInteractEntity() {
        SpellBend.registerEvent(this);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (GameMode.ADVENTURE.equals(event.getPlayer().getGameMode())) {
            event.setCancelled(true);  //basically noInteract for normal players
            //noinspection ConstantConditions
            if (!InteractableEntityHandler.isInteractableEntity(event.getRightClicked()))
                if (SpellHandler.itemIsRegisteredSpell(event.getPlayer().getInventory().getItemInMainHand()))
                    PlayerSessionData.getPlayerSession(event.getPlayer()).getSpellHandler().playerClickedSpellItem(event.getPlayer().getInventory().getItemInMainHand());
        }
    }
}
