package me.chriss99.spellbend.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class BlockManager {
    private static final LinkedHashMap<Block, BlockOverride> blockOverrides = new LinkedHashMap<>();

    public static void addBlockOverride(@NotNull Location location, @NotNull BlockData newBlockData) {
        Block block = location.getBlock();
        BlockOverride blockOverride = blockOverrides.get(block);

        if (blockOverride == null) {
            blockOverride = new BlockOverride(block.getBlockData(), newBlockData);
            blockOverrides.put(block, blockOverride);
        } else blockOverride.addOverride(newBlockData);

        updateBlock(block, blockOverride);
    }

    public static void removeBlockOverride(@NotNull Location location, @NotNull BlockData blockData) {
        Block block = location.getBlock();
        BlockOverride blockOverride = blockOverrides.get(block);

        if (blockOverride == null) {
            Bukkit.getLogger().warning(blockData + " was tried to be removed from blockOverrides, however its location isn't overridden, skipping!");
            return;
        }

        if (blockOverride.removeOverride(blockData))
            blockOverrides.remove(block);
        updateBlock(block, blockOverride);
    }

    private static void updateBlock(@NotNull Block block, @NotNull BlockOverride blockOverride) {
        block.setBlockData(blockOverride.getActiveBlockData(), false);
    }

    public static void clearOverride(@NotNull Location location) {
        BlockOverride override = blockOverrides.remove(location.getBlock());
        if (override != null)
            location.getBlock().setBlockData(override.getOriginal());
    }

    public static void clearOverrides() {
        for (Map.Entry<Block, BlockOverride> override : blockOverrides.entrySet())
            override.getKey().setBlockData(override.getValue().getOriginal());
        blockOverrides.clear();
    }

    public static List<Map.Entry<Block, BlockOverride>> getOverrideView() {
        return new LinkedList<>(blockOverrides.entrySet());
    }
}
