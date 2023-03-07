package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IsMovementStunned extends ValueTracker {
    private final PercentageModifier walkSpeedModifiers;
    private final MultiValueTracker jumpEffect;
    private boolean subsAreIncreased = false;

    public IsMovementStunned(@NotNull LivingEntity livingEntity, @NotNull PercentageModifier walkSpeedModifiers, @NotNull MultiValueTracker jumpEffect) {
        super(livingEntity, PersistentDataKeys.isMovementStunnedKey, "isMovementStunned", 0);
        this.walkSpeedModifiers = walkSpeedModifiers;
        this.jumpEffect = jumpEffect;
        updateStun();
    }

    @Override
    public void displaceValue(int value) {
        super.displaceValue(value);
        updateStun();
    }

    private void updateStun() {
        if (valueIsLargerZero() && !subsAreIncreased) {
            if (livingEntity instanceof Player player) {
                jumpEffect.addValue(128);
                player.setFoodLevel(6);
            }
            walkSpeedModifiers.addModifier(0);

            subsAreIncreased = true;
        } else if (subsAreIncreased) {
            if (livingEntity instanceof Player player) {
                jumpEffect.removeValue(128);
                player.setFoodLevel(20);
            }
            walkSpeedModifiers.removeModifier(0);

            subsAreIncreased = false;
        }
    }
}
