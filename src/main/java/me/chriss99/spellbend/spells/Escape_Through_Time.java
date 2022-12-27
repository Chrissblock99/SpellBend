package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.util.PlayerUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Escape_Through_Time extends Spell implements Killable {
    private final Escape_Through_Time instance;
    private final Location armorStandOrigin;
    private BukkitTask armorStandTask;
    private BukkitTask escapeTask;
    private final CoolDownEntry coolDown;
    private ArmorStand armorStand;
    private final SpellHandler spellHandler;

    public static void register() {
        SpellHandler.registerSpell("escape_through_time", 25, new SpellSubClassBuilder() {
            @Override
            public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
                return new Escape_Through_Time(caster, spellType, item);
            }
        });
    }

    public Escape_Through_Time(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "TRANSPORT", item);
        instance = this;
        coolDown = PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(super.spellType, new float[]{0, 0, 15, 5});
        armorStandOrigin = caster.getLocation();
        setupArmorStand();
        spellHandler = PlayerSessionData.getPlayerSession(caster).getSpellHandler();
        spellHandler.addClickableSpellRunnable(item, this::escapeTeleport);
    }

    private void setupArmorStand() {
        armorStand = caster.getWorld().spawn(armorStandOrigin, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 99999, 255, false, false));

        for (int i = 0; i < 360; i += 10) {
            Color color = Colors.getRandomYellow1or2();
            armorStandOrigin.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, armorStand.getLocation().clone().add(Math.cos(i * MathUtil.DEGTORAD) * 1.5f, 0, Math.sin(i * MathUtil.DEGTORAD) * 1.5f),
                    1, 0, 0, 0, 0,
                    new Particle.DustTransition(color, color, 2));
    }

        armorStandTask = new BukkitRunnable() {
            int time = 300;

            @Override
            public void run() {
                armorStand.teleport(armorStandOrigin.clone().add(0, -Math.sin(time/13.64f)/3f, 0));
                armorStandOrigin.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, armorStand.getLocation().clone().add(0, 1, 0), 1, 0.5, 0.6, 0.5, 0,
                        new Particle.DustTransition(Colors.yellow2, Colors.yellow2, 2));

                if (time == 0) {
                    spellHandler.removeClickableSpellRunnable(item);
                    explode();

                    armorStandTask.cancel();
                    spellHandler.getActivePlayerSpells().remove(instance);
                }
                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void escapeTeleport() {
        spellHandler.removeClickableSpellRunnable(item);

        escapeTask = new BukkitRunnable() {
            final Location casterOrigin = caster.getLocation().clone();
            int time = 20;

            @Override
            public void run() {
                caster.teleport(new Location(caster.getWorld(), 0, 0, 0, casterOrigin.getYaw(), casterOrigin.getPitch()).add(MathUtil.lerpVector(casterOrigin.toVector(), armorStandOrigin.toVector(), Math.pow(time/20f, 0.333333333))));
                caster.getWorld().spawnParticle(Particle.SMOKE_LARGE, caster.getLocation().add(0 , 1.3, 0), 5, 0, 0, 0, 0.5);
                caster.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, caster.getLocation().add(0 , 0.8, 0), 5, 0, 0, 0, 0);

                if (time == 0) {
                    armorStand.remove();

                    escapeTask.cancel();
                    armorStandTask.cancel();
                    coolDown.skipToStage(Enums.CoolDownStage.COOLDOWN);
                    spellHandler.getActivePlayerSpells().remove(instance);
                }
                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void explode() {
        Map<Player, Double> players = PlayerUtil.getPlayersNearLocation(armorStandOrigin, 4.5);
        players.remove(caster);
        for (Map.Entry<Player, Double> entry : players.entrySet())
            PlayerSessionData.getPlayerSession(entry.getKey()).getHealth().damagePlayer(caster, 3, item);

        armorStandOrigin.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, armorStandOrigin.add(0, 1, 0), 1);
        armorStand.remove();
    }

    @Override
    public void casterDeath(@Nullable Entity Killer) {
        cancelSpell();
    }

    @Override
    public void casterLeave() {
        cancelSpell();
    }

    @Override
    public void cancelSpell() {
        if (coolDown != null)
            coolDown.skipToStage(Enums.CoolDownStage.COOLDOWN);
            //only needed for extreme edge cases that currently (25.12.2022) never happen in the code
        else Bukkit.getLogger().warning(caster.getName() + " had Escape Through Time Active but no corresponding CoolDown of type " + spellType + " was found!");

        spellHandler.removeClickableSpellRunnable(item);
        armorStandTask.cancel();
        if (escapeTask != null)
            escapeTask.cancel();
        explode();
    }
}
