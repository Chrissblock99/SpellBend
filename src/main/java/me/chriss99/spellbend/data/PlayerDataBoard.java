package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.ItemData;
import me.chriss99.spellbend.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataBoard {
    private static final Map<Player, String> playersDisplayingCoolDown = new HashMap<>();

    private final Player player;

    public PlayerDataBoard(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Starts the loop to update all players holding a coolDowned item and creates a board for all players currently online
     */
    public static void startUpdater() {
        //creating boards for players already online
        for (Player player : Bukkit.getOnlinePlayers())
            PlayerSessionData.getPlayerSession(player).getPlayerDataBoard().updateBoard();

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Map.Entry<Player, String> entry : playersDisplayingCoolDown.entrySet()) {
                    Player player = entry.getKey();
                    String spellType = entry.getValue();

                    if (!player.isOnline()) {
                        playersDisplayingCoolDown.remove(player);
                        Bukkit.getLogger().warning(player.getName() + " registered in playersHoldingCoolDownedItems is offline, removing from Map!");
                        return;
                    }
                    CoolDownEntry coolDownEntry = PlayerSessionData.getPlayerSession(player).getCoolDowns().getCoolDownEntry(spellType);
                    PlayerDataBoard playerDataBoard = PlayerSessionData.getPlayerSession(player).getPlayerDataBoard();

                    if (coolDownEntry == null) {
                        playerDataBoard.updateBoard(null);
                        return;
                    }

                    playerDataBoard.updateBoard(spellType);
                }
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 2);
    }

    /**
     * Adds the player to the map of all players holding a coolDowned item.
     */
    public void displayCooldown(@NotNull String spellType) {
        playersDisplayingCoolDown.put(player, spellType);
        updateBoard(spellType);
    }

    /**
     * Removes the player from the map of all players holding a coolDowned item.
     */
    public void stopDisplayCooldown() {
        playersDisplayingCoolDown.remove(player);
        updateBoard(false);
    }

    /**
     * Removes the player from the map of all players holding a coolDowned item.
     *
     * @param spellType The spellType to update the board with
     */
    public void stopDisplayCooldown(@Nullable String spellType) {
        playersDisplayingCoolDown.remove(player);
        updateBoard(spellType, false);
    }

    /**
     * Updates the player's scoreboard
     */
    public void updateBoard() {
        String heldCoolDownedSpellType = null;
        String heldSpellType = ItemData.getHeldSpellType(player);
        if (PlayerSessionData.getPlayerSession(player).getCoolDowns().typeIsCooledDown(heldSpellType))
            heldCoolDownedSpellType = heldSpellType;

        updateBoard(heldCoolDownedSpellType);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param heldCoolDownedSpellType The spellType of the currently held CoolDown
     */
    public void updateBoard(@Nullable String heldCoolDownedSpellType) {
        updateBoard(heldCoolDownedSpellType, true);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param deRegister To deRegister the player in specific cases or not
     */
    public void updateBoard(boolean deRegister) {
        String heldCoolDownedSpellType = null;
        String heldSpellType = ItemData.getHeldSpellType(player);
        if (PlayerSessionData.getPlayerSession(player).getCoolDowns().typeIsCooledDown(heldSpellType))
            heldCoolDownedSpellType = heldSpellType;

        updateBoard(heldCoolDownedSpellType, deRegister);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param heldCoolDownedSpellType The spellType of the currently held CoolDown
     * @param deRegister To deRegister the player in specific cases or not
     */
    public void updateBoard(@Nullable String heldCoolDownedSpellType, boolean deRegister) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("playerDataBoard", "dummy", Component.text("WIP"/*PlayerDataUtil.constructDisplayString(player)*/)); //TODO use LuckPerms here
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);

        Score line = obj.getScore("§3§m-------------"); line.setScore(11);
        line = obj.getScore("  §eGold: §b" + Math.round(sessionData.getGold().getCurrency())); line.setScore(10);
        line = obj.getScore("  §3Gems: §b" + Math.round(sessionData.getGems().getCurrency())); line.setScore(9);
        line = obj.getScore("§3§m-------------§r" + ""); line.setScore(8);
        line = obj.getScore("  §cKills: §b" + player.getStatistic(Statistic.PLAYER_KILLS)); line.setScore(7);
        line = obj.getScore("  §4Deaths: §b" + player.getStatistic(Statistic.DEATHS)); line.setScore(6);
        line = obj.getScore("  §cKDR: §b" +
                ((player.getStatistic(Statistic.DEATHS) < 10) ?
                        "inaccurate" :
                        player.getStatistic(Statistic.PLAYER_KILLS)/player.getStatistic(Statistic.DEATHS)));
        line.setScore(5);
        line = obj.getScore("§3§m-------------§r  "); line.setScore(4);
        line = obj.getScore("  §b§nSpellBendV3§b.minehut.gg"); line.setScore(3);
        line = obj.getScore("§3§m-------------§r   "); line.setScore(2);

        if (heldCoolDownedSpellType == null) {
            line = obj.getScore(""); line.setScore(1);
            line = obj.getScore(" "); line.setScore(0);
            player.setScoreboard(board);

            //iason was here
            //makes it not recall itself infinitely if used correctly
            if (deRegister)
                stopDisplayCooldown();
            return;
        }
        CoolDownEntry coolDownEntry = sessionData.getCoolDowns().getCoolDownEntry(heldCoolDownedSpellType);

        if (coolDownEntry == null) {
            Bukkit.getLogger().warning("When updating " + player.getName() + "'s scoreboard, " + heldCoolDownedSpellType + " was given as the coolDowned spellType, however no such CoolDownEntry exists!");

            line = obj.getScore(""); line.setScore(1);
            line = obj.getScore(" "); line.setScore(0);
            player.setScoreboard(board);

            //makes it not recall itself infinitely if used correctly
            if (deRegister)
                stopDisplayCooldown();
            return;
        }

        line = obj.getScore("§7" + TextUtil.standardCapitalize(heldCoolDownedSpellType) + " - " + coolDownEntry.getCoolDownStage().toString().toLowerCase()); line.setScore(1);
        StringBuilder coolDownDisplay = new StringBuilder();

        switch (coolDownEntry.getCoolDownStage()) {
            case WINDUP -> {
                int filled = Math.round(10-(coolDownEntry.getRemainingCoolDownStageTimeInS()/coolDownEntry.getStageTimeInS())*10);
                coolDownDisplay.append("§e").append(Math.round(coolDownEntry.getRemainingCoolDownStageTimeInS()*10f)/10f)
                        .append("s §8▌▌▌▌▌▌▌▌▌▌").insert(coolDownDisplay.length()-10+filled, "§b");
            }
            case ACTIVE -> {
                int filled = Math.round((coolDownEntry.getRemainingCoolDownStageTimeInS()/coolDownEntry.getStageTimeInS())*10);
                coolDownDisplay.append("§e").append(Math.round(coolDownEntry.getRemainingCoolDownStageTimeInS()*10f)/10f)
                        .append("s §8▌▌▌▌▌▌▌▌▌▌").insert(coolDownDisplay.length()-10+filled, "§a");
            }
            case PASSIVE -> {
                int filled = Math.round((coolDownEntry.getRemainingCoolDownStageTimeInS()/coolDownEntry.getStageTimeInS())*10);
                coolDownDisplay.append("§b").append(Math.round(coolDownEntry.getRemainingCoolDownStageTimeInS()*10f)/10f)
                        .append("s §a▌▌▌▌▌▌▌▌▌▌").insert(coolDownDisplay.length()-10+filled, "§8");
            }
            case COOLDOWN -> {
                int filled = Math.round(10-(coolDownEntry.getRemainingCoolDownStageTimeInS()/coolDownEntry.getStageTimeInS())*10);
                coolDownDisplay.append("§b").append(Math.round(coolDownEntry.getRemainingCoolDownStageTimeInS()*10f)/10f)
                        .append("s §b▌▌▌▌▌▌▌▌▌▌").insert(coolDownDisplay.length()-10+filled, "§8");
            }
        }
        line = obj.getScore(coolDownDisplay.toString()); line.setScore(0);

        player.setScoreboard(board);
    }
}
