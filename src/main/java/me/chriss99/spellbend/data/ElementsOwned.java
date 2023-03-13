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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElementsOwned {
    private static final Gson gson = SpellBend.getGson();

    private final Player player;
    private final EnumMap<ElementEnum, EnumSet<SpellEnum>> elementsOwned;

    public ElementsOwned(@NotNull Player player) {
        this.player = player;

        String gsonString = player.getPersistentDataContainer().get(PersistentDataKeys.elementsOwnedKey, PersistentDataType.STRING);

        if (gsonString == null) {
            Bukkit.getLogger().warning(player.getName() + "'s coolDowns were not setup when loading, fixing now!");
            elementsOwned = new EnumMap<>(ElementEnum.class);
            player.getPersistentDataContainer().set(PersistentDataKeys.elementsOwnedKey, PersistentDataType.STRING, gson.toJson(elementsOwned));
            return;
        }

        Type type = new TypeToken<EnumMap<ElementEnum, EnumSet<SpellEnum>>>(){}.getType();
        LinkedHashMap<ElementEnum, EnumSet<SpellEnum>> elementsOwned = gson.fromJson(gsonString, type);
        this.elementsOwned = new EnumMap<>(ElementEnum.class);
        this.elementsOwned.putAll(elementsOwned);
    }

    public void setElementOwned(@NotNull ElementEnum elementEnum) {
        elementsOwned.put(elementEnum, EnumSet.of(elementEnum.getSpell(0)));
    }

    public void setElementOwned(@NotNull ElementEnum elementEnum, @NotNull EnumSet<SpellEnum> ownedSpells) {
        elementsOwned.put(elementEnum, ownedSpells);
    }

    public void addSpellOwnedInElement(@NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        EnumSet<SpellEnum> ownedSpells = elementsOwned.get(elementEnum);
        if (ownedSpells == null) {
            Bukkit.getLogger().warning(spellEnum + " was supposed to be added as owned by " + player.getName() + " in " + elementEnum + " which they do not own!");
            player.sendMessage(SpellBend.getMiniMessage().deserialize("<red>You have to own " + elementEnum + " before you can buy this spell!"));
            return;
        }

        ownedSpells.add(spellEnum);
    }

    public boolean playerOwnsElement(@NotNull ElementEnum elementEnum) {
        return elementsOwned.containsKey(elementEnum);
    }

    /**
     * Checks if the spell is contained in the set under that element
     *
     * @param elementEnum The element to check in
     * @param spellEnum The spell to check for
     * @return If the player owns the spell in that element
     */
    public boolean playerOwnsSpellInElement(@NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        EnumSet<SpellEnum> spellsOwned = elementsOwned.get(elementEnum);
        if (spellsOwned == null)
            return false;
        return spellsOwned.contains(spellEnum);
    }

    /**
     * Checks if the spell is contained in any of the sets in the map
     *
     * @param spellEnum The spell to check for
     * @return If the player owns that spell anywhere
     */
    public boolean playerOwnsSpellGenerally(@NotNull SpellEnum spellEnum) {
        for (Map.Entry<ElementEnum, EnumSet<SpellEnum>> entry : elementsOwned.entrySet())
            if (entry.getValue().contains(spellEnum))
                return true;
        return false;
    }

    public boolean playerOwnsPreviousSpellInElement(@NotNull ElementEnum elementEnum, @NotNull SpellEnum spellEnum) {
        int index = elementEnum.getSpells().indexOf(spellEnum)-1;
        if (index == -2) {
            Bukkit.getLogger().warning("It was queried if " + player.getName() + " owns " + spellEnum + " in " + elementEnum + " which is not contained in that element!");
            return false;
        }
        return playerOwnsSpellInElement(elementEnum, elementEnum.getSpell(index));
    }

    public Player getPlayer() {
        return player;
    }

    public void saveElementsOwned() {
        player.getPersistentDataContainer().set(PersistentDataKeys.elementsOwnedKey, PersistentDataType.STRING, gson.toJson(elementsOwned));
    }
}
