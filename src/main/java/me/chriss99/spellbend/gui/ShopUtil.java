package me.chriss99.spellbend.gui;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.harddata.ElementEnum;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.harddata.SpellEnum;
import me.chriss99.spellbend.util.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShopUtil {
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();
    private static final ElementEnum[] elementEnums = ElementEnum.values();

    public static @NotNull Inventory createShopGUI(@NotNull PlayerSessionData sessionData) {
        Inventory shop = createShopPreset();

        for (int i = 0; i < elementEnums.length; i++) {
            ElementEnum elementEnum = elementEnums[i];
            shop.setItem(GUIUtil.convertNumToOutlinedGUISlot(i), Item.edit(elementEnum.getDisplayItem(), createElementOwningLore(sessionData, elementEnum),
                    101, PersistentDataKeys.itemActionKey, "CLICK_ELEMENT; " + elementEnum));
        }

        return shop;
    }

    public static @NotNull Inventory createElementGUI(@NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        //noinspection ConstantConditions
        Inventory elementShop = createElementGUIPreset(Objects.requireNonNullElse(miniMessage.serializeOrNull(elementEnum.getDisplayItem().getItemMeta().displayName()), ""));
        int length = elementEnum.getSpells().size();
        int[] positions = {12, 14, 22, 30, 32, 33, 34, 35}; //the last 3 are there for the case that we ever do more than 5 spells per Element

        for (int i = 0;i<length;i++) {
            SpellEnum spellEnum = elementEnum.getSpell(i);
            //noinspection ConstantConditions
            elementShop.setItem(positions[i], Item.edit(spellEnum.getDisplayItem(), createSpellOwningLore(sessionData, elementEnum, spellEnum),
                    101, PersistentDataKeys.itemActionKey, "GET_SPELL; " + elementEnum + "; " + spellEnum));
        }

        return elementShop;
    }

    private static @NotNull String[] createElementOwningLore(@NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        if (!sessionData.getElementsOwned().playerOwnsElement(elementEnum))
            return new String[]{"<dark_gray>----------------", "<green>$ §b" + elementEnum.getPrice() + " §3Gems",
                    (elementEnum.playerCanBuy(sessionData)) ? "<orange>You can<yellow> buy<orange> this!" : "<red>You can't <yellow> buy<red> this yet!"};
        return new String[]{"<dark_gray>----------------", "<yellow><bold>SHIFT CLICK TO EQUIP"};
    }

    private static @NotNull String[] createSpellOwningLore(@NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        if (sessionData.getElementsOwned().playerOwnsSpellInElement(elementEnum, spellEnum))
            return new String[]{"<dark_gray>----------------", "<yellow><bold>CLICK TO EQUIP"};
        if (!sessionData.getElementsOwned().playerOwnsPreviousSpellInElement(elementEnum, spellEnum))
            //noinspection ConstantConditions
            return new String[]{"<dark_gray>----------------", "<red>You must learn " + miniMessage.serializeOrNull(elementEnum.getDisplayItem(1/*nth-1*/).getItemMeta().displayName()) + "<red> first!"};
        return new String[]{"<dark_gray>----------------", "<yellow>$ <red>" + spellEnum.getPrice() + " <orange>Gold"};
    }

    /**
     * Creates a 5 row chest GUI named SHOP with a cosmetics button and the outline already done
     *
     * @return The inventory
     */
    private static @NotNull Inventory createShopPreset() {
        Inventory defaultShop = GUIUtil.createOutlinedGUI(Item.create(Material.BLUE_STAINED_GLASS_PANE, Component.text(""), 501), 5, "<blue><bold>SHOP");

        /*Sign*/ defaultShop.setItem(4, Item.create(Material.OAK_SIGN, "<bold>Click on an Element", new String[]{
                "<white>to purchase it!",
                "<dark_gray>---------------",
                "<white><bold>Drag <gray>or<white><bold> shift click <gray>moves",
                "<gray>into the shop to unequip them!"
        }, 501));
        /*Cosmetics*/ defaultShop.setItem(40, Item.create(Material.CHEST, "<blue><bold>COSMETICS", 301,
                PersistentDataKeys.itemActionKey, "OPEN_COSMETICS"));

        return defaultShop;
    }

    /**
     * Creates a 5 row chest GUI with the given name and a back button
     *
     * @param miniMessageName The name the GUI should have in miniMessage format
     * @return The inventory
     */
    private static @NotNull Inventory createElementGUIPreset(@NotNull String miniMessageName) {
        Inventory defaultElementGUI = GUIUtil.createOutlinedGUI(Item.create(Material.BLUE_STAINED_GLASS_PANE, Component.text(""), 501), 5, miniMessageName);

        /*Sign*/ defaultElementGUI.setItem(4, Item.create(Material.OAK_SIGN, "<bold>Click on a move", new String[]{
                "<white>to purchase it!",
                "<dark_gray>---------------",
                "<gray>Click on<white><bold> glowing moves",
                "<gray>to equip them!",
                "",
                "<white><bold>Drag <gray>or<white><bold> shift click <gray>moves",
                "<gray>into the shop to unequip them!"
        }, 501));

        /*Back*/ defaultElementGUI.setItem(18, Item.create(Material.ARROW, "<gray><bold>Back", 301, PersistentDataKeys.itemActionKey, "OPEN_SHOP"));
        return defaultElementGUI;
    }
}
