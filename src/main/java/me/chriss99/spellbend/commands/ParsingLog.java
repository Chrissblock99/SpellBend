package me.chriss99.spellbend.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public record ParsingLog(@NotNull Method method, @NotNull String[] parameterStrings,
                         @Nullable Class<?>[] parameterTypes, @NotNull Object[] parameters) {
}
