package me.chriss99.spellbend.spell.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.ItemData;
import me.chriss99.spellbend.util.math.MathUtil;
import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.playerdata.DmgMods;
import me.chriss99.spellbend.util.math.VectorConversion;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Fiery_Rage extends Spell implements Killable {
    BukkitTask windupTask;
    BukkitTask activeTask;
    Spell instance;
    final String spellType;

    public Fiery_Rage(@NotNull Player caster, @NotNull ItemStack item) {
        super(caster, item);
        instance = this;
        spellType = Objects.requireNonNullElse(ItemData.getSpellType(item), "AURA");
        CoolDowns.setCoolDown(caster, spellType, new float[]{1, 0, 10, 30}, Enums.CoolDownStage.WINDUP);
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

                Particle.DustTransition dustOptions = new Particle.DustTransition(
                        Color.fromRGB((int) MathUtil.random(210d, 255d), (int) MathUtil.random(80d, 120d), (int) MathUtil.random(0d, 40d)),
                        Color.fromRGB((int) MathUtil.random(210d, 255d), (int) MathUtil.random(80d, 120d), (int) MathUtil.random(0d, 40d)),
                        (float) MathUtil.random(1.3d, 2d));
                caster.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(1f/3f)),
                        time/180f, Math.sin((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(1f/3f))), 1, dustOptions);
                caster.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI),
                        time/180f, Math.sin((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI)), 1, dustOptions);
                caster.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, caster.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(5f/3f)),
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
        DmgMods.setDmgMod(Enums.DmgMod.DEALT);
        DmgMods.addDmgMod(caster, Enums.DmgModType.SPELL, 1.5f);
        final Player player = caster;

        activeTask = new BukkitRunnable() {
            int time = 200;

            @Override
            public void run() {
                Particle.DustTransition dustOptions = new Particle.DustTransition(
                        Color.fromRGB((int) MathUtil.random(210d, 255d), (int) MathUtil.random(80d, 120d), (int) MathUtil.random(0d, 40d)),
                        Color.fromRGB((int) MathUtil.random(210d, 255d), (int) MathUtil.random(80d, 120d), (int) MathUtil.random(0d, 40d)),
                        (float) (0.2d + MathUtil.random(time/80d, 1d+time/80d)));
                player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, player.getLocation().add(0d, 1d, 0d), 1, dustOptions);

                if (time == 0) {
                    DmgMods.setDmgMod(Enums.DmgMod.DEALT);
                    DmgMods.removeDmgMod(player, Enums.DmgModType.SPELL, 1.5f);

                    activeTask.cancel();
                    SpellHandler.getActivePlayerSpells(player).remove(instance);
                }
                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    @Override
    public void casterDeath(@Nullable Entity killer) {
        CoolDownEntry entry = CoolDowns.getCoolDownEntry(caster, spellType);
        if (entry == null) {
            Bukkit.getLogger().warning(caster.getName() + " had Fiery Rage active under the type \"" + spellType + "\" while dieing, but no CoolDownEntry was found, skipping it's removal!");
            return;
        }

        entry.skipToStage(Enums.CoolDownStage.COOLDOWN);
        cancelSpell();
    }

    @Override
    public void casterLeave() {
        CoolDownEntry entry = CoolDowns.getCoolDownEntry(caster, spellType);
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
            SpellHandler.getActivePlayerSpells(caster).remove(instance);
            return;
        }
        if (!activeTask.isCancelled()) {
            DmgMods.setDmgMod(Enums.DmgMod.DEALT);
            DmgMods.removeDmgMod(caster, Enums.DmgModType.SPELL, 1.5f);
            activeTask.cancel();
        }
        SpellHandler.getActivePlayerSpells(caster).remove(instance);
    }
}
