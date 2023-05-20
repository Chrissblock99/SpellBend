package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Rock_Body extends Spell {
    public Test_Spell(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{2.5, 0, 20, 25}));
        windup();
    }
    
    private void windup() {
        World world = caster.getWorld()
        MathUtil.DEGTORADcaster.getLocation().getYaw()+90
            for(int i=0, i<=20, i++);
                  
                

    @Override
    public void cancelSpell() {}
}
