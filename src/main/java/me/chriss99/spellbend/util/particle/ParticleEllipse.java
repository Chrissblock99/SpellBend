package me.chriss99.spellbend.util.particle;

import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.function.Supplier;

public class ParticleEllipse {
    private final Location center;
    private final EllipseVectorProvider ellipseVectorProvider;
    private final Supplier<SizedParticleWithData> sizedParticleWithDataSupplier;

    private ParticleEllipse(@NotNull Location center, @NotNull EllipseVectorProvider ellipseVectorProvider, @NotNull Particle particle, @Nullable Object data, double particleSize) {
        this(center, ellipseVectorProvider, () -> new SizedParticleWithData(particle, data, particleSize));
    }

    private ParticleEllipse(@NotNull Location center, @NotNull EllipseVectorProvider ellipseVectorProvider, @NotNull Supplier<SizedParticleWithData> sizedParticleWithDataSupplier) {
        this.center = center;
        this.ellipseVectorProvider = ellipseVectorProvider;
        this.sizedParticleWithDataSupplier = sizedParticleWithDataSupplier;
    }

    private double ellipseSegmentLength(double aRad, double bRad) {
        double acceptableInaccuracy = 0.1;
        int iterations = 10;
        double lastSum = 0;

        for (int i = 1; i <= 5; i++) {
            double sum = 0;
            double oneOverIterations = 1f/iterations;
            Vector3d lastPoint = ellipseVectorProvider.getVector(aRad);

            for (int j = 1; j < iterations; j++) {
                double t = j * oneOverIterations;
                Vector3d point = ellipseVectorProvider.getVector(MathUtil.lerp(aRad, bRad, t));

                sum += lastPoint.distance(point);
                lastPoint = point;
            }

            if (sum - lastSum < acceptableInaccuracy)
                return sum;

            lastSum = sum;
            iterations *= 2;
        }

        return lastSum;
    }

    private static double ellipseSegmentLengthTest(double aRad, double bRad, @NotNull EllipseVectorProvider ellipseVectorProvider) {
        double acceptableInaccuracy = 0.1;
        int iterations = 10;
        double lastSum = 0;

        for (int i = 1; i <= 5; i++) {
            double sum = 0;
            double oneOverIterations = 1f/iterations;
            Vector3d lastPoint = ellipseVectorProvider.getVector(aRad);

            for (int j = 1; j < iterations; j++) {
                double t = j * oneOverIterations;
                Vector3d point = ellipseVectorProvider.getVector(MathUtil.lerp(aRad, bRad, t));

                sum += lastPoint.distance(point);
                lastPoint = point;
            }

            if (sum - lastSum < acceptableInaccuracy)
                return sum;

            lastSum = sum;
            iterations *= 2;
        }

        return lastSum;
    }

    public static void test() {
        EllipseVectorProvider ellipseVectorProvider1 = new EllipseVectorProvider(new Vector3d(1, 0, 0), new Vector3d(0, 1, 0));
        double length = ellipseSegmentLengthTest(0, Math.PI*2f, ellipseVectorProvider1);
        System.out.println(length);
    }

    /*public void drawEntireEllipse() {
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

            SizedParticleWithData sizedParticleWithData = sizedParticleWithDataSupplier.get();
            Vector3d vector = ellipseVectorProvider.getVector(nextMultiple);
            center.getWorld().spawnParticle(sizedParticleWithData.particle(), center.clone().add(vector.x, vector.y, vector.z),
                    1, 0, 0, 0, 0, sizedParticleWithData.data());
        }
    }*/
}
