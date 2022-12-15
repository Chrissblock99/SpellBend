package me.chriss99.spellbend.spell.spells;

import me.chriss99.spellbend.playerdata.CoolDowns;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Rock_Body extends Spell { //TODO this

    public Rock_Body(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "AURA", item);
        CoolDowns.setCoolDown(caster, super.spellType, new float[]{2.5f, 0, 20, 25});
        windup();
    }

    private void windup() {
        caster.setWalkSpeed(0); //TODO use PercentageModifier
    }

    @Override
    public void casterLeave() {

    }

    @Override
    public void cancelSpell() {

    }
}
