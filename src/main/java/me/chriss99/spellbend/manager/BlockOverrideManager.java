package me.chriss99.spellbend.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;


public class BlockOverrideManager {
    private static final LinkedHashMap<Location, BlockOverride> blockOverrides = new LinkedHashMap<>();

    public static void addBlockOverride(@NotNull Block newBlock) {
        Location location = newBlock.getLocation();
        BlockOverride blockOverride = blockOverrides.get(location);

        if (blockOverride == null) {
            blockOverride = new BlockOverride(location.getWorld().getBlockAt(location), newBlock);
            blockOverrides.put(location, blockOverride);
        } else blockOverride.addOverride(newBlock);

        updateBlock(blockOverride);
    }

    public static void removeBlockOverride(@NotNull Block block) {
        Location location = block.getLocation();
        BlockOverride blockOverride = blockOverrides.get(location);

        if (blockOverride == null) {
            Bukkit.getLogger().warning(block + " was tried to be removed from blockOverrides, however its location isn't overridden, skipping!");
            return;
        }

        if (blockOverride.removeOverride(block)) {
            blockOverrides.remove(location);
            return;
        }
        updateBlock(blockOverride);
    }

    //TODO this currently wont place the original block back
    private static void updateBlock(BlockOverride blockOverride) {
        blockOverride.getLatestOverride(); //place this block in world
        //blockOverride.getOriginal().getWorld().setB
    }
}
