package me.chriss99.spellbend.events;

import me.chriss99.spellbend.playerdata.PlayerDataBoard;
import me.chriss99.spellbend.playerdata.PlayerDataUtil;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    public PlayerJoin() {
        GeneralRegisterUtil.registerEvent(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            PlayerDataUtil.setupPlayerData(player);
        }
        PlayerDataUtil.loadAll(player);
        PlayerDataBoard.registerPlayer(player, "FFS FIX THIS LATER");
        SpellHandler.registerPlayer(player);
    }
}
