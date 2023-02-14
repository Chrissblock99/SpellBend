package me.chriss99.spellbend.harddata;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    public static final Map<CoolDownStage, Integer> coolDownStageToIndexMap = createCoolDownStageToIndexMap();

    private static @NotNull Map<CoolDownStage, Integer> createCoolDownStageToIndexMap() {
        Map<CoolDownStage, Integer> map = new HashMap<>();

        map.put(CoolDownStage.WINDUP, 0);
        map.put(CoolDownStage.ACTIVE, 1);
        map.put(CoolDownStage.PASSIVE, 2);
        map.put(CoolDownStage.COOLDOWN, 3);

        return map;
    }
}
