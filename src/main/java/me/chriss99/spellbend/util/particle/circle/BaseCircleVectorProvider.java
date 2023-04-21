package me.chriss99.spellbend.util.particle.circle;

public abstract class BaseCircleVectorProvider implements CircleVectorProvider {
    protected final double radius;
    private final double circumference;

    public BaseCircleVectorProvider(double radius) {
        this.radius = radius;
        circumference = 2*Math.PI*radius;
    }

    @Override
    public double getCircumference() {
        return circumference;
    }

    @Override
    public double getRadius() {
        return radius;
    }
}
