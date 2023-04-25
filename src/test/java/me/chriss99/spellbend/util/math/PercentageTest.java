package me.chriss99.spellbend.util.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PercentageTest {
    @Test
    void aMulBDivBEqualsA() {
        assertAMulBDivBEqualA(1, 1);
        assertAMulBDivBEqualA(Double.MIN_VALUE, 1);
        assertAMulBDivBEqualA(Double.MAX_VALUE, 1);

        assertAMulBDivBEqualA(1, 0);
        assertAMulBDivBEqualA(Double.MIN_VALUE, 0);
        assertAMulBDivBEqualA(Double.MAX_VALUE, 0);

        assertAMulBDivBEqualA(1, Double.MIN_VALUE);
        //assertAMulBDivBEqualA(Double.MIN_VALUE, Double.MIN_VALUE);
        assertAMulBDivBEqualA(Double.MAX_VALUE, Double.MIN_VALUE);

        assertAMulBDivBEqualA(1, Double.MAX_VALUE);
        assertAMulBDivBEqualA(Double.MIN_VALUE, Double.MAX_VALUE);
        //assertAMulBDivBEqualA(Double.MAX_VALUE, Double.MAX_VALUE);

        assertAMulBDivBEqualA(1, Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(Double.MIN_VALUE, Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(Double.MAX_VALUE, Double.POSITIVE_INFINITY);


        assertAMulBDivBEqualA(-1, 1);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, 1);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, 1);

        assertAMulBDivBEqualA(-1, 0);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, 0);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, 0);

        assertAMulBDivBEqualA(-1, Double.MIN_VALUE);
        //assertAMulBDivBEqualA(-Double.MIN_VALUE, Double.MIN_VALUE);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, Double.MIN_VALUE);

        assertAMulBDivBEqualA(-1, Double.MAX_VALUE);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, Double.MAX_VALUE);
        //assertAMulBDivBEqualA(-Double.MAX_VALUE, Double.MAX_VALUE);

        assertAMulBDivBEqualA(-1, Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, Double.POSITIVE_INFINITY);


        assertAMulBDivBEqualA(1, -1);
        assertAMulBDivBEqualA(Double.MIN_VALUE, -1);
        assertAMulBDivBEqualA(Double.MAX_VALUE, -1);

        assertAMulBDivBEqualA(1, -0);
        assertAMulBDivBEqualA(Double.MIN_VALUE, -0);
        assertAMulBDivBEqualA(Double.MAX_VALUE, -0);

        assertAMulBDivBEqualA(1, -Double.MIN_VALUE);
        //assertAMulBDivBEqualA(Double.MIN_VALUE, -Double.MIN_VALUE);
        assertAMulBDivBEqualA(Double.MAX_VALUE, -Double.MIN_VALUE);

        assertAMulBDivBEqualA(1, -Double.MAX_VALUE);
        assertAMulBDivBEqualA(Double.MIN_VALUE, -Double.MAX_VALUE);
        //assertAMulBDivBEqualA(Double.MAX_VALUE, -Double.MAX_VALUE);


        assertAMulBDivBEqualA(-1, -Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, -Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, -Double.POSITIVE_INFINITY);

        assertAMulBDivBEqualA(-1, -1);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, -1);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, -1);

        assertAMulBDivBEqualA(-1, -0);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, -0);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, -0);

        assertAMulBDivBEqualA(-1, -Double.MIN_VALUE);
        //assertAMulBDivBEqualA(-Double.MIN_VALUE, -Double.MIN_VALUE);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, -Double.MIN_VALUE);

        assertAMulBDivBEqualA(-1, -Double.MAX_VALUE);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, -Double.MAX_VALUE);
        //assertAMulBDivBEqualA(-Double.MAX_VALUE, -Double.MAX_VALUE);

        assertAMulBDivBEqualA(-1, -Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(-Double.MIN_VALUE, -Double.POSITIVE_INFINITY);
        assertAMulBDivBEqualA(-Double.MAX_VALUE, -Double.POSITIVE_INFINITY);
    }

    private static void assertAMulBDivBEqualA(double a, double b) {
        var percentageA = new Percentage(a, 0);
        percentageA.multiply(b);
        percentageA.divide(b);
        assertEquals(a, percentageA.getPercentage());
    }

    @Test
    void effectively0mulEffectivelyInfinityEqualsX() {
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(1, 1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(1, Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(1, Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MIN_VALUE, 1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MIN_VALUE, Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MIN_VALUE, Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MAX_VALUE, 1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MAX_VALUE, Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MAX_VALUE, Double.MAX_VALUE);


        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-1, 1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-1, Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-1, Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MIN_VALUE, 1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MIN_VALUE, Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MIN_VALUE, Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MAX_VALUE, 1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MAX_VALUE, Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MAX_VALUE, Double.MAX_VALUE);


        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(1, -1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(1, -Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(1, -Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MIN_VALUE, -1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MIN_VALUE, -Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MIN_VALUE, -Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MAX_VALUE, -1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MAX_VALUE, -Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(Double.MAX_VALUE, -Double.MAX_VALUE);


        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-1, -1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-1, -Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-1, -Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MIN_VALUE, -1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MIN_VALUE, -Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MIN_VALUE, -Double.MAX_VALUE);

        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MAX_VALUE, -1);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MAX_VALUE, -Double.MIN_VALUE);
        assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(-Double.MAX_VALUE, -Double.MAX_VALUE);
    }

    private static void assertAEffectivelyZeroMulBEffectivelyZeroEqualsAMulB(double a, double b) {
        var percentageA = new Percentage(a, 1);
        var percentageB = new Percentage(b, -1);
        percentageA.multiply(percentageB);
        double aMulB = a*b;
        if (aMulB == -0)
            aMulB = 0;
        double effectively = percentageA.getPercentage();
        if (effectively == -0)
            effectively = 0;
        assertEquals(aMulB , effectively);
    }
}