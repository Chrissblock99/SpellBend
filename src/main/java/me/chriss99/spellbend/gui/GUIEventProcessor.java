package me.chriss99.spellbend.gui;

import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class GUIEventProcessor<T> {
    /**
     * A list of all Functions that convert an InventoryEvent to a comparable ArrayList of Objects <br>
     * These return null if the InventoryEvent is not applicable or similar
     */
    private static final LinkedList<GUIEventProcessor<?>> eventProcessors = new LinkedList<>();

    private final HashMap<T, Consumer<InventoryEvent>> comparableEventToConsumerMap;

    public GUIEventProcessor() {
    }

    public static void onInventoryEvent(@NotNull InventoryEvent event) {
        for (GUIEventProcessor<?> eventProcessor : eventProcessors)
            eventProcessor.process(event);
    }

    public void process(@NotNull InventoryEvent event) {
        Consumer<InventoryEvent> eventConsumer = comparableEventToConsumerMap.get(eventAdapter.apply(event));
        if (eventConsumer != null)
            eventConsumer.accept(event);
    }

    public abstract T convertInventoryEvent(@NotNull InventoryEvent event);

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GUIEventProcessor<?>) obj;
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

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ReflectCommand {
        T type;
    }
}
