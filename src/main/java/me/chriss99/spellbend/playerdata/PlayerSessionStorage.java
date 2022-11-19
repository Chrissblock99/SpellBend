package me.chriss99.spellbend.playerdata;

import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.data.DamageEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerSessionStorage {
    public static HashMap<Player, Float> gems = new HashMap<>();
    public static HashMap<Player, Float> gold = new HashMap<>();
    public static HashMap<Player, Float> crystals = new HashMap<>();

    public static HashMap<Player, HashMap<String, CoolDownEntry>> coolDowns = new HashMap<>();
    public static HashMap<Player, float[]> dmgDealtMods = new HashMap<>();
    public static HashMap<Player, float[]> dmgTakenMods = new HashMap<>();
    public static HashMap<Player, ArrayList<DamageEntry>> health = new HashMap<>();
}
