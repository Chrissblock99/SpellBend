package me.chriss99.spellbend.spells;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Spell {
    protected final Player caster;
    protected final String spellType;
    protected final ItemStack item;

    /**
     * @param caster The player that is casting the spell
     * @param spellType The spellType the spell is cast as
     * @param item The item used (name will appear in kill message)
     */
    public Spell(@NotNull Player caster, @Nullable String spellType, @NotNull String standardSpellType, @NotNull ItemStack item) {
        this.caster = caster;
        this.spellType = Objects.requireNonNullElse(spellType, standardSpellType);
        this.item = item;
    }

    public @NotNull Player getCaster() {
        return caster;
    }

    public @NotNull String getSpellType() {
        return spellType;
    }

    public @NotNull ItemStack getItem() {
        return item;
    }

    public abstract void casterLeave();
    public abstract void cancelSpell();
}
