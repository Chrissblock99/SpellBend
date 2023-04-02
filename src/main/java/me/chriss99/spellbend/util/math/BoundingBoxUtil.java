package me.chriss99.spellbend.util.math;

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
        double smallestT = Double.MAX_VALUE;
        Vector smallestOffset = null;

        Vector minO = onto.getMin().clone().subtract(cast.getMax());
        Vector maxO = onto.getMax().clone().subtract(cast.getMin());

        if (direction.getX() != 0) {
            double t = minO.getX() / direction.getX();
            if (t >= 0) {
                Vector offset = new Vector(minO.getX(), direction.getY() * t, 0);
                if (minO.getY() < offset.getY() && offset.getY() < maxO.getY()) {
                    offset.setZ(direction.getZ() * t);
                    if (minO.getZ() < offset.getZ() && offset.getZ() < maxO.getZ()) {
                        smallestT = t;
                        smallestOffset = offset;
                    }
                }
            }

            t = maxO.getX() / direction.getX();
            if (t >= 0 && t < smallestT) {
                Vector offset = new Vector(maxO.getX(), direction.getY() * t, 0);
                if (minO.getY() < offset.getY() && offset.getY() < maxO.getY()) {
                    offset.setZ(direction.getZ() * t);
                    if (minO.getZ() < offset.getZ() && offset.getZ() < maxO.getZ()) {
                        smallestT = t;
                        smallestOffset = offset;
                    }
                }
            }
        }

        if (direction.getY() != 0) {
            double t = minO.getY() / direction.getY();
            if (t >= 0 && t < smallestT) {
                Vector offset = new Vector(direction.getX() * t, minO.getY(), 0);
                if (minO.getX() < offset.getX() && offset.getX() < maxO.getX()) {
                    offset.setZ(direction.getZ() * t);
                    if (minO.getZ() < offset.getZ() && offset.getZ() < maxO.getZ()) {
                        smallestT = t;
                        smallestOffset = offset;
                    }
                }
            }

            t = maxO.getY() / direction.getY();
            if (t >= 0 && t < smallestT) {
                Vector offset = new Vector(direction.getX() * t, maxO.getY(), 0);
                if (minO.getX() < offset.getX() && offset.getX() < maxO.getX()) {
                    offset.setZ(direction.getZ() * t);
                    if (minO.getZ() < offset.getZ() && offset.getZ() < maxO.getZ()) {
                        smallestT = t;
                        smallestOffset = offset;
                    }
                }
            }
        }

        if (direction.getZ() != 0) {
            double t = minO.getZ() / direction.getZ();
            if (t >= 0 && t < smallestT) {
                Vector offset = new Vector(direction.getX() * t, 0, minO.getZ());
                if (minO.getX() < offset.getX() && offset.getX() < maxO.getX()) {
                    offset.setY(direction.getY() * t);
                    if (minO.getY() < offset.getY() && offset.getY() < maxO.getY()) {
                        smallestT = t;
                        smallestOffset = offset;
                    }
                }
            }

            t = maxO.getZ() / direction.getZ();
            if (t >= 0 && t < smallestT) {
                Vector offset = new Vector(direction.getX() * t, 0, maxO.getZ());
                if (minO.getX() < offset.getX() && offset.getX() < maxO.getX()) {
                    offset.setY(direction.getY() * t);
                    if (minO.getY() < offset.getY() && offset.getY() < maxO.getY())// {
                        //smallestT = t;
                        smallestOffset = offset;
                    //}
                }
            }
        }

        if (smallestOffset == null)
            return null;

        return cast.clone().shift(smallestOffset);
    }

    /**
     * Takes two bounding boxes and returns the bounding box on which both intersect <br>
     * if they intersect all side lengths of the returned box will be positive <br>
     * if their intersection is a plane segment one of the side lengths will be 0 and the others positive <br>
     * if their intersection is a line segment two of the side lengths will be 0 and the others positive <br>
     * if their intersection is a point all side lengths will be 0 <br>
     * if they do not intersect the side lengths will be 0 with at least one being negative
     *
     * @param box1 The first bounding box
     * @param box2 The second bounding box
     * @return Their intersection
     */
    private static @NotNull Box getMathIntersection(@NotNull BoundingBox box1, @NotNull BoundingBox box2) {
        return new Box(
                Math.max(box1.getMinX(), box2.getMinX()),
                Math.max(box1.getMinY(), box2.getMinY()),
                Math.max(box1.getMinZ(), box2.getMinZ()),

                Math.min(box1.getMaxX(), box2.getMaxX()),
                Math.min(box1.getMaxY(), box2.getMaxY()),
                Math.min(box1.getMaxZ(), box2.getMaxZ())
                );
    }

    /**
     * A bounding box representation with more mathematical meaning <br>
     * used by <code>getMathInterSection()</code>
     *
     * @param min The minimum Vector
     * @param max The maximum Vector
     */
    private record Box(@NotNull Vector min, @NotNull Vector max) {
        public Box(double x1, double y1, double z1, double x2, double y2, double z2) {
            this(new Vector(x1, y1, z1), new Vector(x2, y2, z2));
        }

        /**
         * Represents this box as a bounding box
         *
         * @return This box as a bounding box
         */
        public @NotNull BoundingBox toBoundingBox() {
            return new BoundingBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
        }

        /**
         * zero if touching <br>
         * positive if intersecting <br>
         * negative if not touching
         *
         * @return The length on this axis
         */
        public double getLengthX() {
            return max.getX() - min.getX();
        }

        /**
         * zero if touching <br>
         * positive if intersecting <br>
         * negative if not touching
         *
         * @return The length on this axis
         */
        public double getLengthY() {
            return max.getY() - min.getY();
        }

        /**
         * zero if touching <br>
         * positive if intersecting <br>
         * negative if not touching
         *
         * @return The length on this axis
         */
        public double getLengthZ() {
            return max.getZ() - min.getZ();
        }
    }
}
