package me.chriss99.spellbend.util;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class BukkitTimer {
    private static final SpellBend plugin = SpellBend.getInstance();

    private final TimedAction[] actions;
    private int i = 0;

    public BukkitTimer(TimedAction... actions) {
        this.actions = actions;
        if (actions.length > 0)
            scheduleCurrentAction();
    }

    private void scheduleCurrentAction() {
        new BukkitRunnable() {
            @Override
            public void run() {
                actions[i].runnable.run();

                i++;
                if (i >= actions.length)
                    return;

                scheduleCurrentAction();
            }
        }.runTaskLater(plugin, actions[i].ticks);
    }

    public record TimedAction(int ticks, @NotNull Runnable runnable) {}
}
