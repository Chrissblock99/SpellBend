package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IsMovementStunned extends ValueTracker {
    private final PercentageModifier walkSpeedModifiers;
    private final ValueTracker canNotJump;
    private boolean subsAreIncreased = false;

    public IsMovementStunned(@NotNull Player player, @NotNull PercentageModifier walkSpeedModifiers, @NotNull ValueTracker canNotJump) {
        super(player, PersistentDataKeys.isMovementStunnedKey, "isMovementStunned", 0);
        this.walkSpeedModifiers = walkSpeedModifiers;
        this.canNotJump = canNotJump;
        updateStun();
    }

    @Override
    public void displaceValue(int value) {
        super.displaceValue(value);
        updateStun();
    }

    private void updateStun() {
        if (valueIsLargerZero() && !subsAreIncreased) {
            walkSpeedModifiers.addModifier(0);
            canNotJump.displaceValue(1);
            subsAreIncreased = true;
        } else if (subsAreIncreased) {
            walkSpeedModifiers.removeModifier(0);
            canNotJump.displaceValue(-1);
            subsAreIncreased = false;
        }
    }
}
