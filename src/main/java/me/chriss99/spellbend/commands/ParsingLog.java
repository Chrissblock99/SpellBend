package me.chriss99.spellbend.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ParsingLog(@NotNull PreParsingMethod preParsingMethod, @NotNull String[] parameterStrings,
                         @Nullable Class<?>[] parameterTypes, @NotNull Object[] parameters) {
}
