package me.chriss99.spellbend.data.sidebar;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.ItemData;
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

public class PlayerSideBar {
    private static final Map<Player, String> playersDisplayingCoolDown = new HashMap<>();

    private final Player player;
    private final AbstractCoolDownTimeDisplayFactory coolDownTimeDisplayFactory;

    public PlayerSideBar(@NotNull Player player) {
        this(player, new CoolDownTimeDisplayFactoryImpl());
    }

    public PlayerSideBar(@NotNull Player player, @NotNull AbstractCoolDownTimeDisplayFactory coolDownTimeDisplayFactory) {
        this.player = player;
        this.coolDownTimeDisplayFactory = coolDownTimeDisplayFactory;
    }

    /**
     * Starts the loop to update all players holding a coolDowned item and creates a board for all players currently online
     */
    public static void startUpdater() {
        //creating boards for players already online
        for (Player player : Bukkit.getOnlinePlayers())
            PlayerSessionData.getPlayerSession(player).getPlayerSideBar().updateBoard();

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
                    PlayerSideBar playerSideBar = PlayerSessionData.getPlayerSession(player).getPlayerSideBar();

                    if (coolDownEntry == null) {
                        playerSideBar.updateBoard(null);
                        return;
                    }

                    playerSideBar.updateBoard(spellType);
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
        updateBoard(true);
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

        CoolDownTimeDisplay coolDownTimeDisplay = coolDownTimeDisplayFactory.createCoolDownTimeDisplay(coolDownEntry);
        line = obj.getScore(coolDownTimeDisplay.name()); line.setScore(1);
        line = obj.getScore(coolDownTimeDisplay.time()); line.setScore(0);

        player.setScoreboard(board);
    }

    private static @NotNull StringBuilder buildTimeDisplay(@NotNull CoolDownEntry coolDownEntry, char numColor, char barColor1, boolean backwards, char barColor2) {
        double forwards = MathUtil.clamp((coolDownEntry.getRemainingCoolDownStageTimeInS()/coolDownEntry.getStageTimeInS())*10, 0, 10);
        int filled = (int) Math.round(backwards ? 10-forwards : forwards);

        StringBuilder timeDisplay = new StringBuilder("§").append(numColor)
                .append(MathUtil.roundToNDigits(coolDownEntry.getRemainingCoolDownStageTimeInS(), 1))
                .append("s §").append(barColor1).append("▌▌▌▌▌▌▌▌▌▌");
        return timeDisplay.insert(timeDisplay.length()-10 + filled, "§" + barColor2);
    }
}
