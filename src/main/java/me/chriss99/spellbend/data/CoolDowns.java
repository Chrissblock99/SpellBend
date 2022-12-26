package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.util.ItemData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class CoolDowns {
    private static final Gson gson = SpellBend.getGson();
    private final Player player;
    private final Map<String, CoolDownEntry> coolDowns;

    public CoolDowns(@NotNull Player player) {
        this.player = player;

        String gsonString = player.getPersistentDataContainer().get(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(player.getName() + "'s coolDowns were not setup when loading, fixing now!");
            coolDowns = new HashMap<>();
            player.getPersistentDataContainer().set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, gson.toJson(coolDowns));
            return;
        }

        Type type = new TypeToken<HashMap<String, CoolDownEntry>>(){}.getType();
        coolDowns = gson.fromJson(gsonString, type);
    }

    /**
     * Removes all coolDowns from the list that have almost no time left
     */
    public void removeExpiredCoolDowns() {
        coolDowns.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Checks if the type is cooled down <br>
     * returns false if expired
     *
     * @param spellType The spellType to check if cooled down
     * @return If the type is cooled down
     */
    public boolean typeIsCooledDown(@Nullable String spellType) {
        if (spellType == null)
            return false;
        CoolDownEntry coolDownEntry = getCoolDownEntry(spellType);
        if (coolDownEntry == null)
            return false;

        if (coolDownEntry.isExpired()) {
            removeCoolDown(spellType);
            return false;
        }
        return true;
    }

    /**
     * Sets the cooldown of the player regardless of present cooldown using WINDUP as the starting stage
     *
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @return The newly added CoolDownEntry
     */
    public CoolDownEntry setCoolDown(@NotNull String spellType, float[] timeInSeconds) {
        return setCoolDown(spellType, timeInSeconds, Enums.CoolDownStage.WINDUP);
    }

    /**
     * Sets the cooldown of the player regardless of present cooldown
     *
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownStage
     * @return The newly added CoolDownEntry
     */
    public CoolDownEntry setCoolDown(@NotNull String spellType, float[] timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        spellType = spellType.toUpperCase();

        CoolDownEntry coolDownEntry = new CoolDownEntry(spellType, timeInSeconds, coolDownStage);
        coolDowns.put(spellType, coolDownEntry);
        if (spellType.equals(ItemData.getHeldSpellType(player)))
            PlayerDataBoard.registerPlayer(player, spellType);
        return coolDownEntry;
    }

    /**
     * Removes the cooldown from the player
     *
     * @param spellType The spellType to remove
     */
    public void removeCoolDown(@NotNull String spellType) {
        coolDowns.remove(spellType);
    }

    /**
     * Adds a cooldown to the player
     * if a cooldown is already present the larger one is assigned
     *
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownStage to start in
     * @return The now present CoolDownEntry
     */
    public CoolDownEntry extendCoolDown(@NotNull String spellType, float[] timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        removeExpiredCoolDowns();

        CoolDownEntry newCoolDown = new CoolDownEntry(spellType, timeInSeconds, coolDownStage);
        if (!coolDowns.containsKey(spellType))
            coolDowns.put(spellType, newCoolDown);

        CoolDownEntry oldCoolDown = coolDowns.get(spellType);

        if (oldCoolDown.getRemainingCoolDownTimeInS()<newCoolDown.getRemainingCoolDownTimeInS())
            coolDowns.put(spellType, newCoolDown);

        if (spellType.equals(ItemData.getHeldSpellType(player)))
            PlayerDataBoard.registerPlayer(player, spellType);
        return getCoolDownEntry(spellType);
    }

    /**
     * Adds a cooldown to the player but warns console if one is already present
     * if already present the larger cooldown is assigned
     *
     * @param spellType The SpellType to cool down
     * @param timeInSeconds The time to cool down
     * @param coolDownStage The CoolDownType
     * @return The now present CoolDownEntry
     */
    public CoolDownEntry addCoolDown(@NotNull String spellType, float[] timeInSeconds, @NotNull Enums.CoolDownStage coolDownStage) {
        if (coolDowns.containsKey(spellType))
            Bukkit.getLogger().warning("CoolDown " + spellType + " is already present (" + coolDowns.get(spellType) + ") " +
                    "when trying to add (" + Arrays.toString(timeInSeconds) + ", " + coolDownStage + ") to " + player.getName() + ", assigning larger coolDown!");
        return extendCoolDown(spellType, timeInSeconds, coolDownStage);
    }

    /**
     * @param spellType The CoolDown to get
     * @return A CoolDownEntry containing {TimeInS, startDate, CoolDownType}
     */
    public @Nullable CoolDownEntry getCoolDownEntry(@NotNull String spellType) {
        removeExpiredCoolDowns();
        return coolDowns.get(spellType);
    }

    /**
     * @return The coolDown Map
     * @deprecated gives too much access
     */
    @Deprecated
    public Map<String, CoolDownEntry> getCoolDowns() {
        removeExpiredCoolDowns();
        return coolDowns;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Saves the coolDowns to the players PersistentDataContainer
     */
    public void saveCoolDowns() {
        removeExpiredCoolDowns();
        player.getPersistentDataContainer().set(PersistentDataKeys.coolDownsKey, PersistentDataType.STRING, gson.toJson(coolDowns));
    }
}
