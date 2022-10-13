package me.chriss99.spellbend.spell.spells;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Spell {
    protected final Player caster;
    protected final ItemStack item;

    public Spell(@NotNull Player caster, @NotNull ItemStack item) {
        this.caster = caster;
        this.item = item;
    }

    public @NotNull Player getCaster() {
        return caster;
    }

    public @NotNull ItemStack getItem() {
        return item;
    }

    public abstract void casterLeave();
    public abstract void cancelSpell();
}
