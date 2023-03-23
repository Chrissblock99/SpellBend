package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlock implements Listener {
    public EntityChangeBlock() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType().equals(EntityType.FALLING_BLOCK))
            event.setCancelled(true);
    }
}
