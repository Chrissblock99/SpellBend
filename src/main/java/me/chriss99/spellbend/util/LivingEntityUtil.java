package me.chriss99.spellbend.util;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class LivingEntityUtil {
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
     * Gets the voxelShapes of the 9 blocks below the livingEntity and compares them to its boundingBox <br>
     * Returns false if the livingEntity is inside a vehicle
     *
     * @param livingEntity The livingEntity to check for
     * @return If the livingEntity is on ground
     */
    public static boolean isOnGround(@NotNull LivingEntity livingEntity) {
        if (livingEntity.isInsideVehicle())
            return false;

        BoundingBox livingEntityBound = livingEntity.getBoundingBox().shift(0, -0.03, 0);
        World world = livingEntity.getWorld();
        Location belowLivingEntity = livingEntity.getLocation().add(0, -0.03, 0).toBlockLocation();

        for (Vector offset : offset) {
            Location relativeLoc = belowLivingEntity.clone().add(offset);
            BoundingBox relativeBound = livingEntityBound.clone().shift(relativeLoc.clone().multiply(-1));
            if (world.getBlockAt(relativeLoc).getCollisionShape().overlaps(relativeBound))
                return true;
        }
        return false;
    }

    /**
     * Checks if the entity has the spellAffectAbleKey
     *
     * @param entity The entity to check for
     * @return If the entity should be affected by spells
     */
    public static boolean entityIsSpellAffectAble(@NotNull Entity entity) {
        if (entity instanceof Player)
            return true;
        return entity.getPersistentDataContainer().has(PersistentDataKeys.spellAffectAbleKey);
    }

    public static void setLivingEntitySpellAffectAble(@NotNull LivingEntity livingEntity, boolean affectAble) {
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (affectAble)
            data.set(PersistentDataKeys.spellAffectAbleKey, PersistentDataType.STRING, "");
        else data.remove(PersistentDataKeys.spellAffectAbleKey);
    }
}
