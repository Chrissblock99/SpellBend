package me.chriss99.spellbend.spell.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.math.MathUtil;
import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.playerdata.DmgMods;
import me.chriss99.spellbend.util.math.VectorConversion;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Fiery_Rage extends Spell implements Killable {
    BukkitTask windupTask;
    BukkitTask activeTask;
    Spell instance;
    String spellType;

    public Fiery_Rage(@NotNull Player caster, @NotNull ItemStack item) {
        super(caster, item);
        instance = this;
        spellType = item.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING);
        windup();
    }

    private void windup() {
        super.caster.setGravity(false);
        CoolDowns.setCoolDown(super.caster, spellType, 1f, Enums.CoolDownStage.WINDUP);
        final Player player = super.caster;

        windupTask = new BukkitRunnable() {
            final int startRot = Math.round(player.getLocation().getYaw());
            int time = 0;

            @Override
            public void run() {
                Location location = player.getLocation();
                location.setYaw(startRot + time);
                player.teleport(location);

                Particle.DustTransition dustOptions = new Particle.DustTransition(
                        Color.fromRGB((int) MathUtil.random(210d, 255d), (int) MathUtil.random(80d, 120d), (int) MathUtil.random(0d, 40d)),
                        Color.fromRGB((int) MathUtil.random(210d, 255d), (int) MathUtil.random(80d, 120d), (int) MathUtil.random(0d, 40d)),
                        (float) MathUtil.random(1.3d, 2d));
                player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, player.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(1f/3f)),
                        time/180f, Math.sin((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(1f/3f))), 1, dustOptions);
                player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, player.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI),
                        time/180f, Math.sin((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI)), 1, dustOptions);
                player.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, player.getLocation().add(Math.cos((time*(1f/3f))*MathUtil.DEGTORAD+Math.PI*(5f/3f)),
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
        Vector vel = super.caster.getLocation().getDirection();

        double pitch0TO1 = (VectorConversion.getPitch(vel)+90)/180;
        vel = VectorConversion.setPitch(vel, (float) Math.cbrt(pitch0TO1)*180 - 90);

        vel.multiply(0.9 + Math.pow(pitch0TO1, 4));
        super.caster.setGravity(true);
        super.caster.setVelocity(vel);
    }

    private void activate() {
        CoolDowns.setCoolDown(super.caster, spellType, 10f, Enums.CoolDownStage.ACTIVE);
        DmgMods.addDmgMod(super.caster, Enums.DmgModType.SPELL, 1.5f);
        final Player player = super.caster;

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
                    DmgMods.removeDmgMod(player, Enums.DmgModType.SPELL, 1.5f);
                    CoolDowns.setCoolDown(player, spellType, 30f, Enums.CoolDownStage.COOLDOWN);

                    activeTask.cancel();
                    SpellHandler.getActivePlayerSpells(player).remove(instance);
                }
                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    @Override
    public void casterDeath(Player killer) {
        CoolDowns.setCoolDown(super.caster, spellType, 30f, Enums.CoolDownStage.COOLDOWN);
        cancelSpell();
    }

    @Override
    public void casterLeave() {
        CoolDownEntry entry = CoolDowns.getCoolDownEntry(super.caster, spellType);
        switch (entry.coolDownStage()) {
            case WINDUP -> CoolDowns.setCoolDown(super.caster, spellType, entry.timeInS()+40f, Enums.CoolDownStage.COOLDOWN);
            case ACTIVE -> CoolDowns.setCoolDown(super.caster, spellType, entry.timeInS()+30f, Enums.CoolDownStage.COOLDOWN);
        }
        cancelSpell();
    }

    @Override
    public void cancelSpell() {
        if (!windupTask.isCancelled()) {
            windupTask.cancel();
            super.caster.setGravity(true);
            SpellHandler.getActivePlayerSpells(super.caster).remove(instance);
            return;
        }
        if (!activeTask.isCancelled()) {
            DmgMods.removeDmgMod(super.caster, Enums.DmgModType.SPELL, 1.5f);
            activeTask.cancel();
        }
        SpellHandler.getActivePlayerSpells(super.caster).remove(instance);
    }
}
