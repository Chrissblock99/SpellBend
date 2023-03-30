package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.math.RotationUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Flash extends Spell {
    public Flash(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0, 0, 0, 5}));

        Location location = findNearest(caster.getEyeLocation(), 6).add(0, -1.62, 0);
        //caster.teleport(location);
        //caster.setVelocity(caster.getVelocity().add(location.getDirection().multiply(0.5)));

        World world = caster.getWorld();
        //eTrail(location 1.3 above player,{_l},2)
        world.playSound(location, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 2f, 1.2f);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 2f, 1.2f);
        world.spawnParticle(Particle.VILLAGER_ANGRY, location, 3);

        naturalSpellEnd();
    }

    private static Location findNearest(@NotNull Location location, double distance) {
        Location bestCase = location.clone().add(location.getDirection().clone().multiply(distance));
        World world = location.getWorld();

        if (world.rayTraceBlocks(location, location.getDirection(), distance) == null)
            return bestCase;

        final Location[] nearest = {location};
        final double[] smallestDistanceSquared = {6 * 6};

        forEachRotatedCircleGrid(location, circleGridLocation -> {
            RayTraceResult rayTraceResult = world.rayTraceBlocks(circleGridLocation, location.getDirection(), distance);
            Location hit;
            if (rayTraceResult != null)
                hit = rayTraceResult.getHitPosition().toLocation(world);
            else hit = circleGridLocation.clone().add(location.getDirection().clone().multiply(distance));

            double distanceSquared = nearest[0].distanceSquared(hit);
            if (distanceSquared < smallestDistanceSquared[0]) {
                nearest[0] = hit;
                smallestDistanceSquared[0] = distanceSquared;
            }
        });

        return nearest[0];
    }

    private static void forEachRotatedCircleGrid(@NotNull Location source, @NotNull Consumer<Location> consumer) {
        World world = source.getWorld();

        for (float x = -1; x < 1; x += 0.2) {
            for (float y = -1; y < 1; y += 0.2) {
                if (x*x + y*y > 1)
                    continue;

                Vector grid = new Vector(x, y, 0);
                Vector rotatedCircleGrid = RotationUtil.rotateVectorAroundLocationRotation(grid, source);
                Location circleGridLocation = source.clone().add(rotatedCircleGrid);
                world.spawnParticle(Particle.DRIPPING_HONEY, circleGridLocation, 1, 0, 0, 0, 0);

                consumer.accept(circleGridLocation);
            }
        }
    }

    @Override
    public void cancelSpell() {}
}
