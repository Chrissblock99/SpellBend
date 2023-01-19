package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChange implements Listener {
    public FoodLevelChange() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (!player.getGameMode().equals(GameMode.ADVENTURE))
            return;

        event.setCancelled(true);
        player.setFoodLevel(20);
    }
}
