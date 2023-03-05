package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDowns;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.yaml.snakeyaml.error.YAMLException;

public class Ember_Blast extends Spell { //TODO this
    private BukkitTask windupTask;
    private final Spell instance;
    private final CoolDowns coolDowns;

    public static void register() {
        SpellHandler.registerSpell("ember_blast", 35, new SpellSubClassBuilder() {
            @Override
            public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
                return new Ember_Blast(caster, spellType, item);
            }
        });
    }

    public Ember_Blast(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "BLAST", item);
        instance = this;
        coolDowns = PlayerSessionData.getPlayerSession(caster).getCoolDowns();
        coolDowns.setCoolDown(super.spellType, new float[]{2, 0, 0, 0});
        windup();
    }

    private void windup() {
        windupTask = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                for (int i = 0;i<3;i++) {
                    /*double radiants = time * MathUtil.DEGTORAD;
                    Location location = caster.getEyeLocation();
                    Location location1 = location.clone();
                    Vector vector = new Vector(Math.cos(radiants) * 1.25, Math.sin(radiants) * 1.25, 1);
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


                    double radiants = time * MathUtil.DEGTORAD;
                    Location location = caster.getEyeLocation();
                    Bukkit.getLogger().info(location.toString());
                    Quaterniond quaternion = new Quaterniond(Math.cos(radiants) * 1.25, Math.sin(radiants) * 1.25, 1, 0);
                    Bukkit.getLogger().info(quaternion.toString());
                    quaternion.mul(new Quaterniond().rotationY(location.getYaw()*MathUtil.DEGTORAD)).mul(new Quaterniond().rotationX(location.getPitch()*MathUtil.DEGTORAD));
                    Bukkit.getLogger().info(quaternion.toString());

                    location.add(new Vector(quaternion.x, quaternion.y, quaternion.z));
                    Bukkit.getLogger().info(location.toString());
                    caster.getWorld().spawnParticle(Particle.FLAME, location, 1, 0.02, 0.02, 0.02, 0);

                    time += 6;
                }

                if (time >= 720) { //TODO Does this iterrate one too much ?
                    windupTask.cancel();
                    activate();
                }
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void activate(){

        Fireball fireball = caster.getWorld().spawn(caster.getEyeLocation().add(caster.getEyeLocation().getX(), caster.getEyeLocation().getY()+1, caster.getEyeLocation().getZ()), Fireball.class);
        caster.launchProjectile(fireball.getClass());

        int x = (int) Math.round(caster.getEyeLocation().getDirection().getX());
        caster.sendMessage("x: "+x);
        int y = (int) Math.round(caster.getEyeLocation().getDirection().getY());
        caster.sendMessage("y: "+y);
        int z = (int) Math.round(caster.getEyeLocation().getDirection().getZ());
        caster.sendMessage("z: "+z);

        naturalSpellEnd();//wenn nach normalen verlauf zuende
    }

    @Override
    public void casterLeave() {

    }

    @Override
    public void cancelSpell() {

    }
}
