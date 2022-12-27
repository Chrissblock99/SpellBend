package me.chriss99.spellbend.util;

import org.jetbrains.annotations.NotNull;

public class TextUtil {
    /**
     * Capitalizes the first character in the String and puts the rest into lower case
     *
     * @param string The String to standard Capitalize
     * @return The standard capitalized String
     */
    public static @NotNull String standardCapitalize(@NotNull String string) {
        if (string.length()<1)
            return string.toUpperCase();

        return String.valueOf(string.charAt(0)).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String roundToNDecimalPlaces(double a, int n) {
        int multiplier = (int) Math.pow(10, n);
        a *= multiplier;
        a = Math.round(a);
        a /= multiplier;
        return String.valueOf(a);
    }
}
