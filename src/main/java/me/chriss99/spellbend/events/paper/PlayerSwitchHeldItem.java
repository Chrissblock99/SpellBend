package me.chriss99.spellbend.events.paper;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.data.SpellHandler;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerSwitchHeldItem implements Listener {
    public PlayerSwitchHeldItem() {
        SpellBend.registerPaperEvent(this);
    }

    @EventHandler
    public void onPlayerSwitchHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        processPlayerSwitchHeldItem(player, player.getInventory().getItemInMainHand(), player.getInventory().getItem(event.getNewSlot()));
    }

    public static void processPlayerSwitchHeldItem(@NotNull Player player, @Nullable ItemStack oldItem, @Nullable ItemStack newItem) {
        if (SpellHandler.itemIsSpell(oldItem) || SpellHandler.itemIsSpell(newItem)) {
            String spellType = ItemData.getSpellType(newItem);
            PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);

            if (sessionData.getCoolDowns().typeIsCooledDown(spellType))
                //noinspection ConstantConditions because if it would be null, then the type cannot be cooled down, and it would not reach this statement
                sessionData.getPlayerDataBoard().displayCooldown(spellType);
                //have to pass null here as the event isn't finished yet.
                //therefore the item the player is currently holding is still the old one
            else sessionData.getPlayerDataBoard().stopDisplayCooldown(null);
        }
    }
}
