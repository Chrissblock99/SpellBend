package me.chriss99.spellbend.spell;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.playerdata.PlayerSessionStorage;
import me.chriss99.spellbend.spell.spells.Killable;
import me.chriss99.spellbend.spell.spells.Spell;
import me.chriss99.spellbend.spell.spellsubclassbuilder.SpellSubClassBuilder;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class SpellHandler {
    private static final HashMap<String, SpellSubClassBuilder> nameToSpellBuilderMap = new HashMap<>();
    private static final HashMap<Player, ArrayList<Spell>> playerToActiveSpellListMap = new HashMap<>();

    /**
     * @throws IllegalArgumentException If name is already contained in the map.
     *
     * @param name The name of the Spell to add.
     * @param builder The SpellSubClassBuilder object which will return a Spell
     */
    public static void addSpellBuilderToMap(@NotNull String name, @NotNull SpellSubClassBuilder builder) {
        if (nameToSpellBuilderMap.containsKey(name))
            throw new IllegalArgumentException("Spell name is already contained in the map!");

        nameToSpellBuilderMap.put(name.toUpperCase(), builder);
    }

    /**
     * Creates a spell of item's named type and adds it to the players activeSpellList.
     *
     * @param player The player casting the spell
     * @param spellItem The item used (HAS to be a spell)
     * @return If the spell was cast or not
     */
    public static boolean letPlayerCastSpell(@NotNull Player player, @NotNull ItemStack spellItem) {
        return letPlayerCastSpell(player, spellItem, false);
    }

    /**
     * Creates a spell of named type and adds it to the players activeSpellList.
     *
     * @param player The player casting the spell
     * @param spellName The name of the spell
     * @param spellItem The item used (doesn't HAVE to be a spell)
     * @return If the spell was cast or not
     */
    public static boolean letPlayerCastSpell(@NotNull Player player, @NotNull String spellName, @NotNull ItemStack spellItem) {
        return letPlayerCastSpell(player, spellName, spellItem, false);
    }

    /**
     * Creates a spell of item's named type and adds it to the players activeSpellList.
     *
     * @param player The player casting the spell
     * @param spellItem The item used (HAS to be a spell)
     * @param force To force the spell even if coolDowned
     * @return If the spell was cast or not
     */
    public static boolean letPlayerCastSpell(@NotNull Player player, @NotNull ItemStack spellItem, boolean force) {
        if (!itemIsRegisteredSpell(spellItem)) {
            Bukkit.getLogger().warning("The spell item \"" + spellItem + "\" " + player.getName() + " tried to cast is not a spell, casting skipped!");
            return false;
        }

        //noinspection ConstantConditions
        return letPlayerCastSpell(player, spellItem.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING).toUpperCase(), spellItem, force);
    }

    /**
     * Creates a spell of named type and adds it to the players activeSpellList.
     *
     * @param player The player casting the spell
     * @param spellName The name of the spell
     * @param spellItem The item used (doesn't HAVE to be a spell)
     * @param force To force the spell even if coolDowned
     * @return If the spell was cast or not
     */
    public static boolean letPlayerCastSpell(@NotNull Player player, @NotNull String spellName, @NotNull ItemStack spellItem, boolean force) {
        //TODO enforce spellTypes being given
        if (!force && PlayerSessionStorage.coolDowns.get(player).containsKey(ItemData.getSpellType(spellItem)))
            return false;

        playerToActiveSpellListMap.get(player).add(nameToSpellBuilderMap.get(spellName.toUpperCase()).createSpell(player, spellItem));
        return true;
    }

    public static ArrayList<Spell> getActivePlayerSpells(@NotNull Player player) {
        if (!playerToActiveSpellListMap.containsKey(player)) {
            Bukkit.getLogger().warning("Player " + player.getName() + " was not contained in playerToActiveSpellListMap! Registering manually.\n(This probably won't affect the program directly, it just shouldn't have happened and is probably a bug that could cause other problems.)");
            playerToActiveSpellListMap.put(player, new ArrayList<>());
        }

        return playerToActiveSpellListMap.get(player);
    }

    public static void killPlayer(@NotNull Player player, @Nullable Entity killer) {
        ArrayList<Spell> activeSpells = playerToActiveSpellListMap.get(player);
        for (int i = activeSpells.size()-1;i>=0;i--) {
            Spell spell = activeSpells.get(i);
            if (spell instanceof Killable killable)
                killable.casterDeath(killer);
            spell.cancelSpell();
        }
    }

    /**
     * Adds the player and an empty ArrayList to the playerToActiveSpellListMap.
     * <b>This is only intended to be used if the player joins the server.</b>
     *
     * @param player The player to register
     */
    public static void registerPlayer(@NotNull Player player) {
        if (playerToActiveSpellListMap.containsKey(player)) {
            Bukkit.getLogger().warning("A call to register \"" + player.getName() + "\" in the playerToActiveSpellListMap was received but the player is already contained in the map! Ignoring the call.");
            return;
        }

        playerToActiveSpellListMap.put(player, new ArrayList<>());
    }

    /**
     * Removes the player from the playerToActiveSpellListMap.
     * <b>This is only intended to be used if the player leaves the server.</b>
     *
     * @param player The player to remove
     */
    public static void deRegisterPlayer(@NotNull Player player) {
        if (!playerToActiveSpellListMap.containsKey(player)) {
            Bukkit.getLogger().warning("A call to deRegister \"" + player.getName() + "\" in the playerToActiveSpellListMap was received but the player is not contained in the map! Ignoring the call.");
            return;
        }

        ArrayList<Spell> activeSpells = playerToActiveSpellListMap.get(player);
        for (int i = activeSpells.size()-1;i>=0;i--)
            activeSpells.get(i).casterLeave();

        playerToActiveSpellListMap.remove(player);
    }

    public static boolean spellBuilderIsRegistered(@NotNull String name) {
        return nameToSpellBuilderMap.containsKey(name.toUpperCase());
    }

    public static boolean spellBuilderIsRegistered(@NotNull SpellSubClassBuilder builder) {
        return nameToSpellBuilderMap.containsValue(builder);
    }

    /**
     * Checks if the item has a spellName and spellType argument, both not being null
     *
     * @param item The item to be checked
     * @return If it is a spell
     */
    public static boolean itemIsSpell(@Nullable ItemStack item) {
        if (item == null)
            return false;

        if (item.hasItemMeta()) {
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if (data.has(PersistentDataKeys.spellNameKey, PersistentDataType.STRING) && data.has(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING))
                return data.get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING) != null && data.get(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING) != null;
        }
        return false;
    }

    /**
     * Checks itemIsSpell() and if the name is contained in the nameToSpellBuilderMap
     *
     * @param item The item to be checked
     * @return If it is a registered spell
     */
    public static boolean itemIsRegisteredSpell(@Nullable ItemStack item) {
        if (item == null)
            return false;

        //noinspection ConstantConditions
        return itemIsSpell(item) && spellBuilderIsRegistered(item.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING));
    }
}
