package me.chriss99.spellbend.manager;

import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

public class BlockOverride {
    private final BlockData original;
    private final LinkedList<BlockData> overrides = new LinkedList<>();

    public BlockOverride(@NotNull BlockData original, @NotNull BlockData override) {
        this.original = original;
        overrides.add(override);
    }

    public void addOverride(@NotNull BlockData block) {
        overrides.add(block);
    }

    /**
     * @param block The block to remove
     * @return If the list is empty now
     */
    public boolean removeOverride(@NotNull BlockData block) {
        overrides.remove(block);
        return overrides.size() == 0;
    }

    public @Nullable BlockData getLatestOverride() {
        if (overrides.isEmpty())
            return null;
        return overrides.get(overrides.size()-1);
    }

    public @Nullable BlockData getFirstOverride() {
        if (overrides.isEmpty())
            return null;
        return overrides.get(0);
    }

    public @NotNull BlockData getOriginal() {
        return original;
    }

    public @NotNull BlockData getActiveBlockData() {
        BlockData active = getLatestOverride();
        if (active == null)
            active = original;
        return active;
    }
}
