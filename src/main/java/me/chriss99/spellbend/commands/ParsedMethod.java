package me.chriss99.spellbend.commands;

import org.jetbrains.annotations.NotNull;

public record ParsedMethod(@NotNull PreParsingMethod preParsedMethod, @NotNull Object[] parameters) {
}
