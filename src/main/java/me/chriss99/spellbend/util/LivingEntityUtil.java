package me.chriss99.spellbend.util;

import me.chriss99.spellbend.data.LivingEntitySessionData;
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
import java.util.function.Predicate;


public class LivingEntityUtil {
    /**
     * Gets all livingEntities near the location and returns them with their distance^2
     *
     * @param location The location to get livingEntities near
     * @param distance The distance
     * @return The livingEntityToDistanceMap
     */
    public static <C extends LivingEntity> Map<C, Double> getLivingEntitiesNearLocation(@NotNull Location location, double distance,
                                                                                        @NotNull Predicate<C> predicate, @NotNull Class<C> returnMapKeyType) {
        Chunk centerChunk = location.getChunk();
        World world = location.getWorld();
        int chunkX = centerChunk.getX();
        int chunkZ = centerChunk.getZ();
        int chunkDistance = (int) Math.ceil(distance/16);
        int maxX = chunkX+chunkDistance;
        int maxZ = chunkZ+chunkDistance;
        Set<C> entities = new HashSet<>();

        for (int x = chunkX-chunkDistance;x<maxX;x++)
            for (int z = chunkZ-chunkDistance;z<maxZ;z++)

                for (Entity entity : world.getChunkAt(x, z).getEntities())
                    if (returnMapKeyType.isInstance(entity)) {
                        //noinspection unchecked
                        C livingEntity = (C) entity;
                        if (predicate.test(livingEntity))
                            entities.add(livingEntity);
                    }

        Map<C, Double> livingEntityToDistanceSquaredMap = new HashMap<>();
        double distanceSquared = distance * distance;

        for (C livingEntity : entities) {
            Location entityLocation = livingEntity.getLocation();
            double deltaX = entityLocation.getX()-location.getX();
            double deltaY = entityLocation.getY()-location.getY();
            double deltaZ = entityLocation.getZ()-location.getZ();
            double livingEntityDistanceSquared = deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ;
            if (livingEntityDistanceSquared<=distanceSquared)
                livingEntityToDistanceSquaredMap.put(livingEntity, livingEntityDistanceSquared);
        }
        return livingEntityToDistanceSquaredMap;
    }

    /**
     * Gets all players in adventure mode near the location and returns them with their distance^2
     *
     * @param location The location to get players near
     * @param distance The distance
     * @return The playerToDistanceMap
     */
    public static Map<Player, Double> getPlayersNearLocation(@NotNull Location location, double distance) {
        return getLivingEntitiesNearLocation(location, distance,
                (player) -> GameMode.ADVENTURE.equals(player.getGameMode()), Player.class);
    }

    /**
     * Gets all livingEntities near the location and returns them with their distance^2
     *
     * @param location The location to get livingEntities near
     * @param distance The distance
     * @return The livingEntityToDistanceMap
     */
    public static Map<LivingEntity, Double> getLivingEntitiesNearLocation(@NotNull Location location, double distance) {
        return getLivingEntitiesNearLocation(location, distance, (a) -> true, LivingEntity.class);
    }

    /**
     * Gets all spell affect-able livingEntities near the location and returns them with their distance^2
     *
     * @param location The location to get spellAffectAbleEntities near
     * @param distance The distance
     * @return The livingEntityToDistanceMap
     */
    public static Map<LivingEntity, Double> getSpellAffectAbleEntitiesNearLocation(@NotNull Location location, double distance) {
        return getLivingEntitiesNearLocation(location, distance, LivingEntityUtil::entityIsSpellAffectAble, LivingEntity.class);
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
        if (affectAble) {
            if (!entityIsSpellAffectAble(livingEntity)) {
                data.set(PersistentDataKeys.spellAffectAbleKey, PersistentDataType.STRING, "");
                LivingEntitySessionData.setupLivingEntityData(livingEntity);
                LivingEntitySessionData.loadLivingEntitySession(livingEntity);
            }
        } else
            if (entityIsSpellAffectAble(livingEntity)) {
                LivingEntitySessionData.getLivingEntitySession(livingEntity).endSession();
                LivingEntitySessionData.removeLivingEntityData(livingEntity);
                data.remove(PersistentDataKeys.spellAffectAbleKey);
            }
    }
}
