package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.*;
import me.chriss99.spellbend.harddata.Action;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.harddata.Currency;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.manager.BlockManager;
import me.chriss99.spellbend.manager.BlockOverride;
import me.chriss99.spellbend.spells.Spell;
import me.chriss99.spellbend.util.ItemData;
import me.chriss99.spellbend.util.LivingEntityUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.*;

public class Test extends ReflectiveCommandBase {
    private static final MiniMessage miniMessage = SpellBend.getMiniMessage();

    public Test() {
        super("test", "test command for testing test stuff", new ArrayList<>());
    }

    @ReflectCommand(path = "stun")
    public void stun(Player toStun, int timeInTicks) {
        PlayerSessionData.getPlayerSession(toStun).stunEntity(timeInTicks);
    }

    @ReflectCommand(path = "kill")
    public void kill(Player toKill, Player killer) {
        PlayerSessionData.getPlayerSession(toKill).getHealth().onPlayerDeath(killer, null);
    }

    @ReflectCommand(path = "spellLeave")
    public void spellLeave(Player toLeave) {
        PlayerSessionData.getPlayerSession(toLeave).getSpellHandler().playerLeave();
    }

    @ReflectCommand(path = "endSpellActivity")
    public void endSpellActivity(Player toEnd) {
        PlayerSessionData.getPlayerSession(toEnd).getSpellHandler().endSpellActivity();
    }

    @ReflectCommand(path = "update sidebar")
    public void update_sidebar(Player commandSender) {
        PlayerSessionData.getPlayerSession(commandSender).getPlayerDataBoard().updateBoard();
    }

    @ReflectCommand(path = "memory spell")
    public void memory_spell(CommandSender commandSender, Player player) {
        commandSender.sendMessage(miniMessage.deserialize("Spells:"));
        Set<Spell> playerSpells = PlayerSessionData.getPlayerSession(player).getSpellHandler().getActivePlayerSpells();
        if (playerSpells.size() == 0) {
            commandSender.sendMessage(miniMessage.deserialize("none"));
            return;
        }
        for (Spell spell : playerSpells)
            commandSender.sendMessage(miniMessage.deserialize(spell.getClass().getName()));
    }

    @ReflectCommand(path = "memory spell headless")
    public void memory_spell_headless(CommandSender commandSender) {
        commandSender.sendMessage(miniMessage.deserialize("Spells:"));
        List<Spell> headlessSpellsView = SpellHandler.getHeadlessSpellsView();
        if (headlessSpellsView.size() == 0) {
            commandSender.sendMessage(miniMessage.deserialize("none"));
            return;
        }
        for (Spell spell : headlessSpellsView)
            commandSender.sendMessage(miniMessage.deserialize(spell.getClass().getName()));
    }

    @ReflectCommand(path = "memory spellHandler fallingBlock")
    public void memory_spellHandler_fallingBlock(CommandSender commandSender) {
        commandSender.sendMessage(miniMessage.deserialize("falling blocks listened for:"));
        Set<FallingBlock> view = SpellHandler.getFallingBlockHitGroundEventListenersView().keySet();
        if (view.size() == 0) {
            commandSender.sendMessage(miniMessage.deserialize("none"));
            return;
        }
        for (FallingBlock fallingBlock : view)
            commandSender.sendMessage(miniMessage.deserialize(fallingBlock.toString()));
    }

    @ReflectCommand(path = "memory spellHandler projectile")
    public void memory_spellHandler_projectile(CommandSender commandSender) {
        commandSender.sendMessage(miniMessage.deserialize("projectiles listened for:"));
        Set<Projectile> view = SpellHandler.getProjectileHitEventConsumersView().keySet();
        if (view.size() == 0) {
            commandSender.sendMessage(miniMessage.deserialize("none"));
            return;
        }
        for (Projectile projectile : view)
            commandSender.sendMessage(miniMessage.deserialize(projectile.toString()));
    }

    @ReflectCommand(path = "memory block")
    public void memory_block(CommandSender commandSender) {
        commandSender.sendMessage(miniMessage.deserialize("Block overrides:"));
        List<Map.Entry<Block, BlockOverride>> blockOverrides = BlockManager.getOverrideView();
        if (blockOverrides.size() == 0) {
            commandSender.sendMessage(miniMessage.deserialize("none"));
            return;
        }

        for (Map.Entry<Block, BlockOverride> overrideEntry : blockOverrides) {
            Location location = overrideEntry.getKey().getLocation();
            commandSender.sendMessage(miniMessage.deserialize("Location: " + location.getX() + " " + location.getY() + " " + location.getZ() +
                    "\nMaterial: " + overrideEntry.getKey().getType() +
                    "\nOriginal: " + overrideEntry.getValue().getOriginal().getMaterial() +
                    "\n  Overrides: " + Arrays.toString(overrideEntry.getValue().getOverridesView().stream().map(BlockData::getMaterial).toArray())));
        }
    }

    @ReflectCommand(path = "memory block remove")
    public void memory_block_remove(Player commandSender) {
        Block targetBlock = commandSender.getTargetBlock(4);
        if (targetBlock == null) {
            commandSender.sendMessage(miniMessage.deserialize("<red>Your not facing a block!"));
            return;
        }

        BlockManager.clearOverride(targetBlock.getLocation());
    }

    @ReflectCommand(path = "memory block removeExact")
    public void memory_block_removeExact(Player commandSender) {
        BlockManager.clearOverride(commandSender.getLocation());
    }

    @ReflectCommand(path = "memory block clear")
    public void memory_block_clear() {
        BlockManager.clearOverrides();
    }

    @ReflectCommand(path = "memory tasks")
    public void memory_tasks(CommandSender commandSender, String filter) {
        boolean filtering = !filter.equalsIgnoreCase("all");
        int printed = 0;

        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("Workers:"));
        List<BukkitWorker> workers = Bukkit.getScheduler().getActiveWorkers();
        for (BukkitWorker worker : workers) {
            if (filtering && !worker.getOwner().getName().equals(filter))
                continue;
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize((worker.getOwner().getName() + " " + worker.getTaskId() + ": " + worker)));
            printed++;
        }
        if (printed == 0)
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("none"));

        printed = 0;

        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("Tasks:"));
        List<BukkitTask> tasks = Bukkit.getScheduler().getPendingTasks();
        for (BukkitTask task : tasks) {
            if (filtering && !task.getOwner().getName().equals(filter))
                continue;
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize((task.getOwner().getName() + " " + task.getTaskId() + ": " + task)));
            printed++;
        }
        if (printed == 0)
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
    }

    @ReflectCommand(path = "memory sessions")
    public void memory_sessions(CommandSender commandSender) {
        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("Sessions:"));
        int printed = 0;
        for (Map.Entry<Player, PlayerSessionData> entry : PlayerSessionData.getPlayerSessions().entrySet()) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(entry.getKey().getName()));
            printed++;
        }
        if (printed == 0)
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
    }

    @ReflectCommand(path = "value item get spellName")
    public void value_item_get_spellName(CommandSender commandSender, Player player) {
        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(player.getName() + "'s held item's spellName is " + ItemData.getHeldSpellName(player) + "."));
    }

    @ReflectCommand(path = "value item get spellType")
    public void value_item_get_spellType(CommandSender commandSender, Player player) {
        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(player.getName() + "'s held item's spellType is " + ItemData.getHeldSpellType(player) + "."));
    }

    @ReflectCommand(path = "value item get manaCost")
    public void value_item_get_manaCost(CommandSender commandSender, Player player) {
        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(player.getName() + "'s held item's manaCost is " +
                ItemData.getPersistentDataValue(ItemData.getHeldItem(player), PersistentDataKeys.MANA_COST_KEY, PersistentDataType.INTEGER) + "."));
    }

    @ReflectCommand(path = "value item set spellName")
    public void value_item_set_spellName(Player player, String spellName) {
        ItemData.setSpellName(ItemData.getHeldItem(player), spellName);
    }

    @ReflectCommand(path = "value item set spellType")
    public void value_item_set_spellType(Player player, String spellType) {
        ItemData.setSpellType(ItemData.getHeldItem(player), spellType);
    }

    @ReflectCommand(path = "value item set manaCost")
    public void value_item_set_manaCost(Player player, int manaCost) {
        ItemData.setPersistentDataValue(ItemData.getHeldItem(player), PersistentDataKeys.MANA_COST_KEY, PersistentDataType.INTEGER, manaCost);
    }

    @ReflectCommand(path = "value modifier get")
    public void value_modifier_get(CommandSender commandSender, Modifier modifier, Player player) {
        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
        PercentageModifier percentageModifier = null;
        switch (modifier) {
            case DAMAGE_TAKEN ->
                    percentageModifier = sessionData.getDamageTakenModifiers();
            case DAMAGE_DEALT ->
                    percentageModifier = sessionData.getDamageDealtModifiers();
            case WALK_SPEED ->
                    percentageModifier = sessionData.getWalkSpeedModifiers();
        }

        if (percentageModifier == null) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(modifier + " is not a valid modifier!"));
            return;
        }

        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(modifier + " of " + player.getName() + " is " + percentageModifier.getModifier()));
    }

    @ReflectCommand(path = "value modifier")
    public void value_modifier(CommandSender commandSender, Action action, Modifier modifier, Player player, double number) {
        PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
        PercentageModifier percentageModifier = null;
        switch (modifier) {
            case DAMAGE_TAKEN ->
                    percentageModifier = sessionData.getDamageTakenModifiers();
            case DAMAGE_DEALT ->
                    percentageModifier = sessionData.getDamageDealtModifiers();
            case WALK_SPEED ->
                    percentageModifier = sessionData.getWalkSpeedModifiers();
        }

        if (percentageModifier == null) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(modifier + " is not a valid modifier!"));
            return;
        }

        switch (action) {
            case ADD -> percentageModifier.addModifier(number);
            case REMOVE -> percentageModifier.removeModifier(number);
            case GET -> commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(modifier + " of " + player.getName() + " is " + percentageModifier.getModifier()));
            default -> commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("<red>Action: \"" + action + "\" is not supported by this subCommand!"));
        }
    }

    @ReflectCommand(path = "value cooldown get")
    public void value_cooldown_get(CommandSender commandSender, String spellType, Player player) {
        CoolDowns coolDowns = PlayerSessionData.getPlayerSession(player).getCoolDowns();

        if (spellType.equals("ALL")) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("active CoolDowns of " + player.getName() + ":"));
            List<Map.Entry<String, CoolDownEntry>> entries = coolDowns.getCoolDownsView();
            if (entries.size() == 0) {
                commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
                return;
            }
            for (Map.Entry<String, CoolDownEntry> entry : entries) {
                CoolDownEntry coolDownEntry = entry.getValue();
                commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(entry.getKey() + ": " + coolDownEntry.getRemainingCoolDownTimeInS() + ", " +
                        coolDownEntry.getTimeInS() + ", " + coolDownEntry.getSpellType()));
            }
            return;
        }

        CoolDownEntry coolDownEntry = coolDowns.getCoolDownEntry(spellType);
        if (coolDownEntry == null) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(spellType + " is not cooled down for " + player.getName()));
            return;
        }

        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("CoolDown " + spellType + " of " + player.getName() + ": "
                + coolDownEntry.getRemainingCoolDownTimeInS() + ", " + coolDownEntry.getTimeInS() + ", " + coolDownEntry.getSpellType()));
    }

    @ReflectCommand(path = "value cooldown set")
    public void value_cooldown_set(String spellType, Player player, float windupTime, float activeTime, float passiveTime, float coolDownTime, CoolDownStage coolDownStage) {
        float[] timeInS = new float[]{windupTime, activeTime, passiveTime, coolDownTime};
        PlayerSessionData.getPlayerSession(player).getCoolDowns().setCoolDown(spellType, timeInS, coolDownStage);
    }

    @ReflectCommand(path = "value currency get")
    public void value_currency_get(CommandSender commandSender, Currency currency, Player player) {
        CurrencyTracker currencyTracker = null;
        switch (currency) {
            case GEMS -> currencyTracker = PlayerSessionData.getPlayerSession(player).getGems();
            case GOLD -> currencyTracker = PlayerSessionData.getPlayerSession(player).getGold();
            case CRYSTALS -> currencyTracker = PlayerSessionData.getPlayerSession(player).getCrystals();
        }

        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(player.getName() + "'s " + currency + " count is: " + currencyTracker.getCurrency()));
    }

    @ReflectCommand(path = "value currency")
    public void value_currency(CommandSender commandSender, Action action, Currency currency, Player player, float number) {
        CurrencyTracker currencyTracker = null;
        switch (currency) {
            case GEMS -> currencyTracker = PlayerSessionData.getPlayerSession(player).getGems();
            case GOLD -> currencyTracker = PlayerSessionData.getPlayerSession(player).getGold();
            case CRYSTALS -> currencyTracker = PlayerSessionData.getPlayerSession(player).getCrystals();
        }

        switch (action) {
            case ADD -> currencyTracker.addCurrency(number);
            case REMOVE -> currencyTracker.addCurrency(-number);
            case SET -> currencyTracker.setCurrency(number);
            case GET -> commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(player.getName() + "'s " + currency + " count is: " + currencyTracker.getCurrency()));
            default -> commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("&cAction: \"" + action + "\" is not supported by this subCommand!"));
        }
    }

    @ReflectCommand(path = "value entity set isSpellAffectAble")
    public void value_entity_set_isSpellAffectAble(Player commandSender, boolean affectAble) {
        Entity targetEntity = commandSender.getTargetEntity(4);
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("<red>Targeted entity is not a livingEntity!"));
            return;
        }

        LivingEntityUtil.setLivingEntitySpellAffectAble(livingEntity, affectAble);
    }

    @ReflectCommand(path = "value entity get isSpellAffectAble")
    public void value_entity_get_isSpellAffectAble(Player commandSender) {
        Entity targetEntity = commandSender.getTargetEntity(4);
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("<red>Targeted entity is not a livingEntity!"));
            return;
        }

        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(String.valueOf(LivingEntityUtil.entityIsSpellAffectAble(livingEntity))));
    }

    public enum Modifier {
        DAMAGE_TAKEN,
        DAMAGE_DEALT,
        WALK_SPEED
    }
}