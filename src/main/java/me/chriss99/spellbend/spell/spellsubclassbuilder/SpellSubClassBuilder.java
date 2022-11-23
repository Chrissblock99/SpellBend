package me.chriss99.spellbend.spell.spellsubclassbuilder;

import me.chriss99.spellbend.spell.spells.Spell;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SpellSubClassBuilder {
    public abstract Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item);
}
