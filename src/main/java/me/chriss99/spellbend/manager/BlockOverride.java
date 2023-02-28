package me.chriss99.spellbend.manager;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class BlockOverride {
    private final Block original;
    private final LinkedList<Block> overrides = new LinkedList<>();

    public BlockOverride(@NotNull Block original, @NotNull Block override) {
        this.original = original;
        overrides.add(override);
    }

    public BlockOverride(@NotNull Block original, @NotNull List<Block> overrides) {
        this.original = original;
        this.overrides.addAll(overrides);
    }

    public void addOverride(@NotNull Block block) {
        overrides.add(block);
    }

    /**
     * @param block The block to remove
     * @return If the list is empty now
     */
    public boolean removeOverride(@NotNull Block block) {
        overrides.remove(block);
        return overrides.size() == 0;
    }

    public Block getLatestOverride() {
        return overrides.get(overrides.size()-1);
    }

    public Block getFirstOverride() {
        return overrides.get(0);
    }

    public Block getOriginal() {
        return original;
    }
}
