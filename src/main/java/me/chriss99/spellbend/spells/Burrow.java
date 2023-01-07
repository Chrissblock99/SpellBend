package me.chriss99.spellbend.spells;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Burrow extends Spell implements Killable, Stunable {
    private final PlayerSessionData sessionData;

    public static void register() {
        SpellHandler.registerSpell("burrow", 10, new SpellSubClassBuilder() {
            @Override
            public Spell createSpell(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
                return new Burrow(caster, spellType, item);
            }
        });
    }

    public Burrow(@NotNull Player caster, @Nullable String spellType, @NotNull ItemStack item) {
        super(caster, spellType, "TRANSPORT", item);
        sessionData = PlayerSessionData.getPlayerSession(caster);
        sessionData.getCoolDowns().setCoolDown(super.spellType, new float[]{2, 2, 2, 2});
        windup();
    }

    private void windup() {
        sessionData.getWalkSpeedModifiers().addModifier(2);
        sessionData.getDamageTakenModifiers().addModifier(0);
        caster.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10, 128, false, false, false));
        caster.setVelocity(caster.getVelocity().add(new Vector(0, 0.75, 0)));
        caster.getWorld().playSound(caster.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_3, 3f, 1.2f);
    }

    @Override
    public void casterLeave() {
        cancelSpell();
    }

    @Override
    public void cancelSpell() {

    }

    @Override
    public void casterDeath(@Nullable Entity killer) {
        cancelSpell();
    }

    @Override
    public void casterStun(int timeInTicks) {
        cancelSpell();
    }
}
