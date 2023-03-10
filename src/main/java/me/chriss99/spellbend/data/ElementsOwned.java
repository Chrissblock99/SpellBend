package me.chriss99.spellbend.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chriss99.spellbend.SpellBend;
import me.chriss99.spellbend.harddata.ElementEnum;
import me.chriss99.spellbend.harddata.PersistentDataKeys;
import me.chriss99.spellbend.harddata.SpellEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;

public class ElementsOwned {
    private static final Gson gson = SpellBend.getGson();

    private final Player player;
    private final EnumMap<ElementEnum, ArrayList<SpellEnum>> elementsOwned;

    public ElementsOwned(@NotNull Player player) {
        this.player = player;

        String gsonString = player.getPersistentDataContainer().get(PersistentDataKeys.elementsOwnedKey, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(player.getName() + "'s coolDowns were not setup when loading, fixing now!");
            elementsOwned = new EnumMap<>(ElementEnum.class);
            player.getPersistentDataContainer().set(PersistentDataKeys.elementsOwnedKey, PersistentDataType.STRING, gson.toJson(elementsOwned));
            return;
        }

        Type type = new TypeToken<EnumMap<ElementEnum, ArrayList<SpellEnum>>>(){}.getType();
        elementsOwned = gson.fromJson(gsonString, type);
    }

    public boolean playerOwnsElement(@NotNull ElementEnum elementEnum) {
        return elementsOwned.containsKey(elementEnum);
    }

    public boolean playerOwnsSpell(@NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        ArrayList<SpellEnum> spellsOwned = elementsOwned.get(elementEnum);
        if (spellsOwned == null)
            return false;
        return spellsOwned.contains(spellEnum);
    }

    public Player getPlayer() {
        return player;
    }

    public void saveElementsOwned() {
        player.getPersistentDataContainer().set(PersistentDataKeys.elementsOwnedKey, PersistentDataType.STRING, gson.toJson(elementsOwned));
    }
}
