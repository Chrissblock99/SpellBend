package me.chriss99.spellbend.util.particle.circle;

import me.chriss99.spellbend.util.math.RotationUtil;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VectorRotatedCircleVectorProvider implements CircleVectorProvider {
    private final CircleVectorProvider provider;
    private final Vector rotation;

    public VectorRotatedCircleVectorProvider(@NotNull CircleVectorProvider provider, @NotNull Vector rotation) {
        this.provider = provider;
        this.rotation = rotation;
    }

    @Override
    public @NotNull Vector getVector(double radians) {
        return RotationUtil.rotateVectorAroundVectorRotation(provider.getVector(radians), rotation);
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
