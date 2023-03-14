package me.chriss99.spellbend.gui;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CurrencyTracker;
import me.chriss99.spellbend.data.ElementsOwned;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.guiframework.GuiButton;
import me.chriss99.spellbend.guiframework.GuiInventory;
import me.chriss99.spellbend.guiframework.GuiItem;
import me.chriss99.spellbend.harddata.ElementEnum;
import me.chriss99.spellbend.harddata.SpellEnum;
import me.chriss99.spellbend.util.GuiUtil;
import me.chriss99.spellbend.util.InventoryUtil;
import me.chriss99.spellbend.util.Item;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShopGui extends GuiInventory {
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();
    private static final ElementEnum[] elementEnums = ElementEnum.values();

    public ShopGui(@NotNull Player player) {
        super("<blue><bold>SHOP", 5);
        GuiUtil.outLineGui(this, Item.create(Material.BLUE_STAINED_GLASS_PANE, Component.text(""), 501), 5);

        new GuiItem(Item.create(Material.OAK_SIGN, "<bold>Click on an Element", new String[]{
                "<white>to purchase it!",
                "<dark_gray>---------------",
                "<white><bold>Drag <gray>or<white><bold> shift click <gray>moves",
                "<gray>into the shop to unequip them!"
        }, 501))
                .registerIn(this, 4);
        new GuiButton(Item.create(Material.CHEST, "<blue><bold>COSMETICS", 301))
                .onClick(clickEvent -> player.sendMessage("You've been trolled!"))
                .registerIn(this, 40);

        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
        for (int i = 0; i < elementEnums.length; i++) {
            ElementEnum elementEnum = elementEnums[i];
            new GuiButton(Item.edit(elementEnum.getDisplayItem(), createElementOwningLore(sessionData, elementEnum)))
                    .onClick(clickEvent -> elementClickEvent(clickEvent, player, sessionData, elementEnum))
                    .registerIn(this, GuiUtil.convertNumToOutlinedGUISlot(i));
        }

        player.openInventory(inventory);
    }

    @Override
    public void clickInInventory(@NotNull InventoryClickEvent event) {
        shopClick(event);
    }

    @Override
    public void clickInOtherInventory(@NotNull InventoryClickEvent event) {
        nonShopClick(event);
    }

    public static void shopClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        if (SpellHandler.itemIsSpell(event.getCursor()))
            event.setCursor(null);
    }

    public static void nonShopClick(@NotNull InventoryClickEvent event) {
        if (event.getClick().isShiftClick()) {
            event.setCancelled(true);
            if (SpellHandler.itemIsSpell(event.getCurrentItem()))
                event.setCurrentItem(null);
        }
    }

    private static void elementClickEvent(@NotNull InventoryClickEvent clickEvent, @NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        ClickType clickType = clickEvent.getClick();
        if (clickType.equals(ClickType.RIGHT) || clickType.equals(ClickType.LEFT)) {
            elementClick(player, sessionData, elementEnum);
            return;
        }
        if (clickType.isShiftClick())
            elementShiftClick(player, sessionData, elementEnum);
    }

    private static void elementShiftClick(@NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        ElementsOwned elementsOwned = sessionData.getElementsOwned();
        if (!elementsOwned.playerOwnsElement(elementEnum)) {
            player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <dark_gray>» <red>You do not own " + elementEnum + "<red>!"));
            return;
        }

        addAllElementSpells(player, elementsOwned, elementEnum);
    }

    private static void elementClick(@NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        ElementsOwned elementsOwned = sessionData.getElementsOwned();
        if (elementsOwned.playerOwnsElement(elementEnum)) {
            new ElementGui(player, elementEnum);
            return;
        }

        CurrencyTracker gems = sessionData.getGems();
        if (!elementEnum.playerCanBuy(sessionData)) {
            player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <dark_gray>»<red> Not enough Gems! Need <aqua>" +
                    (long) (elementEnum.getPrice() - gems.getCurrency()) + "<red> more!"));
            return;
        }

        gems.addCurrency(-elementEnum.getPrice());
        elementsOwned.setElementOwned(elementEnum);
        new ElementGui(player, elementEnum);
        player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <dark_gray>»<yellow> Purchased " +
                miniMessage.serializeOrNull(elementEnum.getDisplayItem().getItemMeta().displayName()) + " <gold>for <aqua>" + elementEnum.getPrice() + " Gems<yellow>!"));
        //TODO buy sound here
    }

    public static void addAllElementSpells(@NotNull Player player, @NotNull ElementsOwned elementsOwned, @NotNull ElementEnum elementEnum) {
        for (SpellEnum spellEnum : elementEnum.getSpells()) {
            if (!elementsOwned.playerOwnsSpellInElement(elementEnum, spellEnum))
                break;
            Inventory playerInventory = player.getInventory();
            if (InventoryUtil.spellsInsideInventory(playerInventory) < 5 && !InventoryUtil.inventoryContainsSpellName(playerInventory, spellEnum.toString()))
                player.getInventory().addItem(spellEnum.getUseItem());
        }
    }

    private static @NotNull String[] createElementOwningLore(@NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        if (sessionData.getElementsOwned().playerOwnsElement(elementEnum))
            return new String[]{"<dark_gray>----------------", "<yellow><bold>SHIFT CLICK TO EQUIP"};

        return new String[]{"<dark_gray>----------------", "<green>$ <aqua>" + elementEnum.getPrice() + " <dark_aqua>Gems",
                (elementEnum.playerCanBuy(sessionData)) ? "<gold>You can<yellow> buy<gold> this!" : "<red>You can't<yellow> buy<red> this yet!"};

    }
}
