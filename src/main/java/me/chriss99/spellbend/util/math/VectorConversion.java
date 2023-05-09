package me.chriss99.spellbend.util.math;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VectorConversion {

    public static final double PI = Math.PI;
    public static final double DEG_TO_RAD = PI / 180;
    public static final double RAD_TO_DEG =  180 / PI;

    public static float getYaw(@NotNull Vector vector) {
        if (((Double) vector.getX()).equals((double) 0) && ((Double) vector.getZ()).equals((double) 0))
            return 0;
        return (float) (Math.atan2(vector.getZ(), vector.getX()) * RAD_TO_DEG);
    }

    public static float getPitch(@NotNull Vector vector) {
        double xy = Math.sqrt(vector.getX() * vector.getX() + vector.getZ() * vector.getZ());
        return (float) (Math.atan(vector.getY() / xy) * RAD_TO_DEG);
    }

    public static @NotNull Vector setYaw(Vector vector, float yaw) {
        vector = fromYawAndPitch(yaw, getPitch(vector));
        return vector;
    }

    public static @NotNull Vector setPitch(Vector vector, float pitch) {
        vector = fromYawAndPitch(getYaw(vector), pitch);
        return vector;
    }

    public static Vector fromYawAndPitch(float yaw, float pitch) {
        double y = Math.sin(pitch * DEG_TO_RAD);
        double div = Math.cos(pitch * DEG_TO_RAD);
        double x = Math.cos(yaw * DEG_TO_RAD);
        double z = Math.sin(yaw * DEG_TO_RAD);
        x *= div;
        z *= div;
        return new Vector(x,y,z);
    }
}
