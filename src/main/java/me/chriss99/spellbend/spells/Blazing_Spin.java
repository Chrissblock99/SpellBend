package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Blazing_Spin extends Spell {
    private static final SpellBend plugin = SpellBend.getInstance();

    private final PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(caster);
    private BukkitTask windupTask;
    private BukkitTask activeTask;

    public Blazing_Spin(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0.75f, 2, 0, 7}));
        windup();
    }

    World world = caster.getWorld();
    private void windup() {
        sessionData.getWalkSpeedModifiers().addModifier(0.55f);
        windupTask = new BukkitRunnable() {
            int time = 1;
            @Override
            public void run() {
                float radius = 0.5f + time*0.5f;
                for (int i = 0; i < 360; i++) {
                    double radians = i * MathUtil.DEGTORAD;
                    Vector circlePos = new Vector(Math.cos(radians) * radius, 1, Math.sin(radians) * radius);
                    world.spawnParticle(Particle.REDSTONE, caster.getLocation().add(circlePos), 1, 0, 0, 0, 0,
                            new Particle.DustOptions(Colors.getRandomOrange3or4(), time*0.15f));
                }
                Location location = caster.getLocation().add(0, 1, 0);
                world.spawnParticle(Particle.FLAME, location, time, 0, 0, 0, 0.05);
                world.spawnParticle(Particle.SMOKE_LARGE, location, time, 0, 0, 0, 0.05);
                world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 3f, 1 + time*0.1f);

                if (time >= 5) {
                    windupTask.cancel();
                    sessionData.getWalkSpeedModifiers().removeModifier(0.55f);
                    active();
                }

                time++;
            }
        }.runTaskTimer(plugin, 0, 3);
    }

    private void active() {
        sessionData.getIsMovementStunned().displaceValue(1);
        activeTask = new BukkitRunnable() {
            int time = 1;
            int sub = 1;
            Location prePushLocation = caster.getLocation().add(0, 1, 0);

            @Override
            public void run() {
                if (sub == 1) {
                    var nearByEnemies = LivingEntityUtil.getSpellAffectAbleEntitiesNearLocation(caster.getLocation().add(0, 1, 0), 6);
                    nearByEnemies.remove(caster);
                    for (Map.Entry<LivingEntity, Double> livingEntityToDistance : nearByEnemies.entrySet())
                        LivingEntitySessionData.getLivingEntitySession(livingEntityToDistance.getKey()).getHealth()
                                .damageLivingEntity(caster, 6f/Math.ceil(Math.sqrt(livingEntityToDistance.getValue())), item);
                    prePushLocation = caster.getLocation().add(0, 1, 0);
                    caster.setVelocity(caster.getVelocity().add(
                            caster.getEyeLocation().getDirection().rotateAroundY((Math.PI/2) * ((time%2 == 0) ? 1 : -1)).multiply(0.5)));
                }

                float radius = sub * 1.2f;
                for (int i = 0; i < 360; i++) {
                    double radians = i * MathUtil.DEGTORAD;
                    Vector circlePos = new Vector(Math.cos(radians) * radius, 1, Math.sin(radians) * radius);
                    world.spawnParticle(Particle.REDSTONE, caster.getLocation().add(circlePos), 1, 0, 0, 0, 0,
                            new Particle.DustOptions((time%2 == 0) ? Colors.orange1 : Colors.orange2, 0.75f));
                }
                world.playSound(caster.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2, 1.2f + sub*0.1f);

                if (time >= 5) {
                    activeTask.cancel();
                    world.playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 1);
                    sessionData.getIsMovementStunned().displaceValue(-1);
                    naturalSpellEnd();
                }

                if (sub >= 3) {
                    sub = 1;
                    time++;
                    return;
                }

                sub++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void cancelSpell() {
        if (windupTask != null && !windupTask.isCancelled()) {
            windupTask.cancel();
            sessionData.getWalkSpeedModifiers().removeModifier(0.55f);
        }

        if (activeTask != null && !activeTask.isCancelled()) {
            activeTask.cancel();
            world.playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 1);
            sessionData.getIsMovementStunned().displaceValue(-1);
        }
    }
}