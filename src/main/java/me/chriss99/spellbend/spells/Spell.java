package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.harddata.CoolDownStage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Spell {
    protected final Player caster;
    protected final String spellType;
    protected final ItemStack item;
    protected final CoolDownEntry coolDown;
    private boolean spellEnded = false;

    /**
     * @param caster The player that is casting the spell
     * @param spellType The spellType the spell is cast as
     * @param item The item used (name will appear in kill message)
     */
    public Spell(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item, @NotNull CoolDownEntry coolDown) {
        this.caster = caster;
        this.spellType = spellType;
        this.item = item;
        this.coolDown = coolDown;
    }

    /**
     * Removes the spell from the players active spells
     * <b>Is supposed to be called when the spell ends with its normal process</b>
     */
    protected void naturalSpellEnd() {
        spellEnded = true;
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
    
    public boolean spellEnded() {
        return spellEnded;
    }

    public void casterDeath(@Nullable LivingEntity killer) {
        coolDown.skipToStage(CoolDownStage.COOLDOWN);
        cancelSpell();
    }

    public abstract void casterLeave();
    public abstract void cancelSpell();
}
