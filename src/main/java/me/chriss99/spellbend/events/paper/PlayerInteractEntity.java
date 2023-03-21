package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.manager.InteractableEntityManager;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntity implements Listener {
    public PlayerInteractEntity() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (GameMode.ADVENTURE.equals(event.getPlayer().getGameMode())) {
            event.setCancelled(true);  //basically noInteract for normal players
            //noinspection ConstantConditions
            if (!InteractableEntityManager.isInteractableEntity(event.getRightClicked()))
                if (ItemData.itemIsRegisteredSpell(event.getPlayer().getInventory().getItemInMainHand()))
                    PlayerSessionData.getPlayerSession(event.getPlayer()).getSpellHandler().playerClickedSpellItem(event.getPlayer().getInventory().getItemInMainHand());
        }
    }
}
