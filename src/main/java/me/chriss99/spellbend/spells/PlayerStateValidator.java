package me.chriss99.spellbend.spells;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerStateValidator {
    public abstract String validateState(@NotNull Player player);
}
