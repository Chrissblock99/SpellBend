package me.chriss99.spellbend.data.sidebar;

import me.chriss99.spellbend.data.CoolDownEntry;
import org.jetbrains.annotations.NotNull;

public interface AbstractCoolDownTimeDisplayFactory {
    @NotNull CoolDownTimeDisplay createCoolDownTimeDisplay(@NotNull CoolDownEntry coolDown);
}
