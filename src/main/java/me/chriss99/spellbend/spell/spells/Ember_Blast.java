package me.chriss99.spellbend.spell.spells;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Ember_Blast extends Spell { //TODO this
    public Ember_Blast(@NotNull Player caster, @NotNull ItemStack item) {
        super(caster, item);
    }

    @Override
    public void casterLeave() {

    }

    @Override
    public void cancelSpell() {

    }
}
