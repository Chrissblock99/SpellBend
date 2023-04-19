package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class WalkSpeed extends PercentageModifier {
    public WalkSpeed(@NotNull LivingEntity livingEntity) {
        super(livingEntity, PersistentDataKeys.WALK_SPEED_MODIFIERS_KEY, "walkSpeedModifiers");
        updateWalkSpeed();
    }

    /**
     * Adds the Modifier to the livingEntity
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    @Override
    public void addModifier(double modifier) {
        super.addModifier(modifier);
        updateWalkSpeed();
    }

    /**
     * Removes the modifier from the livingEntity
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    @Override
    public void removeModifier(double modifier) {
        super.removeModifier(modifier);
        updateWalkSpeed();
    }

    private void updateWalkSpeed() {
        LivingEntity livingEntity = getLivingEntity();

        if (livingEntity instanceof Player player) {
            player.setWalkSpeed((float) Math.min(1, getModifier()*0.2f));
            return;
        }

        if (getModifier() == 0) {
            livingEntity.setAI(false);
            return;
        }

        livingEntity.setAI(true);
        livingEntity.removePotionEffect(PotionEffectType.SPEED);
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, (int) Math.round(getModifier()), //TODO //HACK find out how to balance this properly
                false, false, false));
    }
}
