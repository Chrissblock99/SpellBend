package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import net.kyori.adventure.sound.SoundStop;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Escape_Through_Time extends Spell {
    private final Location armorStandOrigin;
    private BukkitTask armorStandTask;
    private BukkitTask escapeTask;
    private ArmorStand armorStand;
    private final SpellHandler spellHandler;
    public Escape_Through_Time(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0, 0, 15, 5}));
        armorStandOrigin = caster.getLocation();
        setupArmorStand();
        spellHandler = PlayerSessionData.getPlayerSession(caster).getSpellHandler();
        spellHandler.addClickableSpellRunnable(item, this::escapeTeleport);
    }

    private void setupArmorStand() {
        World world = caster.getWorld();
        armorStand = world.spawn(armorStandOrigin, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 99999, 255, false, false));

        world.playSound(armorStandOrigin, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 3f, 1.2f);
        world.playSound(armorStandOrigin, Sound.ENTITY_ENDERMAN_TELEPORT, 3f, 1.2f);
        world.playSound(armorStandOrigin, Sound.BLOCK_BEACON_ACTIVATE, 3f, 1.2f);

        for (int i = 0; i < 360; i += 10) {
            Color color = Colors.getRandomYellow1or2();
            armorStandOrigin.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, armorStand.getLocation().clone().add(Math.cos(i * MathUtil.DEGTORAD) * 1.5f, 0, Math.sin(i * MathUtil.DEGTORAD) * 1.5f),
                    1, 0, 0, 0, 0,
                    new Particle.DustTransition(color, color, 2));
    }

        armorStandTask = new BukkitRunnable() {
            int time = 300;

            @Override
            public void run() {
                armorStand.teleport(armorStandOrigin.clone().add(0, -Math.sin(time/13.64f)/3f, 0));
                armorStandOrigin.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, armorStand.getLocation().clone().add(0, 1, 0), 1, 0.5, 0.6, 0.5, 0,
                        new Particle.DustTransition(Colors.yellow2, Colors.yellow2, 2));

                if (time == 0) {
                    spellHandler.removeClickableSpellRunnable(item);
                    explode();

                    armorStandTask.cancel();
                    naturalSpellEnd();
                }
                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void escapeTeleport() {
        spellHandler.removeClickableSpellRunnable(item);

        escapeTask = new BukkitRunnable() {
            final Location casterOrigin = caster.getLocation().clone();
            int time = 20;

            @Override
            public void run() {
                World world = caster.getWorld();
                Location location = caster.getLocation();
                caster.teleport(new Location(world, 0, 0, 0, casterOrigin.getYaw(), casterOrigin.getPitch()).add(MathUtil.lerpVector(casterOrigin.toVector(), armorStandOrigin.toVector(), Math.pow(time/20f, 0.333333333))));
                world.spawnParticle(Particle.SMOKE_LARGE, location.add(0 , 1.3, 0), 5, 0, 0, 0, 0.5);
                world.spawnParticle(Particle.VILLAGER_ANGRY, location.add(0 , 0.8, 0), 5, 0, 0, 0, 0);
                world.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 2f, time/10f);
                world.playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 2f, time/10f);

                if (time == 0) {
                    armorStand.remove();
                    world.stopSound(SoundStop.named(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR));
                    world.stopSound(SoundStop.named(Sound.ENTITY_ENDERMAN_TELEPORT));
                    world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);

                    escapeTask.cancel();
                    armorStandTask.cancel();
                    coolDown.skipToStage(CoolDownStage.COOLDOWN);
                    naturalSpellEnd();
                }
                time--;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void explode() {
        World world = caster.getWorld();
        Map<LivingEntity, Double> players = LivingEntityUtil.getSpellAffectAbleEntitiesNearLocation(armorStandOrigin, 4.5);
        players.remove(caster);
        for (Map.Entry<LivingEntity, Double> entry : players.entrySet()) {
            LivingEntity livingEntity = entry.getKey();

            LivingEntitySessionData sessionData = LivingEntitySessionData.getLivingEntitySession(livingEntity);
            sessionData.getHealth().damageLivingEntity(caster, 3, item);
            sessionData.stunEntity(20);

            world.spawnParticle(Particle.FLASH, livingEntity.getLocation(), 1, 0, 0, 0, 0);
            //player.moveUp(1);
            //player.moveAway(1);
        }

        world.spawnParticle(Particle.EXPLOSION_LARGE, armorStandOrigin.add(0, 1.3, 0), 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.VILLAGER_ANGRY, armorStandOrigin.add(0, 0.8, 0), 1, 0, 0, 0, 0);
        world.playSound(armorStandOrigin, Sound.ENTITY_GENERIC_EXPLODE, 2f, 1f);
        armorStand.remove();
    }

    @Override
    public void casterLeave() {
        cancelSpell();
    }

    @Override
    public void cancelSpell() {
        spellHandler.removeClickableSpellRunnable(item);
        armorStandTask.cancel();
        if (escapeTask != null)
            escapeTask.cancel();
        explode();
    }
}
