package me.chriss99.spellbend.playerdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.data.CoolDownEntry;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.util.ItemData;
import me.chriss99.spellbend.harddata.Maps;
import me.chriss99.spellbend.util.math.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class CoolDowns {
    private static final Gson gson = SpellBend.getGson();

    /**
     * Loads the players CoolDowns from its PersistentDataContainer into the PlayerSessionStorage.
     * <b>Intended to be used when the player joins.</b>
     *
     * @param player The player whose CoolDowns to load
     */
    public static void loadCoolDowns(@NotNull Player player) { //ToDo test if this works
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load CoolDowns, skipping loading!");
            return;
        }
        if (PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading coolDowns, skipping loading!");
            return;
        }

        Type type = new TypeToken<HashMap<String, CoolDownEntry>>(){}.getType();
        PlayerSessionStorage.coolDowns.put(player, gson.fromJson(player.getPersistentDataContainer().get(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING), type));
    }

    /*public static void loadCoolDowns(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.displayName() + " is not online when trying to load CoolDowns, skipping loading!");
            return;
        }
        if (PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.displayName() + " is already loaded when loading coolDowns, skipping loading!");
            return;
        }
        ArrayList<String> coolDownEntries;

        PersistentDataContainer data = player.getPersistentDataContainer();
        try {
            //noinspection ConstantConditions
            coolDownEntries = new ArrayList<>(Arrays.asList(data.get(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING).split(", ")));
            //noinspection RedundantCollectionOperation
            if (coolDownEntries.contains("")) coolDownEntries.remove("");
        } catch (NullPointerException npe) {
            Bukkit.getLogger().warning(player.displayName() + " did not have coolDowns set up, setting coolDowns to \"\"!");
            data.set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, "");
            PlayerSessionStorage.coolDowns.put(player, new ArrayList<>());
            return;
        }

        ArrayList<CoolDownEntry> coolDowns = new ArrayList<>();

        for (String coolDownEntry : coolDownEntries) {
            String[] entry = coolDownEntry.split(": ");
            String[] infoStrings = entry[1].split("; ");

            try {
                coolDowns.add(new CoolDownEntry(entry[0], Float.parseFloat(infoStrings[0]), new Date(Long.parseLong(infoStrings[1])), Enums.CoolDownStage.valueOf(infoStrings[2])));
            } catch (NumberFormatException nfe) {
                Bukkit.getLogger().warning("String \"" + infoStrings[0] + "\" is supposed to be a Float but isn't! or\nString \"" + infoStrings[1] + "\" is supposed to be a Long but isn't!" + nfe);
            } catch (IllegalArgumentException iae) {
                Bukkit.getLogger().warning("String \"" + infoStrings[2] + "\" is supposed to be a CoolDownStage Enum but isn't! " + iae);
            }
        }

        PlayerSessionStorage.coolDowns.put(player, coolDowns);
    }*/

    /**Sets the cooldown of the player
     * regardless of present cooldown
     *
     * @param player The player to add the cooldown to
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownStage
     */
    public static void setCoolDown(@NotNull Player player, @NotNull String spellType, float timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        PlayerSessionStorage.coolDowns.get(player).put(spellType, new CoolDownEntry(spellType, new Date(), timeInSeconds, coolDownStage));
        if (spellType.equals(ItemData.getHeldSpellType(player)))
            PlayerDataBoard.registerPlayer(player, spellType);
    }

    /**Removes the cooldown from the player
     *
     * @param player The player to remove a cooldown from
     * @param spellType The spellType to remove
     */
    public static void removeCoolDown(@NotNull Player player, @NotNull String spellType) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        PlayerSessionStorage.coolDowns.get(player).remove(spellType);
    }

    /**Adds a cooldown to the player
     * if a cooldown is already present the larger one is assigned
     *
     * @param player The player to add the cooldown to
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownStage
     */
    public static void extendCoolDown(@NotNull Player player, @NotNull String spellType, float timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns.containsKey(spellType)) {
            CoolDownEntry oldCoolDown = coolDowns.get(spellType);
            if (MathUtil.ASmallerB(
                    new long[]{(long) Maps.coolDownStageToIndexMap.get(oldCoolDown.coolDownStage())*(-1), (long) oldCoolDown.timeInS()*1000-(new Date().getTime()-oldCoolDown.startDate().getTime())},
                    new long[]{(long) Maps.coolDownStageToIndexMap.get(coolDownStage)*(-1), (long) timeInSeconds*1000}))
                coolDowns.put(spellType, new CoolDownEntry(spellType, new Date(), timeInSeconds, coolDownStage));
        } else PlayerSessionStorage.coolDowns.get(player).put(spellType, new CoolDownEntry(spellType, new Date(), timeInSeconds, coolDownStage));

        if (spellType.equals(ItemData.getHeldSpellType(player)))
            PlayerDataBoard.registerPlayer(player, spellType);
    }

    /**Adds a cooldown to the player but warns console if one is already present
     * if already present the larger cooldown is assigned
     *
     * @param player The player to add the cooldown to
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownType
     */
    public static void addCoolDown(@NotNull Player player, @NotNull String spellType, float timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        if (PlayerSessionStorage.coolDowns.get(player).containsKey(spellType))
            Bukkit.getLogger().warning("CoolDown " + spellType + " is already present (" + PlayerSessionStorage.coolDowns.get(player).get(spellType) + ") " +
                    "when trying to add (" + timeInSeconds + ", " + coolDownStage + ") to " + player.getName() + ", assigning larger coolDown!");
        extendCoolDown(player, spellType, timeInSeconds, coolDownStage);
    }


    /**
     *
     * @param player The player to get the CoolDown from
     * @param spellType The CoolDown to get
     * @return A CoolDownEntry containing {TimeInS, startDate, CoolDownType}
     */
    public static @Nullable CoolDownEntry getCoolDownEntry(@NotNull Player player, @NotNull String spellType) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        if (coolDowns.containsKey(spellType)) {
            if (coolDowns.get(spellType).getRemainingCoolDownTime() <= 0.1f){
                coolDowns.remove(spellType);
                return null;
            }
            return coolDowns.get(spellType);
        }
        return null;
    }

    /**
     *
     * @param player The player to get the CoolDown from
     * @return All CoolDownEntries containing {TimeInS, startDate, CoolDownType}
     */
    public static HashMap<String, CoolDownEntry> getCoolDowns(@NotNull Player player) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not loaded in PlayerToCoolDowns map, now fixing!");
            loadCoolDowns(player);
        }

        HashMap<String, CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);
        for (Map.Entry<String, CoolDownEntry> entry : coolDowns.entrySet())
            if (entry.getValue().getRemainingCoolDownTime() <= 0.1f)
                coolDowns.remove(entry.getKey());
        return coolDowns;
    }

    /**
     * Saves the players CoolDowns to its PersistentDataContainer and removes it from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose CoolDowns to save
     */
    public static void saveCoolDowns(@NotNull Player player) { //ToDo test if this works
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToCoolDowns map when saving, saving skipped!");
            return;
        }

        player.getPersistentDataContainer().set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, gson.toJson(PlayerSessionStorage.coolDowns.get(player)));
        PlayerSessionStorage.coolDowns.remove(player);
    }

    /*public static void saveCoolDowns(@NotNull Player player) {
        if (!PlayerSessionStorage.coolDowns.containsKey(player)) {
            Bukkit.getLogger().warning(player.displayName() + " was not logged in UUIDToCoolDowns map when saving, saving skipped!");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<CoolDownEntry> coolDowns = PlayerSessionStorage.coolDowns.get(player);

        coolDowns.removeIf(entry -> entry.getRemainingCoolDownTime() <= 0.1f);

        boolean hasEntries = false;
        for (CoolDownEntry entry : coolDowns) {
            stringBuilder
                    .append(entry.spellType()).append(": ")
                    .append(entry.timeInS()).append("; ").append(entry.startDate().getTime()).append("; ").append(entry.coolDownStage())
                    .append(", ");
            hasEntries = true;
        }
        if (hasEntries) {
            int length = stringBuilder.length();
            stringBuilder.delete(length-2,length);
        }

        player.getPersistentDataContainer().set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, stringBuilder.toString());
        PlayerSessionStorage.coolDowns.remove(player);
    }*/
}
