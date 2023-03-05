package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class JumpEffect extends MultiValueTracker {
    public JumpEffect(@NotNull LivingEntity livingEntity) {
        super(livingEntity, PersistentDataKeys.jumpEffect, "jumpEffect", new int[0]);
        updateJumpEffect();
    }

    @Override
    public void addValue(int value) {
        super.addValue(value);
        updateJumpEffect();
    }

    @Override
    public void removeValue(int value) {
        super.removeValue(value);
        updateJumpEffect();
    }

    private void updateJumpEffect() {
        getLivingEntity().removePotionEffect(PotionEffectType.JUMP);

        Integer value = largestValue();
        if (value == null || value == 0)
            return;

        getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, value, false, false, false));
    }
}
