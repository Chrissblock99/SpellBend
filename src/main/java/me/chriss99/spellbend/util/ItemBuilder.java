package me.chriss99.spellbend.util;

import me.chriss99.spellbend.SpellBend;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemBuilder {
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();


    private final ItemStack item;
    private ItemMeta meta;
    private final PersistentDataContainer persistentData;

    public ItemBuilder(@NotNull Material material) {
        item = new ItemStack(material);
        meta = item.getItemMeta();
        persistentData = meta.getPersistentDataContainer();
    }

    public ItemBuilder(@NotNull ItemStack item) {
        this.item = item.clone();
        meta = item.getItemMeta();
        persistentData = meta.getPersistentDataContainer();
    }


    public ItemBuilder setMaterial(@NotNull Material material) {
        item.setType(material);
        return this;
    }

    public Material getMaterial() {
        return item.getType();
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public int getAmount() {
        return item.getAmount();
    }


    public ItemBuilder setDisplayName(@NotNull Component displayName) {
        meta.displayName(displayName);
        return this;
    }

    public ItemBuilder setMiniMessageDisplayName(@NotNull String miniMessageDisplayName) {
        meta.displayName(miniMessageStringToComponent(miniMessageDisplayName));
        return this;
    }

    public Component getDisplayName() {
        return meta.displayName();
    }

    public ItemBuilder setLore(@NotNull Component... lore) {
        meta.lore(Arrays.stream(lore).toList());
        return this;
    }

    public ItemBuilder setLore(@NotNull List<Component> lore) {
        meta.lore(lore);
        return this;
    }

    public ItemBuilder setMiniMessageLore(@NotNull String... miniMessageLore) {
        meta.lore(miniMessageStringListToComponentList(Arrays.stream(miniMessageLore).toList()));
        return this;
    }

    public ItemBuilder setMiniMessageLore(@NotNull List<String> miniMessageLore) {
        meta.lore(miniMessageStringListToComponentList(miniMessageLore));
        return this;
    }

    public ItemBuilder addLore(@NotNull Component... lore) {
        addLore(Arrays.stream(lore).toList());
        return this;
    }

    public ItemBuilder addLore(@NotNull List<Component> lore) {
        if (meta.hasLore()) {
            List<Component> currentLore = meta.lore();
            //noinspection DataFlowIssue
            currentLore.addAll(lore);
            meta.lore(currentLore);
        } else
            meta.lore(lore);
        return this;
    }

    public ItemBuilder addMiniMessageLore(@NotNull String... miniMessageLore) {
        addMiniMessageLore(Arrays.stream(miniMessageLore).toList());
        return this;
    }

    public ItemBuilder addMiniMessageLore(@NotNull List<String> miniMessageLore) {
        addLore(miniMessageStringListToComponentList(miniMessageLore));
        return this;
    }

    public List<Component> getLore() {
        return meta.lore();
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    public int getCustomModelData() {
        return meta.getCustomModelData();
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public boolean isUnbreakable() {
        return meta.isUnbreakable();
    }

    public ItemBuilder hideAllFlags() {
        meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemBuilder setItemFlags(@NotNull ItemFlag... itemFlags) {
        meta.removeItemFlags(ItemFlag.values());
        meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setItemFlags(@NotNull Set<ItemFlag> itemFlags) {
        setItemFlags(itemFlags.toArray(new ItemFlag[0]));
        return this;
    }

    public ItemBuilder addItemFlags(@NotNull ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder addItemFlags(@NotNull Set<ItemFlag> itemFlags) {
        addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        return this;
    }

    public ItemBuilder removeItemFlags(@NotNull ItemFlag... itemFlags) {
        meta.removeItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder removeItemFlags(@NotNull Set<ItemFlag> itemFlags) {
        removeItemFlags(itemFlags.toArray(new ItemFlag[0]));
        return this;
    }

    public Set<ItemFlag> getItemFlags() {
        return meta.getItemFlags();
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment enchantment : Enchantment.values())
            meta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder setEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        clearEnchantments();
        addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder addEnchantment(@NotNull Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : enchantments.entrySet())
            meta.addEnchant(enchantmentIntegerEntry.getKey(), enchantmentIntegerEntry.getValue(), true);
        return this;
    }

    public ItemBuilder removeEnchantment(@NotNull Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder removeEnchantments(@NotNull Enchantment... enchantments) {
        for (Enchantment enchantment : enchantments)
            meta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder removeEnchantments(@NotNull List<Enchantment> enchantments) {
        for (Enchantment enchantment : enchantments)
            meta.removeEnchant(enchantment);
        return this;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return meta.getEnchants();
    }

    //TODO meta.setAttributeModifiers();
    //TODO meta.setDestroyableKeys();
    //TODO meta.setPlaceableKeys();


    public ItemBuilder clearPersistentData() {
        ItemBuilder oldBuilder = new ItemBuilder(item);
        meta = new ItemStack(Material.STONE).getItemMeta();

        meta.displayName(oldBuilder.getDisplayName());
        meta.lore(oldBuilder.getLore());
        meta.setCustomModelData(oldBuilder.getCustomModelData());
        meta.setUnbreakable(oldBuilder.isUnbreakable());
        setItemFlags(oldBuilder.getItemFlags());
        setEnchantments(oldBuilder.getEnchantments());
        //TODO meta.setAttributeModifiers();
        //TODO meta.setDestroyableKeys();
        //TODO meta.setPlaceableKeys();
        //and here, after we copied everything over, we leave out the persistent data

        return this;
    }

    public ItemBuilder setPersistentData(@NotNull PersistentData<?, ?>... persistentData) {
        addPersistentData(Arrays.stream(persistentData).toList());
        return this;
    }

    public ItemBuilder setPersistentData(@NotNull List<PersistentData<?, ?>> persistentData) {
        clearPersistentData();
        addPersistentData(persistentData);
        return this;
    }

    public ItemBuilder addPersistentData(@NotNull PersistentData<?, ?>... persistentData) {
        addPersistentData(Arrays.stream(persistentData).toList());
        return this;
    }

    public ItemBuilder addPersistentData(@NotNull List<PersistentData<?, ?>> persistentData) {
        for (PersistentData onePersistentData : persistentData)
            //noinspection unchecked
            this.persistentData.set(onePersistentData.key(), onePersistentData.type(), onePersistentData.value());
        return this;
    }

    public PersistentDataContainer getPersistentDataContainer() {
        return persistentData;
    }


    public ItemStack build() {
        item.setItemMeta(meta);
        return item.clone();
    }



    private static @NotNull Component miniMessageStringToComponent(@NotNull String miniMessageString) {
        return Component.text().decoration(TextDecoration.ITALIC, false).append(miniMessage.deserialize(miniMessageString)).build();
    }

    private static @NotNull List<Component> miniMessageStringListToComponentList(@NotNull List<String> miniMessageStringArray) {
        return miniMessageStringArray.stream().map(ItemBuilder::miniMessageStringToComponent).toList();
    }
}
