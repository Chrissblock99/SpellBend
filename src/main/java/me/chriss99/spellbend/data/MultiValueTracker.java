package me.chriss99.spellbend.data;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiValueTracker {
    private final LivingEntity livingEntity;
    private final NamespacedKey key;
    private final List<Integer> values;

    public MultiValueTracker(@NotNull LivingEntity livingEntity, @NotNull NamespacedKey key, @NotNull String name, int[] defaultArray) {
        this.livingEntity = livingEntity;
        this.key = key;

        int[] value = livingEntity.getPersistentDataContainer().get(key, PersistentDataType.INTEGER_ARRAY);
        if (value == null) {
            Bukkit.getLogger().warning(livingEntity.getName() + " did not have " + name + " set up when loading, fixing now!");
            livingEntity.getPersistentDataContainer().set(key, PersistentDataType.INTEGER_ARRAY, defaultArray);
            value = defaultArray;
        }

        this.values = Arrays.stream(value).boxed().collect(Collectors.toCollection(LinkedList::new));
    }

    public void addValue(int value) {
        if (values.size() == 0) {
            values.add(value);
            return;
        }

        for (int i = 0;i<values.size();i++) {
            if (values.get(i)>value)
                continue;
            values.add(i ,value);
            return;
        }

        values.add(value);
    }

    public void removeValue(int value) {
        //has to cast to Integer cause otherwise it will take the int as an index
        values.remove((Integer) value);
    }

    /**
     * @return a COPY of the values (edits of this won't affect the real list)
     */
    public List<Integer> getValues() {
        return new LinkedList<>(values);
    }

    public @NotNull LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public Integer largestValue() {
        if (values.size() == 0)
            return null;

        return values.get(0);
    }

    public void saveValue() {
        livingEntity.getPersistentDataContainer().set(key, PersistentDataType.INTEGER_ARRAY, values.stream().mapToInt(i->i).toArray());
    }
}
