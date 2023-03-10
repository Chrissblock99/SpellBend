package me.chriss99.spellbend.gui;

import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class GUIManager {
    /**
     * A list of all Functions that convert an InventoryEvent to a comparable ArrayList of Objects <br>
     * These return null if the InventoryEvent is not applicable or similar
     */
    private static final LinkedList<EventProcessor> eventProcessors = new LinkedList<>();

    public static void onInventoryEvent(@NotNull InventoryEvent event) {
        for (EventProcessor eventProcessor : eventProcessors)
            eventProcessor.process(event);
    }

    public static void registerEventProcessor(@NotNull Function<InventoryEvent, ArrayList<Object>> eventAdapter, @NotNull HashMap<ArrayList<Object>, Consumer<InventoryEvent>> comparableEventToConsumerMap) {
        eventProcessors.add(new EventProcessor(eventAdapter, comparableEventToConsumerMap));
    }

    public static void registerEventProcessor(@NotNull EventProcessor eventProcessor) {
        eventProcessors.add(eventProcessor);
    }
}