package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.ElementEnum;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerSessionData extends LivingEntitySessionData {
    private static final Map<Player, PlayerSessionData> playerSessions = new HashMap<>();

    private final Player player;

    private final SpellHandler spellHandler;
    private final PlayerDataBoard playerDataBoard;
    private final ActionBarController actionBarController;

    private final CurrencyTracker mana;
    private final CurrencyTracker gems;
    private final CurrencyTracker gold;
    private final CurrencyTracker crystals;

    private final ElementsOwned elementsOwned;

    private final CoolDowns coolDowns;

    public static void startManaRegenerator() {
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Map.Entry<Player, PlayerSessionData> entry : playerSessions.entrySet()) {
                    CurrencyTracker mana = entry.getValue().getMana();
                    mana.setCurrency(Math.min(100, mana.getCurrency()+5));
                }
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 20);
    }

    /**
     * Loads the players sessionData from their PersistentData, checking if it is already loaded or if the player isn't online
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
        setupLivingEntityData(player);
        PersistentDataContainer data = player.getPersistentDataContainer();

        data.set(PersistentDataKeys.GEMS_KEY, PersistentDataType.FLOAT, 150f);
        data.set(PersistentDataKeys.GOLD_KEY, PersistentDataType.FLOAT, 650f);
        data.set(PersistentDataKeys.CRYSTALS_KEY, PersistentDataType.FLOAT, 0f);

        data.set(PersistentDataKeys.ELEMENTS_OWNED_KEY, PersistentDataType.STRING, gson.toJson(new EnumMap<>(ElementEnum.class)));

        data.set(PersistentDataKeys.COOLDOWNS_KEY, PersistentDataType.STRING, gson.toJson(new HashMap<String, CoolDownEntry>()));
    }

    private PlayerSessionData(@NotNull Player player) {
        super(player);
        this.player = player;

        spellHandler = new SpellHandler(player);
        playerDataBoard = new PlayerDataBoard(player);
        actionBarController = new ActionBarController(player);

        mana = new CurrencyTracker(player, 100, false, true);
        gems = new CurrencyTracker(player, PersistentDataKeys.GEMS_KEY, "Gems", 150, true, false);
        gold = new CurrencyTracker(player, PersistentDataKeys.GOLD_KEY, "Gold", 650, true, false);
        crystals = new CurrencyTracker(player, PersistentDataKeys.CRYSTALS_KEY, "Crystals", 0, false, false);

        elementsOwned = new ElementsOwned(player);

        coolDowns = new CoolDowns(player);
    }

    @Override
    public void stunEntity(int timeInTicks) {
        super.stunEntity(timeInTicks);
        spellHandler.stunPlayer(timeInTicks);
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

    public CurrencyTracker getMana() {
        return mana;
    }

    public CurrencyTracker getGems() {
        return gems;
    }

    public CurrencyTracker getGold() {
        return gold;
    }

    public CurrencyTracker getCrystals() {
        return crystals;
    }

    public ElementsOwned getElementsOwned() {
        return elementsOwned;
    }

    public CoolDowns getCoolDowns() {
        return coolDowns;
    }

    public static Map<Player, PlayerSessionData> getPlayerSessions() {
        return playerSessions;
    }

    /**
     * Saves the sessionData to the players PersistentData
     */
    @Override
    public void saveSession() {
        super.saveSession();
        gems.saveCurrency();
        gold.saveCurrency();
        crystals.saveCurrency();

        elementsOwned.saveElementsOwned();

        coolDowns.saveCoolDowns();
    }

    /**
     * Saves the players sessionData and removes it from the sessionMap
     */
    @Override
    public void endSession(boolean pluginDisable) {
        if (pluginDisable)
            spellHandler.endSpellActivity();
        else spellHandler.playerLeave();

        playerDataBoard.stopDisplayCooldown();
        super.endSession(pluginDisable);
        playerSessions.remove(player);
    }
}
