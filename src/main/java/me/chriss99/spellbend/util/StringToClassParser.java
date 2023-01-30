package me.chriss99.spellbend.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Parses a given string to the class represented by the Class instance, throwing exceptions if it goes wrong <br>
 * <b>This should only return null if the string actually represents a null value in the classes serialization</b>
 *
 * @param <C> The type of the returned class that the string will be parsed to
 */
@FunctionalInterface
public interface StringToClassParser<C> {
    /**
     * Parses the string to the class which the given Class instance represents, throwing exceptions if something goes wrong<br>
     * <b>This should only return null if the string actually represents a null value in the classes serialization</b>
     *
     * @param parseFrom The string to parse from
     * @param parseTo   The Class instance of the class to parse to
     * @return The class which the Class instance represents, parsed from the string
     * @throws Exception Any exception that might happen during parsing
     */
    @Nullable C parseStringToClass(@NotNull String parseFrom, @NotNull Class<C> parseTo) throws Exception;
}
