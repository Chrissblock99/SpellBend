package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Health {
    private final static SpellBend plugin = SpellBend.getInstance();
    private final Player player;
    private final ArrayList<DamageEntry> damageEntries = new ArrayList<>();

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
     * @param attacker The attacking entity
     * @param rawDamage The damage dealt not modified by dmgMods
     * @param item The item used to damage
     * @return The damage dealt after modification by dmgMods (and limited to health left)
     */
    public double damagePlayer(@NotNull Entity attacker, double rawDamage, @NotNull ItemStack item) {
        if (rawDamage < 0)
            throw new IllegalArgumentException("Damage cannot be negative!");

        double damage = rawDamage;
        damage *= (attacker instanceof Player attackerPlayer) ? PlayerSessionData.getPlayerSession(attackerPlayer).getDamageDealtModifiers().getModifier(null) : 1;
        damage *= PlayerSessionData.getPlayerSession(player).getDamageTakenModifiers().getModifier(null);

        double healthBefore = getHealth();
        damageEntries.add(0, new DamageEntry(attacker,
                (getHealth()-damage <= 0) ? healthBefore : damage)); //if the health before dmg is smaller than health needed to kill use dmg as value
        double health = getHealth();

        if (health <= 0d) {
            onPlayerDeath(attacker, item);
            return healthBefore; //the health before dmg is equal to health needed to kill
        }

        player.setHealth(health);
        return damage;
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

        return getHealth();
    }

    /**
     * Kills the player and distributes Assists
     *
     * @param killer The entity that killed them
     * @param item The item used
     */
    public void onPlayerDeath(@NotNull Entity killer, @NotNull ItemStack item) {
        String message = player.getName() + " was slain by " + killer.getName() + " using " + item.getItemMeta().displayName(); //TODO use LuckPerms here ALSO implement cosmetics at some point
        for (Player playerInWorld : player.getWorld().getPlayers())
            playerInWorld.sendMessage(message);

        ArrayList<DamageEntry> uniqueAttackers = new ArrayList<>();

        for (DamageEntry damageEntry : damageEntries) {
            Entity attacker = damageEntry.getAttacker();
            boolean foundAlreadyExistingEntry = false;

            for (DamageEntry uniqueEntry : uniqueAttackers) //checking if such an attacker already is in list
                if (attacker.equals(uniqueEntry.getAttacker())) {
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
                int gold = (int) Math.ceil(10 * percentage);
                int gems = (int) Math.ceil(3 * percentage);

                uniqueAttacker.sendMessage("§e" + ((killer.equals(entry.getAttacker())) ? "Kill" : "Assist") + "! §6+" + gold + " Gold §8| §b+" + gems + " Gems");
                PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(uniqueAttacker);
                sessionData.getGold().addCurrency(gold);
                sessionData.getGems().addCurrency(gems);
            }
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(killer);
        damageEntries.clear(); //practically setting health back to max

        new BukkitRunnable() {
            @Override
            public void run() {
                //TODO tp victim to spawn here
                player.setGameMode(GameMode.ADVENTURE);
            }
        }.runTaskLater(plugin, 100);
    }

    public Player getPlayer() {
        return player;
    }
}
