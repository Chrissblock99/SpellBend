package me.chriss99.spellbend.util;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class PlayerUtil {
    /**
     * Gets all adventure mode players near the location and returns them with their distance^2
     *
     * @param location The location to get players near
     * @param distance The distance
     * @return The playerToDistanceMap
     */
    public static Map<Player, Double> getPlayersNearLocation(@NotNull Location location, double distance) {
        Chunk centerChunk = location.getChunk();
        World world = location.getWorld();
        int chunkX = centerChunk.getX();
        int chunkZ = centerChunk.getZ();
        int chunkDistance = (int) Math.ceil(distance/16);
        int maxX = chunkX+chunkDistance;
        int maxZ = chunkZ+chunkDistance;
        Set<Entity> entities = new HashSet<>();

        for (int x = chunkX-chunkDistance;x<maxX;x++)
            for (int z = chunkZ-chunkDistance;z<maxZ;z++)
                entities.addAll(List.of(world.getChunkAt(x, z).getEntities()));

        Set<Player> players = new HashSet<>();
        for (Entity entity : entities)
            if (entity instanceof Player player)
                players.add(player);

        Map<Player, Double> playerToDistanceSquaredMap = new HashMap<>();
        double distanceSquared = distance * distance;

        for (Player player : players) {
            if (!GameMode.ADVENTURE.equals(player.getGameMode()))
                continue;

            Location playerLocation = player.getLocation();
            double deltaX = playerLocation.getX()-location.getX();
            double deltaY = playerLocation.getY()-location.getY();
            double deltaZ = playerLocation.getZ()-location.getZ();
            double playerDistanceSquared = deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ;
            if (playerDistanceSquared<=distanceSquared)
                playerToDistanceSquaredMap.put(player, playerDistanceSquared);
        }
        return playerToDistanceSquaredMap;
    }

    private static final Vector[] offset = new Vector[]{
            new Vector(0, 0, 0),
            new Vector(0, 0, 1), new Vector(0, 0, -1), new Vector(1, 0, 0), new Vector(-1, 0, 0),
            new Vector(-1, 0, -1), new Vector(1, 0, -1), new Vector(-1, 0, 1), new Vector(1, 0, 1)};

    /**
     * Gets the voxelShapes of the 9 blocks below the player and compares them to the player's boundingBox <br>
     * Returns false if the player is inside a vehicle
     *
     * @param player The player to check for
     * @return If the player is on ground
     */
    public static boolean isOnGround(@NotNull Player player) {
        if (player.isInsideVehicle())
            return false;

        BoundingBox playerBound = player.getBoundingBox().shift(0, -0.03, 0);
        World world = player.getWorld();
        Location belowPlayer = player.getLocation().add(0, -0.03, 0).toBlockLocation();

        for (Vector offset : offset) {
            Location relativeLoc = belowPlayer.clone().add(offset);
            BoundingBox relativeBound = playerBound.clone().shift(relativeLoc.clone().multiply(-1));
            if (world.getBlockAt(relativeLoc).getCollisionShape().overlaps(relativeBound))
                return true;
        }
        return false;
    }
}
