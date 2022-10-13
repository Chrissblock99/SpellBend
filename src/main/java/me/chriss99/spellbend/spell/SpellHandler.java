package me.chriss99.spellbend.spell;

import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.spell.spells.Spell;
import me.chriss99.spellbend.spell.spellsubclassbuilder.SpellSubClassBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

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
     * Creates a spell of named type and adds it to the players activeSpellList.
     *
     * @param player The player casting the spell
     * @param spellName The name of the spell
     * @param spellItem The item used (doesn't HAVE to be a spell)
     */
    public static void letPlayerCastSpell(@NotNull Player player, @NotNull String spellName, @NotNull ItemStack spellItem) {
        Spell spell = nameToSpellBuilderMap.get(spellName.toLowerCase()).createSpell(player, spellItem);
        playerToActiveSpellListMap.get(player).add(spell);
    }

    /**
     * Creates a spell of item's named type and adds it to the players activeSpellList.
     *
     * @param player The player casting the spell
     * @param spellItem The item used (HAS to be a spell)
     */
    public static void letPlayerCastSpell(@NotNull Player player, @NotNull ItemStack spellItem) {
        String spellName = spellItem.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING);
        Spell spell = nameToSpellBuilderMap.get(spellName).createSpell(player, spellItem);
        playerToActiveSpellListMap.get(player).add(spell);
    }

    public static ArrayList<Spell> getActivePlayerSpells(@NotNull Player player) {
        if (!playerToActiveSpellListMap.containsKey(player)) {
            Bukkit.getLogger().warning("Player " + player.getName() + " was not contained in playerToActiveSpellListMap! Registering manually.\n(This probably won't affect the program, it just shouldn't have happened.)");
            playerToActiveSpellListMap.put(player, new ArrayList<>());
        }

        return playerToActiveSpellListMap.get(player);
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

        for (Spell spell : playerToActiveSpellListMap.get(player)) {
            spell.casterLeave();
        }

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
    public static boolean itemIsSpell(ItemStack item) {
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
    public static boolean itemIsRegisteredSpell(ItemStack item) {
        if (item == null)
            return false;
        //noinspection ConstantConditions
        Bukkit.getLogger().info("item isn't null, is spell: " + itemIsSpell(item) + " and is registered: " + spellBuilderIsRegistered(item.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING)));

        //noinspection ConstantConditions
        return itemIsSpell(item) && spellBuilderIsRegistered(item.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING));
    }
}
