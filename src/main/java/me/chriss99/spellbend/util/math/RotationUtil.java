package me.chriss99.spellbend.util.math;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class RotationUtil {
    public static Vector rotateVectorAroundMinecraftYawAndPitch(@NotNull Vector vector, double yaw, double pitch) {
        return vector.clone()
                .rotateAroundX(pitch * MathUtil.DEGTORAD)
                .rotateAroundY(-yaw * MathUtil.DEGTORAD);
    }

    public static Vector rotateVectorAroundLocationRotation(@NotNull Vector vector, @NotNull Location location) {
        return rotateVectorAroundMinecraftYawAndPitch(vector, location.getYaw(), location.getPitch());
    }
}
