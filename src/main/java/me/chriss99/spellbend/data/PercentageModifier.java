package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class PercentageModifier {
    private static final Gson gson = SpellBend.getGson();

    private final Player player;
    private float modifier;
    private int activeModifiers;
    private int isZero;
    private final NamespacedKey key;

    public PercentageModifier(@NotNull Player player, @NotNull NamespacedKey key, @NotNull String name) {
        this.player = player;
        this.key = key;

        String gsonString = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(player.getName() + "'s " + name + " were not setup when loading, fixing now!");
            modifier = 1;
            activeModifiers = 0;
            isZero = 0;
            player.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(getDefaultData()));
            return;
        }

        Type type = new TypeToken<Data>(){}.getType();
        Data data = gson.fromJson(gsonString, type);
        modifier = data.modifier;
        activeModifiers = data.activeModifiers;
        isZero = data.isZero;
    }

    /**
     * Gets the modifier
     *
     * @return The modifier
     */
    public float getModifier() {
        if (isZero > 0)
            return 0;

        return modifier;
    }

    /**
     * Adds the Modifier to the player
     * It does this by multiplying the number with the modifier
     * therefore it can be undone by dividing it by the same modifier
     * To do that call removeModifier()
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    public void addModifier(float modifier) {
        if (modifier < 0)
            throw new IllegalArgumentException("Modifier cannot be negative!");
        if (modifier == 1)
            return;

        activeModifiers++;
        if (modifier == 0) {
            isZero++;
            return;
        }

        this.modifier *= modifier;
    }

    /**
     * Removes the modifier from the player
     * It does this by dividing the number with the modifier, undoing addModifier() in the process
     *
     * @throws IllegalArgumentException If the modifier is negative
     *
     * @param modifier The modifier not negative
     */
    public void removeModifier(float modifier) {
        if (modifier < 0)
            throw new IllegalArgumentException("Modifier cannot be negative!");
        if (modifier == 1)
            return;

        activeModifiers--;
        if (activeModifiers == 0)
            this.modifier = 1;

        if (modifier == 0) {
            isZero--;
            return;
        }

        this.modifier /= modifier;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Saves the players modifiers to their PersistentDataContainer
     */
    public void saveModifiers() {
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING,
                gson.toJson(new Data(modifier, activeModifiers, isZero)));
    }

    public static Data getDefaultData() {
        return new Data(1, 0, 0);
    }

    private static class Data {
        public float modifier;
        public int activeModifiers;
        public int isZero;

        public Data(float modifiers, int activeModifiers, int isZero) {
            this.modifier = modifiers;
            this.activeModifiers = activeModifiers;
            this.isZero = isZero;
        }
    }
}
