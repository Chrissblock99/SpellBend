package me.chriss99.spellbend.util.particle;

import org.joml.Matrix3d;
import org.joml.Vector3d;

public class EllipseVectorProvider {
    private final Matrix3d matrix;

    public static EllipseVectorProvider fromStartAndNormal(Vector3d start, Vector3d normal) {
        Vector3d cross = new Vector3d();
        start.cross(normal.normalize(), cross);
        return new EllipseVectorProvider(start, cross);
    }

    public EllipseVectorProvider(Vector3d iHat, Vector3d jHat) {
        matrix = new Matrix3d().setColumn(0, iHat).setColumn(1, jHat).setColumn(2, new Vector3d(0, 0, 0));
    }

    public Vector3d getVector(double radians) {
        return matrix.transform(new Vector3d(Math.cos(radians), Math.sin(radians), 0));
    }

    public Matrix3d getMatrix() {
        return matrix;
    }
}
