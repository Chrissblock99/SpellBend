package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.harddata.SpellEnum;
import me.chriss99.spellbend.spells.*;
import me.chriss99.spellbend.util.ItemData;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class SpellHandler {
    private static final SpellBend plugin = SpellBend.getInstance();

    private final Player player;
    private final Set<Spell> activeSpells = new HashSet<>();
    private final Map<ItemStack, Runnable> clickableSpellRunnables = new HashMap<>();
    private BukkitTask stunReverseTask = null;

    public SpellHandler(@NotNull Player player) {
        this.player = player;
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

        getSpellInitializer(spellItem).cast();
    }

    public SpellInitializer getSpellInitializer(@NotNull ItemStack spellItem) {
        return new SpellInitializer(spellItem);
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

    public boolean isStunned() {
        return stunReverseTask != null;
    }


    public enum IgnoreConditionFlag {
        COOLDOWN,
        STUN,
        MANA,
        PLAYER_STATE
    }

    private class SpellInitializer {
        private final @NotNull ItemStack spellItem;
        private final @NotNull SpellEnum spellEnum;
        private @NotNull String spellType;
        private int manaCost;
        private @Nullable Function<@NotNull Player, @Nullable Component> playerStateValidator;

        private EnumSet<IgnoreConditionFlag> ignoreConditionFlags = EnumSet.noneOf(IgnoreConditionFlag.class);
        private boolean valid = true;

        public SpellInitializer(@NotNull ItemStack spellItem) {
            if (!ItemData.itemIsExecutableSpell(spellItem)) {
                Bukkit.getLogger().warning(spellItem + " is not an executable spell but was supposed to be cast by player " + player + "!");
                valid = false;
                //these are only here such that intelliJ doesn't complain
                this.spellItem = new ItemStack(Material.AIR);
                spellEnum = SpellEnum.MAGMA_BURST;
                spellType = "";
                return;
            }

            this.spellItem = spellItem;
            //noinspection DataFlowIssue cant be null as it only gets here if it is a spell
            spellEnum = SpellEnum.spellEnumOf(ItemData.getSpellName(spellItem).toUpperCase());

            String spellType = ItemData.getSpellType(spellItem);
            if (spellType == null)
                //noinspection DataFlowIssue
                spellType = spellEnum.getSpellType();
            this.spellType = spellType;

            Integer manaCost = ItemData.getPersistentDataValue(spellItem, PersistentDataKeys.MANA_COST_KEY, PersistentDataType.INTEGER);
            if (manaCost == null)
                //noinspection DataFlowIssue
                manaCost = spellEnum.getManaCost();
            this.manaCost = manaCost;

            //noinspection DataFlowIssue
            playerStateValidator = spellEnum.getPlayerStateValidator();
        }

        public SpellInitializer setSpellType(@NotNull String spellType) {
            this.spellType = spellType;
            return this;
        }

        public SpellInitializer setManaCost(int manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        public SpellInitializer setPlayerStateValidator(@Nullable Function<@NotNull Player, @Nullable Component> playerStateValidator) {
            this.playerStateValidator = playerStateValidator;
            return this;
        }

        public SpellInitializer setIgnoreConditionFlags(@NotNull EnumSet<IgnoreConditionFlag> ignoreConditionFlags) {
            this.ignoreConditionFlags = ignoreConditionFlags;
            return this;
        }

        /**
         * Attempts to cast the spell
         *
         * @return If the spell was cast
         */
        public boolean cast() {
            if (!valid)
                return false;
            if (!ignoreConditionFlags.contains(IgnoreConditionFlag.COOLDOWN) && PlayerSessionData.getPlayerSession(player).getCoolDowns().typeIsCooledDown(spellType) &&
                    !spellType.equals("NO_COOLDOWN"))
                return false;
            if (!ignoreConditionFlags.contains(IgnoreConditionFlag.STUN) && isStunned())
                return false;

            PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);

            CurrencyTracker mana = sessionData.getMana();
            if (!ignoreConditionFlags.contains(IgnoreConditionFlag.MANA) && mana.getCurrency() < manaCost) {
                sessionData.getActionBarController().displayMessage(SpellBend.getMiniMessage().deserialize("<red><bold>Not enough mana!</red>"));
                return false;
            }

            if (!ignoreConditionFlags.contains(IgnoreConditionFlag.PLAYER_STATE) && playerStateValidator != null) {
                Component errorMessage = playerStateValidator.apply(player);
                if (errorMessage != null) {
                    sessionData.getActionBarController().displayMessage(errorMessage);
                    return false;
                }
            }

            mana.addCurrency(-manaCost);
            //noinspection DataFlowIssue
            Spell spell = spellEnum.getSpellBuilder().apply(player, spellType, spellItem);
            activeSpells.add(spell);
            return true;
        }
    }
}
