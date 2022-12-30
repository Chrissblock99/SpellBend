package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.NamespacedKey;

public class PersistentDataKeys {
    private static final SpellBend plugin = SpellBend.getInstance();

    public static final NamespacedKey spellNameKey = new NamespacedKey(plugin, "spellName");
    public static final NamespacedKey spellTypeKey = new NamespacedKey(plugin, "spellType");

    public static final NamespacedKey gemsKey = new NamespacedKey(plugin, "gems");
    public static final NamespacedKey goldKey = new NamespacedKey(plugin, "gold");
    public static final NamespacedKey crystalsKey = new NamespacedKey(plugin, "crystals");

    public static final NamespacedKey invisibilityKey = new NamespacedKey(plugin, "invisibility");

    public static final NamespacedKey coolDownsKey = new NamespacedKey(plugin, "coolDowns");
    public static final NamespacedKey damageDealtModifiersKey = new NamespacedKey(plugin, "dmgDealtMods");
    public static final NamespacedKey damageTakenModifiersKey = new NamespacedKey(plugin, "dmgTakenMods");
    public static final NamespacedKey walkSpeedModifiersKey = new NamespacedKey(plugin, "walkSpeedMods");
}
