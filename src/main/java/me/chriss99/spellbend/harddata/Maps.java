package me.chriss99.spellbend.harddata;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Maps {
    public static final Map<Enums.CoolDownStage, Integer> coolDownStageToIndexMap = createCoolDownStageToIndexMap();
    public static final Map<Enums.DmgModType, Integer> modifierToIndexMap = createDmgModToIndexMap();

    private static @NotNull Map<Enums.CoolDownStage, Integer> createCoolDownStageToIndexMap() {
        Map<Enums.CoolDownStage, Integer> map = new HashMap<>();

        map.put(Enums.CoolDownStage.WINDUP, 0);
        map.put(Enums.CoolDownStage.ACTIVE, 1);
        map.put(Enums.CoolDownStage.PASSIVE, 2);
        map.put(Enums.CoolDownStage.COOLDOWN, 3);

        return map;
    }

    private static @NotNull Map<Enums.DmgModType, Integer> createDmgModToIndexMap() {
        Map<Enums.DmgModType, Integer> map = new HashMap<>();

        map.put(Enums.DmgModType.SPELL, 0);
        map.put(Enums.DmgModType.HANDICAP, 1);
        map.put(Enums.DmgModType.FORCE, 2);

        return map;
    }
}
