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

import java.util.List;

public class Flash extends Spell {
    public Flash(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0, 0, 0, 5}));

        Location location = rayTraceBlocks(caster.getEyeLocation(), caster.getEyeLocation().getDirection(), 6, caster.getBoundingBox());
        //Location location = findNearest(caster.getEyeLocation(), 6, caster.getBoundingBox()).add(0, -1.62, 0);
        //caster.teleport(location);
        //caster.setVelocity(caster.getVelocity().add(location.getDirection().multiply(0.5)));

        World world = caster.getWorld();
        //eTrail(location 1.3 above player,{_l},2)
        world.playSound(location, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 2f, 1.2f);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 2f, 1.2f);
        world.spawnParticle(Particle.VILLAGER_ANGRY, location, 3);

        naturalSpellEnd();
    }

    private static Location findNearest(@NotNull Location location, double distance, @NotNull BoundingBox rayShape) {
        Location bestCase = location.clone().add(location.getDirection().clone().multiply(distance));
        World world = location.getWorld();

        if (world.rayTraceBlocks(location, location.getDirection(), distance) == null)
            return bestCase;

        var currentBest = new Object() {
            Location nearest = location;
            double smallestDistanceSquared = Double.MAX_VALUE;
        };

        forEachRotatedCircleGrid(location, circleGridLocation -> {
            Location hit = rayTraceBlocks(circleGridLocation, location.getDirection(), 6, rayShape);
            ParticleUtil.visualizeBoundingBoxInWorld(world,
                    rayShape.clone()
                            .shift(hit.clone()
                                    .add(circleGridLocation.clone().multiply(-1))),
                    5, Particle.DRIPPING_HONEY, null);

            double distanceSquared = currentBest.nearest.distanceSquared(hit);
            if (distanceSquared < currentBest.smallestDistanceSquared) {
                currentBest.nearest = hit;
                currentBest.smallestDistanceSquared = distanceSquared;
            }
        });

        return currentBest.nearest;
    }

    private static void forEachRotatedCircleGrid(@NotNull Location source, @NotNull Consumer<Location> consumer) {
        World world = source.getWorld();

        for (float x = -1; x < 1; x += 0.2) {
            for (float y = -1; y < 1; y += 0.2) {
                if (x*x + y*y > 1)
                    continue;

                Vector grid = new Vector(x, y, 0);
                Vector rotatedCircleGrid = RotationUtil.rotateVectorAroundLocationRotation(grid, source);
                Location circleGridLocation = source.clone().add(rotatedCircleGrid);
                world.spawnParticle(Particle.DRIPPING_HONEY, circleGridLocation, 1, 0, 0, 0, 0);

                consumer.accept(circleGridLocation);
            }
        }
    }

    private static Location rayTraceBlocks(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull BoundingBox rayShape) {
        World world = start.getWorld();
        Vector startVector = start.toVector();
        Vector distanceVector = direction.clone().normalize().multiply(maxDistance);
        Vector raySize = new Vector(rayShape.getWidthX()/2d, rayShape.getHeight()/2d, rayShape.getWidthZ()/2d);
        BoundingBox tracingArea = BoundingBox.of(rayShape.getCenter(), rayShape.getCenter()).expandDirectional(distanceVector).expand(raySize);
        List<BoundingBox> blockCollisionShapeBoxes = BoundingBoxUtil.getBlockShapesOverlapping(world, tracingArea);

        Location nearestHitLocation = null;
        double nearestDistanceSq = Double.MAX_VALUE;

        for (BoundingBox boundingBox : blockCollisionShapeBoxes) {
            BoundingBox hitResult = BoundingBoxUtil.boxCast(rayShape, boundingBox, direction);
            if (hitResult == null)
                continue;
            ParticleUtil.visualizeBoundingBoxInWorld(world, hitResult, 5, Particle.DRIPPING_HONEY, null);

            double distanceSquared = startVector.distanceSquared(hitResult.getCenter());
            if (distanceSquared < nearestDistanceSq) {
                nearestHitLocation = hitResult.getCenter().toLocation(world);
                nearestDistanceSq = distanceSquared;
            }
        }

        return (nearestHitLocation == null) ?
                start.clone().add(distanceVector) :
                nearestHitLocation;
    }

    @Override
    public void cancelSpell() {}
}
