package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.NamespacedKey;

public class PersistentDataKeys {
    private static final SpellBend plugin = SpellBend.getInstance();

    public static final NamespacedKey spellNameKey = new NamespacedKey(plugin, "spellName");
    public static final NamespacedKey spellTypeKey = new NamespacedKey(plugin, "spellType");
    public static final NamespacedKey coolDownsKey = new NamespacedKey(plugin, "coolDowns");
    public static final NamespacedKey dmgModsKey = new NamespacedKey(plugin, "dmgMods");
}
