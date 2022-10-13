package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.Enums;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public record CoolDownEntry(@NotNull String spellType, @NotNull Date startDate, float timeInS, @NotNull Enums.CoolDownStage coolDownStage) {
    public float getRemainingCoolDownTime() {
        return timeInS - (new Date().getTime() - startDate.getTime()) / 1000f;
    }
}
