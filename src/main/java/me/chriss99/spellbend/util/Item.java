package me.chriss99.spellbend.util;

import me.chriss99.spellbend.SpellBend;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@SuppressWarnings("unused")
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
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull String key, @NotNull String customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, lore, 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData)
    {return Item.create(material, name, new Component[0], CustomModelData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData)
    {return create(material, name, lore, CustomModelData, new NamespacedKey[0], new String[0]);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull String key, @NotNull String customData)
    {return create(material, name, CustomModelData, new String[]{key}, new String[]{customData});}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, new Component[0], CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, CustomModelData, new NamespacedKey[]{key}, new String[]{customData});}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, new Component[0], CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull String key, @NotNull String customData)
    {return create(material, name, lore, CustomModelData, new String[]{key}, new String[]{customData});}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, lore, CustomModelData, Arrays.stream(key).map(string -> new NamespacedKey(plugin, string)).toList().toArray(new NamespacedKey[0]), customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, lore, CustomModelData, new NamespacedKey[]{key}, new String[]{customData});}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, lore, CustomModelData, new ItemFlag[0], key, customData);}

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, key, customData);}

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull ItemFlag[] itemFlags)
    {return create(material, name, 0, itemFlags);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull ItemFlag[] itemFlags, @NotNull Component[] lore)
    {return create(material, name, lore, 0, itemFlags);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, name, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, name, lore, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, lore, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, lore, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, lore, 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull ItemFlag[] itemFlags)
    {return Item.create(material, name, new Component[0], CustomModelData, itemFlags);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull ItemFlag[] itemFlags)
    {return create(material, name, lore, CustomModelData, itemFlags, new NamespacedKey[0], new String[0]);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, name, CustomModelData, itemFlags, new String[]{key}, new String[]{customData});}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, new Component[0], CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, CustomModelData, itemFlags, new NamespacedKey[]{key}, new String[]{customData});}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, name, new Component[0], CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, name, lore, CustomModelData, itemFlags, new String[]{key}, new String[]{customData});}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, name, lore, CustomModelData, itemFlags, Arrays.stream(key).map(string -> new NamespacedKey(plugin, string)).toList().toArray(new NamespacedKey[0]), customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, name, lore, CustomModelData, itemFlags, new NamespacedKey[]{key}, new String[]{customData});}

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull ItemFlag[] itemFlags)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, itemFlags);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull ItemFlag[] itemFlags)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, itemFlags);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), 0, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull ItemFlag[] itemFlags)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, itemFlags);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull ItemFlag[] itemFlags)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, itemFlags);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull String[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey key, @NotNull String customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, itemFlags, key, customData);}
    public static @NotNull ItemStack create(@NotNull Material material, @NotNull String miniMessageName, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData)
    {return create(material, miniMessageStringToComponent(miniMessageName), miniMessageStringArrayToComponentArray(miniMessageLore), CustomModelData, itemFlags, key, customData);}

    public static @NotNull ItemStack create(@NotNull Material material, @NotNull Component name, @NotNull Component[] lore, int CustomModelData, @NotNull ItemFlag[] itemFlags, @NotNull NamespacedKey[] key, @NotNull String[] customData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.displayName(name);
        meta.lore(Arrays.asList(lore));
        meta.setCustomModelData(CustomModelData);
        meta.addItemFlags(itemFlags);

        for (int i = 0;i<key.length;i++)
            data.set(key[i], PersistentDataType.STRING, customData[i]);

        item.setItemMeta(meta);
        return item;
    }


    public static @NotNull ItemStack edit(@NotNull ItemStack item, @NotNull String[] miniMessageLore)
    {return edit(item, miniMessageStringArrayToComponentArray(miniMessageLore));}

    public static @NotNull ItemStack edit(@NotNull ItemStack item, @NotNull Component[] lore) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();

        meta.lore(Arrays.asList(lore));

        item.setItemMeta(meta);
        return item;
    }

    public static @NotNull ItemStack edit(@NotNull ItemStack item, @NotNull String[] miniMessageLore, int CustomModelData, @NotNull NamespacedKey key, @NotNull String customData) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        meta.lore(Arrays.asList(miniMessageStringArrayToComponentArray(miniMessageLore)));
        meta.setCustomModelData(CustomModelData);

        data.set(key, PersistentDataType.STRING, customData);

        item.setItemMeta(meta);
        return item;
    }


    private static @NotNull Component miniMessageStringToComponent(@NotNull String miniMessageString) {
        return Component.text().decoration(TextDecoration.ITALIC, false).append(miniMessage.deserialize(miniMessageString)).build();
    }

    private static @NotNull Component[] miniMessageStringArrayToComponentArray(@NotNull String[] miniMessageStringArray) {
        return Arrays.stream(miniMessageStringArray).map(Item::miniMessageStringToComponent).toList().toArray(new Component[0]);
    }
}
