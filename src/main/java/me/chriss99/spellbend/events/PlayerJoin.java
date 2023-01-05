package me.chriss99.spellbend.events;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    public PlayerJoin() {
        SpellBend.registerEvent(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore())
            PlayerSessionData.setupPlayerData(player);

        PlayerSessionData.loadPlayerSession(player);

        String heldSpellType = ItemData.getHeldSpellType(player);
        if (heldSpellType != null)
            PlayerSessionData.getPlayerSession(player).getPlayerDataBoard().displayCooldown(heldSpellType);
        else PlayerSessionData.getPlayerSession(player).getPlayerDataBoard().updateBoard();
    }
}
