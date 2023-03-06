package me.chriss99.spellbend.util;

import me.chriss99.spellbend.SpellBend;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

@SuppressWarnings({"unused"})
public class Item {
    private final static SpellBend plugin = SpellBend.getInstance();
    private final static MiniMessage miniMessage = SpellBend.getMiniMessage();

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name)
    {return create(material, name, 0);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore)
    {return create(material, name, lore, 0);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull String key, @NotNull String customData)
    {return create(material, name, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Map<NamespacedKey, String> persistentData)
    {return create(material, name, 0, persistentData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull String key, @NotNull String customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull Map<NamespacedKey, String> persistentData)
    {return create(material, name, lore, 0, persistentData);}

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(name);
        meta.setCustomModelData(CustomModelData);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(name);
        meta.lore(Arrays.asList(lore));
        meta.setCustomModelData(CustomModelData);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull String key, @NotNull String customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.setCustomModelData(CustomModelData);

        data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, customData);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull String[] key, @NotNull String[] customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.setCustomModelData(CustomModelData);

        for (int i = 0;i<key.length;i++)
            data.set(new NamespacedKey(plugin, key[i]), PersistentDataType.STRING, customData[i]);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.setCustomModelData(CustomModelData);

        data.set(key, PersistentDataType.STRING, customData);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.setCustomModelData(CustomModelData);

        for (int i = 0;i<key.length;i++)
            data.set(key[i], PersistentDataType.STRING, customData[i]);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull Map<NamespacedKey, String> persistentData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.setCustomModelData(CustomModelData);

        for (Map.Entry<NamespacedKey, String> entry : persistentData.entrySet())
            data.set(entry.getKey(), PersistentDataType.STRING, entry.getValue());

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull String key, @NotNull String customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.lore(Arrays.asList(lore));
        meta.setCustomModelData(CustomModelData);

        data.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, customData);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull String[] key, @NotNull String[] customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.lore(Arrays.asList(lore));
        meta.setCustomModelData(CustomModelData);

        for (int i = 0;i<key.length;i++)
            data.set(new NamespacedKey(plugin, key[i]), PersistentDataType.STRING, customData[i]);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.lore(Arrays.asList(lore));
        meta.setCustomModelData(CustomModelData);

        data.set(key, PersistentDataType.STRING, customData);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.lore(Arrays.asList(lore));
        meta.setCustomModelData(CustomModelData);

        for (int i = 0;i<key.length;i++)
            data.set(key[i], PersistentDataType.STRING, customData[i]);

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull Map<NamespacedKey, String> persistentData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.lore(Arrays.asList(lore));
        meta.setCustomModelData(CustomModelData);

        for (Map.Entry<NamespacedKey, String> entry : persistentData.entrySet())
            data.set(entry.getKey(), PersistentDataType.STRING, entry.getValue());

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack edit(@NotNull ItemStack item, @NotNull Component[] lore) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();

        meta.lore(Arrays.asList(lore));

        item.setItemMeta(meta);
        return item;
    }


    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName)
    {return create(material, miniMessage.deserialize(miniMessageName), 0);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull Map<NamespacedKey, String> persistentData)
    {return create(material, miniMessage.deserialize(miniMessageName), 0, persistentData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull Map<NamespacedKey, String> persistentData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, persistentData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData)
    {return create(material, miniMessage.deserialize(miniMessageName), CustomModelData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull Map<NamespacedKey, String> persistentData)
    {return create(material, miniMessage.deserialize(miniMessageName), CustomModelData, persistentData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull Map<NamespacedKey, String> persistentData)
    {return create(material, miniMessage.deserialize(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, persistentData);}

    public static @NotNull ItemStack edit(@NotNull ItemStack item, @NotNull String[] miniMessageLore)
    {return edit(item, miniMessageStringArrayToComponentArray(miniMessageLore));}


    private static @NotNull Component[] miniMessageStringArrayToComponentArray(@NotNull String[] miniMessageStringArray) {
        return Arrays.stream(miniMessageStringArray).map(miniMessage::deserialize).toList().toArray(new Component[0]);
    }
}
