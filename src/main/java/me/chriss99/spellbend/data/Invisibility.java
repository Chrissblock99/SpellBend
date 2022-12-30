package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Invisibility extends ValueTracker {
    public Invisibility(@NotNull Player player) {
        super(player, PersistentDataKeys.invisibilityKey, "invisibility", 0);
        updateInvisibility();
    }

    @Override
    public void displaceValue(int value) {
        super.displaceValue(value);
        updateInvisibility();
    }

    private void updateInvisibility() {
        getPlayer().setInvisible(valueIsLargerZero());
    }
}
