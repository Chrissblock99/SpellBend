package me.chriss99.spellbend.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class GUIManager {
    private static final LinkedHashMap<Function<ItemStack, Boolean>, Consumer<InventoryClickEvent>> inventoryClickEventConsumers = new LinkedList<>();

    private static void itemClickEvent(@NotNull InventoryClickEvent event) {

    }
}