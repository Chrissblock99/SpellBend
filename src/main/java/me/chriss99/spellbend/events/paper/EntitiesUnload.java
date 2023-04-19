package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.util.LivingEntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesUnloadEvent;

public class EntitiesUnload implements Listener {
    public EntitiesUnload() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (!LivingEntityUtil.entityIsSpellAffectAble(entity))
                continue;

            Bukkit.getLogger().info("unload " + entity.getName());
            LivingEntitySessionData.getLivingEntitySession((LivingEntity) entity).endSession();
        }
    }
}
