package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Test_Spell extends Spell implements Killable, Stunable {
    public Test_Spell(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item);
        PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(super.spellType, new float[]{2, 2, 2, 2});
        naturalSpellEnd();
    }

    @Override
    public void casterLeave() {
        cancelSpell();
    }

    @Override
    public void cancelSpell() {

    }

    @Override
    public void casterDeath(@Nullable LivingEntity killer) {
        cancelSpell();
    }

    @Override
    public void casterStun(int timeInTicks) {
        cancelSpell();
    }
}