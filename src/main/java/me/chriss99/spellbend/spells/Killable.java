package me.chriss99.spellbend.spells;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface Killable {
    void casterDeath(@Nullable Entity killer);
}
