package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.util.LivingEntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {
    public EntityDeath() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!event.isCancelled() && LivingEntityUtil.entityIsSpellAffectAble(livingEntity)) {
            Bukkit.getLogger().info("unload " + livingEntity.getName());
            LivingEntitySessionData.getLivingEntitySession(livingEntity).endSession();
        }
    }
}
