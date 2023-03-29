package me.chriss99.spellbend.util.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class RotationUtil {
    public static Vector rotateVectorAroundRadianYawAndPitch(@NotNull Vector vector, double yaw, double pitch) {
        return vector.clone()
                .rotateAroundX(pitch)
                .rotateAroundY(yaw);
    }

    public static Vector rotateVectorAroundDegreeYawAndPitch(@NotNull Vector vector, double yaw, double pitch) {
        return rotateVectorAroundRadianYawAndPitch(vector, yaw * MathUtil.DEGTORAD, pitch * MathUtil.DEGTORAD);
    }

    public static Vector rotateVectorAroundMinecraftYawAndPitch(@NotNull Vector vector, double yaw, double pitch) {
        return rotateVectorAroundDegreeYawAndPitch(vector, -yaw, pitch);
    }

    public static Vector rotateVectorAroundLocationRotation(@NotNull Vector vector, @NotNull Location location) {
        return rotateVectorAroundMinecraftYawAndPitch(vector, location.getYaw(), location.getPitch());
    }

    public static Vector rotateVectorAroundVectorRotation(@NotNull Vector vector, @NotNull Vector rotation) {
        return rotateVectorAroundRadianYawAndPitch(vector, getRadYaw(rotation), getRadPitch(rotation));
    }

    private static double getRadYaw(Vector vector) {
        vector = vector.clone().normalize();
        return Math.atan2(vector.getX(), vector.getZ());
    }

    private static double getRadPitch(Vector vector) {
        vector = vector.clone().normalize();
        return Math.asin(vector.getY());
    }
}
