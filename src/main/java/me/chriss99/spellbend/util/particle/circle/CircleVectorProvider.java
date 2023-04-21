package me.chriss99.spellbend.util.particle.circle;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public interface CircleVectorProvider {
    @NotNull Vector getVector(double radians);
    double getCircumference();
    double getRadius();
}
