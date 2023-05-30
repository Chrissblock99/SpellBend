package me.chriss99.spellbend.util.particle;

import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleUtil {
    public static void drawLine(@NotNull World world, @NotNull Vector a, @NotNull Vector b, int countPerBlock, @NotNull Particle particle, @Nullable Object data) {
        double deltaX = a.getX()-b.getX();
        double deltaY = a.getY()-b.getY();
        double deltaZ = a.getZ()-b.getZ();
        double distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
        int count = (int) (distance*countPerBlock);

        for (int i = 0;i<count;i++) {
            double t = (float) i/(float) count;
            Vector vector = MathUtil.lerpVector(a, b, t);
            Location location = new Location(world, vector.getX(), vector.getY(), vector.getZ());
            world.spawnParticle(particle, location, 1, 0, 0, 0, 0, data);
        }
    }

    public static void visualizeBoundingBoxInWorld(@NotNull World world, @NotNull BoundingBox boundingBox, int countPerBlock, @NotNull Particle particle, @Nullable Object data) {
        Vector topLeftFront = new Vector(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMinZ());
        Vector topRightFront = new Vector(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMinZ());
        Vector bottomLeftFront = new Vector(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ());
        Vector bottomRightFront = new Vector(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMinZ());
        Vector topLeftBack = new Vector(boundingBox.getMinX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
        Vector topRightBack = new Vector(boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
        Vector bottomLeftBack = new Vector(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMaxZ());
        Vector bottomRightBack = new Vector(boundingBox.getMaxX(), boundingBox.getMinY(), boundingBox.getMaxZ());

        drawLine(world, topLeftFront, topRightFront, countPerBlock, particle, data);
        drawLine(world, bottomLeftFront, bottomRightFront, countPerBlock, particle, data);
        drawLine(world, topLeftFront, bottomLeftFront, countPerBlock, particle, data);
        drawLine(world, topRightFront, bottomRightFront, countPerBlock, particle, data);

        drawLine(world, topLeftBack, topRightBack, countPerBlock, particle, data);
        drawLine(world, bottomLeftBack, bottomRightBack, countPerBlock, particle, data);
        drawLine(world, topLeftBack, bottomLeftBack, countPerBlock, particle, data);
        drawLine(world, topRightBack, bottomRightBack, countPerBlock, particle, data);

        drawLine(world, topLeftFront, topLeftBack, countPerBlock, particle, data);
        drawLine(world, topRightFront, topRightBack, countPerBlock, particle, data);
        drawLine(world, bottomLeftFront, bottomLeftBack, countPerBlock, particle, data);
        drawLine(world, bottomRightFront, bottomRightBack, countPerBlock, particle, data);
    }
}
