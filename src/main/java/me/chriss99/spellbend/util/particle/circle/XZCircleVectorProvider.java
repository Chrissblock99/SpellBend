package me.chriss99.spellbend.util.particle.circle;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class XZCircleVectorProvider extends BaseCircleVectorProvider {
    public XZCircleVectorProvider(double radius) {
        super(radius);
    }

    @Override
    public @NotNull Vector getVector(double radians) {
        return new Vector(Math.cos(radians) * radius, 0, Math.sin(radians) * radius);
    }
}
