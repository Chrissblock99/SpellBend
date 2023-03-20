package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.ItemBuilder;
import me.chriss99.spellbend.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public enum ElementEnum {
    EMBER(Material.FIRE_CHARGE, "red", 150,
            new SpellEnum[]{SpellEnum.MAGMA_BURST, SpellEnum.EMBER_BLAST, SpellEnum.SCORCHING_COLUMN, SpellEnum.BLAZING_SPIN, SpellEnum.FIERY_RAGE}),
    WATER(Material.WATER_BUCKET, "blue", 150,
            new SpellEnum[]{SpellEnum.HYDRO_BLAST, SpellEnum.WATER_SPRAY, SpellEnum.WATER_TORRENT, SpellEnum.SEA_SHIELD, SpellEnum.RISING_TIDE}),
    NATURE(Material.OAK_SAPLING, "green", 150,
            new SpellEnum[]{SpellEnum.VERDANT_SPORES, SpellEnum.POISON_DARTS, SpellEnum.AUTUMN_WINDS, SpellEnum.VINE_GRAB, SpellEnum.NATURES_AEGIS}),
    EARTH(Material.COARSE_DIRT, "dark_green", 300,
            new SpellEnum[]{SpellEnum.SERRATED_EARTH, SpellEnum.LANDSLIDE, SpellEnum.BURROW, SpellEnum.SEISMIC_SMASH, SpellEnum.ROCK_BODY}),
    ELECTRO(Material.BEACON, "aqua", 300,
            new SpellEnum[]{SpellEnum.LIGHTNING_BOLT, SpellEnum.FLASH, SpellEnum.SEISMIC_SHOCK, SpellEnum.LIGHTNING_CHAIN, SpellEnum.GALVANISE}),
    ICE(Material.PACKED_ICE, "white", 300,
            new SpellEnum[]{SpellEnum.TWISTED_FLURRY, SpellEnum.FROSTBITE, SpellEnum.WINTERS_HEAVE, SpellEnum.AVALANCHE, SpellEnum.GLACIAL_ARMAMENT}),
    AETHER(Material.NETHER_STAR, "gray", 650,
            new SpellEnum[]{SpellEnum.METEOR_BELT, SpellEnum.BLACK_HOLE, SpellEnum.STELLAR_SLAM, SpellEnum.AETHERS_WRATH, SpellEnum.COSMIC_SMASH}),
    SOUL(Material.SKELETON_SKULL, "dark_purple", 650,
            new SpellEnum[]{SpellEnum.SCATTER_BONES, SpellEnum.SOUL_DRAIN, SpellEnum.SEEKING_SKULL, SpellEnum.GASHING_FOSSILS, SpellEnum.PHANTOMS_CURSE}),
    TIME(Material.CLOCK, "yellow", 650,
            new SpellEnum[]{SpellEnum.DARTS_OF_TIME, SpellEnum.ESCAPE_THROUGH_TIME, SpellEnum.DEATHLY_HOUR, SpellEnum.CHRONOPUNCH, SpellEnum.TEMPORAL_ILLUSION}),
    METAL(Material.IRON_INGOT, "#808080", 900,
            new SpellEnum[]{SpellEnum.CATAPULT, SpellEnum.ALLOYED_BARRIER, SpellEnum.METARANG, SpellEnum.RAZOR_SPIN, SpellEnum.ANCIENT_TEMPER}),
    EXPLOSION(Material.TNT, "gold", 900,
            new SpellEnum[]{SpellEnum.FIERY_MISSILE, SpellEnum.LANDMINES, SpellEnum.BLAST_OFF, SpellEnum.FLYING_BLITZ, SpellEnum.BOMB_HEAD});

    private final ItemStack displayItem;
    private final List<SpellEnum> spells;
    private final int price;

    public boolean playerCanBuy(@NotNull PlayerSessionData sessionData) {
        return sessionData.getGems().getCurrency() >= price;
    }

    public ItemStack getDisplayItem(int index) {
        return spells.get(index).getDisplayItem();
    }

    public ItemStack getUseItem(int index) {
        return spells.get(index).getUseItem();
    }

    public SpellEnum getSpell(int index) {
        return spells.get(index);
    }

    public @Nullable SpellEnum getSpellBefore(@NotNull SpellEnum spellEnum) {
        int index = spells.indexOf(spellEnum)-1;
        if (index == -2) {
            Bukkit.getLogger().warning("The spell before " + spellEnum + " in " + this + " was queried which is not contained in the element!");
            return null;
        }
        return spells.get(index);
    }


    ElementEnum(@NotNull Material material, @NotNull String color, int price, @NotNull SpellEnum[] spells, @NotNull String... miniMessageExplanation) {
        this(material, color, price, spells, null, miniMessageExplanation);
    }

    ElementEnum(@NotNull Material material, @NotNull String color, int price, @NotNull SpellEnum[] spells,
                @Nullable Function<ItemStack, ItemStack> itemModifier, @NotNull String... miniMessageExplanation) {

        String miniMessageName = "<" + color + "><bold>" + TextUtil.standardCapitalize(name());
        LinkedList<String> miniMessageLore = new LinkedList<>(List.of("<dark_gray>----------------"));
        miniMessageLore.addAll(List.of(miniMessageExplanation));
        miniMessageLore.add("<dark_gray>----------------");

        ItemStack item = new ItemBuilder(material)
                .setMiniMessageDisplayName(miniMessageName)
                .setMiniMessageLore(miniMessageLore)
                .setCustomModelData(101) //TODO this number and the general CMD system
                .hideAllFlags()
                .build();
        if (itemModifier != null)
            item = itemModifier.apply(item);

        displayItem = item;
        this.spells = List.of(spells);
        this.price = price;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public List<SpellEnum> getSpells() {
        return spells;
    }

    public int getPrice() {
        return price;
    }
}
