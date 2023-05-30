package me.chriss99.spellbend.cosmetics;

import me.chriss99.spellbend.SpellBend;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShulkerLootBox {
    private static final SpellBend plugin = SpellBend.getInstance();

    private final Block block;
    private final ShulkerBox shulkerBox;
    private final ArrayList<TimedItemDisplay> circulatingTimedItemDisplays = new ArrayList<>();
    private final TextDisplay frontItemName;
    private final World world;

    private TimedItemDisplay wonItem = null;

    public ShulkerLootBox(@NotNull Block shulkerBox, @NotNull List<ItemStack> items) {
        if (!shulkerBox.getType().equals(Material.SHULKER_BOX))
            throw new IllegalArgumentException("Block must be a Shulker Box!");
        if (items.size() < 2)
            throw new IllegalArgumentException("items has to contain at least two items!");

        this.block = shulkerBox;
        world = shulkerBox.getWorld();
        this.shulkerBox = (ShulkerBox) shulkerBox.getState();

        double twoPiOverSize = 2*Math.PI/ items.size();
        for (int i = 0; i < items.size(); i++) {
            int constantI = i;
            circulatingTimedItemDisplays.add(new TimedItemDisplay(world.spawn(getAnimationLocation(0), ItemDisplay.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (entity -> {
                entity.setItemStack(items.get(constantI));
                Transformation transformation = entity.getTransformation();
                transformation.getScale().set(0.19, 0.19, 0.19);
                entity.setTransformation(transformation);
            })), i*twoPiOverSize - Math.PI));
        }
        frontItemName = world.spawn(getAnimationLocation(Math.PI*4).add(0, 0.2, 0), TextDisplay.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (entity -> {
            Transformation transformation = entity.getTransformation();
            transformation.getScale().set(0.5, 0.5, 0.5);
            entity.setTransformation(transformation);
            entity.text(circulatingTimedItemDisplays.get(0).getDisplay().getItemStack().displayName());
        }));
    }

    public void startAnimation(int updateRate, double speed, int delayInTicks, int slowDownLengthInTicks) {
        if (updateRate <= 0)
            throw new IllegalArgumentException("updateRate cannot be negative or zero!");
        if (delayInTicks < 0)
            throw new IllegalArgumentException("delayInTicks cannot be negative!");
        if (slowDownLengthInTicks < 0)
            throw new IllegalArgumentException("slowDownLengthInTicks cannot be negative!");

        if (slowDownLengthInTicks == 0)
            slowDownLengthInTicks = 1;

        shulkerBox.open();

        final double constantSlowDownLengthInTicks = slowDownLengthInTicks;

        new BukkitRunnable() {
            double ticks = 0;
            double a = 1d/(0.4*constantSlowDownLengthInTicks);

            @Override
            public void run() {
                double smoothSpeed = (ticks >= delayInTicks) ? speed * Math.pow(Math.E, -Math.pow((ticks - delayInTicks) * a, 2)) : speed;

                for (TimedItemDisplay timedItemDisplay : circulatingTimedItemDisplays)
                    timedItemDisplay.addT(smoothSpeed, updateRate);
                frontItemName.text(getFrontTimedItemDisplay().getDisplay().getItemStack().displayName());

                if (ticks >= delayInTicks + constantSlowDownLengthInTicks) {
                    cancel();
                    animationItemsToInside(updateRate, speed);
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0, updateRate);
    }

    private static final double oneOverPiCubed = 1d/(Math.PI*Math.PI*Math.PI);
    private void animationItemsToInside(int updateRate, double speed) {
        wonItem = getFrontTimedItemDisplay();
        circulatingTimedItemDisplays.remove(wonItem);

        new BukkitRunnable() {
            double ticks = 0;
            boolean closedBox = false;
            boolean glorifiedWonItem = false;

            @Override
            public void run() {
                boolean canCloseBox = false;
                boolean done = true;

                if (!allCirclingItemsInside()) {
                    for (TimedItemDisplay timedItemDisplay : circulatingTimedItemDisplays)
                        timedItemDisplay.addT(speed * Math.pow(Math.PI - (timedItemDisplay.getT() % (Math.PI*2)), 3) * oneOverPiCubed, updateRate);
                    done = false;
                } else canCloseBox = true;

                Vector3f translation = wonItem.getDisplay().getTransformation().getTranslation();
                if (translation.z < 0.165 || translation.x > 0.01 || translation.x < -0.01) {
                    wonItem.addT(0.15 * -((wonItem.getT() + Math.PI) % (Math.PI*2) - Math.PI), updateRate);
                    done = false;
                } else
                    if (!glorifiedWonItem) {
                        Bukkit.getLogger().info("glorifying!");
                        glorifyWonItem();
                        glorifiedWonItem = true;
                    }

                if (!wonItemOutside())
                    canCloseBox = false;

                if (canCloseBox && !closedBox) {
                    shulkerBox.close();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (TimedItemDisplay timedItemDisplay : circulatingTimedItemDisplays)
                                timedItemDisplay.getDisplay().remove();
                            circulatingTimedItemDisplays.clear();
                        }
                    }.runTaskLater(plugin, 20);
                    closedBox = true;
                }

                if (done)
                    cancel();

                ticks++;
            }
        }.runTaskTimer(plugin, 0, updateRate);
    }

    private void glorifyWonItem() {
        Bukkit.getLogger().info("starting cooldown " + wonItem.getDisplay().getItemStack().getType());
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("starting glorification " + wonItem.getDisplay().getItemStack().getType());

                Transformation transformation = wonItem.getDisplay().getTransformation();
                transformation.getTranslation().add(0, 0.6f, 0);
                wonItem.getDisplay().setInterpolationDuration(100);
                wonItem.getDisplay().setTransformation(transformation);

                transformation = frontItemName.getTransformation();
                transformation.getTranslation().add(0, 0.6f, 0);
                frontItemName.setInterpolationDuration(100);
                frontItemName.setTransformation(transformation);
            }
        }.runTaskLater(plugin, 20);

    }

    private boolean allCirclingItemsInside() {
        for (TimedItemDisplay timedItemDisplay : circulatingTimedItemDisplays)
            if (timedItemDisplay.getDisplay().getTransformation().getTranslation().z >= 0)
                return false;
        return true;
    }

    private boolean wonItemOutside() {
        return wonItem.getDisplay().getTransformation().getTranslation().z > 0.14;
    }

    private TimedItemDisplay getFrontTimedItemDisplay() {
        double largestZ = -Double.MAX_VALUE;
        TimedItemDisplay largestZTimedItemDisplay = null;

        for (TimedItemDisplay timedItemDisplay : circulatingTimedItemDisplays) {
            double z = timedItemDisplay.getDisplay().getLocation().getZ() + timedItemDisplay.getDisplay().getTransformation().getTranslation().z;
            if (z > largestZ) {
                largestZ = z;
                largestZTimedItemDisplay = timedItemDisplay;
            }
        }

        return largestZTimedItemDisplay;
    }


    private Location getAnimationLocation(double t) {
        return block.getLocation().add(0.5, 0, 0.5).add(getAnimationVector(t));
    }

    private Vector getAnimationVector(double t) {
        double x = Math.sin(t)*0.4d;
        double z = Math.cos(t)*0.4d;
        double y = 0.375d;

        if (z > -0.2d && t > Math.PI) {
            z += Math.pow(Math.E, Math.pow(x, 20d) * -30000000000d) * 0.17d;
            y += Math.pow(Math.E, Math.pow((z - 0.9d) * 0.6d, 4d) * -20d) * 0.25d;
        }

        return new Vector(x, y, z);
    }


    final class TimedItemDisplay {
        private final @NotNull ItemDisplay display;
        private double t;

        TimedItemDisplay(@NotNull ItemDisplay display, double t) {
            this.display = display;
            this.t = t;
        }

        public @NotNull ItemDisplay getDisplay() {
            return display;
        }

        public double getT() {
            return t;
        }

        public void addT(double tAdd, int interpolationDurationInTicks) {
            t += tAdd;
            Location location = getAnimationLocation(t).subtract(display.getLocation());

            Transformation transformation = display.getTransformation();
            transformation.getTranslation().set(location.getX(), location.getY(), location.getZ());
            transformation.getScale().set(0.19, 0.19, 0.19)
                    .add(new Vector3f(0.31f, 0.31f, 0.31f).mul((float) Math.pow((transformation.getTranslation().z - 0.17) * 0.5d + 1, 200)));
            display.setInterpolationDuration(interpolationDurationInTicks * 100);
            display.setTransformation(transformation);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TimedItemDisplay) obj;
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
