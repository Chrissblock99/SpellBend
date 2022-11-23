package me.chriss99.spellbend.spell.spells;

import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.spell.SpellHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Test_Spell extends Spell implements Killable, Stunable {
    public Test_Spell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "TEST", item);
        CoolDowns.setCoolDown(caster, super.spellType, new float[]{2, 2, 2, 2});
    }

    @Override
    public void casterLeave() {

        cancelSpell();
    }

    @Override
    public void cancelSpell() {

        SpellHandler.getActivePlayerSpells(caster).remove(this);
    }

    @Override
    public void casterDeath(@Nullable Entity killer) {

        cancelSpell();
    }

    @Override
    public void casterStun(int timeInS) {

        cancelSpell();
    }
}
