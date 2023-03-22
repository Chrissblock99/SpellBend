package me.chriss99.spellbend.data;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.harddata.SpellEnum;
import me.chriss99.spellbend.spells.*;
import me.chriss99.spellbend.util.ItemData;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class SpellHandler {
    private final Player player;
    private final Set<Spell> activeSpells = new HashSet<>();
    private final Map<ItemStack, Runnable> clickableSpellRunnables = new HashMap<>();

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

        SpellInitializer initializer = getSpellInitializer(spellItem).setSpellName(spellName);
        if (spellType != null)
            initializer.setSpellType(spellType);
        initializer.cast();
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
        for (Spell spell : new HashSet<>(activeSpells))
            spell.casterStun(timeInTicks);
    }

    /**
     * Kills the player and broadcasts that to their spells
     *
     * @param killer The Nullable entity which killed them
     */
    public void killPlayer(@Nullable LivingEntity killer) {
        for (Spell spell : new HashSet<>(activeSpells))
            spell.casterDeath(killer);
    }

    /**
     * calls casterLeave() on all the players Spells
     * <b>This is only intended to be used if the player leaves the server.</b>
     */
    public void playerLeave() {
        for (Spell spell : activeSpells)
            spell.casterLeave();
        activeSpells.clear();
    }


    public enum IgnoreConditionFlag {
        COOLDOWN,
        STUN,
        MANA,
        PLAYER_STATE
    }

    private class SpellInitializer {
        private final @NotNull ItemStack spellItem;
        private SpellEnum spellEnum = null;
        private boolean spellEnumHasBeenSet = false;
        private String spellType = null;
        private boolean spellTypeHasBeenSet = false;
        private int manaCost = 0;
        private boolean manaCostHasBeenSet = false;
        private @Nullable Function<@NotNull Player, @Nullable Component> playerStateValidator = null;
        private boolean validatorHasBeenSet = false;

        private EnumSet<IgnoreConditionFlag> ignoreConditionFlags = EnumSet.noneOf(IgnoreConditionFlag.class);
        private boolean valid = true;

        public SpellInitializer(@NotNull ItemStack spellItem) {
            this.spellItem = spellItem;
        }

        public SpellInitializer setSpellName(@NotNull String spellName) {
            spellEnum = SpellEnum.spellEnumOf(spellName.toUpperCase());
            if (spellEnum == null)
                valid = false;
            spellEnumHasBeenSet = true;
            return this;
        }

        public SpellInitializer setSpellType(@NotNull String spellType) {
            this.spellType = spellType;
            spellTypeHasBeenSet = true;
            return this;
        }

        public SpellInitializer setManaCost(int manaCost) {
            this.manaCost = manaCost;
            manaCostHasBeenSet = true;
            return this;
        }

        public SpellInitializer setPlayerStateValidator(@Nullable Function<@NotNull Player, @Nullable Component> playerStateValidator) {
            this.playerStateValidator = playerStateValidator;
            validatorHasBeenSet = true;
            return this;
        }

        public SpellInitializer setIgnoreConditionFlags(@NotNull EnumSet<IgnoreConditionFlag> ignoreConditionFlags) {
            this.ignoreConditionFlags = ignoreConditionFlags;
            return this;
        }

        private void addFallBackValues() {
            if (!spellEnumHasBeenSet) {
                String spellName = ItemData.getSpellName(spellItem);
                if (spellName == null) {
                    valid = false;
                    return;
                }

                spellEnum = SpellEnum.spellEnumOf(spellName.toUpperCase());
                if (spellEnum == null) {
                    valid = false;
                    return;
                }
            }

            if (!spellTypeHasBeenSet) {
                String spellType = ItemData.getSpellType(spellItem);
                if (spellType == null)
                    spellType = spellEnum.getSpellType();
                this.spellType = spellType;
            }

            if (!manaCostHasBeenSet) {
                Integer manaCost = ItemData.getPersistentDataValue(spellItem, PersistentDataKeys.MANA_COST_KEY, PersistentDataType.INTEGER);
                if (manaCost == null)
                    manaCost = spellEnum.getManaCost();
                this.manaCost = manaCost;
            }

            if (!validatorHasBeenSet)
                playerStateValidator = spellEnum.getPlayerStateValidator();
        }

        /**
         * Attempts to cast the spell
         *
         * @return If the spell was cast
         */
        public boolean cast() {
            addFallBackValues();
            if (!valid) {
                Bukkit.getLogger().warning(player + " was supposed to cast with an invalid Initializer!\n" + this);
                return false;
            }

            PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
            if (!ignoreConditionFlags.contains(IgnoreConditionFlag.COOLDOWN) && sessionData.getCoolDowns().typeIsCooledDown(spellType) &&
                    !spellType.equals("NO_COOLDOWN"))
                return false;
            if (!ignoreConditionFlags.contains(IgnoreConditionFlag.STUN) && sessionData.isStunned())
                return false;

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
            Spell spell = spellEnum.getSpellBuilder().apply(player, spellType, spellItem);
            if (!spell.spellEnded())
                activeSpells.add(spell);
            return true;
        }

        @Override
        public String toString() {
            return "SpellInitializer{" +
                    "spellItem=" + spellItem +
                    ", spellEnum=" + spellEnum +
                    ", spellEnumHasBeenSet=" + spellEnumHasBeenSet +
                    ", spellType='" + spellType + '\'' +
                    ", spellTypeHasBeenSet=" + spellTypeHasBeenSet +
                    ", manaCost=" + manaCost +
                    ", manaCostHasBeenSet=" + manaCostHasBeenSet +
                    ", playerStateValidator=" + playerStateValidator +
                    ", validatorHasBeenSet=" + validatorHasBeenSet +
                    ", ignoreConditionFlags=" + ignoreConditionFlags +
                    ", valid=" + valid +
                    '}';
        }
    }
}
