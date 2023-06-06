package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.Colors;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Scorching_Column extends Spell {
    private static final SpellBend plugin = SpellBend.getInstance();
    private static final Vector[] fallingBlockOffsets = createFallingBlockOffsets();

    private BukkitTask windupTask;
    private BukkitTask removeTask;

    public Scorching_Column(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0.45f, 0, 0, 7}));
        Location location = findHitPosition(caster, 32);
        if (location == null) {
            Bukkit.getLogger().warning("Scorching Column could be activated by " + caster.getName() + " despite them having no suitable target Location!");
            caster.sendMessage(SpellBend.getMiniMessage().deserialize("<red>No suitable target location, please notify a developer!</red>\n" +
                    "This only stops the spell from working this time, you can continue playing normally."));
            coolDown.skipToStage(CoolDownStage.COOLDOWN);
            coolDown.updateCoolDownStage();
            coolDown.skipCurrentStage();
            naturalSpellEnd();
            return;
        }

        windup(location);
    }

    public static @Nullable Location findHitPosition(@NotNull LivingEntity livingEntity, double maxDistance) {
        World world = livingEntity.getWorld();
        RayTraceResult rayTraceResult = world.rayTrace(livingEntity.getEyeLocation(), livingEntity.getEyeLocation().getDirection(),
                maxDistance, FluidCollisionMode.NEVER, true,
                0, entity -> LivingEntityUtil.entityIsSpellAffectAble(entity) && !entity.equals(livingEntity));
        if (rayTraceResult == null)
            return null;

        Location hitLocation = rayTraceResult.getHitPosition().toLocation(world);
        Entity hitEntity = rayTraceResult.getHitEntity();
        if (hitEntity == null)
            return hitLocation;

        if (LivingEntityUtil.isOnGround(hitEntity)) {
            hitLocation.setY(hitEntity.getBoundingBox().getMinY());
            return hitLocation;
        }

        RayTraceResult rayTraceBlocksResult = world.rayTraceBlocks(hitLocation, new Vector(0, -1, 0),
                3, FluidCollisionMode.NEVER, true);

        if (rayTraceBlocksResult == null)
            return null;
        return rayTraceBlocksResult.getHitPosition().toLocation(world);
    }

    final World world = caster.getWorld();
    private void windup(@NotNull Location location) {
        windupTask = new BukkitRunnable() {
            int time = 0;

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    double radians = Math.toRadians(time*4);
                    Vector circlePos = new Vector(Math.cos(radians) * 3, 0.05, Math.sin(radians) * 3);
                    world.spawnParticle(Particle.REDSTONE, location.clone().add(circlePos), 1, 0, 0, 0, 0,
                            new Particle.DustOptions(Colors.getRandomOrange1or2(), 3));

                    time++;
                }
                world.playSound(location, Sound.BLOCK_NETHERRACK_BREAK, 3f, 0.5f + time/168f);
                world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 2f, 1 + time/168f);

                if (time >= 90) {
                    windupTask.cancel();
                    active(location);
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    final LinkedList<FallingBlock> fallingFireBlocks = new LinkedList<>();
    private void active(@NotNull Location location) {
        Set<LivingEntity> hitEntities = LivingEntityUtil.getSpellAffectAbleEntitiesNearLocation(location, 3).keySet();
        hitEntities.remove(caster);
        for (LivingEntity hitEntity : hitEntities) {
            if (!LivingEntityUtil.isOnGround(hitEntity))
                continue;
            LivingEntitySessionData sessionData = LivingEntitySessionData.getLivingEntitySession(hitEntity);

            sessionData.getHealth().damageLivingEntity(caster, 6, item);
            hitEntity.setVelocity(hitEntity.getVelocity().add(new Vector(0, 0.75, 0)));
            LivingEntityUtil.igniteLivingEntity(hitEntity);
            sessionData.stunEntity(25);
        }

        for (Vector fallingBlockOffset : fallingBlockOffsets) {
            FallingBlock fallingFireBlock = world.spawnFallingBlock(location.clone().add(fallingBlockOffset), Material.FIRE.createBlockData());
            fallingFireBlock.setVelocity(new Vector(0, 0.35, 0));
            fallingFireBlocks.add(fallingFireBlock);
            SpellHandler.registerFallingBlockHitGroundEventListener(fallingFireBlock, event -> removeFallingFireBlock(fallingFireBlock));
        }

        world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 3f, 0.8f);

        removeFallingBlocks();
    }

    private void removeFallingBlocks() {
        long endTime = new Date().getTime() + 20000;
        removeTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = fallingFireBlocks.size()-1; i >= 0; i--) {
                    FallingBlock fallingFireBlock = fallingFireBlocks.get(i);
                    Location fireLocation = fallingFireBlock.getLocation();
                    if (fallingFireBlock.isDead() || new Date().getTime() >= endTime) {
                        removeFallingFireBlock(fallingFireBlock);
                        continue;
                    }

                    if (MathUtil.randomChance(0.1))
                        world.spawnParticle(Particle.LAVA, fireLocation, 1);
                    if (MathUtil.randomChance(0.2)) {
                        RayTraceResult rayTraceResult = world.rayTraceBlocks(fireLocation, new Vector(0, -1, 0),
                                10, FluidCollisionMode.ALWAYS, true);
                        float pitch = (rayTraceResult == null) ? 10 :
                                (float) (fireLocation.getY() - rayTraceResult.getHitPosition().getY());
                        world.playSound(fireLocation, Sound.BLOCK_LAVA_POP, 2f, pitch);
                    }
                }

                if (fallingFireBlocks.isEmpty()) {
                    removeTask.cancel();
                    naturalSpellEnd();
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    private void removeFallingFireBlock(@NotNull FallingBlock fallingFireBlock) {
        fallingFireBlock.remove();
        SpellHandler.removeFallingBlockHitGroundEventListener(fallingFireBlock);
        fallingFireBlocks.remove(fallingFireBlock);
        if (MathUtil.randomChance(0.25)) {
            Location fireLocation = fallingFireBlock.getLocation();
            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, fireLocation, 1, 0, 0, 0, 0);
            world.playSound(fireLocation, Sound.BLOCK_FIRE_EXTINGUISH, 2, 2);
        }
    }

    @Override
    public void casterStun(int timeInTicks) {
        if (coolDown.getCoolDownStage().equals(CoolDownStage.WINDUP)) {
            coolDown.skipToStage(CoolDownStage.COOLDOWN);
            cancelSpell();
            naturalSpellEnd();
        }
    }

    @Override
    public void casterDeath(@Nullable LivingEntity killer) {
        if (coolDown.getCoolDownStage().equals(CoolDownStage.WINDUP)) {
            coolDown.skipToStage(CoolDownStage.COOLDOWN);
            cancelSpell();
            naturalSpellEnd();
        }
    }

    @Override
    public void casterLeave() {
        if (coolDown.getCoolDownStage().equals(CoolDownStage.WINDUP)) {
            cancelSpell();
            naturalSpellEnd();
        }
        coolDown.transformToStage(CoolDownStage.COOLDOWN);
    }

    @Override
    public void cancelSpell() {
        if (windupTask != null)
            windupTask.cancel();

        if (removeTask != null) {
            removeTask.cancel();
            for (int i = fallingFireBlocks.size() - 1; i >= 0; i--)
                removeFallingFireBlock(fallingFireBlocks.get(i));
        }
    }


    private static @NotNull Vector[] createFallingBlockOffsets() {
        LinkedList<Vector> fallingBlockOffsets = new LinkedList<>();

        for (int i = -1; i < 5; i++) {
            if (i < 1)
                fallingBlockOffsets.addAll(getLayerSize3(i));
            if (i < 3)
                fallingBlockOffsets.addAll(getLayerSize2(i));
            fallingBlockOffsets.add(new Vector(0, i, 0));
        }

        return fallingBlockOffsets.toArray(new Vector[0]);
    }

    private static @NotNull List<Vector> getLayerSize2(int y) {
        return Arrays.stream(new Vector[]{
                new Vector(1, y, 0),
                new Vector(0, y, 1),
                new Vector(-1, y, 0),
                new Vector(0, y, -1)
        }).toList();
    }

    private static @NotNull List<Vector> getLayerSize3(int y) {
        return Arrays.stream(new Vector[]{
                new Vector(2, y, 0),
                new Vector(1, y, 1),
                new Vector(0, y, 2),
                new Vector(1, y, -1),
                new Vector(-2, y, 0),
                new Vector(-1, y, -1),
                new Vector(0, y, -2),
                new Vector(-1, y, 1)
        }).toList();
    }


    public static @Nullable Component validatePlayerState(@NotNull Player player, double distance) {
        return (findHitPosition(player, distance) == null) ? SpellBend.getMiniMessage().deserialize("<red><bold>Too far away!") : null;
    }
}