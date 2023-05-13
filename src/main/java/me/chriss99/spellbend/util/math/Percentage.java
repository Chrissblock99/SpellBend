package me.chriss99.spellbend.util.math;

import org.jetbrains.annotations.NotNull;

public class Percentage {
    /**
     * Represents the real number part of this percentage <br>
     * It can never be 0, positive or negative infinity or NaN
     */
    private double realPart;
    /**
     * Determines if the percentage is 0, real or infinity <br>
     * at 0 the percentage is its real number part <br>
     * at positive values the percentage is 0 with the sign of the real number part <br>
     * at negative values it is infinity with the sign of the real number part
     */
    private int indicator;

    public Percentage(double realPart, int indicator) {
        if (realPart == 0 ||
                realPart == Double.POSITIVE_INFINITY || realPart == Double.NEGATIVE_INFINITY ||
                Double.isNaN(realPart))
            throw new IllegalArgumentException("realNumberPercentage can not be " + realPart + "!");
        this.realPart = realPart;
        this.indicator = indicator;
    }

    public double getPercentage() {
        if (indicator == 0)
            return realPart;

        if (indicator > 0)
            return realPart * 0;

        return realPart / 0;
    }

    public double getRealPart() {
        return realPart;
    }

    public int getIndicator() {
        return indicator;
    }

    public void multiply(@NotNull Percentage percentage) {
        indicator += percentage.indicator;
        multiply(percentage.realPart);
    }

    public void multiply(double percentage) {
        if (Double.isNaN(percentage))
            throw new IllegalArgumentException("percentage cannot be NaN!");
        if (percentage == 1)
            return;

        double newRealPart = this.realPart * percentage;
        if (newRealPart == 0d) {
            indicator++;
            fixSign(percentage);
            return;
        }

        if (newRealPart == Double.POSITIVE_INFINITY || newRealPart == Double.NEGATIVE_INFINITY) {
            indicator--;
            fixSign(percentage);
            return;
        }

        this.realPart = newRealPart;
    }

    private void fixSign(double percentage) {
        double sign = Math.copySign(1, percentage);
        if (String.valueOf(percentage).equals("-0.0"))
            sign = -1;
        realPart *= sign;
    }

    public void divide(@NotNull Percentage percentage) {
        indicator -= percentage.indicator;
        divide(percentage.realPart);
    }

    public void divide(double percentage) {
        if (Double.isNaN(percentage))
            throw new IllegalArgumentException("percentage cannot be NaN!");
        if (percentage == 1)
            return;

        double newRealPart = this.realPart / percentage;
        if (newRealPart == 0d) {
            indicator++;
            fixSign(percentage);
            return;
        }

        if (newRealPart == Double.POSITIVE_INFINITY || newRealPart == Double.NEGATIVE_INFINITY) {
            indicator--;
            fixSign(percentage);
            return;
        }

        this.realPart = newRealPart;
    }

    public @NotNull Percentage clone() {
        return new Percentage(realPart, indicator);
    }
}
