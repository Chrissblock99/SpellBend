package me.chriss99.spellbend.util.particle;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ParticleWithData(@NotNull Particle particle, @Nullable Object data) {
}
