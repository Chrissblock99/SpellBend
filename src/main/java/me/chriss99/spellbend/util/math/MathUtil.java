package me.chriss99.spellbend.util.math;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MathUtil {
    public static final double DEGTORAD = Math.PI/180;
    public static final double RADTODEG = 180/Math.PI;

    public static double random(double min, double max) {
        return Math.random() * (max-min) + min;
    }

    public static @NotNull Object randomEntry(@NotNull Object[] a) {
        return a[(int) Math.round(random(0, a.length-1))];
    }

    public static @NotNull Object randomEntry(@NotNull ArrayList<?> a) {
        return a.get((int) Math.round(random(0, a.size()-1)));
    }

    public static boolean ALargerB(int[] a, int[] b) {
        try {
            for (int i = 0;i<a.length;i++) {
                if (a[i]>b[i]) return true;
                if (a[i]<b[i]) return false;
            }
            return false;
        } catch (IndexOutOfBoundsException exception) {
            return true;
        }
    }

    public static boolean ALargerB(long[] a, long[] b) {
        try {
            for (int i = 0;i<a.length;i++) {
                if (a[i]>b[i]) return true;
                if (a[i]<b[i]) return false;
            }
            return false;
        } catch (IndexOutOfBoundsException exception) {
            return true;
        }
    }

    public static boolean ASmallerB(int[] a, int[] b) {
        try {
            for (int i = 0;i<a.length;i++) {
                if (a[i]<b[i]) return true;
                if (a[i]>b[i]) return false;
            }
            return false;
        } catch (IndexOutOfBoundsException exception) {
            return false;
        }
    }

    public static boolean ASmallerB(long[] a, long[] b) {
        try {
            for (int i = 0;i<a.length;i++) {
                if (a[i]<b[i]) return true;
                if (a[i]>b[i]) return false;
            }
            return false;
        } catch (IndexOutOfBoundsException exception) {
            return false;
        }
    }

    public static int additiveArrayValue(int[] a) {
        for (int i = 1;i<a.length;i++) a[0] += a[i];
        return a[0];
    }

    public static @NotNull Vector lerp(@NotNull Vector a, @NotNull Vector b, float t) {
        return new Vector(b.getX() + t * (a.getX() - b.getX()), b.getY() + t * (a.getY() - b.getY()), b.getZ() + t * (a.getZ() - b.getZ()));
    }
}
