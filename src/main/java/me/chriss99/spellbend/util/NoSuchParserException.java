package me.chriss99.spellbend.util;

/**
 * This exception is thrown when CustomizableStringToClassParser does not have a parser for a given class
 */
public class NoSuchParserException extends IllegalArgumentException {
    public NoSuchParserException() {
    }

    public NoSuchParserException(String message) {
        super(message);
    }

    public NoSuchParserException(Throwable cause) {
        super(cause);
    }

    public NoSuchParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
