package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {
    public EntityDamageByEntity() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        boolean previousState = event.isCancelled();
        event.setCancelled(true);

        if (event.getDamager() instanceof Player player && !player.getGameMode().equals(GameMode.ADVENTURE))
            event.setCancelled(previousState); //non adventure mode players can damage anything
    }
}
