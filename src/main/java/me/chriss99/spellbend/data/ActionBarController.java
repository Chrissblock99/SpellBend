package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.TextUtil;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ActionBarController {
    private final Player player;
    private Component displayMessage = null;
    private BukkitTask displayMessageRemover = null;

    public static void startUpdater() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            PlayerSessionData.getPlayerSession(onlinePlayer).getActionBarController().updateBar();

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Map.Entry<Player, PlayerSessionData> entry : PlayerSessionData.getPlayerSessionsView().entrySet())
                    entry.getValue().getActionBarController().updateBar();
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 20);
    }

    public ActionBarController(@NotNull Player player) {
        this.player = player;
    }

    public void displayMessage(@NotNull Component message) {
        displayMessage = message;
        updateBar();

        if (displayMessageRemover != null)
            displayMessageRemover.cancel();
        displayMessageRemover = new BukkitRunnable(){
            @Override
            public void run() {
                displayMessage = null;
                displayMessageRemover = null;
                updateBar();
            }
        }.runTaskLater(SpellBend.getInstance(), 20);
    }

    public void updateBar() {
        if (displayMessage != null) {
            player.sendActionBar(displayMessage);
            return;
        }

        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
        player.sendActionBar(SpellBend.getMiniMessage().deserialize("<red>❤</red> <gold>" + TextUtil.roundToNDecimalPlaces(sessionData.getHealth().getHealth()/2, 2) + "</gold><red>/</red><gold>10</gold> <dark_red>|</dark_red> <aqua>★</aqua> <gold>" + Math.round(sessionData.getMana().getCurrency()) + "</gold><blue>/</blue><gold>100</gold>"));
    }

    public Player getPlayer() {
        return player;
    }
}
