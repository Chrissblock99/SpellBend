package me.chriss99.spellbend.events.paper;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJump implements Listener {
    public PlayerJump() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();

        if (GameMode.ADVENTURE.equals(player.getGameMode()) &&
                PlayerSessionData.getPlayerSession(player).getCanNotJump().valueIsLargerZero())
            event.setCancelled(true);
    }
}
