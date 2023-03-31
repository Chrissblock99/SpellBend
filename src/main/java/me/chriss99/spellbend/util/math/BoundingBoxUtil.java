package me.chriss99.spellbend.util.math;

import me.chriss99.spellbend.util.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class BoundingBoxUtil {
    /**
     * Checks if the bounding box overlaps any blocks in the world
     *
     * @param world The world to check in
     * @param boundingBox The box to check for
     * @return If it overlaps
     */
    public static boolean BoundingBoxOverlapsBlocks(@NotNull World world, @NotNull BoundingBox boundingBox) {
        for (BoundingBox box : getBlockShapesOverlapping(world, boundingBox))
            if (box.overlaps(boundingBox))
                return true;
        return false;
    }

    /**
     * Gets all bounding boxes of blocks inside the passed bounding box <br>
     * <b>Note: This can contain bounding boxes which do not overlap the given bounding box</b> <br>
     * <b>That is because their blocks may overlap the box but the individual shapes of the block might not</b>
     *
     * @param world The world to get from
     * @param boundingBox The box to get inside
     * @return All boxes which Blocks overlap the bounding box
     */
    public static @NotNull List<BoundingBox> getBlockShapesOverlapping(@NotNull World world, @NotNull BoundingBox boundingBox) {
        //ParticleUtil.visualizeBoundingBoxInWorld(world, boundingBox, 5, Particle.FLAME, null);
        List<BoundingBox> offsetOverlappingBoxes = new LinkedList<>();
        int maxX = (int) Math.floor(boundingBox.getMaxX());
        int maxY = (int) Math.floor(boundingBox.getMaxY());
        int maxZ = (int) Math.floor(boundingBox.getMaxZ());

        for (int x = (int) Math.floor(boundingBox.getMinX()); x <= maxX; x++)
            for (int y = (int) Math.floor(boundingBox.getMinY()); y <= maxY; y++)
                for (int z = (int) Math.floor(boundingBox.getMinZ()); z <= maxZ; z++) {
                    Vector position = new Vector(x, y, z);

                    offsetOverlappingBoxes.addAll(world.getBlockAt(position.toLocation(world).toBlockLocation()).getCollisionShape().getBoundingBoxes().stream()
                            .peek(a -> a.shift(position)).toList());
                }

        //for (BoundingBox box : offsetOverlappingBoxes)
        //    ParticleUtil.visualizeBoundingBoxInWorld(world, box, 5, Particle.SOUL_FIRE_FLAME, null);

        return offsetOverlappingBoxes;
    }

    public static @Nullable BoundingBox boxCast(@NotNull BoundingBox cast, @NotNull BoundingBox onto, @NotNull Vector direction) {
        direction = direction.clone().normalize();
        //Vector castMinimum = cast.getMin(); because math is cool, this isn't needed
        Vector castMaximum = cast.getMax();
        Vector ontoMinimum = onto.getMin();
        //Vector ontoMaximum = cast.getMax(); because math is cool, this isn't needed
        World world = Bukkit.getPlayerExact("Chriss99").getWorld();
        world.spawnParticle(Particle.SOUL_FIRE_FLAME, castMaximum.toLocation(world), 1, 0, 0, 0, 0);
        world.spawnParticle(Particle.FLAME, ontoMinimum.toLocation(world), 1, 0, 0, 0, 0);

        Vector tVector = ontoMinimum.clone().subtract(castMaximum).divide(direction);

        Bukkit.getLogger().info(tVector.toString());
        if (tVector.getX() != tVector.getY() || tVector.getY() != tVector.getZ() || tVector.getX() != tVector.getZ())
            return null;
        double t = tVector.getX() * tVector.getX();

        if (t < 0 || t > cast.getCenter().distanceSquared(onto.getCenter()))
            return null;

        return cast.clone().shift(direction.clone().multiply(tVector.getX())); //not the squared one
    }
}
