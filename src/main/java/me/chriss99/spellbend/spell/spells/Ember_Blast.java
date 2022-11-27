package me.chriss99.spellbend.spell.spells;

import me.chriss99.spellbend.playerdata.CoolDowns;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ember_Blast extends Spell { //TODO this

    public Ember_Blast(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "BLAST", item);
        CoolDowns.setCoolDown(caster, super.spellType, new float[]{0, 0, 0, 0});
    }

    @Override
    public void casterLeave() {

    }

    @Override
    public void cancelSpell() {

    }
}
