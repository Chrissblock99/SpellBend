package me.chriss99.spellbend.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJump implements Listener {
    public PlayerJump() {
        SpellBend.registerEvent(this);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        if (GameMode.ADVENTURE.equals(event.getPlayer().getGameMode()) &&
                PlayerSessionData.getPlayerSession(event.getPlayer()).getCanNotJump().valueIsLargerZero())
            event.setCancelled(true);
    }
}
