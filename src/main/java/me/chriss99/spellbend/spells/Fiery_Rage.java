package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.harddata.Enums;
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
    private final Spell instance;
    private final PlayerSessionData sessionData;

    public static void register() {
        SpellHandler.addSpellBuilderToMap("fiery_rage", new SpellSubClassBuilder() {
            @Override
            public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
                return new Fiery_Rage(caster, spellType, item);
            }
        });
    }

    public Fiery_Rage(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "AURA", item);
        instance = this;
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
        sessionData.getDamageDealtModifiers().addModifier(Enums.DmgModType.SPELL, 1.5f);

        activeTask = new BukkitRunnable() {
            int time = 200;

            @Override
            public void run() {
                Color color = Colors.getRandomOrange1or2();
                Particle.DustTransition dustOptions = new Particle.DustTransition(color, color, (float) (0.2d + MathUtil.random(time/80d, 1d+time/80d)));
                caster.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(0, 1.3, 0), 1, dustOptions);

                if (time == 0) {
                    sessionData.getDamageDealtModifiers().removeModifier(Enums.DmgModType.SPELL, 1.5f);

                    activeTask.cancel();
                    PlayerSessionData.getPlayerSession(caster).getSpellHandler().getActivePlayerSpells().remove(instance);
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

        entry.skipToStage(Enums.CoolDownStage.COOLDOWN);
    }

    @Override
    public void casterLeave() {
        CoolDownEntry entry = sessionData.getCoolDowns().getCoolDownEntry(spellType);
        if (entry == null) {
            Bukkit.getLogger().warning(caster.getName() + " left while having FieryRage active but had no corresponding CoolDown (" + spellType + ") active!");
            return;
        }

        entry.transformToStage(Enums.CoolDownStage.COOLDOWN);
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
            sessionData.getDamageDealtModifiers().removeModifier(Enums.DmgModType.SPELL, 1.5f);
            activeTask.cancel();
        }
    }
}
