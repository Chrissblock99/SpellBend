package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Spell {
    protected final Player caster;
    protected final String spellType;
    protected final ItemStack item;

    /**
     * @param caster The player that is casting the spell
     * @param spellType The spellType the spell is cast as
     * @param item The item used (name will appear in kill message)
     */
    public Spell(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        this.caster = caster;
        this.spellType = spellType;
        this.item = item;
    }

    /**
     * Removes the spell from the players active spells
     * <b>Is supposed to be called when the spell ends with its normal process</b>
     */
    protected void naturalSpellEnd() {
        PlayerSessionData.getPlayerSession(caster).getSpellHandler().getActivePlayerSpells().remove(this);
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
