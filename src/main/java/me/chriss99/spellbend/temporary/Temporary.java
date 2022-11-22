package me.chriss99.spellbend.temporary;

import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.playerdata.PlayerDataBoard;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This should be a class from temporary utilities, which haven't found a class yet!
 */
public class Temporary {
    //TODO find a class for this
    public static void playerSwitchHeldItem(@NotNull Player player, @Nullable ItemStack oldItem, @Nullable ItemStack newItem) {
        if (SpellHandler.itemIsSpell(oldItem) || SpellHandler.itemIsSpell(newItem)) {
            String spellType = ItemData.getSpellType(newItem);
            if (CoolDowns.typeIsCooledDown(player, spellType))
                PlayerDataBoard.registerPlayer(player, spellType);
                //have to pass null here as the event isn't finished yet.
                //therefore the item the player is currently holding is still the old one
            else PlayerDataBoard.deRegisterPlayer(player, null);
        }
    }
}
