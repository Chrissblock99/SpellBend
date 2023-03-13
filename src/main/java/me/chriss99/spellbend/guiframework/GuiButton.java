package me.chriss99.spellbend.guiframework;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class GuiButton extends GuiItem {
    private Consumer<InventoryClickEvent> clickEventConsumer = null;

    public GuiButton(@NotNull ItemStack item) {
        super(item);
    }

    public GuiButton onClick(@NotNull Consumer<InventoryClickEvent> clickEventConsumer) {
        this.clickEventConsumer = clickEventConsumer;
        return this;
    }

    public void click(@NotNull InventoryClickEvent clickEvent) {
        if (clickEventConsumer != null)
            clickEventConsumer.accept(clickEvent);
    }
}
