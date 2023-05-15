package me.chriss99.spellbend.util.math;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a value which can be multiplied and divided by zero and infinite values <br>
 * <br>
 * Internally two values are used: the real part and the indicator <br>
 * The real part is represented as a double which can never be 0, infinite or NaN <br>
 * whenever the percentage is multiplied or divided by a number that is not zero or infinite this is too <br>
 * in the case of multiplying or dividing by zero or infinite values the indicator is modified following this table: <br>
 * <table>
 *     <tr>
 *         <th></th>
 *         <th>zero</th>
 *         <th>infinite</th>
 *     </tr>
 *     <tr>
 *         <th>multiplying</th>
 *         <th>+1</th>
 *         <th>-1</th>
 *     </tr>
 *     <tr>
 *         <th>dividing</th>
 *         <th>-1</th>
 *         <th>+1</th>
 *     </tr>
 * </table>
 * additionally in the case of negative infinity the sign is inverted <br>
 * <br>
 * The value that <code>getPercentage()</code> returns depends on the indicator <br>
 * at 0 it will return the real part <br>
 * at positive values 0 <br>
 * at negative values infinity with the sign of the real part
 */
public class Percentage {
    /**
     * Represents the real number part of this percentage <br>
     * It can never be 0, infinite or NaN
     */
    private double realPart;
    /**
     * Determines if the percentage is 0, real or infinite <br>
     * at 0 the percentage is its real number part <br>
     * at positive values the percentage is 0 <br>
     * at negative values it is infinity with the sign of the real number part
     */
    private int indicator;

    /**
     * Constructs a Percentage with the given real part and indicator
     *
     * @throws IllegalArgumentException If the real part is zero, infinite or NaN
     *
     * @param realPart The real part of the new percentage
     * @param indicator The special case indicator for the new percentage
     */
    public Percentage(double realPart, int indicator) {
        if (realPart == 0 ||
                realPart == Double.POSITIVE_INFINITY || realPart == Double.NEGATIVE_INFINITY ||
                Double.isNaN(realPart))
            throw new IllegalArgumentException("realNumberPercentage can not be " + realPart + "!");
        this.realPart = realPart;
        this.indicator = indicator;
    }

    /**
     * Returns the extended real number represented by this
     *
     * @return The extended real number represented by this
     */
    public double getPercentage() {
        if (indicator == 0d)
            return realPart;

        if (indicator > 0d)
            return realPart * 0d;

        return realPart / 0d;
    }

    /**
     * Returns the real part of this percentage
     *
     * @return The real part of this percentage
     */
    public double getRealPart() {
        return realPart;
    }

    /**
     * Returns the special case indicator of this percentage
     *
     * @return The special case indicator of this percentage
     */
    public int getIndicator() {
        return indicator;
    }

    /**
     * Multiplies this percentage with another percentage
     *
     * @param percentage The percentage to multiply with
     */
    public void multiply(@NotNull Percentage percentage) {
        indicator += percentage.indicator;
        multiply(percentage.realPart);
    }

    /**
     * Multiplies this percentage with an extended real number
     *
     * @param percentage The extended real number to multiply with
     */
    public void multiply(double percentage) {
        if (Double.isNaN(percentage))
            throw new IllegalArgumentException("percentage cannot be NaN!");
        if (percentage == 1)
            return;

        changeRealPart(realPart * percentage, percentage);
    }

    /**
     * Divides this percentage by another percentage
     *
     * @param percentage The percentage to divide by
     */
    public void divide(@NotNull Percentage percentage) {
        indicator -= percentage.indicator;
        divide(percentage.realPart);
    }

    /**
     * Divides this percentage by an extended real number
     *
     * @param percentage The extended real number to divide by
     */
    public void divide(double percentage) {
        if (Double.isNaN(percentage))
            throw new IllegalArgumentException("percentage cannot be NaN!");
        if (percentage == 1)
            return;

        changeRealPart(realPart / percentage, percentage);
    }

    /**
     * Checks for zero and infinite special cases and applies the value change if not present <br>
     * If an edge case is present the sign of the "percentage" parameter is multiplied onto the real part
     *
     * @param newRealPart The new real part, zero and infinite values are handled here
     * @param percentage The number which sign to use if an edge case is present
     */
    private void changeRealPart(double newRealPart, double percentage) {
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

    /**
     * Multiplies the sign of the given number onto the real part
     *
     * @param percentage The number which sign to multiply onto the real part
     */
    private void fixSign(double percentage) {
        realPart *= Math.copySign(1, percentage);
    }

    /**
     * Returns a new Percentage with the same values as this one
     *
     * @return A clone of this percentage
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public @NotNull Percentage clone() {
        return new Percentage(realPart, indicator);
    }
}
