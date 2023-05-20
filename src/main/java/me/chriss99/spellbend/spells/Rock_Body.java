package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.manager.BlockManager;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class Rock_Body extends Spell {
    public Test_Spell(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{2.5, 0, 20, 25}));
        windup();
    }
    
    private void windup() {
        World world = caster.getWorld()
        double vector = MathUtil.DEGTORAD(caster.getLocation().getYaw())+90
            for(int i=0, i<=20, i++);
            FallingBlock lodestones = world.spawnFallingBlock(location,  
                  
                

    @Override
    public void cancelSpell() {}
}
