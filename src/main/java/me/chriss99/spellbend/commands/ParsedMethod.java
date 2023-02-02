package me.chriss99.spellbend.commands;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public record ParsedMethod(@NotNull Method method, @NotNull Object[] parameters) {
}
