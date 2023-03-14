package me.chriss99.spellbend.guiframework;

import me.chriss99.spellbend.SpellBend;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public abstract class GuiInventory {
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();

    protected final Inventory inventory;
    private final HashMap<ItemStack, GuiItem> guiItems = new HashMap<>();

    public GuiInventory(@NotNull Component title, int rows) {
        inventory = Bukkit.createInventory(null, rows*9, title);
        GuiManager.registerGuiInventory(this);
    }

    public GuiInventory(@NotNull String miniMessageTitle, int rows) {
        this(miniMessage.deserialize(miniMessageTitle), rows);
    }

    public void click(@NotNull InventoryClickEvent clickEvent) {
        clickInInventory(clickEvent);
        GuiItem guiItem = guiItems.get(clickEvent.getCurrentItem());
        if (guiItem == null)
            return;

        clickEvent.setCancelled(true);
        if (!(guiItem instanceof GuiButton guiButton))
            return;

        guiButton.click(clickEvent);
    }

    public void clickInInventory(@NotNull InventoryClickEvent clickEvent) {}

    public void clickInOtherInventory(@NotNull InventoryClickEvent clickEvent) {}

    public void registerItem(@NotNull GuiItem guiItem, @NotNull List<Integer> slots) {
        guiItems.put(guiItem.item, guiItem);
        for (int slot : slots)
            inventory.setItem(slot, guiItem.item);
    }

    public void registerItem(@NotNull GuiItem guiItem, int slot) {
        guiItems.put(guiItem.item, guiItem);
        inventory.setItem(slot, guiItem.item);
    }
}
