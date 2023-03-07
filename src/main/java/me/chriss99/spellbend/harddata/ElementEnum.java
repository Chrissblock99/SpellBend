package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ElementEnum {
    EMBER(new ItemStack(Material.FIRE_CHARGE), new SpellEnum[]{SpellEnum.FIERY_RAGE}, 150);

    private final ItemStack displayItem;
    private final List<SpellEnum> spells;
    private final int price;


    public boolean playerOwns(@NotNull PlayerSessionData sessionData) {
        return true;//TODO sessionData.getSpellsOwned(this) > 0;
    }

    public boolean playerCanBuy(@NotNull PlayerSessionData sessionData) {
        return sessionData.getGems().getCurrency() >= price;
    }

    public boolean playerOwnsSpell(@NotNull PlayerSessionData sessionData, int nth) {
        return true;//TODO sessionData.getSpellsOwned(this) >= nth+1;
    }

    public ItemStack getDisplayItem(int index) {
        return spells.get(index).getDisplayItem();
    }

    public ItemStack getUseItem(int index) {
        return spells.get(index).getUseItem();
    }

    public SpellEnum getSpell(int index) {
        return spells.get(index);
    }


    ElementEnum(@NotNull ItemStack displayItem, @NotNull SpellEnum[] spells, int price) {
        this.displayItem = displayItem;
        this.spells = List.of(spells);
        this.price = price;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public List<SpellEnum> getSpells() {
        return spells;
    }

    public int getPrice() {
        return price;
    }
}
