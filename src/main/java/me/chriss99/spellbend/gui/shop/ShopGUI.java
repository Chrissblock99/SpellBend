package me.chriss99.spellbend.gui.shop;

import me.chriss99.spellbend.gui.GUIEventProcessor;
import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.NotNull;

public class ShopGUI extends GUIEventProcessor<ShopGUI.Data> {
    @Override
    public Data convertInventoryEvent(@NotNull InventoryEvent event) {

    }

    public record Data() {}
}

/*
        (sessionData.getElementsOwned().playerOwnsElement(elementEnum)) ?
                "OPEN_SHOP; " + elementEnum :
                (elementEnum.playerCanBuy(sessionData)) ?
                        "BUY_ELEMENT; " + elementEnum :
                        "SEND_MESSAGE; <blue><bold>SHOP <gray>»<red> Not enough Gems! Need §b" + elementEnum.getPrice() + "<red> more!"));

        String itemAction = "BUY_SPELL; " + elementEnum + i;
        if (!spellEnum.playerCanBuy(sessionData))
            itemAction = "SEND_MESSAGE; <blue><bold>SHOP <dark_gray>»<red> Not enough Gold! Need §e" + spellEnum.getPrice() + "<red> more!";
        if (sessionData.getElementsOwned(elementObj.getIndex()) != i)
            itemAction = "SEND_MESSAGE; <blue><bold>SHOP <dark_gray>»<red> You must learn " + elementObj.getSpellItem(i-1).getItemMeta().getDisplayName() + "<red> first!";
        if (sessionData.getElementsOwned().playerOwnsSpell(elementEnum, spellEnum))
            itemAction = "GIVE_SPELL; " + elementEnum.getSpell(i);
 */
