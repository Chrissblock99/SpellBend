package me.chriss99.spellbend.spell.spellsubclassbuilder;

import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.spell.spells.Spell;
import me.chriss99.spellbend.spell.spells.Ember_Blast;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Ember_BlastBuilder extends SpellSubClassBuilder {
    public Ember_BlastBuilder() {
        SpellHandler.addSpellBuilderToMap("ember_blast", this);
    }

    @Override
    public Spell createSpell(@NotNull Player caster, @NotNull ItemStack item) {
        return new Ember_Blast(caster, item);
    }
}
