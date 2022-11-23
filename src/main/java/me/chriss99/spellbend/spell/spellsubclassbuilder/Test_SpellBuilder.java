package me.chriss99.spellbend.spell.spellsubclassbuilder;

import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.spell.spells.Spell;
import me.chriss99.spellbend.spell.spells.Test_Spell;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Test_SpellBuilder extends SpellSubClassBuilder {
    public Test_SpellBuilder() {
        SpellHandler.addSpellBuilderToMap("test_spell", this);
    }

    @Override
    public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        return new Test_Spell(caster, spellType, item);
    }
}
