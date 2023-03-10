package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.NamespacedKey;

public class PersistentDataKeys {
    private static final SpellBend plugin = SpellBend.getInstance();

    public static final NamespacedKey spellNameKey = new NamespacedKey(plugin, "spellName");
    public static final NamespacedKey spellTypeKey = new NamespacedKey(plugin, "spellType");
    public static final NamespacedKey itemActionKey = new NamespacedKey(plugin, "itemAction");

    public static final NamespacedKey gemsKey = new NamespacedKey(plugin, "gems");
    public static final NamespacedKey goldKey = new NamespacedKey(plugin, "gold");
    public static final NamespacedKey crystalsKey = new NamespacedKey(plugin, "crystals");

    public static final NamespacedKey elementsOwnedKey = new NamespacedKey(plugin, "elementsOwned");
    public static final NamespacedKey cosmeticsOwnedKey = new NamespacedKey(plugin, "cosmeticsOwned");

    public static final NamespacedKey isMovementStunnedKey = new NamespacedKey(plugin, "isMovementStunned");
    public static final NamespacedKey jumpEffect = new NamespacedKey(plugin, "jumpEffect");
    public static final NamespacedKey isInvisibleKey = new NamespacedKey(plugin, "isInvisible");

    public static final NamespacedKey coolDownsKey = new NamespacedKey(plugin, "coolDowns");
    public static final NamespacedKey damageDealtModifiersKey = new NamespacedKey(plugin, "dmgDealtModifiers");
    public static final NamespacedKey damageTakenModifiersKey = new NamespacedKey(plugin, "dmgTakenModifiers");
    public static final NamespacedKey walkSpeedModifiersKey = new NamespacedKey(plugin, "walkSpeedModifiers");

    public static final NamespacedKey spellAffectAbleKey = new NamespacedKey(plugin, "spellAffectAble");
}
