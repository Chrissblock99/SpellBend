package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ActionBarController {
    private final Player player;
    private String displayMessage = null;
    private BukkitTask displayMessageRemover = null;

    public static void startUpdater() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            PlayerSessionData.getPlayerSession(onlinePlayer).getActionBarController().updateBar();

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Map.Entry<Player, PlayerSessionData> entry : PlayerSessionData.getPlayerSessions().entrySet())
                    entry.getValue().getActionBarController().updateBar();
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 20);
    }

    public ActionBarController(@NotNull Player player) {
        this.player = player;
    }

    public void displayMessage(@NotNull String message) {
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
        player.sendActionBar("§c♥ §6" + TextUtil.roundToNDecimalPlaces(sessionData.getHealth().getHealth()/2, 2) + "§c/§610 §4| §b★ §6" + Math.round(sessionData.getMana().getCurrency()) + "§9/§6100");
    }

    public Player getPlayer() {
        return player;
    }
}
