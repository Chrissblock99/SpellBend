package me.chriss99.spellbend.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface OptionEnumerator {
    @NotNull List<String> listStrings(@NotNull Class<?> listFor, @NotNull String input);
}
