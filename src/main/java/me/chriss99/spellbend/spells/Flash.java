package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.LivingEntityUtil;
import me.chriss99.spellbend.util.ParticleUtil;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Flash extends Spell {
    public Flash(@NotNull Player caster, @NotNull String spellType, @NotNull ItemStack item) {
        super(caster, spellType, item, PlayerSessionData.getPlayerSession(caster).getCoolDowns().setCoolDown(spellType, new float[]{0, 0, 0, 5}));

        RayTraceResult rayTraceResult = caster.rayTraceBlocks(6);
        /*Location location = caster.getEyeLocation();
        double distance;
        Vector direction;
        Vector secondaryFix = null;

        if (rayTraceResult != null) {
            distance = Math.min(6, location.distance(rayTraceResult.getHitPosition().toLocation(location.getWorld())));

            BlockFace hitFace = rayTraceResult.getHitBlockFace();
            if (hitFace != null)
                secondaryFix = hitFace.getDirection();
        } else distance = 6;

        direction = location.getDirection().multiply(distance);

        if (secondaryFix == null) {
            byte[] highest = new byte[]{(byte) Math.copySign(1, direction.getX()), 0, 0};
            if (Math.abs(direction.getY()) > Math.abs(direction.getX()))
                highest = new byte[]{0, (byte) Math.copySign(1, direction.getY()), 0};
            if (Math.abs(direction.getZ()) > Math.abs(direction.getY()))
                highest = new byte[]{0, 0, (byte) Math.copySign(1, direction.getZ())};

            secondaryFix = new Vector(highest[0], highest[1], highest[2]);
        }
        if (distance < 1.62)
            secondaryFix = new Vector();


        for (int i = 0; LivingEntityUtil.BoundingBoxOverlapsBlocks(caster.getWorld(), caster.getBoundingBox().clone().shift(direction)) && i*0.1 <= distance; i++) {
            direction = location.getDirection().multiply(distance - i*0.1);

            for (int j = 0; LivingEntityUtil.BoundingBoxOverlapsBlocks(caster.getWorld(), caster.getBoundingBox().clone().shift(direction)) && j*0.1 <= 0.5; j++)
                direction = direction.add(secondaryFix.clone().multiply(j*0.1));
        }

        if (LivingEntityUtil.BoundingBoxOverlapsBlocks(caster.getWorld(), caster.getBoundingBox().clone().shift(direction)))
            direction = new Vector();*/

        location = location.add(direction).add(0, -1.62, 0);
        caster.teleport(location);
        caster.setVelocity(caster.getVelocity().add(location.getDirection().multiply(0.5)));

        //eTrail(location 1.3 above player,{_l},2)
        World world = caster.getWorld();
        world.playSound(location, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 2f, 1.2f);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 2f, 1.2f);
        world.spawnParticle(Particle.VILLAGER_ANGRY, location, 3);

        naturalSpellEnd();
    }

    @Override
    public void cancelSpell() {}
}
