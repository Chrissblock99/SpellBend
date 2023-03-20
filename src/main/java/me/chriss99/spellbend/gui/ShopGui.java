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
import me.chriss99.spellbend.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ShopGui extends GuiInventory {
    private static final SpellBend plugin = SpellBend.getInstance();
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();
    private static final ElementEnum[] elementEnums = ElementEnum.values();

    public ShopGui(@NotNull Player player) {
        super("<blue><bold>SHOP", 5);
        GuiUtil.outLineGui(this, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE)
                .setDisplayName(Component.text(""))
                .setCustomModelData(501)
                .build(), 5);

        new GuiItem(new ItemBuilder(Material.OAK_SIGN)
                        .setMiniMessageDisplayName("<bold>Click on an Element")
                        .setMiniMessageLore(
                            "<white>to purchase it!",
                            "<dark_gray>---------------",
                            "<white><bold>Drag <gray>or<white><bold> shift click <gray>moves",
                            "<gray>into the shop to unequip them!")
                        .setCustomModelData(501)
                        .build())
                .registerIn(this, 4);
        new GuiButton(new ItemBuilder(Material.CHEST)
                        .setMiniMessageDisplayName("<blue><bold>COSMETICS")
                        .setCustomModelData(301)
                        .build())
                .onClick(clickEvent -> player.sendMessage("You've been trolled!"))
                .registerIn(this, 40);

        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
        for (int i = 0; i < elementEnums.length; i++) {
            ElementEnum elementEnum = elementEnums[i];
            ItemBuilder itemBuilder = new ItemBuilder(elementEnum.getDisplayItem())
                    .addMiniMessageLore(createElementOwningLore(sessionData, elementEnum));
            if (sessionData.getElementsOwned().playerOwnsElement(elementEnum))
                itemBuilder.addEnchantment(Enchantment.MENDING, 0);

            new GuiButton(itemBuilder.build())
                    .onClick(clickEvent -> elementClickEvent(clickEvent, player, sessionData, elementEnum))
                    .registerIn(this, GuiUtil.convertNumToOutlinedGUISlot(i));
        }

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.2f);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
            }
        }.runTaskLater(plugin, 4);
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
        if (SpellHandler.itemIsSpell(event.getCursor())) {
            unequipMessage((Player) event.getWhoClicked(), event.getCursor());
            event.setCursor(null);
        }
    }

    public static void nonShopClick(@NotNull InventoryClickEvent event) {
        if (event.getClick().isShiftClick()) {
            event.setCancelled(true);
            if (SpellHandler.itemIsSpell(event.getCurrentItem())) {
                unequipMessage((Player) event.getWhoClicked(), event.getCurrentItem());
                event.setCurrentItem(null);
            }
        }
    }

    public static void unequipMessage(@NotNull Player player, @NotNull ItemStack item) {
        player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>» <yellow>Unequipped " + miniMessage.serializeOrNull(item.getItemMeta().displayName()) +
                "<reset><yellow>!"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 0.8f);
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
            player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>» <red>You do not own " +
                    miniMessage.serializeOrNull(elementEnum.getDisplayItem().getItemMeta().displayName()) + "<reset><red>!"));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
            return;
        }

        addAllElementSpells(player, elementsOwned, elementEnum);
    }

    private static void elementClick(@NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        ElementsOwned elementsOwned = sessionData.getElementsOwned();
        if (elementsOwned.playerOwnsElement(elementEnum)) {
            ElementGui.open(player, elementEnum, false);
            return;
        }

        CurrencyTracker gems = sessionData.getGems();
        if (!elementEnum.playerCanBuy(sessionData)) {
            player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>»<red> Not enough Gems! Need <aqua>" +
                    (long) (elementEnum.getPrice() - gems.getCurrency()) + "<red> more!"));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
            return;
        }

        gems.addCurrency(-elementEnum.getPrice());
        elementsOwned.setElementOwned(elementEnum);
        ElementGui.open(player, elementEnum, true);
        player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>»<yellow> Purchased " +
                miniMessage.serializeOrNull(elementEnum.getDisplayItem().getItemMeta().displayName()) + " <reset><gold>for <aqua>" + elementEnum.getPrice() + " Gems<yellow>!"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.2f);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1.5f);
    }

    public static void addAllElementSpells(@NotNull Player player, @NotNull ElementsOwned elementsOwned, @NotNull ElementEnum elementEnum) {
        Inventory playerInventory = player.getInventory();
        if (InventoryUtil.spellsInsideInventory(playerInventory) >= 5) {
            player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <reset><dark_gray>»<red> Unequip a spell " +
                    "first! <dark_gray>(<gray>Drag into Shop<dark_gray>)"));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
            return;
        }

        boolean spellAdded = false;

        for (SpellEnum spellEnum : elementEnum.getSpells()) {
            if (!elementsOwned.playerOwnsSpellInElement(elementEnum, spellEnum)) {
                player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <reset><dark_gray>»<red> You do not own " +
                        miniMessage.serializeOrNull(spellEnum.getDisplayItem().getItemMeta().displayName()) + "<reset><yellow>!"));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
                spellAdded = false;
                break;
            }

            if (InventoryUtil.inventoryContainsSpellName(playerInventory, spellEnum.toString()))
                continue;
            if (InventoryUtil.spellsInsideInventory(playerInventory) >= 5)
                break;

            ItemStack item = spellEnum.getUseItem();
            player.getInventory().addItem(item);
            player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>» <yellow>Equipped " +
                    miniMessage.serializeOrNull(item.getItemMeta().displayName()) + "<reset><yellow>!"));
            spellAdded = true;
        }

        if (spellAdded)
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
    }

    private static @NotNull String[] createElementOwningLore(@NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum) {
        if (sessionData.getElementsOwned().playerOwnsElement(elementEnum))
            return new String[]{"<green><bold>SHIFT CLICK TO EQUIP"};

        return new String[]{"<green>$ <aqua>" + elementEnum.getPrice() + " <dark_aqua>Gems",
                (elementEnum.playerCanBuy(sessionData)) ? "<gold>You can<yellow> buy<gold> this!" : "<red>You can't<yellow> buy<red> this yet!"};

    }
}
