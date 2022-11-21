package me.chriss99.spellbend.events;

import me.chriss99.spellbend.playerdata.PlayerDataBoard;
import me.chriss99.spellbend.playerdata.PlayerDataUtil;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.GeneralRegisterUtil;
import me.chriss99.spellbend.util.ItemData;
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

        if (!player.hasPlayedBefore())
            PlayerDataUtil.setupPlayerData(player);

        PlayerDataUtil.loadAll(player);
        SpellHandler.registerPlayer(player);

        String heldSpellType = ItemData.getHeldSpellType(player);
        if (heldSpellType != null)
            PlayerDataBoard.registerPlayer(player, heldSpellType);
        else PlayerDataBoard.updateBoard(player);
    }
}
