package me.chriss99.spellbend.cosmetics;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.util.BukkitTimer;
import me.chriss99.spellbend.util.ParticleUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EndPortalFrameLootBox {
    private static final SpellBend plugin = SpellBend.getInstance();

    private final Block endPortalFrame;
    private final BlockDisplay shulkerBox;
    private final ArrayList<OrbitingItemDisplay> orbitingItemDisplays = new ArrayList<>();
    private final World world;

    public EndPortalFrameLootBox(@NotNull Block endPortalFrame, Material shulkerBox, @NotNull List<ItemStack> items) {
        if (!endPortalFrame.getType().equals(Material.END_PORTAL_FRAME))
            throw new IllegalArgumentException("Block must be an End Portal Frame!");
        if (!(shulkerBox.equals(Material.SHULKER_BOX) || shulkerBox.equals(Material.LIME_SHULKER_BOX) || shulkerBox.equals(Material.BROWN_SHULKER_BOX) ||
                shulkerBox.equals(Material.BLUE_SHULKER_BOX) || shulkerBox.equals(Material.BLACK_SHULKER_BOX) || shulkerBox.equals(Material.GRAY_SHULKER_BOX) ||
                shulkerBox.equals(Material.CYAN_SHULKER_BOX) || shulkerBox.equals(Material.GREEN_SHULKER_BOX) || shulkerBox.equals(Material.LIGHT_BLUE_SHULKER_BOX) ||
                shulkerBox.equals(Material.LIGHT_GRAY_SHULKER_BOX) || shulkerBox.equals(Material.MAGENTA_SHULKER_BOX) || shulkerBox.equals(Material.ORANGE_SHULKER_BOX) ||
                shulkerBox.equals(Material.PINK_SHULKER_BOX) || shulkerBox.equals(Material.PURPLE_SHULKER_BOX) || shulkerBox.equals(Material.RED_SHULKER_BOX) ||
                shulkerBox.equals(Material.WHITE_SHULKER_BOX) || shulkerBox.equals(Material.YELLOW_SHULKER_BOX)))
            throw new IllegalArgumentException("shulkerBox must be a shulker box!");
        if (items.size() < 2)
            throw new IllegalArgumentException("items has to contain at least two items!");

        this.endPortalFrame = endPortalFrame;
        world = endPortalFrame.getWorld();

        Location shulkerBoxLocation = getOrbitLocation(0, 0, 0, 1).subtract(0.2, 0.2, 0.2);
        this.shulkerBox = world.spawn(shulkerBoxLocation, BlockDisplay.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (entity -> {
            entity.setBlock(shulkerBox.createBlockData());
            Transformation transformation = entity.getTransformation();
            transformation.getScale().set(0.4, 0.4, 0.4);
            entity.setTransformation(transformation);
        }));

        double twoPiOverSize = 2*Math.PI/ items.size();
        for (int i = 0; i < items.size(); i++) {
            int constantI = i;
            orbitingItemDisplays.add(new OrbitingItemDisplay(world.spawn(getOrbitLocation(0, 0, 0, 1), ItemDisplay.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (entity -> {
                entity.setItemStack(items.get(constantI));
                Transformation transformation = entity.getTransformation();
                transformation.getScale().set(0.3, 0.3, 0.3);
                entity.setTransformation(transformation);
            })), 0, i*twoPiOverSize));
        }

        shulkerBoxLocation.add(0.2, 0.2, 0.2);

        new BukkitTimer(
                new BukkitTimer.TimedAction(10, () -> {
                    world.playSound(this.endPortalFrame.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1.8f);
                    world.spawnParticle(Particle.BLOCK_CRACK, shulkerBoxLocation, 5, 0.2, 0.2, 0.2, shulkerBox.createBlockData());
                }),
                new BukkitTimer.TimedAction(30, () -> {
                    world.playSound(this.endPortalFrame.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1.8f);
                    world.spawnParticle(Particle.BLOCK_CRACK, shulkerBoxLocation, 8, 0.2, 0.2, 0.2, shulkerBox.createBlockData());
                }),
                new BukkitTimer.TimedAction(30, () -> {
                    world.playSound(this.endPortalFrame.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 1);
                    world.spawnParticle(Particle.BLOCK_CRACK, shulkerBoxLocation, 12, 0.2, 0.2, 0.2, shulkerBox.createBlockData());

                    startItemOrbit(1);
                    this.shulkerBox.remove();
                }));
    }

    private BukkitTask beaconAmbient;
    private final double oneOver100 = 1d/300d;
    private void startItemOrbit(int updateRate) {
        if (updateRate <= 0)
            throw new IllegalArgumentException("updateRate cannot be negative or zero!");

        world.playSound(endPortalFrame.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);

        beaconAmbient = new BukkitRunnable() {
            @Override
            public void run() {
                world.playSound(endPortalFrame.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 1, 1);
            }
        }.runTaskTimer(plugin, 40, 40);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                for (int i = orbitingItemDisplays.size()-1; i >= 0; i--) {
                    OrbitingItemDisplay orbitingItemDisplay = orbitingItemDisplays.get(i);
                    orbitingItemDisplay.addT(0.3, updateRate, 1);

                    if (orbitingItemDisplays.size() == 1) {
                        cancel();
                        beaconAmbient.cancel();
                        world.playSound(endPortalFrame.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);

                        moveBackToCenter(ticks, updateRate, orbitingItemDisplays.get(0));
                        return;
                    }

                    if (ticks >= 100 && MathUtil.randomChance(0.05 * Math.pow(ticks * oneOver100, 1.5)))
                        orbitingItemDisplay.addDegradation(Math.random() * 0.3);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0, updateRate);
    }

    private void moveBackToCenter(int startingAtTick, int updateRate, OrbitingItemDisplay wonItem) {
        new BukkitRunnable() {
            int ticks = startingAtTick;

            @Override
            public void run() {
                int ticksSinceCentering = ticks - startingAtTick;
                wonItem.addT(0.3, updateRate, 1 - ticksSinceCentering * (1d/20d));

                if (ticksSinceCentering >= 20) {
                    cancel();
                    Bukkit.getLogger().info("done " + wonItem + " " + wonItem.display.getItemStack());
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0, updateRate);

    }

    private Location getOrbitLocation(double t, double rotationOffset, double radiusOffset, double moveBackToCenter) {
        return endPortalFrame.getLocation().add(0.5, 0, 0.5).add(getOrbitVector(t, rotationOffset, radiusOffset, moveBackToCenter));
    }

    private Vector getOrbitVector(double t, double rotationOffset, double radiusOffset, double moveBackToCenter) {
        double oneAt20 = 1d - Math.pow(Math.E, t * -0.2d);

        double radius = oneAt20 * 0.8d * moveBackToCenter;

        double x = Math.sin(t + rotationOffset) * (radius + radiusOffset);
        double z = Math.cos(t + rotationOffset) * (radius + radiusOffset);
        double y = 1d + 0.3d * oneAt20 * moveBackToCenter;

        return new Vector(x, y, z);
    }


    final class OrbitingItemDisplay {
        private final @NotNull ItemDisplay display;
        private final @NotNull ItemStack itemStack;
        private double t;
        private final double rotationOffset;
        private final double inverseOrbitYSpeed = Math.random();
        private final double orbitOffset = Math.random() * Math.PI*2;
        private final double radiusOffset = Math.random() * 0.2 - 0.1;
        private double degradation = 0;

        OrbitingItemDisplay(@NotNull ItemDisplay display, double t, double rotationOffset) {
            this.display = display;
            itemStack = display.getItemStack();
            this.t = t;
            this.rotationOffset = rotationOffset;
        }

        public @NotNull ItemDisplay getDisplay() {
            return display;
        }

        public double getT() {
            return t;
        }

        public void addT(double tAdd, int interpolationDurationInTicks, double moveBackToCenter) {
            t += tAdd;

            double oneAt200 = 1d - Math.pow(Math.E, t * -0.02d);
            double oneAt300 = 1d - Math.pow(Math.E, t * -0.012d);
            double orbitYOffset = Math.sin(t * inverseOrbitYSpeed*1.5 + orbitOffset) * 0.2d * oneAt300 * moveBackToCenter;

            Location location = getOrbitLocation(t * oneAt200, rotationOffset, radiusOffset * oneAt300, moveBackToCenter).subtract(display.getLocation());

            Transformation transformation = display.getTransformation();

            transformation.getTranslation().set(location.getX(), location.getY() + orbitYOffset, location.getZ());
            transformation.getLeftRotation().set(new AxisAngle4f((float) (t * oneAt200 + rotationOffset), 0, 1, 0));

            display.setInterpolationDuration(interpolationDurationInTicks * 100);
            display.setTransformation(transformation);
        }

        final Vector lightningCenter = endPortalFrame.getLocation().clone().add(0.5, 1.3, 0.5).toVector();
        public void addDegradation(double degradation) {
            degradation *= 1d - Math.pow(Math.E, t * -0.012d);
            this.degradation += degradation;

            Transformation transformation = display.getTransformation();
            transformation.getScale().sub(new Vector3f(0.05f, 0.05f, 0.05f).mul((float) degradation));
            display.setTransformation(transformation);

            Particle.DustOptions particleData = new Particle.DustOptions(Color.fromRGB(240, 240, 225), 0.2f);

            new BukkitRunnable() {
                int ticks = 0;
                Vector lastPos = lightningCenter;
                
                @Override
                public void run() {
                    Vector3f translation = display.getTransformation().getTranslation();
                    Vector interpolated = MathUtil.lerpVector(lastPos, display.getLocation().add(translation.x, translation.y, translation.z).toVector(), ticks/10f);
                    ParticleUtil.drawLine(world, lastPos, interpolated, 10, Particle.REDSTONE, particleData);

                    if (ticks >= 10) {
                        display.setItemStack(new ItemStack(Material.GLOW_LICHEN));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                display.setItemStack(itemStack);
                            }
                        }.runTaskLater(plugin, 10);

                        cancel();
                        degradeEffect();
                        return;
                    }

                    lastPos = interpolated;
                    ticks += 2;
                }
            }.runTaskTimer(plugin, 0, 2);
        }

        private void degradeEffect() {
            Vector3f translation = display.getTransformation().getTranslation();
            Location visualItemLocation = display.getLocation().add(translation.x, translation.y, translation.z);

            if (this.degradation >= 1) {
                world.playSound(visualItemLocation, Sound.ENTITY_ITEM_BREAK, 0.7f, 1);
                world.spawnParticle(Particle.ITEM_CRACK, visualItemLocation, 4, 0, 0, 0, 0.1, itemStack);

                display.remove();
                orbitingItemDisplays.remove(this);
                return;
            }

            world.playSound(visualItemLocation, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 0.3f, 2);
            world.spawnParticle(Particle.ITEM_CRACK, visualItemLocation, (int) Math.round(degradation * 7), 0, 0, 0, 0.1, display.getItemStack());
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (OrbitingItemDisplay) obj;
            return Objects.equals(this.display, that.display) &&
                    Double.doubleToLongBits(this.t) == Double.doubleToLongBits(that.t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(display, t);
        }

        @Override
        public String toString() {
            return "TimedItemDisplay[" +
                    "display=" + display + ", " +
                    "t=" + t + ']';
        }
    }
}
