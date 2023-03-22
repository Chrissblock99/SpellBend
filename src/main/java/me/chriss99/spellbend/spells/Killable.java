package me.chriss99.spellbend.spells;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface Killable {
    void casterDeath(@Nullable LivingEntity killer);
}
