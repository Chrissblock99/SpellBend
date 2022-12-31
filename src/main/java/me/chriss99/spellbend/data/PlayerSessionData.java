package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerSessionData {
    private static final Gson gson = SpellBend.getGson();
    private static final Map<Player, PlayerSessionData> playerSessions = new HashMap<>();


    private final Player player;

    private final SpellHandler spellHandler;
    private final PlayerDataBoard playerDataBoard;
    private final ActionBarController actionBarController;

    private final Currency mana;
    private final Currency gems;
    private final Currency gold;
    private final Currency crystals;

    private final ValueTracker canNotJump;
    private final ValueTracker invisibility;

    private final CoolDowns coolDowns;
    private final PercentageModifier damageDealtModifiers;
    private final PercentageModifier damageTakenModifiers;
    private final PercentageModifier walkSpeedModifiers;
    private final Health health;

    public static void startManaRegenerator() {
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Map.Entry<Player, PlayerSessionData> entry : playerSessions.entrySet()) {
                    Currency mana = entry.getValue().getMana();
                    mana.setCurrency(Math.min(100, mana.getCurrency()+5));
                }
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 20);
    }

    /**
     * Loads the players sessionData from his PersistentData, checking if it is already loaded or if the player isn't online
     *
     * @param player The player whose sessionData to load
     * @return The player's sessionData, null if offline
     */
    public static @Nullable PlayerSessionData loadPlayerSession(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load their session, skipping loading!");
            return null;
        }
        PlayerSessionData playerSession = playerSessions.get(player);
        if (playerSession != null) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading their session, skipping loading!");
            return playerSession;
        }

        playerSession = new PlayerSessionData(player);

        playerSessions.put(player, playerSession);
        return playerSession;
    }

    /**
     * Gets the player's sessionData, loading it if not existent
     *
     * @throws NullPointerException If the player is offline
     *
     * @param player The player whose sessionData to get
     * @return The player's session, null if offline
     */
    public static @NotNull PlayerSessionData getPlayerSession(@NotNull Player player) {
        if (!player.isOnline()) {
            //noinspection DataFlowIssue
            return null;
        }

        PlayerSessionData playerSession = playerSessions.get(player);
        if (playerSession == null) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerSessions map, now fixing!");
            playerSession = Objects.requireNonNull(loadPlayerSession(player));
        }

        return playerSession;
    }

    /**
     * Sets up all the PersistentData of the player
     *
     * @param player The player who's PersistentData to set up
     */
    public static void setupPlayerData(@NotNull Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();

        data.set(PersistentDataKeys.gemsKey, PersistentDataType.FLOAT, 150f);
        data.set(PersistentDataKeys.goldKey, PersistentDataType.FLOAT, 650f);
        data.set(PersistentDataKeys.crystalsKey, PersistentDataType.FLOAT, 0f);

        data.set(PersistentDataKeys.canNotJumpKey, PersistentDataType.INTEGER, 0);
        data.set(PersistentDataKeys.invisibilityKey, PersistentDataType.INTEGER, 0);

        data.set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, gson.toJson(new HashMap<String, CoolDownEntry>()));
        data.set(PersistentDataKeys.damageDealtModifiersKey, PersistentDataType.STRING, gson.toJson(PercentageModifier.getDefaultData()));
        data.set(PersistentDataKeys.damageTakenModifiersKey, PersistentDataType.STRING, gson.toJson(PercentageModifier.getDefaultData()));
        data.set(PersistentDataKeys.walkSpeedModifiersKey, PersistentDataType.STRING, gson.toJson(PercentageModifier.getDefaultData()));
    }

    private PlayerSessionData(@NotNull Player player) {
        this.player = player;

        spellHandler = new SpellHandler(player);
        playerDataBoard = new PlayerDataBoard(player);
        actionBarController = new ActionBarController(player);

        mana = new Currency(player, 100, false, true);
        gems = new Currency(player, PersistentDataKeys.gemsKey, "Gems", 150, true, false);
        gold = new Currency(player, PersistentDataKeys.goldKey, "Gold", 650, true, false);
        crystals = new Currency(player, PersistentDataKeys.crystalsKey, "Crystals", 0, false, false);

        canNotJump = new ValueTracker(player, PersistentDataKeys.canNotJumpKey, "canNotJump", 0);
        invisibility = new Invisibility(player);

        coolDowns = new CoolDowns(player);
        damageDealtModifiers = new PercentageModifier(player, PersistentDataKeys.damageDealtModifiersKey, "damageDealtModifiers");
        damageTakenModifiers = new PercentageModifier(player, PersistentDataKeys.damageTakenModifiersKey, "damageTakenModifiers");
        walkSpeedModifiers = new WalkSpeed(player);
        health = new Health(player);
    }

    public Player getPlayer() {
        return player;
    }

    public SpellHandler getSpellHandler() {
        return spellHandler;
    }

    public PlayerDataBoard getPlayerDataBoard() {
        return playerDataBoard;
    }

    public ActionBarController getActionBarController() {
        return actionBarController;
    }

    public Currency getMana() {
        return mana;
    }

    public Currency getGems() {
        return gems;
    }

    public Currency getGold() {
        return gold;
    }

    public Currency getCrystals() {
        return crystals;
    }

    public ValueTracker getCanNotJump() {
        return canNotJump;
    }

    public ValueTracker getInvisibility() {
        return invisibility;
    }

    public CoolDowns getCoolDowns() {
        return coolDowns;
    }

    public PercentageModifier getDamageDealtModifiers() {
        return damageDealtModifiers;
    }

    public PercentageModifier getDamageTakenModifiers() {
        return damageTakenModifiers;
    }

    public PercentageModifier getWalkSpeedModifiers() {
        return walkSpeedModifiers;
    }

    public Health getHealth() {
        return health;
    }

    public static Map<Player, PlayerSessionData> getPlayerSessions() {
        return playerSessions;
    }

    /**
     * Saves the sessionData to the players PersistentData
     */
    public void saveSession() {
        gems.saveCurrency();
        gold.saveCurrency();
        crystals.saveCurrency();

        canNotJump.saveValue();
        invisibility.saveValue();

        coolDowns.saveCoolDowns();
        damageDealtModifiers.saveModifiers();
        damageTakenModifiers.saveModifiers();
        walkSpeedModifiers.saveModifiers();
    }

    /**
     * Saves the players sessionData and removes it from the sessionMap
     */
    public void endSession() {
        saveSession();
        spellHandler.playerLeave();
        playerDataBoard.playerNoLongerHasActiveVisibleCoolDown();
        playerSessions.remove(player);
    }

    public static void endAllSessions() {
        for (Map.Entry<Player, PlayerSessionData> playerToSessionData : playerSessions.entrySet())
            playerToSessionData.getValue().endSession();
    }
}
