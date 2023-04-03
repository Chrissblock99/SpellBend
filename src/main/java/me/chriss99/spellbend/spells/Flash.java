package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.ParticleUtil;
import me.chriss99.spellbend.util.math.BoundingBoxUtil;
import me.chriss99.spellbend.util.math.RotationUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Flash extends Spell {
    public Flash(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0, 0, 0, 5}));

        Location location = bestResult(caster, 6);
        //caster.teleport(location);
        //caster.setVelocity(caster.getVelocity().add(location.getDirection().multiply(0.5)));

        World world = caster.getWorld();
        //eTrail(location 1.3 above player,{_l},2)
        world.playSound(location, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 2f, 1.2f);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 2f, 1.2f);
        world.spawnParticle(Particle.VILLAGER_ANGRY, location, 3);

        naturalSpellEnd();
    }

    private static @NotNull Location bestResult(@NotNull Player player, double maxDistance) {
        World world = player.getWorld();
        Location eyes = player.getEyeLocation();
        Vector direction = eyes.getDirection();
        RayTraceResult eyeTraceResult = world.rayTraceBlocks(eyes, direction, maxDistance);
        Vector eyesToLocationOffset = eyes.toVector().subtract(player.getLocation().toVector());
        if (eyeTraceResult == null)
            return eyes.clone().add(direction.clone().multiply(maxDistance)).subtract(eyesToLocationOffset);
        Location eyeTarget = eyeTraceResult.getHitPosition().toLocation(world);
        world.spawnParticle(Particle.DRIPPING_HONEY, eyeTarget, 1);

        var best = new Object() {
            double smallestDistanceSquared = Double.MAX_VALUE;
            BoundingBoxUtil.BoxTraceResult closestResult = null;
        };
        forEachRotatedCircleGridOffset(eyes, offset -> {
            BoundingBox offsetBox = player.getBoundingBox().clone().shift(offset);
            BoundingBoxUtil.BoxTraceResult result = BoundingBoxUtil.boxTraceWorldBlocks(world, offsetBox, direction, maxDistance);
            if (result == null)
                return;

            ParticleUtil.visualizeBoundingBoxInWorld(world, result.result(), 5, Particle.FLAME, null);

            //TODO TEMPORARY!!!
            double distanceSquaredToTarget = result.result().getCenter().distanceSquared(eyeTarget.toVector());
            if (distanceSquaredToTarget < best.smallestDistanceSquared) {
                best.smallestDistanceSquared = distanceSquaredToTarget;
                best.closestResult = result;
            }
        });

        if (best.closestResult == null)
            return player.getLocation();

        BoundingBox closestResult = best.closestResult.result();
        ParticleUtil.visualizeBoundingBoxInWorld(world, closestResult, 5, Particle.DRIPPING_HONEY, null);
        return closestResult.getCenter()
                .subtract(new Vector(0, closestResult.getHeight()/2, 0))
                .toLocation(world);
    }

    private static void forEachRotatedCircleGridOffset(@NotNull Location source, @NotNull Consumer<Location> consumer) {
        World world = source.getWorld();

        for (float x = -1; x < 1; x += 0.2) {
            for (float y = -1; y < 1; y += 0.2) {
                if (x*x + y*y > 1)
                    continue;

                Vector grid = new Vector(x, y, 0);
                Vector rotatedCircleGrid = RotationUtil.rotateVectorAroundLocationRotation(grid, source);

                consumer.accept(rotatedCircleGrid.toLocation(world));
            }
        }
    }

    @Override
    public void cancelSpell() {}
}
