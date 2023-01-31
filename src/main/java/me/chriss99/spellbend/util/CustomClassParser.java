package me.chriss99.spellbend.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public class CustomClassParser {
    private final HashMap<Class, StringToClassParser> parsers;

    /**
     * Initializes the parser map with the default parser map
     */
    public CustomClassParser() {
         parsers = defaultParserMap();
    }

    /**
     * Initializes the parser map with a custom parser map
     *
     * @param parsers The custom parser map
     */
    public CustomClassParser(HashMap<Class, StringToClassParser> parsers) {
        this.parsers = parsers;
    }

    /**
     * Takes a String and Class instance and returns the class which the Class instance represents, parsed from the string, if a parser for it is present in the parser map <br>
     * This will throw exceptions if something goes wrong during parsing, but may return null if the string represents that in the classes serialisation <br>
     * <b>If custom parsers are done improperly, this might return null although the string doesn't represent it</b>
     *
     * @param parseFrom The string to parse from
     * @param parseTo The Class instance of the class to parse to
     * @return The class which the Class instance represents, parsed from the string
     * @param <C> The class to parse to
     * @throws NoSuchParserException If the map does not contain a parser for the given class <br>
     * <b>Catch this exception first!</b>
     * @throws Exception Any exception that might happen during parsing
     */
    public <C> C parseStringToClass(@NotNull String parseFrom, @NotNull Class<C> parseTo) throws Exception, NoSuchParserException {
        StringToClassParser parser = parsers.get(parseTo);

        if (parser == null && parseTo.getSuperclass() == Enum.class)
            parser = parsers.get(Enum.class);

        if (parser == null)
            throw new NoSuchParserException("The parser map does not contain a parser for the class: \"" + parseTo.getName() + "\"!");

        //noinspection unchecked
        return (C) parser.parseStringToClass(parseFrom, parseTo);
    }

    /**
     * Adds a parser to the parser map
     *
     * @param parseTo The Class instance of the class the parser parses to
     * @param parser The parser
     * @param <C> The class the parser parses to
     * @return Itself
     */
    public <C> CustomClassParser addParser(Class<C> parseTo, StringToClassParser<C> parser) {
        parsers.put(parseTo, parser);
        return this;
    }

    /**
     * Removes a parser from the parser map
     *
     * @param parseTo The Class instance of the class that should no longer be parsed to
     * @return Itself
     */
    public CustomClassParser removeParser(Class<?> parseTo) {
        parsers.remove(parseTo);
        return this;
    }

    private static HashMap<Class, StringToClassParser> defaultParserMap() {
        HashMap<Class, StringToClassParser> parsers = new HashMap<>();

        parsers.put(Player.class, (parseFrom, parseTo) -> Objects.requireNonNull(Bukkit.getPlayerExact(parseFrom), "The player doesn't exist or isn't online!"));
        parsers.put(String.class, (parseFrom, parseTo) -> parseFrom);
        //noinspection unchecked
        parsers.put(Enum.class, (parseFrom, parseTo) -> Enum.valueOf(parseTo, parseFrom.toUpperCase()));
        parsers.put(Double.class, (parseFrom, parseTo) -> Double.valueOf(parseFrom));
        parsers.put(Float.class, (parseFrom, parseTo) -> Float.valueOf(parseFrom));
        parsers.put(Long.class, (parseFrom, parseTo) -> Long.valueOf(parseFrom));
        parsers.put(Integer.class, (parseFrom, parseTo) -> Integer.valueOf(parseFrom));
        parsers.put(Short.class, (parseFrom, parseTo) -> Short.valueOf(parseFrom));
        parsers.put(Byte.class, (parseFrom, parseTo) -> Byte.valueOf(parseFrom));
        parsers.put(Boolean.class, (parseFrom, parseTo) -> Boolean.valueOf(parseFrom));

        return parsers;
    }
}
