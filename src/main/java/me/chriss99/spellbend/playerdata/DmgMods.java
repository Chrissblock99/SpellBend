package me.chriss99.spellbend.playerdata;

import com.google.gson.Gson;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.Enums;
import me.chriss99.spellbend.harddata.Maps;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DmgMods {
    private static final Gson gson = SpellBend.getGson();

    /**
     * Loads the players DmgMods from its PersistentDataContainer into the PlayerSessionStorage.
     * <b>Intended to be used when the player joins.</b>
     *
     * @param player The player whose DmgMods to load
     */
    public static void loadDmgMods(@NotNull Player player) { //TODO test if this works
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.getName() + " is not online when trying to load DmgMods, skipping loading!");
            return;
        }
        if (PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " is already loaded when loading dmgMods, skipping loading!");
            return;
        }

        PlayerSessionStorage.dmgMods.put(player, gson.fromJson(player.getPersistentDataContainer().get(PersistentDataKeys.dmgModsKey, PersistentDataType.STRING), float[].class));
    }

    /*public static void loadDmgMods(@NotNull Player player) {
        if (!player.isOnline()) {
            Bukkit.getLogger().warning(player.displayName() + " is not online when trying to load DmgMods, skipping loading!");
            return;
        }
        if (PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.displayName() + " is already loaded when loading dmgMods, skipping loading!");
            return;
        }

        PersistentDataContainer data = player.getPersistentDataContainer();
        try {
            float[] dmgMods = new float[3];
            //noinspection ConstantConditions
            String[] stringFloats = data.get(PersistentDataKeys.dmgModsKey, PersistentDataType.STRING).split(", ");

            for (int i = 0;i<stringFloats.length;i++) dmgMods[i] = Float.parseFloat(stringFloats[i]);

            PlayerSessionStorage.dmgMods.put(player, dmgMods);
        } catch (NullPointerException exception) {
            Bukkit.getLogger().warning(player.displayName() + " did not have dmgMods set up, setting dmgMods to 1, 1, 1");
            data.set(PersistentDataKeys.dmgModsKey, PersistentDataType.STRING, "1, 1, 1");
            PlayerSessionStorage.dmgMods.put(player, new float[]{1f, 1f, 1f});
        }
    }*/

    /**
     * Gets the specified Damage modifier of the player
     * returns all of them multiplied together if given null
     *
     * @param player The player to get DamageMods from
     * @param modType The damageMod name "all" is possible
     * @return The dmgMod
     */
    public static float getDmgMod(@NotNull Player player, @Nullable Enums.DmgModType modType) {
        if (!PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToDmgMods map, now fixing!");
            loadDmgMods(player);
        }

        float[] dmgMods = PlayerSessionStorage.dmgMods.get(player);
        if (modType == null) {
            float result = 1;
            for (float num : dmgMods)
                result *= num;
            return result;
        }
        return dmgMods[Maps.dmgModToIndexMap.get(modType)];
    }

    /**
     * Adds the DmgMod to the specified type of the player
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeDmgMod()
     *
     * @param player The player to add the DmgMod to
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void addDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (!PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToDmgMods map, now fixing!");
            loadDmgMods(player);
        }

        PlayerSessionStorage.dmgMods.get(player)[Maps.dmgModToIndexMap.get(modType)] *= modifier;
    }

    /**
     * Removes the DmgMod from the specified type of the player
     * It does this by dividing the number with the modifier, undoing addDmgMod() in the process
     *
     * @param player The player to remove the DmgMod from
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void removeDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (!PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToDmgMods map, now fixing!");
            loadDmgMods(player);
        }

        PlayerSessionStorage.dmgMods.get(player)[Maps.dmgModToIndexMap.get(modType)] /= modifier;
    }

    /**
     * Sets the DmgMod from the specified type of the player if it is larger
     * <b>this can mathematically not be undone and will break things if added modifiers are still present
     * therefore it should only be used rarely</b>
     *
     * @param player The player to set the DmgMod of
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void extendDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (!PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToDmgMods map, now fixing!");
            loadDmgMods(player);
        }

        float[] dmgMods = PlayerSessionStorage.dmgMods.get(player);
        int index = Maps.dmgModToIndexMap.get(modType);
        if (dmgMods[index]<modifier)
            dmgMods[index] = modifier;
    }

    /**
     * Sets the DmgMod from the specified type of the player
     * <b>this can mathematically not be undone and will break things if added modifiers are still present
     * therefore it should only be used rarely</b>
     *
     * @param player The player to set the DmgMod of
     * @param modType The DmgMod type
     * @param modifier The damage modifier
     */
    public static void setDmgMod(@NotNull Player player, @NotNull Enums.DmgModType modType, float modifier) {
        if (!PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToDmgMods map, now fixing!");
            loadDmgMods(player);
        }

        PlayerSessionStorage.dmgMods.get(player)[Maps.dmgModToIndexMap.get(modType)] = modifier;
    }

    /**
     * Saves the players DmgMods to its PersistentDataContainer and removes it from PlayerSessionStorage.
     * <b>Intended to be used when the player leaves.</b>
     *
     * @param player The player whose DmgMods to save
     */
    public static void saveDmgMods(@NotNull Player player) { //TODO test if this works
        if (PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.getName() + " was not logged in PlayerToDmgMods map when saving, saving skipped!");
            return;
        }

        player.getPersistentDataContainer().set(PersistentDataKeys.dmgModsKey, PersistentDataType.STRING, gson.toJson(PlayerSessionStorage.dmgMods.get(player)));
        PlayerSessionStorage.dmgMods.remove(player);
    }

    /*public static void saveDmgMods(@NotNull Player player) {
        if (PlayerSessionStorage.dmgMods.containsKey(player)) {
            Bukkit.getLogger().warning(player.displayName() + " was not logged in PlayerToDmgMods map when saving, saving skipped!");
            return;
        }

        float[] dmgMods = PlayerSessionStorage.dmgMods.get(player);
        String[] stringFloats = new String[dmgMods.length];

        for (int i = 0;i<dmgMods.length;i++)
            stringFloats[i] = String.valueOf(dmgMods[i]);

        player.getPersistentDataContainer().set(PersistentDataKeys.dmgModsKey, PersistentDataType.STRING, String.join(", ", stringFloats));
        PlayerSessionStorage.dmgMods.remove(player);
    }*/
}
