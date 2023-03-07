package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum SpellEnum {
    FIERY_RAGE(
            Item.create(Material.CAMPFIRE, Component.text().content("Fiery Rage").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC,false).build(), 1),
            Item.create(Material.CAMPFIRE, Component.text().content("Fiery Rage").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC,false).build(), 1, new NamespacedKey[]{PersistentDataKeys.spellNameKey, PersistentDataKeys.spellTypeKey}, new String[]{"fiery_rage", "AURA"}),
            1) //TODO price here
    ;
    
    private final ItemStack displayItem;
    private final ItemStack useItem;
    private final int price;


    public boolean playerCanBuy(@NotNull PlayerSessionData sessionData) {
        return sessionData.getGold().getCurrency() >= price;
    }


    SpellEnum(@NotNull ItemStack displayItem, @NotNull ItemStack useItem, int price) {
        this.displayItem = displayItem;
        this.useItem = useItem;
        this.price = price;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ItemStack getUseItem() {
        return useItem;
    }

    public int getPrice() {
        return price;
    }
}
