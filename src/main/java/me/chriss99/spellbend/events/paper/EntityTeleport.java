package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.util.LivingEntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;

public class EntityTeleport implements Listener {
    public EntityTeleport() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTeleport(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if (!event.isCancelled() && LivingEntityUtil.entityIsSpellAffectAble(entity)) {
            boolean wasLoaded = event.getFrom().isChunkLoaded();
            boolean willBeLoaded = event.getTo() != null && event.getTo().isChunkLoaded();

            if (wasLoaded == willBeLoaded)
                return;

            if (!wasLoaded/* && willBeLoaded*/) {
                Bukkit.getLogger().info("load " + entity.getName());
                LivingEntitySessionData.loadLivingEntitySession((LivingEntity) entity);
                return;
            }

            /*wasLoaded && !willBeLoaded*/
            Bukkit.getLogger().info("unload " + entity.getName());
            LivingEntitySessionData.getLivingEntitySession((LivingEntity) entity).endSession();
        }
    }
}
