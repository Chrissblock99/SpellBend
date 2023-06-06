package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import me.chriss99.spellbend.util.math.RotationUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Ember_Blast extends Spell {
    private static final SpellBend plugin = SpellBend.getInstance();

    private BukkitTask windupTask;
    private Fireball fireball;
    private BukkitTask activeTask;

    public Ember_Blast(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0.75f, 0, 5, 7}));
        windup();
    }

    World world = caster.getWorld();
    private void windup() {
        windupTask = new BukkitRunnable() {
            int time = 1;

            @Override
            public void run() {
                for (int i = 0;i<48;i++) {
                    double radians = Math.toRadians(-time);
                    Location location = caster.getEyeLocation().clone();

                    location.add(RotationUtil.rotateVectorAroundLocationRotation(new Vector(Math.cos(radians) * -1.25, Math.sin(radians) * 1.25, 1), location));
                    world.spawnParticle(Particle.FLAME, location, 1, 0.02, 0.02, 0.02, 0);

                    time++;
                }

                Location location = caster.getLocation();
                world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1 + (time/720f));
                world.playSound(location, Sound.ENTITY_BLAZE_AMBIENT, 1f, 1 + (time/720f));

                if (time >= 720) {
                    windupTask.cancel();
                    activate();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void activate() {
        fireball = world.spawn(caster.getEyeLocation().add(caster.getEyeLocation().getDirection()), Fireball.class,
                projectile -> projectile.setVelocity(caster.getEyeLocation().getDirection().clone().multiply(1.35)),CreatureSpawnEvent.SpawnReason.CUSTOM);
        SpellHandler.addProjectileConsumer(fireball, this::fireBallHit);

        Location location = fireball.getLocation();
        world.playSound(location, Sound.ENTITY_BLAZE_HURT, 3f, 0.5f);
        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 3f, 1f);

        activateLoop();
    }

    private void activateLoop() {
        activeTask = new BukkitRunnable() {
            int time = 1;
            Location lastLocation = caster.getEyeLocation();
            @Override
            public void run() {
                for (int i = 0; i < 6; i += 2) {
                    Color color;
                    if (time % 2 == 0)
                        color = Colors.orange3;
                    else color = Colors.orange4;

                    double radians = Math.toRadians((time * 4) % 360);
                    Vector vector = RotationUtil.rotateVectorAroundVectorRotation(new Vector(Math.cos(radians), Math.sin(radians), 0), fireball.getVelocity());
                    Location location = MathUtil.lerpVector(lastLocation.toVector(), fireball.getLocation().toVector(), i/6f).add(vector).toLocation(world);
                    world.spawnParticle(Particle.REDSTONE, location, 1, new Particle.DustOptions(color, 2.5f));

                    //TODO Alloyed_Barrier reflection here
                    //loop-value is divisible by 9
                    //  loop zombies in radius 5 of {_e} where [input's name contains "'s <##808080>&lShield"]:
                    //    ({_c} ? 2 second ago) was more than 1 second ago
                    //    set {_c} to now
                    //    push {_e} direction from loop-value-2 to {_e} at speed 1.35
                    //    draw 5 crit at loop-value-2 with extra 0
                    //    draw 1 sweep attack 1.3 above loop-value-2
                    //    play sound "entity.iron_golem.repair" and "enchant.thorns.hit" at volume 3 at pitch 0.8 at loop-value-2

                    time += 2;
                }

                if (fireball.isDead() || time/120f > 5f)
                    fireBallHit();

                lastLocation = fireball.getLocation();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void fireBallHit(@NotNull ProjectileHitEvent event) {
        fireBallHit();
    }

    private void fireBallHit() {
        fireball.remove();
        if (activeTask != null) {
            if (activeTask.isCancelled())
                return;
            activeTask.cancel();
            coolDown.skipToStage(CoolDownStage.COOLDOWN);
        }

        List<LivingEntity> hitEntities = new ArrayList<>(LivingEntityUtil.getSpellAffectAbleEntitiesNearLocation(fireball.getLocation(), 3).keySet());
        hitEntities.remove(caster);
        Player onlineCasterOrNull = caster.isOnline() ? caster : null;
        for (LivingEntity hitEntity : hitEntities) {
            LivingEntitySessionData.getLivingEntitySession(hitEntity).getHealth().damageLivingEntity(onlineCasterOrNull, 3.5, item);
            LivingEntityUtil.igniteLivingEntity(hitEntity);
        }

        Location location = fireball.getLocation();
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();

        meta.setPower(2);
        meta.addEffect(FireworkEffect.builder().withColor(Color.RED, Color.ORANGE, Color.YELLOW).build());

        firework.setFireworkMeta(meta);
        firework.detonate();

        SpellHandler.removeProjectileConsumer(fireball);
        naturalSpellEnd();
    }

    @Override
    public void casterStun(int timeInTicks) {
        if (coolDown.getCoolDownStage().equals(CoolDownStage.WINDUP))
            super.casterStun(timeInTicks);
    }

    @Override
    public void casterDeath(@Nullable LivingEntity killer) {
        if (coolDown.getCoolDownStage().equals(CoolDownStage.WINDUP))
            super.casterDeath(killer);
    }

    @Override
    public void casterLeave() {
        if (coolDown.getCoolDownStage().equals(CoolDownStage.WINDUP))
            super.casterLeave();
    }

    @Override
    public void cancelSpell() {
        if (windupTask != null)
            windupTask.cancel();
        if (activeTask != null)
            activeTask.cancel();
        if (fireball != null)
            fireball.remove();
    }
}
