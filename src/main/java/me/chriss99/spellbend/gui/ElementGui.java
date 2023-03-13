package me.chriss99.spellbend.gui;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.guiframework.GuiButton;
import me.chriss99.spellbend.guiframework.GuiInventory;
import me.chriss99.spellbend.guiframework.GuiItem;
import me.chriss99.spellbend.harddata.ElementEnum;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.harddata.SpellEnum;
import me.chriss99.spellbend.util.GuiUtil;
import me.chriss99.spellbend.util.InventoryUtil;
import me.chriss99.spellbend.util.Item;
import me.chriss99.spellbend.util.ItemData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ElementGui extends GuiInventory {
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();

    public ElementGui(@NotNull Player player, @NotNull ElementEnum elementEnum) {
        super(Objects.requireNonNullElse(elementEnum.getDisplayItem().getItemMeta().displayName(), Component.text("NULL PLS HELP")), 5);
        GuiUtil.outLineGui(this, Item.create(Material.BLUE_STAINED_GLASS_PANE, Component.text(""), 501), 5);

        new GuiItem(Item.create(Material.OAK_SIGN, "<bold>Click on a move", new String[]{
                "<white>to purchase it!",
                "<dark_gray>---------------",
                "<gray>Click on<white><bold> glowing moves",
                "<gray>to equip them!",
                "",
                "<white><bold>Drag <gray>or<white><bold> shift click <gray>moves",
                "<gray>into the shop to unequip them!"
        }, 501))
                .registerIn(this, 4);
        new GuiButton(Item.create(Material.ARROW, "<gray><bold>Back", 301))
                .onClick(clickEvent -> new ShopGui(player))
                .registerIn(this, 18);

        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
        int length = elementEnum.getSpells().size();
        int[] positions = {12, 14, 22, 30, 32, 33, 34, 35}; //the last 3 are there for the case that we ever do more than 5 spells per Element

        for (int i = 0;i<length;i++) {
            SpellEnum spellEnum = elementEnum.getSpell(i);
            new GuiButton(Item.edit(spellEnum.getDisplayItem(), createSpellOwningLore(sessionData, elementEnum, spellEnum)))
                    .onClick(clickEvent -> spellClickEvent(player, sessionData, elementEnum, spellEnum))
                    .registerIn(this, positions[i]);
        }

        player.openInventory(inventory);
    }

    private static void spellClickEvent(@NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        if (sessionData.getElementsOwned().playerOwnsSpellInElement(elementEnum, spellEnum)) {
            giveSpell(player, spellEnum);
            return;
        }

        if (spellEnum.playerCanBuy(sessionData)) {
            buySpell(player, sessionData, elementEnum, spellEnum);
            return;
        }

        player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <dark_gray>»<red> Not enough Gold! Need <gold>" + (long) (spellEnum.getPrice() - sessionData.getGold().getCurrency()) + "<red> more!"));
    }

    private static void buySpell(@NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        sessionData.getElementsOwned().addSpellOwnedInElement(elementEnum, spellEnum);
        sessionData.getGold().addCurrency(-spellEnum.getPrice());

        if (InventoryUtil.spellsInsideInventory(player.getInventory()) < 5 && !InventoryUtil.inventoryContainsSpellName(player.getInventory(), spellEnum.toString()))
            player.getInventory().addItem(spellEnum.getUseItem());

        player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <dark_gray>»<yellow> Learnt " +
                SpellBend.getMiniMessage().serializeOrNull(spellEnum.getDisplayItem().getItemMeta().displayName()) + " <gold>for <yellow>" + spellEnum.getPrice() + " Gold!"));

        new ElementGui(player, elementEnum);
    }

    private static void giveSpell(@NotNull Player player, @NotNull SpellEnum spellEnum) {
        ItemStack item = spellEnum.getUseItem();
        if (InventoryUtil.inventoryContainsSpellName(player.getInventory(), ItemData.getPersistentDataValue(item, PersistentDataKeys.spellNameKey, PersistentDataType.STRING))) {
            player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <dark_gray>»<red> You already have this spell equipped!"));
            return;
        }

        if (InventoryUtil.spellsInsideInventory(player.getInventory()) >= 5) {
            player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <dark_gray>»<red> Unequip a spell first! <dark_gray>(<gray>Drag into Shop<dark_gray>)"));
            return;
        }

        player.getInventory().addItem(item);
    }

    private static @NotNull String[] createSpellOwningLore(@NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        if (sessionData.getElementsOwned().playerOwnsSpellInElement(elementEnum, spellEnum))
            return new String[]{"<dark_gray>----------------", "<yellow><bold>CLICK TO EQUIP"};

        if (sessionData.getElementsOwned().playerOwnsPreviousSpellInElement(elementEnum, spellEnum))
            return new String[]{"<dark_gray>----------------", "<yellow>$ <red>" + spellEnum.getPrice() + " <gold>Gold"};

        String spellToLearnBefore = null;

        SpellEnum spellEnumBefore = elementEnum.getSpellBefore(spellEnum);
        if (spellEnumBefore != null)
            spellToLearnBefore = miniMessage.serializeOrNull(spellEnumBefore.getDisplayItem().getItemMeta().displayName());

        return new String[]{"<dark_gray>----------------", "<red>You must learn " + spellToLearnBefore + "<red> first!"};
    }
}
