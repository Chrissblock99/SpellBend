package me.chriss99.spellbend.spell.spellsubclassbuilder;

import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.spell.spells.Escape_Through_Time;
import me.chriss99.spellbend.spell.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Escape_Through_TimeBuilder extends SpellSubClassBuilder {
    public Escape_Through_TimeBuilder() {
        SpellHandler.addSpellBuilderToMap("escape_through_time", this);
    }

    @Override
    public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        return new Escape_Through_Time(caster, spellType, item);
    }
}
