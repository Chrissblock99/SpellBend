package me.chriss99.spellbend.util.particle.circle;

import me.chriss99.spellbend.util.math.RotationUtil;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class RotatedCircleVectorProvider implements CircleVectorProvider {
    private final CircleVectorProvider provider;
    private final double yaw;
    private final double pitch;

    public RotatedCircleVectorProvider(@NotNull CircleVectorProvider provider, double yaw, double pitch) {
        this.provider = provider;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public @NotNull Vector getVector(double radians) {
        return RotationUtil.rotateVectorAroundRadianYawAndPitch(provider.getVector(radians), yaw, pitch);
    }

    @Override
    public double getCircumference() {
        return provider.getCircumference();
    }

    @Override
    public double getRadius() {
        return provider.getRadius();
    }
}
