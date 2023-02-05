package me.chriss99.spellbend.commands;

import org.jetbrains.annotations.NotNull;

public record ParsedSubCommand(@NotNull SubCommand subCommand, @NotNull Object[] parameters) {
}
