package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.ParticleUtil;
import me.chriss99.spellbend.util.math.RotationUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Magma_Burst extends Spell {
    private static final SpellBend plugin = SpellBend.getInstance();

    private BukkitTask windupTask;
    private BukkitTask activeTask;
    private BukkitTask fireballVisualsTask;
    private final LinkedHashMap<SmallFireball, Location> fireballs = new LinkedHashMap<>();

    public Magma_Burst(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0.75f, 1, 0, 7}));
        windup();
    }

    final World world = caster.getWorld();
    private void windup() {
        windupTask = new BukkitRunnable() {
            int time = 1;

            @Override
            public void run() {
                for (int i = 0;i<72;i++) {
                    double radians = Math.toRadians(-time);
                    Location location = caster.getEyeLocation().clone();

                    location.add(RotationUtil.rotateVectorAroundLocationRotation(new Vector(Math.cos(radians) * -1, Math.sin(radians) * 1, 1), location));
                    world.spawnParticle(Particle.REDSTONE, location, 1, 0.02, 0.02, 0.02, 0,
                            new Particle.DustOptions(Colors.getRandomOrange1or2(), 1));

                    time++;
                }

                Location location = caster.getLocation();
                world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1 + (time/1080f));
                world.playSound(location, Sound.BLOCK_LAVA_POP, 2f, 1 + (time/1080f));

                if (time >= 1080) {
                    windupTask.cancel();
                    activate();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void activate() {
        activeTask = new BukkitRunnable() {
            int time = 1;

            @Override
            public void run() {
                SmallFireball fireball = world.spawn(caster.getEyeLocation().add(caster.getEyeLocation().getDirection()), SmallFireball.class,
                        projectile -> {
                            projectile.setVelocity(caster.getEyeLocation().getDirection());
                            projectile.setIsIncendiary(false);
                            fireballs.put(projectile, projectile.getLocation());
                        }, CreatureSpawnEvent.SpawnReason.CUSTOM);
                SpellHandler.addProjectileConsumer(fireball, event -> fireballHit(event));

                world.playSound(caster.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2f, 1 + (time/30f));

                if (time >= 20) {
                    activeTask.cancel();
                    world.playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1);
                }

                time++;
            }
        }.runTaskTimer(plugin, 0, 1);
        fireballVisuals();
    }

    private void fireballVisuals() {
        fireballVisualsTask = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run()  {
                for (SmallFireball fireball : new LinkedList<>(fireballs.keySet())) {
                    if (fireball.isDead()) {
                        fireballHit(fireball, null);
                        continue;
                    }

                    ParticleUtil.drawLine(world, fireballs.get(fireball).toVector(), fireball.getLocation().toVector(), 10, Particle.REDSTONE,
                            new Particle.DustOptions(Color.fromRGB(255, 165, 0), 0.5f));
                    fireballs.put(fireball, fireball.getLocation().clone());
                }

                if (time >= 6*20)
                    for (SmallFireball fireball : new LinkedList<>(fireballs.keySet())) {
                        fireball.remove();
                        fireballHit(fireball, null);
                    }

                if (fireballs.isEmpty()) {
                    fireballVisualsTask.cancel();
                    naturalSpellEnd();
                }

                time++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void fireballHit(@NotNull ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof SmallFireball fireball))
            return;

        fireballHit(fireball, event.getHitEntity());
    }

    public void fireballHit(@NotNull SmallFireball fireball, @Nullable Entity hitEntity) {
        SpellHandler.removeProjectileConsumer(fireball);
        fireballs.remove(fireball);
        world.spawnParticle(Particle.LAVA, fireball.getLocation(), 1);
        if (!LivingEntityUtil.entityIsSpellAffectAble(hitEntity))
            return;

        LivingEntitySessionData.getLivingEntitySession((LivingEntity) hitEntity).getHealth().damageLivingEntity((caster.isOnline()) ? caster : null, 3, item);
    }

    @Override
    public void casterStun(int timeInTicks) {
        possiblyEndSpell();
        coolDown.skipToStage(CoolDownStage.COOLDOWN);
    }

    @Override
    public void casterDeath(@Nullable LivingEntity killer) {
        possiblyEndSpell();
        coolDown.skipToStage(CoolDownStage.COOLDOWN);
    }

    @Override
    public void casterLeave() {
        possiblyEndSpell();
        coolDown.transformToStage(CoolDownStage.COOLDOWN);
    }

    private void possiblyEndSpell() {
        if (coolDown.getCoolDownStage().equals(CoolDownStage.WINDUP)) {
            cancelSpell();
            naturalSpellEnd();
        }

        if (coolDown.getCoolDownStage().equals(CoolDownStage.ACTIVE)) {
            world.playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1);
            if (activeTask != null)
                activeTask.cancel();
        }
    }

    @Override
    public void cancelSpell() {
        if (windupTask != null)
            windupTask.cancel();
        if (activeTask != null)
            activeTask.cancel();
        if (fireballVisualsTask != null)
            fireballVisualsTask.cancel();

        for (SmallFireball fireball : fireballs.keySet())
            SpellHandler.removeProjectileConsumer(fireball);
    }
}