package me.chriss99.spellbend.playerdata;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.DamageEntry;
import me.chriss99.spellbend.harddata.Enums;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class Health {
    private final static SpellBend plugin = SpellBend.getInstance();

    /**
     * Adds the player to PlayerSessionStorage.
     * <b>Intended to be used when the player joins.</b>
     *
     * @param player The player to register in health
     */
    public static ArrayList<DamageEntry> registerPlayer(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to register health, skipping it!");
            return null;
        }
        ArrayList<DamageEntry> damageEntries = PlayerSessionStorage.health.get(player);
        if (damageEntries != null) {
            Bukkit.getLogger().warning(player.getName() + " is already registered when registering health, skipping it!");
            return damageEntries;
        }

        damageEntries = new ArrayList<>();
        PlayerSessionStorage.health.put(player, damageEntries);
        player.setHealth(20d);
        return damageEntries;
    }

    /**
     * Calculates the health of the player from its DamageEntries
     *
     * @param player The player to get health of
     * @return The health of the player
     */
    public static double getHealth(@NotNull Player player) {
        ArrayList<DamageEntry> damageEntries = PlayerSessionStorage.health.get(player);
        if (damageEntries == null) {
            Bukkit.getLogger().warning(player.getName() + " was not registered in healthMap, now fixing!");
            damageEntries = Objects.requireNonNull(registerPlayer(player));
        }

        double health = 20d;
        for (DamageEntry entry : damageEntries)
            health -= entry.getDamage();
        return health;
    }

    /**
     * @throws IllegalArgumentException If the damage value is negative
     *
     * @param victim The attacked player
     * @param attacker The attacking entity
     * @param rawDamage The damage dealt not modified by dmgMods
     * @param item The item used to damage
     * @return The damage dealt after modification by dmgMods (and limited to health left)
     */
    public static double dmgPlayer(@NotNull Player victim, @NotNull Entity attacker, double rawDamage, @NotNull ItemStack item) {
        if (rawDamage < 0)
            throw new IllegalArgumentException("Damage cannot be negative!");
        ArrayList<DamageEntry> damageEntries = PlayerSessionStorage.health.get(victim);
        if (damageEntries == null) {
            Bukkit.getLogger().warning(victim.getName() + " was not registered in healthMap, now fixing!");
            damageEntries = Objects.requireNonNull(registerPlayer(victim));
        }

        double dmg = rawDamage;
        DmgMods.setDmgMod(Enums.DmgMod.DEALT);
        dmg *= (attacker instanceof Player player) ? DmgMods.getDmgMod(player, null) : 1;
        DmgMods.setDmgMod(Enums.DmgMod.TAKEN);
        dmg *= DmgMods.getDmgMod(victim, null);

        double healthBefore = getHealth(victim);
        damageEntries.add(0, new DamageEntry(attacker,
                (getHealth(victim)-dmg <= 0) ? healthBefore : dmg)); //if the health before dmg is smaller than health needed to kill use dmg as value
        double health = getHealth(victim);

        if (health <= 0d) {
            onPlayerDeath(victim, attacker, item);
            return healthBefore; //the health before dmg is equal to health needed to kill
        }

        victim.setHealth(health);
        return dmg;
    }

    /**
     * Heals the player up to its health limit
     *
     * @throws IllegalArgumentException If the healing amount is negative or zero
     *
     * @param player The player to heal
     * @param heal The amount of health to heal
     * @return The player's health after healing
     */
    public static double healPlayer(@NotNull Player player, double heal) {
        if (heal <= 0)
            throw new IllegalArgumentException("Healing amount cannot be negative or zero!");
        ArrayList<DamageEntry> damageEntries = PlayerSessionStorage.health.get(player);
        if (damageEntries == null) {
            Bukkit.getLogger().warning(player.getName() + " was not registered in healthMap, now fixing!");
            damageEntries = Objects.requireNonNull(registerPlayer(player));
        }

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

        return getHealth(player);
    }

    /**
     * Kills thw victim and distributes Assists
     *
     * @param victim The player who dies
     * @param killer The entity that killed him
     * @param item The item used
     */
    public static void onPlayerDeath(@NotNull Player victim, @NotNull Entity killer, @NotNull ItemStack item) {
        ArrayList<DamageEntry> damageEntries = PlayerSessionStorage.health.get(victim);
        if (damageEntries == null) {
            Bukkit.getLogger().warning(victim.getName() + " was not registered in healthMap, now fixing!");
            damageEntries = Objects.requireNonNull(registerPlayer(victim));
        }

        String msg = victim.getName() + " was slain by " + killer.getName() + " using " + item.getItemMeta().getLocalizedName(); //TODO use LuckPerms here ALSO properly get the item name AND implement cosmetics at some point
        for (Player player : victim.getWorld().getPlayers())
            player.sendMessage(msg);

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
                int experience = (int) Math.ceil(10 * percentage);
                int crystals = (int) Math.ceil(3 * percentage);

                uniqueAttacker.sendMessage("§e" + ((killer.equals(entry.getAttacker())) ? "Kill" : "Assist") + "! §6+" + experience + " Experience §8| §b+" + crystals + " Crystals");
                //Gems.addGems(uniqueAttacker, gems); TODO experience here
                //Gold.addGold(uniqueAttacker, gold); TODO crystals here
            }
        }

        victim.setGameMode(GameMode.SPECTATOR);
        victim.setSpectatorTarget(killer);
        damageEntries.clear(); //basically setting health back to max

        new BukkitRunnable() {
            @Override
            public void run() {
                //TODO tp victim to spawn here
                victim.setGameMode(GameMode.ADVENTURE);
            }
        }.runTaskLater(plugin, 100);
    }

    /**
     * Removes the players health from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose to deRegister from Health
     */
    public static void deRegisterPlayer(@NotNull Player player) {
        if (!PlayerSessionStorage.health.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in healthMap when deRegistering, skipping it!");
            return;
        }

        PlayerSessionStorage.health.remove(player);
    }
}