package me.chriss99.spellbend.util;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
    //TODO check if this actually works
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
                playerToDistanceSquaredMap.put(player, distanceSquared);
        }
        return playerToDistanceSquaredMap;
    }
}
