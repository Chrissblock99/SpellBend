package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.util.math.MathUtil;
import me.chriss99.spellbend.util.math.VectorConversion;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Fiery_Rage extends Spell implements Killable {
    private BukkitTask windupTask;
    private BukkitTask activeTask;

    private final PlayerSessionData sessionData;

    public static void register() {
        SpellHandler.registerSpell("fiery_rage", 40, new SpellSubClassBuilder() {
            @Override
            public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
                return new Fiery_Rage(caster, spellType, item);
            }
        });
    }

    public Fiery_Rage(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "AURA", item);
        sessionData = PlayerSessionData.getPlayerSession(caster);
        sessionData.getCoolDowns().setCoolDown(super.spellType, new float[]{1, 0, 10, 30});
        windup();
    }

    private void windup() {
        caster.setGravity(false);

        windupTask = new BukkitRunnable() {
            final int startRot = Math.round(caster.getLocation().getYaw());
            int time = 0;

            @Override
            public void run() {
                Location location = caster.getLocation();
                location.setYaw(startRot + time);
                caster.teleport(location);

                Color color = Colors.getRandomOrange5or6();
                Particle.DustTransition dustOptions = new Particle.DustTransition(color, color, (float) MathUtil.random(1.3d, 2d));
                World world = caster.getWorld();
                world.spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(1f/3f)),
                        time/180f, Math.sin((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(1f/3f))), 1, dustOptions);
                world.spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI),
                        time/180f, Math.sin((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI)), 1, dustOptions);
                world.spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(5f/3f)),
                        time/180f, Math.sin((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(5f/3f))), 1, dustOptions);

                world.playSound(location, Sound.BLOCK_LAVA_POP, 2, 1+time/720f);
                world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 2, 1+time/720f);

                if (time == 360) {
                    windupTask.cancel();
                    launchPlayer();
                    activate();
                }
                time += 18;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void launchPlayer() {
        Vector vel = caster.getLocation().getDirection();

        double pitch0TO1 = (VectorConversion.getPitch(vel)+90)/180;
        vel = VectorConversion.setPitch(vel, (float) Math.cbrt(pitch0TO1)*180 - 90);

        vel.multiply(0.9 + Math.pow(pitch0TO1, 4));
        caster.setGravity(true);
        caster.setVelocity(vel);
    }

    private void activate() {
        sessionData.getDamageDealtModifiers().addModifier(1.5f);
        sessionData.getWalkSpeedModifiers().addModifier(1.2f);

        World world = caster.getWorld();
        Location location = caster.getLocation();
        for (int i = 0;i<360;i += 20) {
            world.spawnParticle(Particle.EXPLOSION_LARGE, location.clone().add(new Vector(Math.cos(i) * 1.5, 0, Math.sin(i) * 1.5)), 1, 0, 0, 0, 0);
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 3, 1f);
        }


        world.playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 3, 1.5f);
        world.playSound(location, Sound.ENTITY_BLAZE_AMBIENT, 3, 1.5f);
        activeTask = new BukkitRunnable() {
            int time = 200;

            @Override
            public void run() {
                boolean burrowIsActive = false;
                for (Spell spell : sessionData.getSpellHandler().getActivePlayerSpells())
                    if (spell instanceof Escape_Through_Time) { //TODO MAKE THIS CHECK FOR BURROW WHEN IT EXISTS!
                        burrowIsActive = true;
                        break;
                    }

                if (!burrowIsActive) {
                    caster.setFireTicks(2);
                    Color color = Colors.getRandomOrange1or2();
                    Particle.DustTransition dustOptions = new Particle.DustTransition(color, color, 0.5f+time/60f);
                    world.spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(0, 1.3, 0), 1, dustOptions);
                }

                if (time == 0) {
                    sessionData.getDamageDealtModifiers().removeModifier(1.5f);
                    sessionData.getWalkSpeedModifiers().removeModifier(1.2f);

                    world.playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1.2f);
                    world.playSound(caster.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1.5f);

                    activeTask.cancel();
                    naturalSpellEnd();
                }
                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    @Override
    public void casterDeath(@Nullable Entity killer) {
        CoolDownEntry entry = sessionData.getCoolDowns().getCoolDownEntry(spellType);
        if (entry == null) {
            Bukkit.getLogger().warning(caster.getName() + " had Fiery Rage active under the type \"" + spellType + "\" while dying, but no CoolDownEntry was found, skipping it's removal!");
            return;
        }

        entry.skipToStage(CoolDownStage.COOLDOWN);
    }

    @Override
    public void casterLeave() {
        CoolDownEntry entry = sessionData.getCoolDowns().getCoolDownEntry(spellType);
        if (entry == null) {
            Bukkit.getLogger().warning(caster.getName() + " left while having FieryRage active but had no corresponding CoolDown (" + spellType + ") active!");
            return;
        }

        entry.transformToStage(CoolDownStage.COOLDOWN);
        cancelSpell();
    }

    @Override
    public void cancelSpell() {
        if (!windupTask.isCancelled()) {
            windupTask.cancel();
            caster.setGravity(true);
            return;
        }

        if (!activeTask.isCancelled()) {
            sessionData.getDamageDealtModifiers().removeModifier(1.5f);
            sessionData.getWalkSpeedModifiers().removeModifier(1.2f);

            World world = caster.getWorld();
            Location location = caster.getLocation();
            world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1.2f);
            world.playSound(location, Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1.5f);

            activeTask.cancel();
        }
    }
}
