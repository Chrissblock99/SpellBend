package me.chriss99.spellbend.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ParsingLog(@NotNull SubCommand subCommand, @NotNull String[] parameterStrings,
                         @Nullable Class<?>[] parameterTypes, @NotNull Object[] parameters) {
}
