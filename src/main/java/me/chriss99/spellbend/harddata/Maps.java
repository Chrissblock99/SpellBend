package me.chriss99.spellbend.harddata;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Maps {
    public static HashMap<Enums.CoolDownStage, Integer> coolDownStageToIndexMap = createCoolDownStageToIndexMap();
    public static HashMap<Enums.DmgModType, Integer> dmgModToIndexMap = createDmgModToIndexMap();

    private static @NotNull HashMap<Enums.CoolDownStage, Integer> createCoolDownStageToIndexMap() {
        HashMap<Enums.CoolDownStage, Integer> map = new HashMap<>();

        map.put(Enums.CoolDownStage.WINDUP, 0);
        map.put(Enums.CoolDownStage.ACTIVE, 1);
        map.put(Enums.CoolDownStage.COOLDOWN, 2);

        return map;
    }

    private static @NotNull HashMap<Enums.DmgModType, Integer> createDmgModToIndexMap() {
        HashMap<Enums.DmgModType, Integer> map = new HashMap<>();

        map.put(Enums.DmgModType.SPELL, 0);
        map.put(Enums.DmgModType.HANDICAP, 1);
        map.put(Enums.DmgModType.FORCE, 2);

        return map;
    }
}
