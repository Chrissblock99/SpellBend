package me.chriss99.spellbend.util.particle;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EllipseVectorProviderTest {
    @Test
    void matrixIs111222000() {
        assertEquals(new Matrix3d(
                1, 1, 1,
                2, 2, 2,
                0, 0, 0
        ), new EllipseVectorProvider(new Vector3d(1, 1, 1), new Vector3d(2, 2, 2)).getMatrix());
    }

    @Test
    void matrixIs010001000() {
        assertEquals(new Matrix3d(
                1, 0, 0,
                0, 0, 1,
                0, 0, 0
        ), EllipseVectorProvider.fromStartAndNormal(new Vector3d(1, 0, 0), new Vector3d(0, 1, 0)).getMatrix());
    }
}