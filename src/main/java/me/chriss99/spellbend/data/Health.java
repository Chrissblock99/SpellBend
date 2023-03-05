package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class Health {
    private final static SpellBend plugin = SpellBend.getInstance();
    public final static long iFrameTimeInMS = 500;

    private final LivingEntity livingEntity;
    private final double maxHealth;
    private final List<DamageEntry> damageEntries = new ArrayList<>();
    private Date iFrameEnd = new Date();
    private double iFrameDamage = 0;

    public Health(@NotNull LivingEntity livingEntity) {
        this.livingEntity = livingEntity;

        AttributeInstance attributeInstance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attributeInstance != null) {
            maxHealth = attributeInstance.getDefaultValue(); //TODO //HACK this does not account for the max health to change LOL!
            return;
        }

        Bukkit.getLogger().log(Level.WARNING, "The livingEntity \"" + livingEntity + "\" did not have a GENERIC_MAX_HEALTH attribute! Setting to 20!");
        maxHealth = 20;
    }

    /**
     * Calculates the livingEntity of the player from its DamageEntries
     *
     * @return The health of the livingEntity
     */
    public double getHealth() {
        double health = maxHealth;
        for (DamageEntry entry : damageEntries)
            health -= entry.getDamage();
        return health;
    }

    /**
     * @throws IllegalArgumentException If the damage value is negative
     *
     * @param attacker The attacking livingEntity (null if undefined)
     * @param rawDamage The damage dealt not modified by dmgMods
     * @param item The item used to damage (null if undefined)
     * @return The damage dealt after modification by dmgMods and iFrameDamage while being limited to health left
     */
    public double damageLivingEntity(@Nullable LivingEntity attacker, double rawDamage, @Nullable ItemStack item) {
        if (rawDamage < 0)
            throw new IllegalArgumentException("Damage cannot be negative!");

        Date currentTime = new Date();
        boolean activeIFrame = currentTime.getTime() < iFrameEnd.getTime();

        double damage = rawDamage;
        damage *= (attacker instanceof Player attackerPlayer) ? PlayerSessionData.getPlayerSession(attackerPlayer).getDamageDealtModifiers().getModifier() : 1;
        damage *= PlayerSessionData.getLivingEntitySession(livingEntity).getDamageTakenModifiers().getModifier();

        if (activeIFrame) {
            damage -= iFrameDamage;
            if (damage <= 0)
                return 0;
        } else {
            iFrameEnd = new Date(currentTime.getTime() + iFrameTimeInMS);
            iFrameDamage = damage;
        }

        double healthBefore = getHealth();
        damageEntries.add(0, new DamageEntry(attacker,
                (getHealth()-damage <= 0) ? healthBefore : damage)); //if the health before dmg is smaller than health needed to kill use dmg as value
        double health = getHealth();

        if (health <= 0d) {
            onPlayerDeath(attacker, item);
            return healthBefore; //the health before dmg is equal to health needed to kill
        }

        livingEntity.setHealth(health);
        if (livingEntity instanceof Player player)
            PlayerSessionData.getPlayerSession(player).getActionBarController().updateBar();
        return damage;
    }

    /**
     * Passes the damage to healLivingEntity() or damageLivingEntity() respectively
     *
     * @param health The health to displace by
     * @param attacker The displacing livingEntity
     * @param item The item used to displace
     */
    public void displaceHealth(double health, @Nullable LivingEntity attacker, @Nullable ItemStack item) {
        if (health == 0)
            return;
        if (health < 0)
            damageLivingEntity(attacker, health*(-1), item);
        else healLivingEntity(health);
    }

    /**
     * Heals the livingEntity up to its health limit
     *
     * @throws IllegalArgumentException If the healing amount is negative
     *
     * @param heal The amount of health to heal
     * @return The player's health after healing
     */
    public double healLivingEntity(double heal) {
        if (heal < 0)
            throw new IllegalArgumentException("Healing amount cannot be negative or zero!");
        if (heal == 0)
            return getHealth();

        int lastIndex;
        while (true) {
            if (damageEntries.size() == 0)
                break;
            lastIndex = damageEntries.size()-1;

            damageEntries.get(lastIndex).setDamage(damageEntries.get(lastIndex).getDamage()-heal);
            heal = damageEntries.get(lastIndex).getDamage();
            if (heal > 0)
                break;
            damageEntries.remove(lastIndex);
            if (heal == 0)
                break;
            heal *= -1;
        }

        if (livingEntity instanceof Player player)
            PlayerSessionData.getPlayerSession(player).getActionBarController().updateBar();
        return getHealth();
    }

    /**
     * Kills the player and distributes Assists
     *
     * @param killer The entity that killed them
     * @param item The item used
     */
    public void onPlayerDeath(@Nullable Entity killer, @Nullable ItemStack item) {
        //TODO use LuckPerms here ALSO implement cosmetics at some point
        StringBuilder messageBuilder = new StringBuilder("<dark_grey>[<red>☠<dark_grey>] <yellow><bold>" + livingEntity.getName() + "<reset><red>");
        switch ((killer != null) + "-" + (item != null)) {
            case "true-true" -> //noinspection ConstantConditions
                    messageBuilder.append(" was slain by <yellow><bold>").append(killer.getName()).append("<reset><red> using ").append(SpellBend.getMiniMessage().serialize(item.getItemMeta().displayName()));
            case "true-false" -> //noinspection ConstantConditions
                    messageBuilder.append(" was slain by <yellow><bold>").append(killer.getName());
            case "false-true" -> //noinspection ConstantConditions
                    messageBuilder.append(" died to").append(SpellBend.getMiniMessage().serialize(item.getItemMeta().displayName()));
            case "false-false" -> messageBuilder.append(" died");
        }

        Component message = SpellBend.getMiniMessage().deserialize(messageBuilder.toString());
        for (Player playerInWorld : livingEntity.getWorld().getPlayers())
            playerInWorld.sendMessage(message);

        List<DamageEntry> uniqueAttackers = new ArrayList<>();

        for (DamageEntry damageEntry : damageEntries) {
            Entity attacker = damageEntry.getAttacker();
            boolean foundAlreadyExistingEntry = false;

            for (DamageEntry uniqueEntry : uniqueAttackers) //checking if such an attacker already is in list, also skipping all nulls
                if (attacker != null && attacker.equals(uniqueEntry.getAttacker())) {
                    uniqueEntry.setDamage(uniqueEntry.getDamage() + damageEntry.getDamage());
                    foundAlreadyExistingEntry = true;
                    break;
                }
            if (!foundAlreadyExistingEntry)
                uniqueAttackers.add(damageEntry);
        }

        for (DamageEntry entry : uniqueAttackers) {
            LivingEntity attacker = entry.getAttacker();
            if (attacker == null)
                continue;

            double percentage = entry.getDamage() / maxHealth;
            float health = (float) (5 * percentage);

            if (attacker instanceof Player uniqueAttacker) {
                float gold = (float) (10 * percentage);
                float gems = (float) (3 * percentage);

                uniqueAttacker.sendMessage(SpellBend.getMiniMessage().deserialize("<yellow>" + ((killer != null && killer.equals(entry.getAttacker())) ? "Kill" : "Assist") + "! <gold>+" + gold + " Gold <dark_grey>| <aqua>+" + gems + " Gems <dark_grey>| <red>+" + health + " Health"));
                PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(uniqueAttacker);
                sessionData.getGold().addCurrency(gold);
                sessionData.getGems().addCurrency(gems);
            }

            LivingEntitySessionData.getLivingEntitySession(attacker).getHealth().healLivingEntity(health);
        }

        damageEntries.clear(); //practically setting health back to max

        if (livingEntity instanceof Player player) {
            player.setGameMode(GameMode.SPECTATOR);
            if (killer != null)
                player.setSpectatorTarget(killer);

            new BukkitRunnable() {
                @Override
                public void run() {
                    //TODO tp victim to spawn here
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }.runTaskLater(plugin, 100);

            PlayerSessionData.getPlayerSession(player).getActionBarController().updateBar();
        }
    }

    /**
     * Gets the current system time and compares it to iFrameEnd
     *
     * @return If an iFrame is active right now
     */
    public boolean activeIFrame() {
        return new Date().getTime() < iFrameEnd.getTime();
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }
}
