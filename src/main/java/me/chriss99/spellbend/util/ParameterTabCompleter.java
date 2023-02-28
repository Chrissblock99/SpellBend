package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.Currency;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ParameterTabCompleter {
    private final HashMap<CommandParameter, OptionEnumerator> enumerators;

    /**
     * Initializes with the default enumerator map
     */
    public ParameterTabCompleter() {
         enumerators = defaultEnumeratorMap();
    }

    /**
     * Initializes with a custom enumerator map
     *
     * @param enumerators The custom enumerators map
     */
    public ParameterTabCompleter(@NotNull HashMap<CommandParameter, OptionEnumerator> enumerators) {
        this.enumerators = enumerators;
    }

    /**
     * Finds a fitting StringEnumerator and returns its result <br>
     * This turns the parameter into a CommandParameter
     *
     * @param parameter The parameter to enumerate for
     * @param input The input the user has already given for the argument
     * @return The enumerated options
     */
    public @NotNull List<String> enumerateOptions(@NotNull Parameter parameter, @NotNull String input) {
        return enumerateOptions(new CommandParameter(parameter), input);
    }

    /**
     * Finds a fitting StringEnumerator and returns its result
     *
     * @param commandParameter The commandParameter to enumerate for
     * @param input The input the user has already given for the argument
     * @return The enumerated options
     */
    public @NotNull List<String> enumerateOptions(@NotNull CommandParameter commandParameter, @NotNull String input) {
        OptionEnumerator enumerator = enumerators.get(commandParameter);
        if (enumerator == null)
            enumerator = enumerators.get(new CommandParameter(commandParameter.type, null));
        if (enumerator == null)
            return List.of();

        return enumerator.listStrings(commandParameter.type, input);
    }

    /**
     * Adds an enumerator to the enumerator map
     *
     * @param enumerateFor The Class instance to list for
     * @param enumerator The lister
     * @return Itself
     */
    public ParameterTabCompleter addEnumerator(CommandParameter enumerateFor, OptionEnumerator enumerator) {
        enumerators.put(enumerateFor, enumerator);
        return this;
    }

    /**
     * Removes an enumerator from the enumerator map
     *
     * @param enumerateFor The Class instance of the class that should no longer be enumerated for
     * @return Itself
     */
    public ParameterTabCompleter addEnumerator(CommandParameter enumerateFor) {
        enumerators.remove(enumerateFor);
        return this;
    }

    private static HashMap<CommandParameter, OptionEnumerator> defaultEnumeratorMap() {
        HashMap<CommandParameter, OptionEnumerator> enumerators = new HashMap<>();

        enumerators.put(new CommandParameter(Player.class, null), (type, input) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        //noinspection unchecked
        enumerators.put(new CommandParameter(Enum.class, null), (type, input) -> {
            try {
                //noinspection unchecked
                return (List<String>) type.getMethod("values").invoke(type);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return List.of();
            }
        });

        return enumerators;
    }

    /**
     * Holds the class type and name of a command parameter
     *
     * @param type The class type
     * @param name The parameter name
     */
    public record CommandParameter(@NotNull Class<?> type, @Nullable String name, boolean enumCompare) {

        /**
         * Constructs a CommandParameter from a Parameter
         *
         * @param parameter The Parameter to copy from
         */
        public CommandParameter(@NotNull Parameter parameter) {
            this(parameter.getType(), parameter.getName(), true);
        }

        /**
         * Constructs a CommandParameter with enumCompare to true
         *
         * @param type The class type
         * @param name The parameter name
         */
        public CommandParameter(@NotNull Class<?> type, @Nullable String name) {
            this(type, name, true);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof CommandParameter other))
                return false;
            if (Objects.equals(other.type, type) && Objects.equals(other.name, name))
                return true;
            if (other.enumCompare && enumCompare) {
                return other.type.getSuperclass().equals(type) || type.getSuperclass().equals(other.type);
            }
            return false;
        }
    }
}
