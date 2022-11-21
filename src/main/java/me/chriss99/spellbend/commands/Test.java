package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.playerdata.Currency;
import me.chriss99.spellbend.playerdata.DmgMods;
import me.chriss99.spellbend.spell.spells.Spell;
import me.chriss99.spellbend.spell.SpellHandler;
import me.chriss99.spellbend.util.Item;
/*import game.spellbend.util.math.MathUtil;
import game.spellbend.playerdata.CoolDowns;
import game.spellbend.playerdata.DmgMods;
import game.spellbend.util.PlayerDataBoard;*/
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.*;

public class Test {
    private final HashMap<String, AdvancedSubCommand> subCommands = new HashMap<>();

    public Test() {
        subCommands.put("item", new AdvancedSubCommand(new Class[0]) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
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

        /*subCommands.put("sidebar", new AdvancedSubCommand(new Class[0]) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§4Only players can use this subCommand!");
                    return true;
                }

                PlayerDataBoard.updateBoard((Player) sender);
                return true;
            }
        });*/

        subCommands.put("memory spell", new AdvancedSubCommand(new Class[]{Player.class}, new String[]{"player"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                sender.sendMessage("Spells:");
                ArrayList<Spell> playerSpells = SpellHandler.getActivePlayerSpells((Player) arguments.get(0));
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
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
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

        subCommands.put("value dmgMod get", new AdvancedSubCommand(new Class[]{Enums.DmgMod.class, String.class, Player.class}, new String[]{"dmgMod", "dmgModType", "player"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.DmgMod dmgMod = (Enums.DmgMod) arguments.get(0);
                String dmgModTypeString = ((String) arguments.get(1)).toUpperCase();
                Player player = (Player) arguments.get(2);

                Enums.DmgModType[] dmgModTypes = Enums.DmgModType.values();
                ArrayList<String> dmgModTypeStrings = new ArrayList<>(3);
                for (Enums.DmgModType modType : dmgModTypes)
                    dmgModTypeStrings.add(modType.toString().toUpperCase());

                if (!dmgModTypeStrings.contains(dmgModTypeString) && !dmgModTypeString.equals("ALL")) {
                    sender.sendMessage("§4" + dmgModTypeString + " is not a valid DmgModifier!");
                    return true;
                }

                DmgMods.setDmgMod(dmgMod);
                StringBuilder stringBuilder = new StringBuilder().append(DmgMods.getCurrentName());
                //noinspection SpellCheckingInspection
                stringBuilder.replace(stringBuilder.length()-1, stringBuilder.length(), "").append("ifier ")
                        .append(dmgModTypeString).append(" of ").append(player.getName()).append(": ")
                        .append(DmgMods.getDmgMod(player, (dmgModTypeString.equals("ALL")) ? null : Enums.DmgModType.valueOf(dmgModTypeString)));
                sender.sendMessage(stringBuilder.toString());
                return true;
            }
        });

        /*subCommands.put("value dmgMod set", new AdvancedSubCommand(new Class[]{String.class, Player.class, Float.class}, new String[]{"dmgMod", "player", "number"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                String dmgMod = (String) arguments.get(0);
                Player player = (Player) arguments.get(1);
                Float num = (Float) arguments.get(2);

                if (Lists.getDmgModTypeByName(dmgMod) == null) {
                    sender.sendMessage("§4" + arguments.get(1) + " is not a settable DmgModifier!");
                    return true;
                }

                DmgMods.setDmgMod(player, dmgMod, num);
                sender.sendMessage("Chriss go implement some more info here and check if it actually has been set (or if setting was rejected)");
                return true;
            }
        });*/

        subCommands.put("value cooldown get", new AdvancedSubCommand(new Class[]{String.class, Player.class}, new String[]{"spellType", "player"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                String spellType = ((String) arguments.get(0)).toUpperCase();
                Player player = (Player) arguments.get(1);

                if (spellType.equals("ALL")) {
                    sender.sendMessage("active CoolDowns of " + player.getName() + ":");
                    Set<Map.Entry<String, CoolDownEntry>> entrySet = CoolDowns.getCoolDowns(player).entrySet();
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
                CoolDownEntry coolDownEntry = CoolDowns.getCoolDownEntry(player, spellType);
                if (coolDownEntry == null) {
                    sender.sendMessage(arguments.get(0) + " is not cooled down for " + player.getName());
                    return true;
                }

                sender.sendMessage("CoolDown " + arguments.get(0) + " of " + player.getName() + ": "
                        + coolDownEntry.getRemainingCoolDownTimeInS() + ", " + coolDownEntry.getTimeInS() + ", " + coolDownEntry.getSpellType());
                return true;
            }
        });

        /*subCommands.put("value cooldown set", new AdvancedSubCommand(new Class[]{Enums.SpellType.class, Player.class, Integer.class, String.class},
                new String[]{"spellType", "player", "timeInTicks", "coolDownStage"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.SpellType spellType = (Enums.SpellType) arguments.get(0);
                Player player = (Player) arguments.get(1);
                Integer timeInTicks = (Integer) arguments.get(2);
                String coolDownStage = (String) arguments.get(3);

                CoolDowns.setCoolDown(player, spellType, timeInTicks, CDType);
                if (!CoolDowns.getCoolDownEntry(player, spellType).equals(new CoolDownEntry(timeInTicks, new Date(), CDType))) {
                    sender.sendMessage("§4Something went wrong when setting " + player.getDisplayName() + "'s cooldown!");
                    return true;
                }
                sender.sendMessage("Successfully set " + player.getDisplayName() + "'s cooldown.");
                return true;
            }
        });

        subCommands.put("value cooldown add", new AdvancedSubCommand(new Class[]{Enums.SpellType.class, Player.class, Integer.class, String.class},
                new String[]{"spellType", "player", "timeInTicks", "coolDownStage"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.SpellType spellType = (Enums.SpellType) arguments.get(0);
                Player player = (Player) arguments.get(1);
                Integer timeInSeconds = (Integer) arguments.get(2);
                String coolDownStage = (String) arguments.get(3);

                if (CoolDowns.getCoolDownEntry(player, spellType).timeInS != 0) {
                    sender.sendMessage("§eWarning: This coolDown is already set, assigning the larger one!");
                    CoolDownEntry oldValues = CoolDowns.getCoolDownEntry(player, spellType);
                    if (MathUtil.ASmallerB(
                            new long[]{Lists.getCoolDownTypeByName(oldValues.spellType).typeInt*(-1), (long) oldValues.timeInS*1000-(new Date().getTime()-oldValues.startDate.getTime())},
                            new long[]{Lists.getCoolDownTypeByName(CDType).typeInt *(-1), (long) timeInSeconds*1000}))
                        CoolDowns.setCoolDown(player, spellType, timeInSeconds, CDType);
                    if (CoolDowns.getCoolDownEntry(player, spellType).equals(new CoolDownEntry(timeInSeconds, new Date(), CDType))) {
                        sender.sendMessage("§4Something went wrong when setting " + player.getDisplayName() + "'s cooldown!");
                        return true;
                    }
                    sender.sendMessage("The coolDown already set was larger so the new coolDown wasn't assigned.");
                    return true;
                }

                CoolDowns.setCoolDown(player, spellType, timeInSeconds, CDType);
                if (CoolDowns.getCoolDownEntry(player, spellType).equals(new CoolDownEntry(timeInSeconds, new Date(), CDType))) {
                    sender.sendMessage("§4Something went wrong when setting " + player.getDisplayName() + "'s cooldown!");
                    return true;
                }
                sender.sendMessage("Successfully added the coolDown to " + player.getDisplayName() + ".");
                return true;
            }
        });*/

        subCommands.put("value currency get", new AdvancedSubCommand(new Class[]{Enums.Currency.class, Player.class}, new String[]{"currency", "player"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.Currency currency = (Enums.Currency) arguments.get(0);
                Player player = (Player) arguments.get(1);

                Currency.setCurrency(currency);
                sender.sendMessage(player.getName() + "'s " + currency + " count is: " + Currency.getCurrency(player));
                return true;
            }
        });

        subCommands.put("value currency add", new AdvancedSubCommand(new Class[]{Enums.Currency.class, Player.class, Float.class}, new String[]{"currency", "player", "value"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.Currency currency = (Enums.Currency) arguments.get(0);
                Player player = (Player) arguments.get(1);
                Float value = (Float) arguments.get(2);

                Currency.setCurrency(currency);
                Currency.addCurrency(player, value);
                return true;
            }
        });

        subCommands.put("value currency set", new AdvancedSubCommand(new Class[]{Enums.Currency.class, Player.class, Float.class}, new String[]{"currency", "player", "value"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.Currency currency = (Enums.Currency) arguments.get(0);
                Player player = (Player) arguments.get(1);
                Float value = (Float) arguments.get(2);

                Currency.setCurrency(currency);
                Currency.setCurrency(player, value);
                return true;
            }
        });

        new AdvancedCommandBase("test", "/test item or /test memory <spell|tasks [filter]> or /test value <dmgMod|cooldown> <valueName> <player>", subCommands){};
    }
}