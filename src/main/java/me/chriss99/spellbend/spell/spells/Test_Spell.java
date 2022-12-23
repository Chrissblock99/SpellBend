package me.chriss99.spellbend.spell.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Test_Spell extends Spell implements Killable, Stunable {

    public Test_Spell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "TEST", item);
        PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(super.spellType, new float[]{2, 2, 2, 2});
    }

    @Override
    public void casterLeave() {

        cancelSpell();
    }

    @Override
    public void cancelSpell() {

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
