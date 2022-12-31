package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WalkSpeed extends PercentageModifier {
    public WalkSpeed(@NotNull Player player) {
        super(player, PersistentDataKeys.walkSpeedModifiersKey, "walkSpeed");
    }

    /**
     * Adds the Modifier to the specified type of the player
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     */
    @Override
    public void addModifier(@NotNull Enums.DmgModType modType, float modifier) {
        super.addModifier(modType, modifier);
        updateWalkSpeed();
    }

    /**
     * Removes the modifier from the specified type of the player
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     */
    @Override
    public void removeModifier(@NotNull Enums.DmgModType modType, float modifier) {
        super.removeModifier(modType, modifier);
        updateWalkSpeed();
    }

    private void updateWalkSpeed() {
        getPlayer().setWalkSpeed(getModifier(null)*0.2f);
    }
}
