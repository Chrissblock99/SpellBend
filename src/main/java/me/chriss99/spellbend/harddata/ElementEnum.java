package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum ElementEnum {
    EMBER(Item.create(Material.FIRE_CHARGE, "<red><bold>Ember"),
            new SpellEnum[]{SpellEnum.MAGMA_BURST, SpellEnum.EMBER_BLAST, SpellEnum.SCORCHING_COLUMN, SpellEnum.BLAZING_SPIN, SpellEnum.FIERY_RAGE}, 150),
    WATER(Item.create(Material.WATER_BUCKET, "<blue><bold>Water"),
            new SpellEnum[]{SpellEnum.HYDRO_BLAST, SpellEnum.WATER_SPRAY, SpellEnum.WATER_TORRENT, SpellEnum.RISING_TIDE, SpellEnum.SEA_SHIELD}, 150),
    NATURE(Item.create(Material.OAK_SAPLING, "<green><bold>Nature"),
            new SpellEnum[]{SpellEnum.VERDANT_SPORES, SpellEnum.POISON_DARTS, SpellEnum.AUTUMN_WINDS, SpellEnum.VINE_GRAB, SpellEnum.NATURES_AEGIS}, 150),
    EARTH(Item.create(Material.COARSE_DIRT, "<dark_green><bold>Earth"),
            new SpellEnum[]{SpellEnum.SERRATED_EARTH, SpellEnum.LANDSLIDE, SpellEnum.BURROW, SpellEnum.SEISMIC_SMASH, SpellEnum.ROCK_BODY}, 300),
    ELECTRO(Item.create(Material.BEACON, "<aqua><bold>Electro"),
            new SpellEnum[]{SpellEnum.LIGHTNING_BOLT, SpellEnum.FLASH, SpellEnum.SEISMIC_SHOCK, SpellEnum.LIGHTNING_CHAIN, SpellEnum.GALVANISE}, 300),
    ICE(Item.create(Material.PACKED_ICE, "<white><bold>Ice"),
            new SpellEnum[]{SpellEnum.TWISTED_FLURRY, SpellEnum.FROSTBITE, SpellEnum.WINTERS_HEAVE, SpellEnum.AVALANCHE, SpellEnum.GLACIAL_ARMAMENT}, 300),
    AETHER(Item.create(Material.NETHER_STAR, "<gray><bold>Aether"),
            new SpellEnum[]{SpellEnum.METEOR_BELT, SpellEnum.BLACK_HOLE, SpellEnum.STELLAR_SLAM, SpellEnum.AETHERS_WRATH, SpellEnum.COSMIC_SMASH}, 650),
    SOUL(Item.create(Material.SKELETON_SKULL, "<dark_purple><bold>Soul"),
            new SpellEnum[]{SpellEnum.SCATTER_BONES, SpellEnum.SOUL_DRAIN, SpellEnum.SEEKING_SKULL, SpellEnum.GASHING_FOSSILS, SpellEnum.PHANTOMS_CURSE}, 650),
    TIME(Item.create(Material.CLOCK, "<yellow><bold>Time"),
            new SpellEnum[]{SpellEnum.DARTS_OF_TIME, SpellEnum.ESCAPE_THROUGH_TIME, SpellEnum.DEATHLY_HOUR, SpellEnum.CHRONOPUNCH, SpellEnum.TEMPORAL_ILLUSION}, 650),
    METAL(Item.create(Material.IRON_INGOT, "<#808080><bold>Metal"),
            new SpellEnum[]{SpellEnum.CATAPULT, SpellEnum.ALLOYED_BARRIER, SpellEnum.METARANG, SpellEnum.RAZOR_SPIN, SpellEnum.ANCIENT_TEMPER}, 900),
    EXPLOSION(Item.create(Material.TNT, "<gold><bold>Explosion"),
            new SpellEnum[]{SpellEnum.FIERY_MISSILE, SpellEnum.LANDMINES, SpellEnum.BLAST_OFF, SpellEnum.FLYING_BLITZ, SpellEnum.BOMB_HEAD}, 900);

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


    ElementEnum(@NotNull ItemStack displayItem, @NotNull SpellEnum[] spells, int price) {
        this.displayItem = displayItem;
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
