package me.chriss99.spellbend.util.particle.circle;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class XYCircleVectorProvider extends BaseCircleVectorProvider {
    public XYCircleVectorProvider(double radius) {
        super(radius);
    }

    @Override
    public @NotNull Vector getVector(double radians) {
        return new Vector(Math.cos(radians) * radius, Math.sin(radians) * radius, 0);
    }
}
