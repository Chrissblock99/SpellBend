package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WalkSpeed extends PercentageModifier {
    public WalkSpeed(@NotNull Player player) {
        super(player, PersistentDataKeys.walkSpeedModifiersKey, "walkSpeed");
    }

    /**
     * Adds the Modifier to the player
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    @Override
    public void addModifier(float modifier) {
        super.addModifier(modifier);
        updateWalkSpeed();
    }

    /**
     * Removes the modifier from the player
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    @Override
    public void removeModifier(float modifier) {
        super.removeModifier(modifier);
        updateWalkSpeed();
    }

    private void updateWalkSpeed() {
        getPlayer().setWalkSpeed(getModifier()*0.2f);
    }
}
