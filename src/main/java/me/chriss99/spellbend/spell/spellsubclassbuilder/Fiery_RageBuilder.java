package me.chriss99.spellbend.spell.spellsubclassbuilder;

import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.spell.spells.Fiery_Rage;
import me.chriss99.spellbend.spell.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Fiery_RageBuilder extends SpellSubClassBuilder {
    public Fiery_RageBuilder() {
        SpellHandler.addSpellBuilderToMap("fiery_rage", this);
    }

    @Override
    public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        return new Fiery_Rage(caster, spellType, item);
    }
}
