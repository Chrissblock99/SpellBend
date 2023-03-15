package me.chriss99.spellbend.gui;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.guiframework.GuiButton;
import me.chriss99.spellbend.guiframework.GuiInventory;
import me.chriss99.spellbend.guiframework.GuiItem;
import me.chriss99.spellbend.harddata.ElementEnum;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.harddata.SpellEnum;
import me.chriss99.spellbend.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ElementGui extends GuiInventory {
    private static final SpellBend plugin = SpellBend.getInstance();
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();

    private ElementGui(@NotNull Player player, @NotNull ElementEnum elementEnum) {
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
            //noinspection DataFlowIssue
            LinkedList<String> lore = new LinkedList<>(spellEnum.getDisplayItem().lore() == null ? new LinkedList<>() : spellEnum.getDisplayItem().lore().stream().map(miniMessage::serialize).toList());
            lore.addAll(List.of(createSpellOwningLore(sessionData, elementEnum, spellEnum)));

            new GuiButton(Item.edit(spellEnum.getDisplayItem(), lore.toArray(new String[0])))
                    .onClick(clickEvent -> spellClickEvent(player, sessionData, elementEnum, spellEnum))
                    .registerIn(this, positions[i]);
        }

        player.openInventory(inventory);
    }

    @Override
    public void clickInInventory(@NotNull InventoryClickEvent event) {
        ShopGui.shopClick(event);
    }

    @Override
    public void clickInOtherInventory(@NotNull InventoryClickEvent event) {
        ShopGui.nonShopClick(event);
    }

    private static void spellClickEvent(@NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        if (sessionData.getElementsOwned().playerOwnsSpellInElement(elementEnum, spellEnum)) {
            giveSpell(player, spellEnum);
            return;
        }

        if (!sessionData.getElementsOwned().playerOwnsPreviousSpellInElement(elementEnum, spellEnum)) {
            String spellToLearnBefore = null;

            SpellEnum spellEnumBefore = elementEnum.getSpellBefore(spellEnum);
            if (spellEnumBefore != null)
                spellToLearnBefore = miniMessage.serializeOrNull(spellEnumBefore.getDisplayItem().getItemMeta().displayName());
            player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>»<red> You must learn " + spellToLearnBefore + "<reset><red> first!"));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
            return;
        }

        if (spellEnum.playerCanBuy(sessionData)) {
            buySpell(player, sessionData, elementEnum, spellEnum);
            return;
        }

        player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>»<red> Not enough Gold! Need <gold>" +
                (long) (spellEnum.getPrice() - sessionData.getGold().getCurrency()) + "<red> more!"));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
    }

    private static void buySpell(@NotNull Player player, @NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        sessionData.getElementsOwned().addSpellOwnedInElement(elementEnum, spellEnum);
        sessionData.getGold().addCurrency(-spellEnum.getPrice());

        if (InventoryUtil.spellsInsideInventory(player.getInventory()) < 5 && !InventoryUtil.inventoryContainsSpellName(player.getInventory(), spellEnum.toString()))
            player.getInventory().addItem(spellEnum.getUseItem());

        player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <reset><dark_gray>»<yellow> Learnt " +
                SpellBend.getMiniMessage().serializeOrNull(spellEnum.getDisplayItem().getItemMeta().displayName()) + " <reset><gold>for <yellow>" + spellEnum.getPrice() + " Gold!"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
        player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 2f);

        new ElementGui(player, elementEnum);
    }

    private static void giveSpell(@NotNull Player player, @NotNull SpellEnum spellEnum) {
        ItemStack item = spellEnum.getUseItem();
        if (InventoryUtil.inventoryContainsSpellName(player.getInventory(), ItemData.getPersistentDataValue(item, PersistentDataKeys.spellNameKey, PersistentDataType.STRING))) {
            player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <reset><dark_gray>»<red> You already have this spell equipped!"));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
            return;
        }

        if (InventoryUtil.spellsInsideInventory(player.getInventory()) >= 5) {
            player.sendMessage(SpellBend.getMiniMessage().deserialize("<blue><bold>SHOP <reset><dark_gray>»<red> Unequip a spell first! <dark_gray>(<gray>Drag into Shop<dark_gray>)"));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 0.8f);
            return;
        }

        player.getInventory().addItem(item);
        player.sendMessage(miniMessage.deserialize("<blue><bold>SHOP <reset><dark_gray>» <yellow>Equipped " + miniMessage.serializeOrNull(item.getItemMeta().displayName()) + "<reset><yellow>!"));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
    }

    private static @NotNull String[] createSpellOwningLore(@NotNull PlayerSessionData sessionData, @NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        if (sessionData.getElementsOwned().playerOwnsSpellInElement(elementEnum, spellEnum))
            return new String[]{"<dark_gray>----------------", "<green><bold>CLICK TO EQUIP"};

        if (sessionData.getElementsOwned().playerOwnsPreviousSpellInElement(elementEnum, spellEnum))
            return new String[]{"<dark_gray>----------------", "<green>$ <yellow>" + spellEnum.getPrice() + " <gold>Gold"};

        String spellToLearnBefore = null;

        SpellEnum spellEnumBefore = elementEnum.getSpellBefore(spellEnum);
        if (spellEnumBefore != null)
            spellToLearnBefore = miniMessage.serializeOrNull(spellEnumBefore.getDisplayItem().getItemMeta().displayName());

        return new String[]{"<dark_gray>----------------", "<red>You must learn " + spellToLearnBefore + "<reset><red> first!"};
    }

    public static void open(@NotNull Player player, @NotNull ElementEnum elementEnum, boolean bought) {
        new ElementGui(player, elementEnum);

        if (!bought) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.2f);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.5f);
                }
            }.runTaskLater(plugin, 4);
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 0.707107f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 0.707107f);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1.414214f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 0.707107f);
        new BukkitTimer(
                new BukkitTimer.TimedAction(2, () -> { //1
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 0.529732f);



                }),
                new BukkitTimer.TimedAction(2, () -> { //2
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 0.840896f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 0.793701f);

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 0.793701f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //3
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 0.943874f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 0.840896f);


                }),
                new BukkitTimer.TimedAction(2, () -> { //4
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.059463f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 0.943874f);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1.414214f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 0.840896f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //5
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 0.943874f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.059463f);

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 0.943874f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //6
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.059463f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.259921f);

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 1.059463f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //7
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.259921f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.414214f);


                }),
                new BukkitTimer.TimedAction(2, () -> { //8
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.414214f);

                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1.414214f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 1.259921f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //9
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.259921f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.059463f);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1.259921f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 1.059463f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //10
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.059463f);

                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1.059463f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 1.122462f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //11
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.259921f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10f, 1.414214f);
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1.259921f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 1.259921f);
                }),
                new BukkitTimer.TimedAction(2, () -> { //12
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 10f, 1.414214f);

                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 1.414214f);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 5f, 1.414214f);
                })
        );
    }
}
