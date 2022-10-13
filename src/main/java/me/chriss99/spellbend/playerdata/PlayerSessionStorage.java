package me.chriss99.spellbend.playerdata;

import me.chriss99.spellbend.data.CoolDownEntry;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerSessionStorage {
    public static HashMap<Player, HashMap<String, CoolDownEntry>> coolDowns = new HashMap<>();
    public static HashMap<Player, float[]> dmgMods = new HashMap<>();
}
