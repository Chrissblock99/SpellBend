package me.chriss99.spellbend.spell;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpellHandler {
    private static final Map<String, SpellSubClassBuilder> nameToSpellBuilderMap = new HashMap<>();

    private final Player player;
    private final Set<Spell> activeSpells = new HashSet<>();
    private final Map<ItemStack, Runnable> clickableSpellRunnables = new HashMap<>();

    public SpellHandler(@NotNull Player player) {
        this.player = player;
    }

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
     * Adds an item and runnable to the clickableSpellRunnables
     *
     * @param item The item to add
     * @param runnable The runnable to execute
     */
    public void addClickableSpellRunnable(@NotNull ItemStack item, @NotNull Runnable runnable) {
        clickableSpellRunnables.put(item, runnable);
    }

    /**
     * Removes the item from the clickableSpellRunnables
     *
     * @param item The item to remove
     */
    public void removeClickableSpellRunnable(@NotNull ItemStack item) {
        clickableSpellRunnables.remove(item);
    }

    public void playerClickedSpellItem(@NotNull ItemStack spellItem) {
        String spellName = ItemData.getSpellName(spellItem);
        if (spellName == null) {
            Bukkit.getLogger().warning(player.getName() + " supposedly clicked a spellItem, but the item has no spellName!");
            return;
        }

        playerClickedSpellItem(spellName, spellItem);
    }

    public void playerClickedSpellItem(@NotNull String spellName, @NotNull ItemStack spellItem) {
        String spellType = ItemData.getSpellType(spellItem);
        if (spellType == null) {
            Bukkit.getLogger().warning(player.getName() + " supposedly clicked a spellItem, but the item has no spellType!");
            return;
        }

        playerClickedSpellItem(spellName, spellType, spellItem);
    }

    public void playerClickedSpellItem(@NotNull String spellName, @NotNull String spellType, @NotNull ItemStack spellItem) {
        Runnable clickableSpellRunnable = clickableSpellRunnables.get(spellItem);
        if (clickableSpellRunnable != null) {
            clickableSpellRunnable.run();
            return;
        }

        letPlayerCastSpell(spellName, spellType, spellItem, false);
    }

    /**
     * Creates a spell of item's named type and adds it to the players activeSpellSet.
     *
     * @param spellItem The item used (HAS to be a spell)
     * @return If the spell was cast or not
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean letPlayerCastSpell(@NotNull ItemStack spellItem) {
        return letPlayerCastSpell(spellItem, false);
    }

    /**
     * Creates a spell of named type and adds it to the players activeSpellSet.
     *
     * @param spellName The name of the spell
     * @param spellItem The item used (doesn't HAVE to be a spell)
     * @return If the spell was cast or not
     */
    public boolean letPlayerCastSpell(@NotNull String spellName, @NotNull ItemStack spellItem) {
        return letPlayerCastSpell(spellName, spellItem, false);
    }

    /**
     * Creates a spell of item's named type and adds it to the players activeSpellSet.
     *
     * @param spellItem The item used (HAS to be a spell)
     * @param force To force the spell even if coolDowned
     * @return If the spell was cast or not
     */
    public boolean letPlayerCastSpell(@NotNull ItemStack spellItem, boolean force) {
        if (!itemIsRegisteredSpell(spellItem)) {
            Bukkit.getLogger().warning("The spell item \"" + spellItem + "\" " + player.getName() + " tried to cast is not a registered spell, casting skipped!");
            return false;
        }

        //noinspection ConstantConditions
        return letPlayerCastSpell(spellItem.getItemMeta().getPersistentDataContainer().get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING).toUpperCase(), spellItem, force);
    }

    /**
     * Creates a spell of named type and adds it to the players activeSpellSet.
     *
     * @param spellName The name of the spell
     * @param spellItem The item used (HAS to be a spell)
     * @param force To force the spell even if coolDowned
     * @return If the spell was cast or not
     */
    public boolean letPlayerCastSpell(@NotNull String spellName, @NotNull ItemStack spellItem, boolean force) {
        if (!itemIsRegisteredSpell(spellItem)) {
            Bukkit.getLogger().warning("The spell item \"" + spellItem + "\" " + player.getName() + " tried to cast is not a registered spell, casting skipped!");
            return false;
        }

        return letPlayerCastSpell(spellName, ItemData.getSpellType(spellItem), spellItem, force);
    }

    /**
     * Creates a spell of named type and adds it to the players activeSpellSet.
     *
     * @param spellName The name of the spell
     * @param spellType The spellType it should be used under
     * @param spellItem The item used (doesn't HAVE to be a spell)
     * @param force To force the spell even if coolDowned
     * @return If the spell was cast or not
     */
    public boolean letPlayerCastSpell(@NotNull String spellName, @Nullable String spellType, @NotNull ItemStack spellItem, boolean force) {
        if (!force && PlayerSessionData.getPlayerSession(player).getCoolDowns().typeIsCooledDown(spellType))
            return false;

        activeSpells.add(nameToSpellBuilderMap.get(spellName.toUpperCase()).createSpell(player, spellType, spellItem));
        return true;
    }

    public Set<Spell> getActivePlayerSpells() {
        return activeSpells;
    }

    public void killPlayer(@Nullable Entity killer) {
        for (Spell spell : activeSpells) {
            if (spell instanceof Killable killable)
                killable.casterDeath(killer);
            spell.cancelSpell();
        }
        activeSpells.clear();
    }

    /**
     * calls casterLeave() on all the players Spells
     * <b>This is only intended to be used if the player leaves the server.</b>
     */
    public void playerLeave() {
        for (Spell spell : activeSpells) {
            spell.casterLeave();
        }
        activeSpells.clear();
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
