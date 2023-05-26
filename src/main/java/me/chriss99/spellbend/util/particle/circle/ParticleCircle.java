package me.chriss99.spellbend.util.particle.circle;

import me.chriss99.spellbend.util.math.RotationUtil;
import me.chriss99.spellbend.util.particle.ParticleWithData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ParticleCircle {
    public static @NotNull ParticleCircle XZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new XZCircleVectorProvider(radius), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle XYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new XYCircleVectorProvider(radius), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle rotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new RotatedCircleVectorProvider(new XZCircleVectorProvider(radius), yaw, pitch), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle rotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new RotatedCircleVectorProvider(new XYCircleVectorProvider(radius), yaw, pitch), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle minecraftRotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new CustomRotatedCircleVectorProvider(new XZCircleVectorProvider(radius), RotationUtil::rotateVectorAroundMinecraftYawAndPitch, yaw, pitch), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle minecraftRotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new CustomRotatedCircleVectorProvider(new XYCircleVectorProvider(radius), RotationUtil::rotateVectorAroundMinecraftYawAndPitch, yaw, pitch), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle locationRotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Location rotation, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new LocationRotatedCircleVectorProvider(new XZCircleVectorProvider(radius), rotation), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle locationRotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Location rotation, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new LocationRotatedCircleVectorProvider(new XYCircleVectorProvider(radius), rotation), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle vectorRotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Vector rotation, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new VectorRotatedCircleVectorProvider(new XZCircleVectorProvider(radius), rotation), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle vectorRotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Vector rotation, @NotNull Particle particle, @Nullable Object data)
    {return new ParticleCircle(center, new VectorRotatedCircleVectorProvider(new XYCircleVectorProvider(radius), rotation), particlesPerBlock, particle, data);}

    public static @NotNull ParticleCircle XZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new XZCircleVectorProvider(radius), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle XYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new XYCircleVectorProvider(radius), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle rotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new RotatedCircleVectorProvider(new XZCircleVectorProvider(radius), yaw, pitch), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle rotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new RotatedCircleVectorProvider(new XYCircleVectorProvider(radius), yaw, pitch), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle minecraftRotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new CustomRotatedCircleVectorProvider(new XZCircleVectorProvider(radius), RotationUtil::rotateVectorAroundMinecraftYawAndPitch, yaw, pitch), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle minecraftRotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, double yaw, double pitch, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new CustomRotatedCircleVectorProvider(new XYCircleVectorProvider(radius), RotationUtil::rotateVectorAroundMinecraftYawAndPitch, yaw, pitch), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle locationRotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Location rotation, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new LocationRotatedCircleVectorProvider(new XZCircleVectorProvider(radius), rotation), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle locationRotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Location rotation, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new LocationRotatedCircleVectorProvider(new XYCircleVectorProvider(radius), rotation), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle vectorRotatedXZParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Vector rotation, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new VectorRotatedCircleVectorProvider(new XZCircleVectorProvider(radius), rotation), particlesPerBlock, particleWithDataSupplier);}

    public static @NotNull ParticleCircle vectorRotatedXYParticleCircle(@NotNull Location center, double radius, double particlesPerBlock, @NotNull Vector rotation, @NotNull Supplier<ParticleWithData> particleWithDataSupplier)
    {return new ParticleCircle(center, new VectorRotatedCircleVectorProvider(new XYCircleVectorProvider(radius), rotation), particlesPerBlock, particleWithDataSupplier);}


    private final Location center;
    private final CircleVectorProvider circleVectorProvider;
    private final double circumference;
    private final double radianParticleLength;
    private final Supplier<ParticleWithData> particleWithDataSupplier;

    private ParticleCircle(@NotNull Location center, @NotNull CircleVectorProvider circleVectorProvider, double particlesPerBlock, @NotNull Particle particle, @Nullable Object data) {
        this(center, circleVectorProvider, particlesPerBlock, () -> new ParticleWithData(particle, data));
    }

    private ParticleCircle(@NotNull Location center, @NotNull CircleVectorProvider circleVectorProvider, double particlesPerBlock, @NotNull Supplier<ParticleWithData> particleWithDataSupplier) {
        this.center = center;
        this.circleVectorProvider = circleVectorProvider;
        this.circumference = circleVectorProvider.getCircumference();
        this.radianParticleLength = (2*Math.PI) / Math.ceil(circumference * particlesPerBlock);
        this.particleWithDataSupplier = particleWithDataSupplier;
    }

    public void drawEntireCircle() {
        drawRadianInterval(0, 2*Math.PI);
    }

    public void drawCircumferencePart(double start, double length) {
        drawCircumferenceInterval(start, start+length);
    }

    public void drawCircumferenceInterval(double start, double end) {
        drawRadianInterval((start / circumference) * (2*Math.PI), (end / circumference) * (2*Math.PI));
    }

    public void drawRadianPart(double start, double length) {
        drawRadianInterval(start, start+length);
    }

    public void drawRadianInterval(double start, double end) {
        double nextMultiple = (Math.ceil(start / radianParticleLength)-1) * radianParticleLength;
        //the -1 is there to make the while loop check the first case too
        while (true) {
            nextMultiple += radianParticleLength; //it is re-added here btw
            if (nextMultiple > end)
                break;

            ParticleWithData particleWithData = particleWithDataSupplier.get();
            center.getWorld().spawnParticle(particleWithData.particle(), center.clone().add(circleVectorProvider.getVector(nextMultiple)),
                    1, 0, 0, 0, 0, particleWithData.data());
        }
    }
}
