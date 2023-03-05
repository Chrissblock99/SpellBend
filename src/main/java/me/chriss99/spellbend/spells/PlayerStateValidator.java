package me.chriss99.spellbend.spells;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

public abstract class PlayerStateValidator {
    public abstract Component validateState(@NotNull Player player);
}
