package me.chriss99.spellbend.data;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IsInvisible extends ValueTracker {
    public IsInvisible(@NotNull Player player) {
        super(player, PersistentDataKeys.isInvisibleKey, "isInvisible", 0);
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
