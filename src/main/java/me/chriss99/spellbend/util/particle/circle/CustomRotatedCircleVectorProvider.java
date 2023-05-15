package me.chriss99.spellbend.util.particle.circle;

import me.chriss99.spellbend.util.TriFunction;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CustomRotatedCircleVectorProvider implements CircleVectorProvider {
    private final CircleVectorProvider provider;
    private final TriFunction<Vector, Double, Double, Vector> vectorRotator;
    private final double yaw;
    private final double pitch;

    public CustomRotatedCircleVectorProvider(@NotNull CircleVectorProvider provider, @NotNull TriFunction<Vector, Double, Double, Vector> vectorRotator, double yaw, double pitch) {
        this.provider = provider;
        this.vectorRotator = vectorRotator;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public @NotNull Vector getVector(double radians) {
        return vectorRotator.apply(provider.getVector(radians), yaw, pitch);
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
