package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.NamespacedKey;

public class PersistentDataKeys {
    private static final SpellBend PLUGIN = SpellBend.getInstance();

    public static final NamespacedKey SPELL_NAME_KEY = new NamespacedKey(PLUGIN, "spellName");
    public static final NamespacedKey SPELL_TYPE_KEY = new NamespacedKey(PLUGIN, "spellType");
    public static final NamespacedKey MANA_COST_KEY = new NamespacedKey(PLUGIN, "manaCost");

    public static final NamespacedKey GEMS_KEY = new NamespacedKey(PLUGIN, "gems");
    public static final NamespacedKey GOLD_KEY = new NamespacedKey(PLUGIN, "gold");
    public static final NamespacedKey CRYSTALS_KEY = new NamespacedKey(PLUGIN, "crystals");

    public static final NamespacedKey ELEMENTS_OWNED_KEY = new NamespacedKey(PLUGIN, "elementsOwned");
    public static final NamespacedKey COSMETICS_OWNED_KEY = new NamespacedKey(PLUGIN, "cosmeticsOwned");

    public static final NamespacedKey IS_MOVEMENT_STUNNED_KEY = new NamespacedKey(PLUGIN, "isMovementStunned");
    public static final NamespacedKey JUMP_EFFECT_KEY = new NamespacedKey(PLUGIN, "jumpEffect");
    public static final NamespacedKey IS_INVISIBLE_KEY = new NamespacedKey(PLUGIN, "isInvisible");

    public static final NamespacedKey COOLDOWNS_KEY = new NamespacedKey(PLUGIN, "coolDowns");
    public static final NamespacedKey DAMAGE_DEALT_MODIFIERS_KEY = new NamespacedKey(PLUGIN, "dmgDealtModifiers");
    public static final NamespacedKey DAMAGE_TAKEN_MODIFIERS_KEY = new NamespacedKey(PLUGIN, "dmgTakenModifiers");
    public static final NamespacedKey WALK_SPEED_MODIFIERS_KEY = new NamespacedKey(PLUGIN, "walkSpeedModifiers");

    public static final NamespacedKey SPELL_AFFECT_ABLE_KEY = new NamespacedKey(PLUGIN, "spellAffectAble");
}
