package me.chriss99.spellbend.data;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DamageEntry {
    private final LivingEntity attacker;
    private double damage;

    public DamageEntry(@Nullable LivingEntity attacker, double damage) {
        this.attacker = attacker;
        this.damage = damage;
    }

    public @Nullable LivingEntity getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DamageEntry) obj;
        return Objects.equals(this.attacker, that.attacker) &&
                Double.doubleToLongBits(this.damage) == Double.doubleToLongBits(that.damage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attacker, damage);
    }

    @Override
    public String toString() {
        return "DamageEntry[" +
                "attacker=" + attacker + ", " +
                "damage=" + damage + ']';
    }

}
