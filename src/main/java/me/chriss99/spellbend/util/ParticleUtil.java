package me.chriss99.spellbend.util;

import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
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
}
