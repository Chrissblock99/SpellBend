package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.data.PlayerSessionData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ElementEnum {
    EMBER(new ItemStack(Material.FIRE_CHARGE), new SpellEnum[]{SpellEnum.MAGMA_BURST, SpellEnum.EMBER_BLAST, SpellEnum.SCORCHING_COLUMN, SpellEnum.BLAZING_SPIN, SpellEnum.FIERY_RAGE}, 150),
    WATER(new ItemStack(Material.WATER_BUCKET), new SpellEnum[]{SpellEnum.HYDRO_BLAST, SpellEnum.WATER_SPRAY, SpellEnum.WATER_TORRENT, SpellEnum.RISING_TIDE, SpellEnum.SEA_SHIELD}, 150),
    NATURE(new ItemStack(Material.OAK_SAPLING), new SpellEnum[]{SpellEnum.VERDANT_SPORES, SpellEnum.POISON_DARTS, SpellEnum.AUTUMN_WINDS, SpellEnum.VINE_GRAB, SpellEnum.NATURES_AEGIS}, 150),
    EARTH(new ItemStack(Material.COARSE_DIRT), new SpellEnum[]{SpellEnum.SERRATED_EARTH, SpellEnum.LANDSLIDE, SpellEnum.BURROW, SpellEnum.SEISMIC_SMASH, SpellEnum.ROCK_BODY}, 300),
    ELECTRO(new ItemStack(Material.BEACON), new SpellEnum[]{SpellEnum.LIGHTNING_BOLT, SpellEnum.FLASH, SpellEnum.SEISMIC_SHOCK, SpellEnum.LIGHTNING_CHAIN, SpellEnum.GALVANISE}, 300),
    ICE(new ItemStack(Material.PACKED_ICE), new SpellEnum[]{SpellEnum.TWISTED_FLURRY, SpellEnum.FROSTBITE, SpellEnum.WINTERS_HEAVE, SpellEnum.AVALANCHE, SpellEnum.GLACIAL_ARMAMENT}, 300),
    AETHER(new ItemStack(Material.CONDUIT), new SpellEnum[]{SpellEnum.METEOR_BELT, SpellEnum.BLACK_HOLE, SpellEnum.STELLAR_SLAM, SpellEnum.AETHERS_WRATH, SpellEnum.COSMIC_SMASH}, 650),
    SOUL(new ItemStack(Material.SKELETON_SKULL), new SpellEnum[]{SpellEnum.SCATTER_BONES, SpellEnum.SOUL_DRAIN, SpellEnum.SEEKING_SKULL, SpellEnum.GASHING_FOSSILS, SpellEnum.PHANTOMS_CURSE}, 650),
    TIME(new ItemStack(Material.CLOCK), new SpellEnum[]{SpellEnum.DARTS_OF_TIME, SpellEnum.ESCAPE_THROUGH_TIME, SpellEnum.DEATHLY_HOUR, SpellEnum.CHRONOPUNCH, SpellEnum.TEMPORAL_ILLUSION}, 650),
    METAL(new ItemStack(Material.IRON_INGOT), new SpellEnum[]{SpellEnum.CATAPULT, SpellEnum.ALLOYED_BARRIER, SpellEnum.METARANG, SpellEnum.RAZOR_SPIN, SpellEnum.ANCIENT_TEMPER}, 1000),
    EXPLOSION(new ItemStack(Material.TNT), new SpellEnum[]{SpellEnum.FIERY_MISSILE, SpellEnum.LANDMINES, SpellEnum.BLAST_OFF, SpellEnum.FLYING_BLITZ, SpellEnum.BOMB_HEAD}, 1000);

    private final ItemStack displayItem;
    private final List<SpellEnum> spells;
    private final int price;


    public boolean playerOwns(@NotNull PlayerSessionData sessionData) {
        return true;//TODO sessionData.getSpellsOwned(this) > 0;
    }

    public boolean playerCanBuy(@NotNull PlayerSessionData sessionData) {
        return sessionData.getGems().getCurrency() >= price;
    }

    public boolean playerOwnsSpell(@NotNull PlayerSessionData sessionData, int nth) {
        return true;//TODO sessionData.getSpellsOwned(this) >= nth+1;
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
