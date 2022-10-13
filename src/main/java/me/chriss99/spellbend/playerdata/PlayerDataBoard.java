package me.chriss99.spellbend.playerdata;

import me.chriss99.spellbend.data.CoolDownEntry;
//import game.spellbend.playerdata.Gems;
//import game.spellbend.playerdata.Gold;
//import game.spellbend.playerdata.PlayerDataUtil;
import me.chriss99.spellbend.SpellBend;
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
                    Bukkit.getLogger().info("§b" + entry.getKey() + ": " + entry.getValue());
                    Player player = entry.getKey();
                    if (!player.isOnline()) {
                        playersHoldingCoolDownedItem.remove(player);
                        Bukkit.getLogger().warning("UUID \"" + entry.getKey() + "\" registered in playersHoldingCoolDownedItems is offline, removing from Map!");
                        return;
                    }
                    updateBoard(player, entry.getValue());
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
        //debug
        Bukkit.getLogger().info("§bBefore removing:");
        for (Map.Entry<Player, String> entry : playersHoldingCoolDownedItem.entrySet())
            Bukkit.getLogger().info("§b" + entry.getKey() + ": " + entry.getValue());

        //code
        playersHoldingCoolDownedItem.remove(player);
        updateBoard(player, false);

        //debug
        Bukkit.getLogger().info("§bAfter removing:");
        for (Map.Entry<Player, String> entry : playersHoldingCoolDownedItem.entrySet())
            Bukkit.getLogger().info("§b" + entry.getKey() + ": " + entry.getValue());
    }

    /**
     * Updates the player's scoreboard
     *
     * @param player The player whose scoreboard to update
     */
    public static void updateBoard(@NotNull Player player) {
        updateBoard(player, ItemData.getHeldSpellType(player));
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param player The player whose scoreboard to update
     * @param spellType The spellType of the current CoolDown
     */
    public static void updateBoard(@NotNull Player player, @Nullable String spellType) {
        updateBoard(player, spellType, true);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param player The player whose scoreboard to update
     * @param deRegisterIfNull To deRegister the player if the spellType is null
     */
    public static void updateBoard(@NotNull Player player, boolean deRegisterIfNull) {
        updateBoard(player, ItemData.getHeldSpellType(player), deRegisterIfNull);
    }

    /**
     * Updates the player's scoreboard and displays the spellTypes coolDown.
     *
     * @param player The player whose scoreboard to update
     * @param spellType The spellType of the current CoolDown
     * @param deRegisterIfNull To deRegister the player if the spellType is null
     */
    public static void updateBoard(@NotNull Player player, @Nullable String spellType, boolean deRegisterIfNull) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("playerDataBoard", "dummy", Component.text("WIP"/*PlayerDataUtil.constructDisplayString(player)*/)); //TODO use LuckPerms here
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score line = obj.getScore("§3§m-------------"); line.setScore(11);
        line = obj.getScore("  §eGold: §b" + "WIP"/*Gold.getGold(player)*/); line.setScore(10);
        line = obj.getScore("  §3Gems: §b" + "WIP"/*Gems.getGems(player)*/); line.setScore(9);
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

        if (spellType != null) {
            CoolDownEntry coolDownEntry = CoolDowns.getCoolDownEntry(player, spellType);

            if (coolDownEntry != null && coolDownEntry.getRemainingCoolDownTime()>0.0001f) {
                line = obj.getScore("§7" + spellType.charAt(0) + spellType.substring(1).toLowerCase()); line.setScore(1);

                StringBuilder coolDownDisplay = new StringBuilder();

                switch (coolDownEntry.coolDownStage()) {
                    case WINDUP -> {
                        int filled = Math.round((coolDownEntry.getRemainingCoolDownTime()/coolDownEntry.timeInS())*10);
                        coolDownDisplay.append("§a").append(Math.round(coolDownEntry.getRemainingCoolDownTime()*10f)/10f).append("s §8▌▌▌▌▌▌▌▌▌▌")
                                .insert(coolDownDisplay.length()-10+filled, "§e");
                    }
                    case ACTIVE -> {
                        int filled = Math.round((coolDownEntry.getRemainingCoolDownTime()/coolDownEntry.timeInS())*10);
                        coolDownDisplay.append("§b").append(Math.round(coolDownEntry.getRemainingCoolDownTime()*10f)/10f).append("s §b▌▌▌▌▌▌▌▌▌▌")
                                .insert(coolDownDisplay.length()-10+filled, "§8");
                    }
                    case COOLDOWN -> {
                        int filled = Math.round(10-(coolDownEntry.getRemainingCoolDownTime()/coolDownEntry.timeInS())*10);
                        coolDownDisplay.append("§e").append(Math.round(coolDownEntry.getRemainingCoolDownTime()*10f)/10f).append("s §a▌▌▌▌▌▌▌▌▌▌")
                                .insert(coolDownDisplay.length()-10+filled, "§8");
                    }
                }

                line = obj.getScore(coolDownDisplay.toString()); line.setScore(0);
            } else {
                Bukkit.getLogger().warning("When updating " + player.displayName() + "'s scoreboard, " + spellType + " was given as the coolDowned spellType, however no such CoolDownEntry exists!");

                line = obj.getScore(""); line.setScore(1);
                line = obj.getScore(" "); line.setScore(0);

                deRegisterPlayer(player);
            }
        } else {
            line = obj.getScore(""); line.setScore(1);
            line = obj.getScore(" "); line.setScore(0);

            //makes it not recall itself infinitely if used correctly
            if (deRegisterIfNull)
                deRegisterPlayer(player);
        }

        player.setScoreboard(board);
    }
}
