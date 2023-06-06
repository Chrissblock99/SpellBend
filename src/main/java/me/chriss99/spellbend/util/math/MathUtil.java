package me.chriss99.spellbend.util.math;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MathUtil {
    public static double random(double min, double max) {
        return Math.random() * (max-min) + min;
    }

    public static int randomSign() {
        return (int) Math.round(random(-1, 1));
    }

    public static int clamp(int num, int min, int max) {
        return Math.min(Math.max(num, min), max);
    }

    public static double clamp(double num, double min, double max) {
        return Math.min(Math.max(num, min), max);
    }

    public static double roundToNDigits(double num, int digits) {
        double offset = Math.pow(10, digits);
        return Math.round(num*offset)/offset;
    }

    public static <T> @NotNull T randomEntry(@NotNull T[] a) {
        return a[(int) Math.round(random(0, a.length-1))];
    }

    public static <T> @NotNull T randomEntry(@NotNull List<T> a) {
        return a.get((int) Math.round(random(0, a.size()-1)));
    }

    public static boolean ALargerB(int[] a, int[] b) {
        try {
            for (int i = 0; i < a.length; i++) {
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
        int sum = 0;
        for (int i : a)
            sum += i;
        return sum;
    }

    public static boolean randomChance(double percentage) {
        return percentage >= Math.random();
    }

    public static double lerp(double a, double b, double t) {
        return b + t * (a - b);
    }

    public static @NotNull Vector lerpVector(@NotNull Vector a, @NotNull Vector b, double t) {
        return new Vector(lerp(a.getX(), b.getX(), t), lerp(a.getY(), b.getY(), t), lerp(a.getZ(), b.getZ(), t));
    }
}
