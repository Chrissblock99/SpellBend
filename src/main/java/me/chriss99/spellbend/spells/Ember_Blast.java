package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;

public class Ember_Blast extends Spell {
    private BukkitTask windupTask;
    private Fireball fireball;

    public Ember_Blast(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{2, 0, 0, 0}));
        windup();
    }

    private void windup() {
        windupTask = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                for (int i = 0;i<3;i++) {
                    /*double radians = time * MathUtil.DEGTORAD;
                    Location location = caster.getEyeLocation();
                    Location location1 = location.clone();
                    Vector vector = new Vector(Math.cos(radians) * 1.25, Math.sin(radians) * 1.25, 1);
                    vector.rotateAroundY(location.getYaw()*MathUtil.DEGTORAD*(-1));

                    Vector sideVec = new Vector(1, 0, 0).rotateAroundY(location.getYaw()*MathUtil.DEGTORAD*(-1));
                    Location locClone = location.clone().add(sideVec);
                    caster.getWorld().spawnParticle(Particle.BUBBLE_POP, locClone, 1, 0.02, 0.02, 0.02, 0);

                    Vector rotatedAroundSideVec = new Vector(0, 0, 1).rotateAroundNonUnitAxis(sideVec, location.getPitch()*MathUtil.DEGTORAD);
                    Location locClone2 = locClone.clone().add(rotatedAroundSideVec);
                    caster.getWorld().spawnParticle(Particle.HEART, locClone, 1, 0.02, 0.02, 0.02, 0);

                    vector.rotateAroundNonUnitAxis(sideVec, location.getPitch()*MathUtil.DEGTORAD);
                    location.add(vector);

                    caster.getWorld().spawnParticle(Particle.FLAME, location, 1, 0.02, 0.02, 0.02, 0);*/


                    double radians = time * MathUtil.DEGTORAD;
                    Location location = caster.getEyeLocation();
                    Bukkit.getLogger().info(location.toString());
                    Quaterniond quaternion = new Quaterniond(Math.cos(radians) * 1.25, Math.sin(radians) * 1.25, 0, 0);
                    Bukkit.getLogger().info(quaternion.toString());
                    quaternion.mul(new Quaterniond().rotationY(location.getYaw()*MathUtil.DEGTORAD)).mul(new Quaterniond().rotationX(location.getPitch()*MathUtil.DEGTORAD));
                    Bukkit.getLogger().info(quaternion.toString());

                    location.add(new Vector(quaternion.x, quaternion.y, quaternion.z));
                    Bukkit.getLogger().info(location.toString());
                    caster.getWorld().spawnParticle(Particle.FLAME, location, 1, 0.02, 0.02, 0.02, 0);

                    time += 6;
                }

                if (time >= 720) { //TODO Does this iterate one too much ?
                    windupTask.cancel();
                    activate();
                }
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void activate() {
        fireball = caster.getWorld().spawn(caster.getEyeLocation().add(caster.getEyeLocation().getDirection()), Fireball.class, (projectile) -> projectile.setVelocity(caster.getEyeLocation().getDirection()),CreatureSpawnEvent.SpawnReason.CUSTOM);
        SpellHandler.addProjectileConsumer(fireball, this::fireBallHit);
    }

    private void fireBallHit(@NotNull ProjectileHitEvent event) {
        Entity hitEntity = event.getHitEntity();
        if (hitEntity instanceof LivingEntity livingEntity && LivingEntityUtil.entityIsSpellAffectAble(livingEntity))
            LivingEntitySessionData.getLivingEntitySession(livingEntity).getHealth().damageLivingEntity(caster, 2.5, item);

        fireball.getLocation(); //visuals on this location (an immediately exploding firework is used)

        SpellHandler.removeProjectileConsumer(fireball);
        naturalSpellEnd();
    }

    @Override
    public void casterLeave() {

    }

    @Override
    public void cancelSpell() {

    }
}
