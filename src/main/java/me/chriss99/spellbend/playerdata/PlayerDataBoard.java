package me.chriss99.spellbend.playerdata;

import me.chriss99.spellbend.data.CoolDownEntry;
//import game.spellbend.playerdata.Gems;
//import game.spellbend.playerdata.Gold;
//import game.spellbend.playerdata.PlayerDataUtil;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.Enums;
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
    private static final HashMap<Player, String> playersHoldingCoolDownedItem = new HashMap<>();

    /**
     * Starts the loop to update all players holding a coolDowned item and creates a board for all players currently online
     */
    public static void start() {
        if (!Bukkit.getOnlinePlayers().isEmpty())                  //creating boards for players already online
            for (Player player : Bukkit.getOnlinePlayers())
                updateBoard(player);

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Map.Entry<Player, String> entry : playersHoldingCoolDownedItem.entrySet()) {
                    Player player = entry.getKey();
                    String spellType = entry.getValue();

                    if (!player.isOnline()) {
                        playersHoldingCoolDownedItem.remove(player);
                        Bukkit.getLogger().warning(player.getName() + " registered in playersHoldingCoolDownedItems is offline, removing from Map!");
                        return;
                    }
                    CoolDownEntry coolDownEntry = CoolDowns.getCoolDownEntry(player, spellType);

                    if (coolDownEntry == null) {
                        updateBoard(player, null);
                        return;
                    }

                    updateBoard(player, spellType);
                }
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 2);
    }

    /**
     * Adds the player to the map of all players holding a coolDowned item.
     *
     * @param player The player to add
     */
    public static void registerPlayer(@NotNull Player player, @NotNull String spellType) {
        playersHoldingCoolDownedItem.put(player, spellType);
        updateBoard(player, spellType);
    }

    /**
     * Removes the player from the map of all players holding a coolDowned item.
     *
     * @param player The player to remove
     */
    public static void deRegisterPlayer(@NotNull Player player) {
        playersHoldingCoolDownedItem.remove(player);
        updateBoard(player, false);
    }

    /**
     * Removes the player from the map of all players holding a coolDowned item.
     *
     * @param player The player to remove
     * @param spellType The spellType to update the board with
     */
    public static void deRegisterPlayer(@NotNull Player player, @Nullable String spellType) {
        playersHoldingCoolDownedItem.remove(player);
        updateBoard(player, spellType, false);
    }

    /**
     * Updates the player's scoreboard
     *
     * @param player The player whose scoreboard to update
     */
    public static void updateBoard(@NotNull Player player) {
        String heldCoolDownedSpellType = null;
        String heldSpellType = ItemData.getHeldSpellType(player);
        if (CoolDowns.getCoolDowns(player).containsKey(heldSpellType))
            heldCoolDownedSpellType = heldSpellType;

        updateBoard(player, heldCoolDownedSpellType);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param player The player whose scoreboard to update
     * @param heldCoolDownedSpellType The spellType of the currently held CoolDown
     */
    public static void updateBoard(@NotNull Player player, @Nullable String heldCoolDownedSpellType) {
        updateBoard(player, heldCoolDownedSpellType, true);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param player The player whose scoreboard to update
     * @param deRegister To deRegister the player in specific cases or not
     */
    public static void updateBoard(@NotNull Player player, boolean deRegister) {
        String heldCoolDownedSpellType = null;
        String heldSpellType = ItemData.getHeldSpellType(player);
        if (CoolDowns.getCoolDowns(player).containsKey(heldSpellType))
            heldCoolDownedSpellType = heldSpellType;

        updateBoard(player, heldCoolDownedSpellType, deRegister);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param player The player whose scoreboard to update
     * @param heldCoolDownedSpellType The spellType of the currently held CoolDown
     * @param deRegister To deRegister the player in specific cases or not
     */
    public static void updateBoard(@NotNull Player player, @Nullable String heldCoolDownedSpellType, boolean deRegister) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("playerDataBoard", "dummy", Component.text("WIP"/*PlayerDataUtil.constructDisplayString(player)*/)); //TODO use LuckPerms here
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score line = obj.getScore("§3§m-------------"); line.setScore(11);
        Currency.setCurrency(Enums.Currency.GOLD);
        line = obj.getScore("  §eGold: §b" + Math.round(Currency.getCurrency(player))); line.setScore(10);
        Currency.setCurrency(Enums.Currency.GEMS);
        line = obj.getScore("  §3Gems: §b" + Math.round(Currency.getCurrency(player))); line.setScore(9);
        line = obj.getScore("§3§m-------------§r" + ""); line.setScore(8);
        line = obj.getScore("  §cKills: §b" + player.getStatistic(Statistic.PLAYER_KILLS)); line.setScore(7);
        line = obj.getScore("  §4Deaths: §b" + player.getStatistic(Statistic.DEATHS)); line.setScore(6);
        line = obj.getScore("  §cKDR: §b" +
                ((player.getStatistic(Statistic.DEATHS) < 10) ?
                        "inaccurate" :
                        player.getStatistic(Statistic.PLAYER_KILLS)/player.getStatistic(Statistic.DEATHS)));
        line.setScore(5);
        line = obj.getScore("§3§m-------------§r  "); line.setScore(4);
        line = obj.getScore("  §b§nSpellBend§b.minehut.gg"); line.setScore(3);
        line = obj.getScore("§3§m-------------§r   "); line.setScore(2);

        if (heldCoolDownedSpellType == null) {
            line = obj.getScore(""); line.setScore(1);
            line = obj.getScore(" "); line.setScore(0);
            player.setScoreboard(board);

            //iason was here
            //makes it not recall itself infinitely if used correctly
            if (deRegister)
                deRegisterPlayer(player);
            return;
        }
        CoolDownEntry coolDownEntry = CoolDowns.getCoolDownEntry(player, heldCoolDownedSpellType);

        if (coolDownEntry == null) {
            Bukkit.getLogger().warning("When updating " + player.getName() + "'s scoreboard, " + heldCoolDownedSpellType + " was given as the coolDowned spellType, however no such CoolDownEntry exists!");

            line = obj.getScore(""); line.setScore(1);
            line = obj.getScore(" "); line.setScore(0);
            player.setScoreboard(board);

            //makes it not recall itself infinitely if used correctly
            if (deRegister)
                deRegisterPlayer(player);
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
