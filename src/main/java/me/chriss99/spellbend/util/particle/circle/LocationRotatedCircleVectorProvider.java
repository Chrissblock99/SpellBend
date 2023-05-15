package me.chriss99.spellbend.util.particle.circle;

import me.chriss99.spellbend.util.math.RotationUtil;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class LocationRotatedCircleVectorProvider implements CircleVectorProvider {
    private final CircleVectorProvider provider;
    private final Location rotation;

    public LocationRotatedCircleVectorProvider(@NotNull CircleVectorProvider provider, @NotNull Location rotation) {
        this.provider = provider;
        this.rotation = rotation;
    }

    @Override
    public @NotNull Vector getVector(double radians) {
        return RotationUtil.rotateVectorAroundLocationRotation(provider.getVector(radians), rotation);
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
