package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.data.*;
import me.chriss99.spellbend.harddata.CoolDownStage;
import me.chriss99.spellbend.harddata.Currency;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.spells.Spell;
import me.chriss99.spellbend.util.Item;
import me.chriss99.spellbend.util.ItemData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class Test {
    private final Map<String, AdvancedSubCommand> subCommands = new HashMap<>();

    public Test() {
        subCommands.put("item", new AdvancedSubCommand(new Class[0]) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§4Only players can use this subCommand!");
                    return true;
                }

                Inventory inv = ((Player) sender).getInventory();
                inv.addItem(Item.create(Material.CAMPFIRE, Component.text("§c§lFiery Rage"), 1, new NamespacedKey[]{PersistentDataKeys.spellNameKey, PersistentDataKeys.spellTypeKey}, new String[]{"fiery_rage", "AURA"}));
                inv.addItem(Item.create(Material.GOLDEN_HORSE_ARMOR, Component.text("§c§lEmber Blast"), 1, new NamespacedKey[]{PersistentDataKeys.spellNameKey, PersistentDataKeys.spellTypeKey}, new String[]{"ember_blast", "BLAST"}));
                inv.addItem(Item.create(Material.IRON_HORSE_ARMOR, Component.text("§c§lTest Spell"), 1, new NamespacedKey[]{PersistentDataKeys.spellNameKey, PersistentDataKeys.spellTypeKey}, new String[]{"test_spell", "TEST"}));
                return true;
            }
        });

        subCommands.put("update sidebar", new AdvancedSubCommand(new Class[0]) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§4Only players can use this subCommand!");
                    return true;
                }

                PlayerSessionData.getPlayerSession((Player) sender).getPlayerDataBoard().updateBoard();
                return true;
            }
        });

        subCommands.put("memory spell", new AdvancedSubCommand(new Class[]{Player.class}, new String[]{"player"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                sender.sendMessage("Spells:");
                Set<Spell> playerSpells = PlayerSessionData.getPlayerSession((Player) arguments.get(0)).getSpellHandler().getActivePlayerSpells();
                if (playerSpells.size() == 0) {
                    sender.sendMessage("none");
                    return true;
                }
                for (Spell spell : playerSpells) {
                    sender.sendMessage(spell.getClass().getName());
                }
                return true;
            }
        });

        subCommands.put("memory tasks", new AdvancedSubCommand(new Class[]{String.class}, new String[]{"filter"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                boolean filtering = !arguments.get(0).equals("all");
                String filter = null;
                if (filtering) filter = (String) arguments.get(0);
                int printed = 0;

                sender.sendMessage("Workers:");
                List<BukkitWorker> workers = Bukkit.getScheduler().getActiveWorkers();
                for (BukkitWorker worker : workers) {
                    if (filtering) if (!worker.getOwner().getName().equals(filter)) continue;
                    sender.sendMessage((worker.getOwner().getName() + " " + worker.getTaskId() + ": " + worker));
                    printed++;
                }
                if (printed == 0) {
                    sender.sendMessage("none");
                }
                printed = 0;

                sender.sendMessage("Tasks:");
                List<BukkitTask> tasks = Bukkit.getScheduler().getPendingTasks();
                for (BukkitTask task : tasks) {
                    if (filtering) if (!task.getOwner().getName().equals(filter)) continue;
                    sender.sendMessage((task.getOwner().getName() + " " + task.getTaskId() + ": " + task));
                    printed ++;
                }
                if (printed == 0) {
                    sender.sendMessage("none");
                }
                return true;
            }
        });

        subCommands.put("memory sessions", new AdvancedSubCommand(new Class[0], new String[0]) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                sender.sendMessage("Sessions:");
                int printed = 0;
                for (Map.Entry<Player, PlayerSessionData> entry : PlayerSessionData.getPlayerSessions().entrySet()) {
                    sender.sendMessage(entry.getKey().getName());
                    printed++;
                }
                if (printed == 0) {
                    sender.sendMessage("none");
                }
                return true;
            }
        });

        subCommands.put("value item get spellName", new AdvancedSubCommand(new Class[]{Player.class}, new String[]{"player"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                Player player = (Player) arguments.get(0);
                sender.sendMessage(player.getName() + "'s held item's spellName is " + ItemData.getHeldSpellName(player) + ".");
                return true;
            }
        });

        subCommands.put("value item get spellType", new AdvancedSubCommand(new Class[]{Player.class}, new String[]{"player"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                Player player = (Player) arguments.get(0);
                sender.sendMessage(player.getName() + "'s held item's spellType is " + ItemData.getHeldSpellType(player) + ".");
                return true;
            }
        });

        subCommands.put("value item set spellName", new AdvancedSubCommand(new Class[]{Player.class, String.class}, new String[]{"player", "spellName"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                ItemData.setSpellName(ItemData.getHeldItem((Player) arguments.get(0)), (String) arguments.get(1));
                return true;
            }
        });

        subCommands.put("value item set spellType", new AdvancedSubCommand(new Class[]{Player.class, String.class}, new String[]{"player", "spellType"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                ItemData.setSpellType(ItemData.getHeldItem((Player) arguments.get(0)), (String) arguments.get(1));
                return true;
            }
        });

        subCommands.put("value modifier get", new AdvancedSubCommand(new Class[]{String.class, Player.class}, new String[]{"modifier", "player"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                String modifier = ((String) arguments.get(0)).toUpperCase();
                Player player = (Player) arguments.get(1);

                PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
                PercentageModifier percentageModifier = null;
                switch (modifier) {
                    case "DAMAGE_TAKEN" ->
                            percentageModifier = sessionData.getDamageTakenModifiers();
                    case "DAMAGE_DEALT" ->
                            percentageModifier = sessionData.getDamageDealtModifiers();
                    case "WALK_SPEED" ->
                            percentageModifier = sessionData.getWalkSpeedModifiers();
                }

                if (percentageModifier == null) {
                    sender.sendMessage(modifier + " is not a valid modifier!");
                    return true;
                }

                sender.sendMessage(modifier + " of " + player.getName() + " is " + percentageModifier.getModifier());
                return true;
            }
        });

        subCommands.put("value modifier add", new AdvancedSubCommand(new Class[]{String.class, Player.class, Float.class}, new String[]{"modifier", "player", "number"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                String modifier = ((String) arguments.get(0)).toUpperCase();
                Player player = (Player) arguments.get(1);
                Float num = (Float) arguments.get(2);

                PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
                PercentageModifier percentageModifier = null;
                switch (modifier) {
                    case "DAMAGE_TAKEN" ->
                        percentageModifier = sessionData.getDamageTakenModifiers();
                    case "DAMAGE_DEALT" ->
                        percentageModifier = sessionData.getDamageDealtModifiers();
                    case "WALK_SPEED" ->
                        percentageModifier = sessionData.getWalkSpeedModifiers();
                }

                if (percentageModifier == null) {
                    sender.sendMessage(modifier + " is not a valid modifier!");
                    return true;
                }

                percentageModifier.addModifier(num);
                return true;
            }
        });

        subCommands.put("value modifier remove", new AdvancedSubCommand(new Class[]{String.class, Player.class, Float.class}, new String[]{"modifier", "player", "number"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                String modifier = ((String) arguments.get(0)).toUpperCase();
                Player player = (Player) arguments.get(1);
                Float num = (Float) arguments.get(2);

                PlayerSessionData sessionData = PlayerSessionData.getPlayerSession(player);
                PercentageModifier percentageModifier = null;
                switch (modifier) {
                    case "DAMAGE_TAKEN" ->
                            percentageModifier = sessionData.getDamageTakenModifiers();
                    case "DAMAGE_DEALT" ->
                            percentageModifier = sessionData.getDamageDealtModifiers();
                    case "WALK_SPEED" ->
                            percentageModifier = sessionData.getWalkSpeedModifiers();
                }

                if (percentageModifier == null) {
                    sender.sendMessage(modifier + " is not a valid modifier!");
                    return true;
                }

                percentageModifier.removeModifier(num);
                return true;
            }
        });

        subCommands.put("value cooldown get", new AdvancedSubCommand(new Class[]{String.class, Player.class}, new String[]{"spellType", "player"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                String spellType = ((String) arguments.get(0)).toUpperCase();
                Player player = (Player) arguments.get(1);

                CoolDowns coolDowns = PlayerSessionData.getPlayerSession(player).getCoolDowns();

                if (spellType.equals("ALL")) {
                    sender.sendMessage("active CoolDowns of " + player.getName() + ":");
                    Set<Map.Entry<String, CoolDownEntry>> entrySet = coolDowns.getCoolDowns().entrySet();
                    if (entrySet.size() == 0) {
                        sender.sendMessage("none");
                        return true;
                    }
                    for (Map.Entry<String, CoolDownEntry> entry : entrySet) {
                        CoolDownEntry coolDownEntry = entry.getValue();
                        sender.sendMessage(entry.getKey() + ": " + coolDownEntry.getRemainingCoolDownTimeInS() + ", " + coolDownEntry.getTimeInS() + ", " + coolDownEntry.getSpellType());
                    }
                    return true;
                }
                CoolDownEntry coolDownEntry = coolDowns.getCoolDownEntry(spellType);
                if (coolDownEntry == null) {
                    sender.sendMessage(arguments.get(0) + " is not cooled down for " + player.getName());
                    return true;
                }

                sender.sendMessage("CoolDown " + arguments.get(0) + " of " + player.getName() + ": "
                        + coolDownEntry.getRemainingCoolDownTimeInS() + ", " + coolDownEntry.getTimeInS() + ", " + coolDownEntry.getSpellType());
                return true;
            }
        });

        subCommands.put("value cooldown set", new AdvancedSubCommand(new Class[]{String.class, Player.class, Float.class, Float.class, Float.class, Float.class, CoolDownStage.class},
                new String[]{"spellType", "player", "windupTime", "activeTime", "passiveTime", "coolDownTime", "coolDownStage"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                String spellType = (String) arguments.get(0);
                Player player = (Player) arguments.get(1);
                float[] timeInS = new float[]{(Float) arguments.get(2), (Float) arguments.get(3), (Float) arguments.get(4), (Float) arguments.get(5)};
                CoolDownStage coolDownStage = (CoolDownStage) arguments.get(6);

                PlayerSessionData.getPlayerSession(player).getCoolDowns().setCoolDown(spellType, timeInS, coolDownStage);
                return true;
            }
        });

        subCommands.put("value currency get", new AdvancedSubCommand(new Class[]{Currency.class, Player.class}, new String[]{"currency", "player"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                Currency currencyEnum = (Currency) arguments.get(0);
                Player player = (Player) arguments.get(1);

                CurrencyTracker currency;
                switch (currencyEnum) {
                    case GEMS -> currency = PlayerSessionData.getPlayerSession(player).getGems();
                    case GOLD -> currency = PlayerSessionData.getPlayerSession(player).getGold();
                    case CRYSTALS -> currency = PlayerSessionData.getPlayerSession(player).getCrystals();
                    default -> currency = null; //this stupid line that never executes and if executed will only cause problems only exists SO THE JAVA COMPILER WON'T SCREAM AROUND THAT currency MIGHT NOT HAVE BEEN INITIALIZED
                }
                sender.sendMessage(player.getName() + "'s " + currencyEnum + " count is: " + currency.getCurrency());
                return true;
            }
        });

        subCommands.put("value currency add", new AdvancedSubCommand(new Class[]{Currency.class, Player.class, Float.class}, new String[]{"currency", "player", "value"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                Currency currencyEnum = (Currency) arguments.get(0);
                Player player = (Player) arguments.get(1);
                Float value = (Float) arguments.get(2);

                CurrencyTracker currency;
                switch (currencyEnum) {
                    case GEMS -> currency = PlayerSessionData.getPlayerSession(player).getGems();
                    case GOLD -> currency = PlayerSessionData.getPlayerSession(player).getGold();
                    case CRYSTALS -> currency = PlayerSessionData.getPlayerSession(player).getCrystals();
                    default -> currency = null; //this stupid line that never executes and if executed will only cause problems only exists SO THE JAVA COMPILER WON'T SCREAM AROUND THAT currency MIGHT NOT HAVE BEEN INITIALIZED
                }
                currency.addCurrency(value);
                return true;
            }
        });

        subCommands.put("value currency set", new AdvancedSubCommand(new Class[]{Currency.class, Player.class, Float.class}, new String[]{"currency", "player", "value"}) {
            @Override
            public boolean onCommand(CommandSender sender, List<Object> arguments) {
                Currency currencyEnum = (Currency) arguments.get(0);
                Player player = (Player) arguments.get(1);
                Float value = (Float) arguments.get(2);

                CurrencyTracker currency;
                switch (currencyEnum) {
                    case GEMS -> currency = PlayerSessionData.getPlayerSession(player).getGems();
                    case GOLD -> currency = PlayerSessionData.getPlayerSession(player).getGold();
                    case CRYSTALS -> currency = PlayerSessionData.getPlayerSession(player).getCrystals();
                    default -> currency = null; //this stupid line that never executes and if executed will only cause problems only exists SO THE JAVA COMPILER WON'T SCREAM AROUND THAT currency MIGHT NOT HAVE BEEN INITIALIZED
                }
                currency.setCurrency(value);
                return true;
            }
        });

        new AdvancedCommandBase("test", "/test item or /test memory <spell|tasks [filter]> or /test value <dmgMod|cooldown> <valueName> <player>", subCommands){};
    }
}