package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.util.LivingEntityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;

public class EntitiesLoad implements Listener {
    public EntitiesLoad() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (!LivingEntityUtil.entityIsSpellAffectAble(entity))
                continue;

            LivingEntitySessionData.loadLivingEntitySession((LivingEntity) entity);
        }
    }
}
