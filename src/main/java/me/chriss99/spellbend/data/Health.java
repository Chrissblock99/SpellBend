package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Health {
    private final static SpellBend plugin = SpellBend.getInstance();
    public final static long iFrameTimeInMS = 500;
    private final Player player;
    private final List<DamageEntry> damageEntries = new ArrayList<>();
    private Date iFrameEnd = new Date();
    private double iFrameDamage = 0;

    public Health(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Calculates the health of the player from its DamageEntries
     *
     * @return The health of the player
     */
    public double getHealth() {
        double health = 20d;
        for (DamageEntry entry : damageEntries)
            health -= entry.getDamage();
        return health;
    }

    /**
     * @throws IllegalArgumentException If the damage value is negative
     *
     * @param attacker The attacking entity (null if undefined)
     * @param rawDamage The damage dealt not modified by dmgMods
     * @param item The item used to damage (null if undefined)
     * @return The damage dealt after modification by dmgMods and iFrameDamage while being limited to health left
     */
    public double damagePlayer(@Nullable Entity attacker, double rawDamage, @Nullable ItemStack item) {
        if (rawDamage < 0)
            throw new IllegalArgumentException("Damage cannot be negative!");

        Date currentTime = new Date();
        boolean activeIFrame = currentTime.getTime() < iFrameEnd.getTime();

        double damage = rawDamage;
        damage *= (attacker instanceof Player attackerPlayer) ? PlayerSessionData.getPlayerSession(attackerPlayer).getDamageDealtModifiers().getModifier() : 1;
        damage *= PlayerSessionData.getPlayerSession(player).getDamageTakenModifiers().getModifier();

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

        player.setHealth(health);
        PlayerSessionData.getPlayerSession(player).getActionBarController().updateBar();
        return damage;
    }

    /**
     * Takes in a value and passes it to healPlayer() or damagePlayer() respectively <br>
     * <b>as this cannot be traced back to a dmg cause, it is discouraged to use this</b>
     *
     * @param health the health to displace by
     */
    public void displaceHealth(double health) {
        if (health == 0)
            return;
        if (health < 0)
            damagePlayer(null, health*(-1), null);
        else healPlayer(health);
    }

    /**
     * Heals the player up to its health limit
     *
     * @throws IllegalArgumentException If the healing amount is negative or zero
     *
     * @param heal The amount of health to heal
     * @return The player's health after healing
     */
    public double healPlayer(double heal) {
        if (heal <= 0)
            throw new IllegalArgumentException("Healing amount cannot be negative or zero!");

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
        StringBuilder message = new StringBuilder("§8[§c☠§8] §e§l" + player.getName() + "§r§c");
        switch ((killer != null) + "-" + (item != null)) {
            case "true-true" -> //noinspection ConstantConditions
                    message.append(" was slain by §e§l").append(killer.getName()).append("§r§c using ").append(item.getItemMeta().getLocalizedName());
            case "true-false" -> //noinspection ConstantConditions
                    message.append(" was slain by §e§l").append(killer.getName());
            case "false-true" -> //noinspection ConstantConditions
                    message.append(" died to").append(item.getItemMeta().getLocalizedName());
            case "false-false" -> message.append(" died");
        }

        for (Player playerInWorld : player.getWorld().getPlayers())
            playerInWorld.sendMessage(message.toString());

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
            if (entry.getAttacker() instanceof Player uniqueAttacker) {
                double percentage = entry.getDamage() / 20d;
                float gold = (float) (10 * percentage);
                float gems = (float) (3 * percentage);
                float health = (float) (5 * percentage);

                uniqueAttacker.sendMessage("§e" + ((killer != null && killer.equals(entry.getAttacker())) ? "Kill" : "Assist") + "! §6+" + gold + " Gold §8| §b+" + gems + " Gems §8| §c+" + health + " Health");
                PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(uniqueAttacker);
                sessionData.getGold().addCurrency(gold);
                sessionData.getGems().addCurrency(gems);
                sessionData.getHealth().healPlayer(health);
            }
        }

        player.setGameMode(GameMode.SPECTATOR);
        if (killer != null)
            player.setSpectatorTarget(killer);
        damageEntries.clear(); //practically setting health back to max

        new BukkitRunnable() {
            @Override
            public void run() {
                //TODO tp victim to spawn here
                player.setGameMode(GameMode.ADVENTURE);
            }
        }.runTaskLater(plugin, 100);

        PlayerSessionData.getPlayerSession(player).getActionBarController().updateBar();
    }

    /**
     * Gets the current system time and compares it to iFrameEnd
     *
     * @return If an iFrame is active right now
     */
    public boolean activeIFrame() {
        return new Date().getTime() < iFrameEnd.getTime();
    }

    public Player getPlayer() {
        return player;
    }
}
