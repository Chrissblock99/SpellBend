package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PercentageModifier;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.util.ParticleUtil;
import me.chriss99.spellbend.util.PlayerUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Seismic_Shock extends Spell implements Killable, Stunable {
    public static Vector[] ring1 = createRing1();
    public static Vector[] ring2 = createRing2();
    public static Vector[] ring3 = createRing3();

    private final PercentageModifier walkSpeed;
    private final BukkitTask stunUndoTask;
    private BukkitTask windupTask;
    private BukkitTask activeTask;

    public static void register() {
        SpellHandler.registerSpell("seismic_shock", 35, new SpellSubClassBuilder() {
            @Override
            public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
                return new Seismic_Shock(caster, spellType, item);
            }
        }, new PlayerStateValidator() {
            @Override
            public String validateState(@NotNull Player player) {
                if (!player.isOnGround())
                    return "&c&lGet on the Ground!";
                return null;
            }
        });
    }

    public Seismic_Shock(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "TEST", item);
        PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(super.spellType, new float[]{0.5f, 1.5f, 0.25f, 10});
        walkSpeed = PlayerSessionData.getPlayerSession(caster).getWalkSpeedModifiers();
        walkSpeed.displaceIsZero(1);
        stunUndoTask = new BukkitRunnable(){
             @Override
            public void run() {
                 walkSpeed.displaceIsZero(-1);
             }
        }.runTaskLater(SpellBend.getInstance(), 45);

        windup();
    }

    private void windup() {
        Location center = caster.getLocation().clone();
        windupTask = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                double t = ((double) time/10d)*3;

                for (int i = 0;i<12;i++) {
                    double value = 1 - Math.min(t, 1);

                    Color color = (((float) i/2f)%1 == 0) ? Colors.getRandomBlue3to5() : Colors.getRandomBlue4to5();
                    Particle.DustTransition dustTransition = new Particle.DustTransition(color, color, 2);
                    ParticleUtil.drawLine(caster.getWorld(), center.toVector(), MathUtil.lerpVector(center.toVector(), center.toVector().add(ring1[i]), value), 3, Particle.DUST_COLOR_TRANSITION, dustTransition);

                    if (t>1) {
                        value = 1 - ((t<2) ? t-1 : 1);

                        color = (((float) i / 2f) % 1 == 0) ? Colors.getRandomBlue3to5() : Colors.getRandomBlue4to5();
                        dustTransition = new Particle.DustTransition(color, color, 2);
                        ParticleUtil.drawLine(caster.getWorld(), center.toVector().add(ring1[i]), MathUtil.lerpVector(center.toVector().add(ring1[i]), center.toVector().add(ring2[i]), value), 3, Particle.DUST_COLOR_TRANSITION, dustTransition);
                    }

                    if (t>2) {
                        value = 1 - (t-2);

                        color = (((float) i / 2f) % 1 == 0) ? Colors.getRandomBlue3to5() : Colors.getRandomBlue4to5();
                        dustTransition = new Particle.DustTransition(color, color, 2);
                        ParticleUtil.drawLine(caster.getWorld(), center.toVector().add(ring2[i]), MathUtil.lerpVector(center.toVector().add(ring2[i]), center.toVector().add(ring3[i]), value), 3, Particle.DUST_COLOR_TRANSITION, dustTransition);
                    }

                    World world = center.getWorld();
                    world.playSound(center, Sound.BLOCK_GRASS_BREAK, time/25f, 1+time/20f);
                    world.playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, time/25f, 1+time/20f);
                }

                if (time == 10) {
                    windupTask.cancel();
                    active(center);
                }

                time++;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void active(@NotNull Location center) {
        activeTask = new BukkitRunnable() {
            int time = 15;

            @Override
            public void run() {
                for (int i = 0;i<12;i++) {
                    Color color = (((float) i/2f)%1 == 0) ? Colors.getRandomBlue1to3() : Colors.getRandomBlue2to5();
                    Particle.DustTransition dustTransition = new Particle.DustTransition(color, color, 2);
                    ParticleUtil.drawLine(caster.getWorld(), center.toVector(), center.toVector().add(ring1[i]), 3, Particle.DUST_COLOR_TRANSITION, dustTransition);

                    color = (((float) i/2f)%1 == 0) ? Colors.getRandomBlue1to3() : Colors.getRandomBlue2to5();
                    dustTransition = new Particle.DustTransition(color, color, 2);
                    ParticleUtil.drawLine(caster.getWorld(), center.toVector().add(ring1[i]), center.toVector().add(ring2[i]), 3, Particle.DUST_COLOR_TRANSITION, dustTransition);

                    color = (((float) i/2f)%1 == 0) ? Colors.getRandomBlue1to3() : Colors.getRandomBlue2to5();
                    dustTransition = new Particle.DustTransition(color, color, 2);
                    ParticleUtil.drawLine(caster.getWorld(), center.toVector().add(ring2[i]), center.toVector().add(ring3[i]), 3, Particle.DUST_COLOR_TRANSITION, dustTransition);

                    World world = center.getWorld();
                    if (MathUtil.randomChance(0.125)) {
                        Location location = center.clone().add(ring3[i]);
                        world.spawnParticle(Particle.FLASH, location, 1, 0, 0 ,0 ,0);
                        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1.8f);
                    }

                    world.playSound(center, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 0.25f, 1.5f);
                    world.playSound(center, Sound.BLOCK_GRASS_BREAK, 0.25f, 1.5f);

                    Map<Player, Double> players = PlayerUtil.getPlayersNearLocation(center, 6);
                    players.remove(caster);
                    for (Map.Entry<Player, Double> entry : players.entrySet()) {
                        Player player = entry.getKey();
                        if (player.isOnGround()) //if this is every removed use a list of already moved players instead, like mango did it
                            player.getVelocity().add(new Vector(0, 0.5, 0));
                        shockPlayer(player, 1);
                        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
                        sessionData.getHealth().damagePlayer(caster, 2.5, item);
                        sessionData.getSpellHandler().stunPlayer(4);
                    }
                }

                if (time == 0) {
                    activeTask.cancel();
                }

                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 2);
    }

    @Override
    public void casterLeave() {
        cancelSpell();
    }

    @Override
    public void cancelSpell() {
        stunUndoTask.cancel();
        walkSpeed.displaceIsZero(-1);

        if (windupTask != null) {
            windupTask.cancel();
        }

        if (activeTask != null) {
            activeTask.cancel();
        }
    }

    @Override
    public void casterDeath(@Nullable Entity killer) {
        cancelSpell();
    }

    @Override
    public void casterStun(int timeInTicks) {
        cancelSpell();
    }

    public static void shockPlayer(@NotNull Player player, int timeIn2ticks) {
        World world = player.getWorld();
        int finalTimeIn2ticks = timeIn2ticks*2;

        new BukkitRunnable(){
            int time = 0;

            @Override
            public void run() {
                Location location = player.getLocation();
                if (time%2 == 0) {
                    world.spawnParticle(Particle.VILLAGER_ANGRY, location, 1, 0, 0, 0, 0);
                    world.playSound(location, Sound.BLOCK_NOTE_BLOCK_BIT, 3f, 1.8f);
                }
                else world.playSound(location, Sound.BLOCK_NOTE_BLOCK_BIT, 3f, 1.5f);

                if (time == finalTimeIn2ticks)
                    this.cancel();

                time++;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    public static Vector[] createRing1() {
        Vector[] vectors = new Vector[12];
        for (int i = 0;i<12;i++)
            vectors[i] = new Vector(Math.cos(i*30*MathUtil.DEGTORAD+Math.PI/12)*2.5, 0.125, Math.sin(i*30* MathUtil.DEGTORAD+Math.PI/12)*2.5);
        return vectors;
    }

    public static Vector[] createRing2() {
        Vector[] vectors = new Vector[12];
        for (int i = 0;i<12;i++)
            vectors[i] = new Vector(Math.cos(i*30*MathUtil.DEGTORAD-(Math.PI*2/3f)/6)*4.5, 0.125, Math.sin(i*30* MathUtil.DEGTORAD-(Math.PI*2/3f)/6)*4.5);
        return vectors;
    }

    public static Vector[] createRing3() {
        Vector[] vectors = new Vector[12];
        for (int i = 0;i<12;i++)
            vectors[i] = new Vector(Math.cos(i*30*MathUtil.DEGTORAD)*6.5, 0.125, Math.sin(i*30* MathUtil.DEGTORAD)*6.5);
        return vectors;
    }
}
