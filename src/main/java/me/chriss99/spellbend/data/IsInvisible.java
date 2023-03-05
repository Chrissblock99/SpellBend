package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class IsInvisible extends ValueTracker {
    public IsInvisible(@NotNull LivingEntity livingEntity) {
        super(livingEntity, PersistentDataKeys.isInvisibleKey, "isInvisible", 0);
        updateInvisibility();
    }

    @Override
    public void displaceValue(int value) {
        super.displaceValue(value);
        updateInvisibility();
    }

    private void updateInvisibility() {
        getLivingEntity().setInvisible(valueIsLargerZero());
    }
}
