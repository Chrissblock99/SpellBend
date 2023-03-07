package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.SpellHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHit implements Listener {
    public ProjectileHit() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        SpellHandler.projectileHit(event);
    }
}
