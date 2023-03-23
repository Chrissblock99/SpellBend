package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.LivingEntitySessionData;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.manager.BlockManager;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class Gashing_Fossils extends Spell {
    private static final SpellBend plugin = SpellBend.getInstance();
    private static final Vector[][] fossil = createFossil();
    private static final BlockData boneBlock = Material.BONE_BLOCK.createBlockData();

    private final PlayerSessionData sessionData;
    private final BukkitTask stunUndoTask;
    private BukkitTask activeTask;
    private BukkitTask waitTask;
    private BukkitTask removeTask;

    public Gashing_Fossils(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0.5f, 1.5f, 0.25f, 10}));
        sessionData = PlayerSessionData.getPlayerSession(caster);
        sessionData.getIsMovementStunned().displaceValue(1);

        stunUndoTask = new BukkitRunnable() {
            @Override
            public void run() {
                 sessionData.getIsMovementStunned().displaceValue(-1);
            }
        }.runTaskLater(plugin, 37);

        windup();
    }

    int i = 1;
    Location center;
    World world;
    private void windup() {
        center = caster.getLocation().clone();
        world = caster.getWorld();
        windupStep();
    }

    private void windupStep() {
        do {
            world.spawnParticle(Particle.SOUL, center.clone().add(Math.cos(i*4* MathUtil.DEGTORAD)*4.25, 0.25, Math.sin(i*4* MathUtil.DEGTORAD)*4.25),
                    5, 0, 0, 0, 0);
            i++;
        } while (i%9 != 0);
        world.playSound(center, Sound.BLOCK_NETHERRACK_BREAK, 3f, 0.5f + (i/720f));
        world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 1.5f, 1.5f + (i/720f));

        if (i >= 90) {
            active();
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                windupStep();
            }
        }.runTaskLater(plugin, 1);
    }

    private void active() {
        Map<LivingEntity, Double> nearbyEntities = LivingEntityUtil.getSpellAffectAbleEntitiesNearLocation(center, 4.25);
        nearbyEntities.remove(caster);
        for (Map.Entry<LivingEntity, Double> livingEntity : nearbyEntities.entrySet()) {
            LivingEntitySessionData sessionData = LivingEntitySessionData.getLivingEntitySession(livingEntity.getKey());
            sessionData.getHealth().damageLivingEntity(caster, 6.5, item);
            sessionData.stunEntity(30);
            //move up 1.25
            //move away 0.625
        }

        for (int i = 1; i <= 2; i++) {
            generateFossilLayer(i);
        }

        activeTask = new BukkitRunnable() {
            int i = 3;
            @Override
            public void run() {
                generateFossilLayer(i);

                if (i >= 6) {
                    activeTask.cancel();
                    waitThenEnd();
                }

                i++;
            }
        }.runTaskTimer(SpellBend.getInstance(), 0, 1);
    }

    private void generateFossilLayer(int i) {
        for (int j = 0; j < 6; j++) {
            Location location = center.clone().add(fossil[i-1][j]);
            BlockManager.addBlockOverride(location, boneBlock);
            world.playSound(location, Sound.BLOCK_BONE_BLOCK_PLACE, 3f, 0.5f+i/5f);
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.8f);
            world.spawnParticle(Particle.SMOKE_LARGE, location, 10, 0, 0, 0, 0.05);
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, location, 10, 0, 0, 0, 0.05);
        }
    }

    private void waitThenEnd() {
        waitTask = new BukkitRunnable() {
            @Override
            public void run() {
                removeFossil();
            }
        }.runTaskLater(SpellBend.getInstance(), 40);
    }

    private void removeFossil() {
        for (int i = 6; i >= 1; i--)
            removeFossilLayer(i);
        removeFallingBlocks();
    }

    private final LinkedList<FallingBlock> fallingBlocks = new LinkedList<>();
    private void removeFossilLayer(int i) {
        for (int j = 0; j < 6; j++) {
            Location location = center.clone().add(fossil[i-1][j]).toCenterLocation();
            BlockManager.removeBlockOverride(location, boneBlock);

            world.playSound(location, Sound.ENTITY_PHANTOM_FLAP, 1f, 1f);

            FallingBlock boneBlock = world.spawnFallingBlock(location, Material.BONE_BLOCK.createBlockData());
            fallingBlocks.add(boneBlock);
            boneBlock.setVelocity(new Vector(0 ,0.35 ,0));
            boneBlock.setDropItem(false);
            boneBlock.setInvulnerable(true);
            boneBlock.shouldAutoExpire(false);

            SpellHandler.registerFallingBlockHitGroundEventListener(boneBlock, event -> {
                FallingBlock fallingBlock = (FallingBlock) event.getEntity();
                fallingBlocks.remove(fallingBlock);
                world.playSound(fallingBlock.getLocation(), Sound.BLOCK_STONE_BREAK, 0.5f, 0.8f);
                world.spawnParticle(Particle.BLOCK_DUST, fallingBlock.getLocation(), 25, 0, 0, 0, 0, Material.BLACKSTONE.createBlockData());
            });
        }
    }

    private void removeFallingBlocks() {
        long endTime = new Date().getTime() + 20000;
        removeTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = fallingBlocks.size()-1; i >= 0; i--) {
                    FallingBlock boneBlock = fallingBlocks.get(i);
                    if (boneBlock.isDead() || new Date().getTime() >= endTime) {
                        boneBlock.remove();
                        fallingBlocks.remove(boneBlock);
                        world.playSound(boneBlock.getLocation(), Sound.BLOCK_STONE_BREAK, 0.5f, 0.8f);
                        world.spawnParticle(Particle.BLOCK_DUST, boneBlock.getLocation(), 25, 0, 0, 0, 0, Material.BLACKSTONE.createBlockData());
                        continue;
                    }

                    world.spawnParticle(Particle.SMOKE_LARGE, boneBlock.getLocation(), 1, 0, 0, 0, 0.05);
                }

                if (fallingBlocks.isEmpty()) {
                    removeTask.cancel();
                    naturalSpellEnd();
                }
            }
        }.runTaskTimer(plugin, 1, 3);
    }

    @Override
    public void cancelSpell() {
        stunUndoTask.cancel();
        sessionData.getIsMovementStunned().displaceValue(-1);

        if (activeTask != null)
            activeTask.cancel();
        if (waitTask != null)
            waitTask.cancel();
        if (removeTask != null)
            removeTask.cancel();
    }


    private static Vector[][] createFossil() {
        Vector[][] fossil = new Vector[6][6];

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                fossil[i][j] = new Vector(Math.cos(j*60* MathUtil.DEGTORAD)*(3+i/2f), i-3, Math.sin(j*60* MathUtil.DEGTORAD)*(3+i/2f));

        return fossil;
    }
}
