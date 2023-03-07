package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.*;
import me.chriss99.spellbend.harddata.Action;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.harddata.Currency;
import me.chriss99.spellbend.spells.Spell;
import me.chriss99.spellbend.util.ItemData;
import me.chriss99.spellbend.util.LivingEntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class Test extends ReflectiveCommandBase {
    public Test() {
        super("test", "test command for testing test stuff", new ArrayList<>());
    }

    @ReflectCommand(path = "update sidebar")
    public void update_sidebar(Player commandSender) {
        PlayerSessionData.getPlayerSession(commandSender).getPlayerDataBoard().updateBoard();
    }

    @ReflectCommand(path = "memory spell")
    public void memory_spell(CommandSender commandSender, Player player) {
        commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("Spells:"));
        Set<Spell> playerSpells = PlayerSessionData.getPlayerSession(player).getSpellHandler().getActivePlayerSpells();
        if (playerSpells.size() == 0) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
            return;
        }
        for (Spell spell : playerSpells)
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(spell.getClass().getName()));
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

    @ReflectCommand(path = "value item set spellName")
    public void value_item_set_spellName(Player player, String spellName) {
        ItemData.setSpellName(ItemData.getHeldItem(player), spellName);
    }

    @ReflectCommand(path = "value item set spellType")
    public void value_item_set_spellType(Player player, String spellType) {
        ItemData.setSpellType(ItemData.getHeldItem(player), spellType);
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
    public void value_modifier(CommandSender commandSender, Action action, Modifier modifier, Player player, float number) {
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
            Set<Map.Entry<String, CoolDownEntry>> entrySet = coolDowns.getCoolDowns().entrySet();
            if (entrySet.size() == 0) {
                commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("none"));
                return;
            }
            for (Map.Entry<String, CoolDownEntry> entry : entrySet) {
                CoolDownEntry coolDownEntry = entry.getValue();
                commandSender.sendMessage(SpellBend.getMiniMessage().deserialize(entry.getKey() + ": " + coolDownEntry.getRemainingCoolDownTimeInS() + ", " + coolDownEntry.getTimeInS() + ", " + coolDownEntry.getSpellType()));
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
        Entity targetEntity = commandSender.getTargetEntity(2);
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            commandSender.sendMessage(SpellBend.getMiniMessage().deserialize("<red>Targeted entity is not a livingEntity!"));
            return;
        }

        LivingEntityUtil.setLivingEntitySpellAffectAble(livingEntity, affectAble);
    }

    @ReflectCommand(path = "value entity get isSpellAffectAble")
    public void value_entity_get_isSpellAffectAble(Player commandSender) {
        Entity targetEntity = commandSender.getTargetEntity(2);
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