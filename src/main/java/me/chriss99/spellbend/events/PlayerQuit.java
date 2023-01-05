package me.chriss99.spellbend.events;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    public PlayerQuit() {
        SpellBend.registerEvent(this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlayerSessionData.getPlayerSession(player).getPlayerDataBoard().stopDisplayCooldown();
        PlayerSessionData.getPlayerSession(player).endSession();
    }
}
