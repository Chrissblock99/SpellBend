package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.util.LivingEntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LivingEntitySessionData {
    protected static final Gson gson = SpellBend.getGson();
    private static final Map<LivingEntity, LivingEntitySessionData> livingEntitySessions = new HashMap<>();

    private final LivingEntity livingEntity;

    private final MultiValueTracker jumpEffect;
    private final ValueTracker isInvisible;

    private final PercentageModifier damageDealtModifiers;
    private final PercentageModifier damageTakenModifiers;
    private final PercentageModifier walkSpeedModifiers;
    private final Health health;

    private final ValueTracker isMovementStunned;

    /**
     * Loads the livingEntities sessionData from their PersistentData, checking if it is already loaded <br>
     *      * If the given livingEntity is a player the result of loadPlayerSession() will be returned
     *
     * @throws NullPointerException When the given livingEntity is an offline player
     *
     * @param livingEntity The livingEntity whose sessionData to load
     * @return The livingEntities sessionData
     */
    public static @NotNull LivingEntitySessionData loadLivingEntitySession(@NotNull LivingEntity livingEntity) {
        if (livingEntity instanceof Player player)
            return Objects.requireNonNull(PlayerSessionData.loadPlayerSession(player));

        LivingEntitySessionData livingEntitySession = livingEntitySessions.get(livingEntity);
        if (livingEntitySession != null) {
            Bukkit.getLogger().warning(livingEntity + " is already loaded when loading their session, skipping loading!");
            return livingEntitySession;
        }

        livingEntitySession = new LivingEntitySessionData(livingEntity);

        livingEntitySessions.put(livingEntity, livingEntitySession);
        return livingEntitySession;
    }

    /**
     * Gets the livingEntities sessionData, loading it if not existent <br>
     * If the given livingEntity is a player the result of getPlayerSession() will be returned
     *
     * @throws IllegalArgumentException If the livingEntity is not spellAffectAble
     * @throws NullPointerException When the given livingEntity is an offline player
     *
     * @param livingEntity The livingEntity whose sessionData to get
     * @return The livingEntities session
     */
    public static @NotNull LivingEntitySessionData getLivingEntitySession(@NotNull LivingEntity livingEntity) {
        if (!LivingEntityUtil.entityIsSpellAffectAble(livingEntity))
            throw new IllegalArgumentException("getLivingEntitySession was called on not spell affectAble livingEntity \"" + livingEntity + "\"");
        if (livingEntity instanceof Player player)
            return Objects.requireNonNull(PlayerSessionData.getPlayerSession(player));

        LivingEntitySessionData livingEntitySession = livingEntitySessions.get(livingEntity);
        if (livingEntitySession == null) {
            Bukkit.getLogger().warning(livingEntity + " was not loaded in LivingEntitySessions map, now fixing!");
            livingEntitySession = loadLivingEntitySession(livingEntity);
        }

        return livingEntitySession;
    }

    /**
     * Sets up all the PersistentData of the livingEntity
     *
     * @param livingEntity The livingEntity whose PersistentData to set up
     */
    public static void setupLivingEntityData(@NotNull LivingEntity livingEntity) {
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();

        data.set(PersistentDataKeys.jumpEffect, PersistentDataType.INTEGER_ARRAY, new int[0]);
        data.set(PersistentDataKeys.isInvisibleKey, PersistentDataType.INTEGER, 0);

        data.set(PersistentDataKeys.damageDealtModifiersKey, PersistentDataType.STRING, gson.toJson(PercentageModifier.getDefaultData()));
        data.set(PersistentDataKeys.damageTakenModifiersKey, PersistentDataType.STRING, gson.toJson(PercentageModifier.getDefaultData()));
        data.set(PersistentDataKeys.walkSpeedModifiersKey, PersistentDataType.STRING, gson.toJson(PercentageModifier.getDefaultData()));

        data.set(PersistentDataKeys.isMovementStunnedKey, PersistentDataType.INTEGER, 0);
    }

    protected LivingEntitySessionData(@NotNull LivingEntity livingEntity) {
        this.livingEntity = livingEntity;

        jumpEffect = new JumpEffect(livingEntity);
        isInvisible = new IsInvisible(livingEntity);

        damageDealtModifiers = new PercentageModifier(livingEntity, PersistentDataKeys.damageDealtModifiersKey, "damageDealtModifiers");
        damageTakenModifiers = new PercentageModifier(livingEntity, PersistentDataKeys.damageTakenModifiersKey, "damageTakenModifiers");
        walkSpeedModifiers = new WalkSpeed(livingEntity);
        health = new Health(livingEntity);

        isMovementStunned = new IsMovementStunned(livingEntity, walkSpeedModifiers, jumpEffect);
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public MultiValueTracker getJumpEffect() {
        return jumpEffect;
    }

    public ValueTracker getIsInvisible() {
        return isInvisible;
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

    public ValueTracker getIsMovementStunned() {
        return isMovementStunned;
    }

    /**
     * Saves the sessionData to the livingEntities PersistentData
     */
    public void saveSession() {
        jumpEffect.saveValue();
        isInvisible.saveValue();

        damageDealtModifiers.saveModifiers();
        damageTakenModifiers.saveModifiers();
        walkSpeedModifiers.saveModifiers();

        isMovementStunned.saveValue();
    }

    /**
     * Saves the livingEntities sessionData and removes it from the sessionMap
     */
    public void endSession() {
        saveSession();
        livingEntitySessions.remove(livingEntity);
    }

    public static void endAllSessions() {
        for (Map.Entry<LivingEntity, LivingEntitySessionData> livingEntityToSessionData : livingEntitySessions.entrySet())
            livingEntityToSessionData.getValue().endSession();
    }
}
