package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
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
    private boolean headless = false;
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

    public @NotNull Player getCaster() {
        return caster;
    }

    public @NotNull String getSpellType() {
        return spellType;
    }

    public @NotNull ItemStack getItem() {
        return item;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public boolean isHeadless() {
        return headless;
    }
    
    public boolean spellEnded() {
        return spellEnded;
    }

    /**
     * This is called when the caster is stunned
     *
     * @param timeInTicks The time the caster is being stunned for
     */
    public void casterStun(int timeInTicks) {
        coolDown.skipToStage(CoolDownStage.COOLDOWN);
        cancelSpell();
        naturalSpellEnd();
    }

    /**
     * This is called when the caster dies
     *
     * @param killer The living entity that killed the caster
     */
    public void casterDeath(@Nullable LivingEntity killer) {
        coolDown.skipToStage(CoolDownStage.COOLDOWN);
        cancelSpell();
        naturalSpellEnd();
    }

    /**
     * This is run when the caster leaves the server
     */
    public void casterLeave() {
        coolDown.transformToStage(CoolDownStage.COOLDOWN);
        cancelSpell();
        naturalSpellEnd();
    }

    /**
     * This is called when the plugin is unloaded (server stop) <br>
     * <b>do NOT call naturalSpellEnd() inside this</b>
     */
    public void endSpellActivity() {
        coolDown.skipToStage(CoolDownStage.COOLDOWN);
        cancelSpell();
    }

    /**
     * stop spell specific activities <br>
     * This is used by the default spell ending functions <br>
     * <b>do NOT call naturalSpellEnd() inside this</b>
     */
    public abstract void cancelSpell();

    /**
     * Removes the spell from the players active spells
     * <b>do NOT call this from endSpellActivity()</b>
     */
    protected void naturalSpellEnd() {
        spellEnded = true;
        if (headless)
            SpellHandler.removeHeadlessSpell(this);
        else PlayerSessionData.getPlayerSession(caster).getSpellHandler().getActivePlayerSpells().remove(this);
    }
}
