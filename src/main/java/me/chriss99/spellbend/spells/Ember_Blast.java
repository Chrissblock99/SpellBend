package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Ember_Blast extends Spell { //TODO this
    public Ember_Blast(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item);
        PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(super.spellType, new float[]{0, 0, 0, 0});
    }

    @Override
    public void casterLeave() {

    }

    @Override
    public void cancelSpell() {

    }
}
