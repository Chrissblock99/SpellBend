package me.chriss99.spellbend.gui;

import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class EventProcessor {
    private final Function<InventoryEvent, ArrayList<Object>> eventAdapter;
    private final HashMap<ArrayList<Object>, Consumer<InventoryEvent>> comparableEventToConsumerMap;

    public EventProcessor(@NotNull Function<InventoryEvent, ArrayList<Object>> eventAdapter,
                          @NotNull HashMap<ArrayList<Object>, Consumer<InventoryEvent>> comparableEventToConsumerMap) {
        this.eventAdapter = eventAdapter;
        this.comparableEventToConsumerMap = comparableEventToConsumerMap;
    }

    public void process(@NotNull InventoryEvent event) {
        Consumer<InventoryEvent> eventConsumer = comparableEventToConsumerMap.get(eventAdapter.apply(event));
        if (eventConsumer != null)
            eventConsumer.accept(event);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EventProcessor) obj;
        return Objects.equals(this.eventAdapter, that.eventAdapter) &&
                Objects.equals(this.comparableEventToConsumerMap, that.comparableEventToConsumerMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventAdapter, comparableEventToConsumerMap);
    }

    @Override
    public String toString() {
        return "EventMappingConsumer[" +
                "eventAdapter=" + eventAdapter + ", " +
                "comparableEventToConsumerMap=" + comparableEventToConsumerMap + ']';
    }
}
