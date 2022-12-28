package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.Enums;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WalkSpeed extends PercentageModifier {

    public WalkSpeed(@NotNull Player player, @NotNull NamespacedKey key, @NotNull String name) {
        super(player, key, name);
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

    /**
     * Sets the modifier from the specified type of the player if it is larger <br>
     * <b>Because this can mathematically not be undone an undo factor will be returned <br>
     * which should be used to undo this modifier with removeModifier() later in the process</b> <br>
     * If the extending action didn't change anything, 1 will be returned
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     * @return An undo factor usable to undo the change later
     */
    @Override
    public float extendModifier(@NotNull Enums.DmgModType modType, float modifier) {
        float returnValue = super.extendModifier(modType, modifier);
        updateWalkSpeed();
        return returnValue;
    }

    /**
     * Sets the Modifier from the specified type of the player <br>
     * <b>Because this can mathematically not be undone an undo factor will be returned <br>
     * which should be used to undo this modifier with removeModifier() later in the process</b>
     *
     * @throws IllegalArgumentException If the modifier is smaller or equal to 0
     *
     * @param modType The type of the modifier
     * @param modifier The modifier not smaller or equal to 0
     * @return An undo factor usable to undo the change later
     */
    @Override
    public float setModifier(@NotNull Enums.DmgModType modType, float modifier) {
        float returnValue = super.setModifier(modType, modifier);
        updateWalkSpeed();
        return returnValue;
    }

    @Override
    public void displaceIsZero(int displace) {
        super.displaceIsZero(displace);
        updateWalkSpeed();
    }

    private void updateWalkSpeed() {
        getPlayer().setWalkSpeed(getModifier(null)*0.2f);
    }
}
