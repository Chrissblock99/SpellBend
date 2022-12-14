package me.chriss99.spellbend.commands;

import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.playerdata.CoolDowns;
import me.chriss99.spellbend.playerdata.Currency;
import me.chriss99.spellbend.playerdata.DmgMods;
import me.chriss99.spellbend.playerdata.PlayerDataBoard;
import me.chriss99.spellbend.spell.spells.Spell;
import me.chriss99.spellbend.spell.SpellHandler;
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

        subCommands.put("update sidebar", new AdvancedSubCommand(new Class[0]) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§4Only players can use this subCommand!");
                    return true;
                }

                PlayerDataBoard.updateBoard((Player) sender);
                return true;
            }
        });

        subCommands.put("memory spell", new AdvancedSubCommand(new Class[]{Player.class}, new String[]{"player"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                sender.sendMessage("Spells:");
                Set<Spell> playerSpells = SpellHandler.getActivePlayerSpells((Player) arguments.get(0));
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

        subCommands.put("value item get spellName", new AdvancedSubCommand(new Class[]{Player.class}, new String[]{"player"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Player player = (Player) arguments.get(0);
                sender.sendMessage(player.getName() + "'s held item's spellName is " + ItemData.getHeldSpellName(player) + ".");
                return true;
            }
        });

        subCommands.put("value item get spellType", new AdvancedSubCommand(new Class[]{Player.class}, new String[]{"player"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Player player = (Player) arguments.get(0);
                sender.sendMessage(player.getName() + "'s held item's spellType is " + ItemData.getHeldSpellType(player) + ".");
                return true;
            }
        });

        subCommands.put("value item set spellName", new AdvancedSubCommand(new Class[]{Player.class, String.class}, new String[]{"player", "spellName"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                ItemData.setSpellName(ItemData.getHeldItem((Player) arguments.get(0)), (String) arguments.get(1));
                return true;
            }
        });

        subCommands.put("value item set spellType", new AdvancedSubCommand(new Class[]{Player.class, String.class}, new String[]{"player", "spellType"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                ItemData.setSpellType(ItemData.getHeldItem((Player) arguments.get(0)), (String) arguments.get(1));
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

        subCommands.put("value dmgMod add", new AdvancedSubCommand(new Class[]{Enums.DmgMod.class, Enums.DmgModType.class, Player.class, Float.class}, new String[]{"dmgMod", "dmgModType", "player", "number"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.DmgMod dmgMod = (Enums.DmgMod) arguments.get(0);
                Enums.DmgModType dmgModType = (Enums.DmgModType) arguments.get(1);
                Player player = (Player) arguments.get(2);
                Float num = (Float) arguments.get(3);

                DmgMods.setDmgMod(dmgMod);
                DmgMods.addDmgMod(player, dmgModType, num);
                return true;
            }
        });

        subCommands.put("value dmgMod remove", new AdvancedSubCommand(new Class[]{Enums.DmgMod.class, Enums.DmgModType.class, Player.class, Float.class}, new String[]{"dmgMod", "dmgModType", "player", "number"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.DmgMod dmgMod = (Enums.DmgMod) arguments.get(0);
                Enums.DmgModType dmgModType = (Enums.DmgModType) arguments.get(1);
                Player player = (Player) arguments.get(2);
                Float num = (Float) arguments.get(3);

                DmgMods.setDmgMod(dmgMod);
                DmgMods.removeDmgMod(player, dmgModType, num);
                return true;
            }
        });

        subCommands.put("value dmgMod set", new AdvancedSubCommand(new Class[]{Enums.DmgMod.class, Enums.DmgModType.class, Player.class, Float.class}, new String[]{"dmgMod", "dmgModType", "player", "number"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                Enums.DmgMod dmgMod = (Enums.DmgMod) arguments.get(0);
                Enums.DmgModType dmgModType = (Enums.DmgModType) arguments.get(1);
                Player player = (Player) arguments.get(2);
                Float num = (Float) arguments.get(3);

                DmgMods.setDmgMod(dmgMod);
                DmgMods.setDmgMod(player, dmgModType, num);
                return true;
            }
        });

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

        subCommands.put("value cooldown set", new AdvancedSubCommand(new Class[]{String.class, Player.class, Float.class, Float.class, Float.class, Float.class, Enums.CoolDownStage.class},
                new String[]{"spellType", "player", "windupTime", "activeTime", "passiveTime", "coolDownTime", "coolDownStage"}) {
            @Override
            public boolean onCommand(CommandSender sender, ArrayList<Object> arguments) {
                String spellType = (String) arguments.get(0);
                Player player = (Player) arguments.get(1);
                float[] timeInS = new float[]{(Float) arguments.get(2), (Float) arguments.get(3), (Float) arguments.get(4), (Float) arguments.get(5)};
                Enums.CoolDownStage coolDownStage = (Enums.CoolDownStage) arguments.get(6);

                CoolDowns.setCoolDown(player, spellType, timeInS, coolDownStage);
                return true;
            }
        });

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