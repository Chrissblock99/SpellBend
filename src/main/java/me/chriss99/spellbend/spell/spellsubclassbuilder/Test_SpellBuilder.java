package me.chriss99.spellbend.spell.spellsubclassbuilder;

import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.spell.spells.Fiery_Rage;
import me.chriss99.spellbend.spell.spells.Spell;
import me.chriss99.spellbend.spell.spells.Test_Spell;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Test_SpellBuilder extends SpellSubClassBuilder {
    public Test_SpellBuilder() {
        SpellHandler.addSpellBuilderToMap("test_spell", this);
    }

    @Override
    public Spell createSpell(@NotNull Player caster, @NotNull ItemStack item) {
        return new Test_Spell(caster, item);
    }
}
