package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
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

public class Ember_Blast extends Spell {
    private static final SpellBend plugin = SpellBend.getInstance();

    private BukkitTask windupTask;
    private Fireball fireball;
    private BukkitTask activeTask;

    public Ember_Blast(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{2, 0, 0, 0}));
        windup();
    }

    World world = caster.getWorld();
    private void windup() {
        windupTask = new BukkitRunnable() {
            int time = 1;

            @Override
            public void run() {
                for (int i = 0;i<48;i++) {
                    double radians = -time * MathUtil.DEGTORAD;
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
            @Override
            public void run() {
                //visuals here

                if (fireball.isDead())
                    cancel();
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void fireBallHit(@NotNull ProjectileHitEvent event) {
        if (activeTask != null)
            activeTask.cancel();

        Entity hitEntity = event.getHitEntity();
        if (hitEntity instanceof LivingEntity livingEntity && LivingEntityUtil.entityIsSpellAffectAble(livingEntity))
            LivingEntitySessionData.getLivingEntitySession(livingEntity).getHealth().damageLivingEntity(caster, 2.5, item);

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
    public void cancelSpell() {
        if (windupTask != null)
            windupTask.cancel();
        if (activeTask != null)
            activeTask.cancel();
    }
}
