package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.spells.*;
import me.chriss99.spellbend.util.ItemData;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpellHandler {
    private static final Map<String, SpellSubClassBuilder> nameToSpellBuilderMap = new HashMap<>();
    private static final Map<String, PlayerStateValidator> nameToPlayerStateValidatorMap = new HashMap<>();
    private static final Map<String, Integer> nameToManaCostMap = new HashMap<>();

    private static final SpellBend plugin = SpellBend.getInstance();

    private final Player player;
    private final Set<Spell> activeSpells = new HashSet<>();
    private final Map<ItemStack, Runnable> clickableSpellRunnables = new HashMap<>();
    private BukkitTask stunReverseTask = null;

    public SpellHandler(@NotNull Player player) {
        this.player = player;
    }

    /**
     * @throws IllegalArgumentException If the name is already contained in the map
     *
     * @param name The name of the Spell to add
     * @param builder The SpellSubClassBuilder object which will return a spell
     * @param manaCost The manaCost of the spell
     */
    public static void registerSpell(@NotNull String name, int manaCost, @NotNull SpellSubClassBuilder builder) {
        if (nameToSpellBuilderMap.containsKey(name))
            throw new IllegalArgumentException("Spell name is already contained in the builderMap!");
        if (nameToManaCostMap.containsKey(name))
            throw new IllegalArgumentException("Spell name is already contained in the manaCostMap!");

        name = name.toUpperCase();
        nameToSpellBuilderMap.put(name, builder);
        nameToManaCostMap.put(name, manaCost);
    }

    /**
     * @throws IllegalArgumentException If the name is already contained in the map
     *
     * @param name The name of the Spell to add
     * @param builder The SpellSubClassBuilder object which will return a spell
     * @param manaCost The manaCost of the spell
     * @param stateValidator The playerStateValidator
     */
    public static void registerSpell(@NotNull String name, int manaCost, @NotNull SpellSubClassBuilder builder,  @NotNull PlayerStateValidator stateValidator) {
        if (nameToSpellBuilderMap.containsKey(name))
            throw new IllegalArgumentException("Spell name is already contained in the builderMap!");
        if (nameToManaCostMap.containsKey(name))
            throw new IllegalArgumentException("Spell name is already contained in the manaCostMap!");
        if (nameToPlayerStateValidatorMap.containsKey(name))
            throw new IllegalArgumentException("Spell name is already contained in the stateValidatorMap!");

        name = name.toUpperCase();
        nameToSpellBuilderMap.put(name, builder);
        nameToManaCostMap.put(name, manaCost);
        nameToPlayerStateValidatorMap.put(name, stateValidator);
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

    /**
     * Either executes the spellName of the item or a mapped clickableSpellRunnable
     *
     * @param spellItem The spellItem clicked (HAS to be a spell)
     */
    public void playerClickedSpellItem(@NotNull ItemStack spellItem) {
        String spellName = ItemData.getSpellName(spellItem);
        if (spellName == null) {
            Bukkit.getLogger().warning(player.getName() + " supposedly clicked a spellItem, but the item has no spellName!");
            return;
        }

        playerClickedSpellItem(spellName, spellItem);
    }

    /**
     * Either executes the spellName of the item or a mapped clickableSpellRunnable
     *
     * @param spellName The spellName
     * @param spellItem The clicked item (DOESN'T HAVE to be a spell)
     */
    public void playerClickedSpellItem(@NotNull String spellName, @NotNull ItemStack spellItem) {
        String spellType = ItemData.getSpellType(spellItem);

        playerClickedSpellItem(spellName, spellType, spellItem);
    }

    /**
     * Either executes the spellName of the item or a mapped clickableSpellRunnable
     *
     * @param spellName The spellName
     * @param spellType The spellType (can be null)
     * @param spellItem The item clicked (DOESN'T HAVE to be a spell)
     */
    public void playerClickedSpellItem(@NotNull String spellName, @Nullable String spellType, @NotNull ItemStack spellItem) {
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
     * @param spellItem The item used (DOESN'T HAVE to be a spell)
     * @return If the spell was cast or not
     */
    public boolean letPlayerCastSpell(@NotNull String spellName, @NotNull ItemStack spellItem) {
        return letPlayerCastSpell(spellName, spellItem, false);
    }

    /**
     * Creates a spell of named type and adds it to the players activeSpellSet.
     *
     * @param spellName The name of the spell
     * @param spellType The spellType (can be null)
     * @param spellItem The item used (DOESN'T HAVE to be a spell)
     * @return If the spell was cast or not
     */
    public boolean letPlayerCastSpell(@NotNull String spellName, @Nullable String spellType, @NotNull ItemStack spellItem) {
        return letPlayerCastSpell(spellName, spellType, spellItem, false);
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

        //noinspection ConstantConditions because it can only get here if it is a spell, therefore the name is present and not null
        return letPlayerCastSpell(ItemData.getSpellName(spellItem), spellItem, force);
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
     * @param spellType The spellType it should be used under (can be null, spell will overwrite with its own)
     * @param spellItem The item used (DOESN'T HAVE to be a spell)
     * @param force To force the spell even if coolDowned
     * @return If the spell was cast or not
     */
    public boolean letPlayerCastSpell(@NotNull String spellName, @Nullable String spellType, @NotNull ItemStack spellItem, boolean force) {
        if (!force && PlayerSessionData.getPlayerSession(player).getCoolDowns().typeIsCooledDown(spellType))
            return false;
        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
        spellName = spellName.toUpperCase();

        int manaCost = nameToManaCostMap.get(spellName);
        CurrencyTracker mana = sessionData.getMana();
        if (mana.getCurrency()<manaCost) {
            sessionData.getActionBarController().displayMessage(SpellBend.getMiniMessage().deserialize("<red><bold>Not enough mana!</red>"));
            return false;
        }

        PlayerStateValidator stateValidator = nameToPlayerStateValidatorMap.get(spellName);
        if (stateValidator != null) {
            Component errorMessage = stateValidator.validateState(player);
            if (errorMessage != null) {
                sessionData.getActionBarController().displayMessage(errorMessage);
                return false;
            }
        }

        mana.addCurrency(-manaCost);
        Spell spell = nameToSpellBuilderMap.get(spellName).createSpell(player, spellType, spellItem);
        activeSpells.add(spell);
        return true;
    }

    public Set<Spell> getActivePlayerSpells() {
        return activeSpells;
    }

    /**
     * Stuns the player and broadcasts that to their spells
     *
     * @param timeInTicks The time to stun for
     */
    public void stunPlayer(int timeInTicks) {
        ValueTracker isMovementStunned = PlayerSessionData.getPlayerSession(player).getIsMovementStunned();

        if (stunReverseTask != null)
            stunReverseTask.cancel();
        else isMovementStunned.displaceValue(1);

        stunReverseTask = new BukkitRunnable(){
            @Override
            public void run() {
                stunReverseTask = null;
                isMovementStunned.displaceValue(-1);
            }
        }.runTaskLater(plugin, timeInTicks);

        for (Spell spell : activeSpells)
            if (spell instanceof Stunable stunable)
                stunable.casterStun(timeInTicks);
    }

    /**
     * Kills the player and broadcasts that to their spells
     *
     * @param killer The Nullable entity which killed them
     */
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
            if (data.has(PersistentDataKeys.spellNameKey, PersistentDataType.STRING))
                return data.get(PersistentDataKeys.spellNameKey, PersistentDataType.STRING) != null;
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
