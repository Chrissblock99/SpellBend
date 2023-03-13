package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.Item;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum SpellEnum {
    MAGMA_BURST(Material.GOLDEN_HOE, "Magma Burst", "red", "MULTI_PROJECTILE", 0),
    EMBER_BLAST(Material.GOLDEN_HORSE_ARMOR, "Ember Blast", "red", "BLAST", 125),
    BLAZING_SPIN(Material.GOLDEN_SWORD, "Blazing Spin", "red", "SHIELD", 150),
    SCORCHING_COLUMN(Material.GOLDEN_SHOVEL, "Scorching Column", "red", "AREA_OF_EFFECT", 175),
    FIERY_RAGE(Material.CAMPFIRE, "Fiery Rage", "red", "AURA",200),
    HYDRO_BLAST(Material.DIAMOND_HORSE_ARMOR, "Hydro Blast", "blue", "BLAST", 0),
    WATER_SPRAY(Material.DIAMOND_HOE, "Water Spray", "blue", "MULTI_PROJECTILE", 125),
    WATER_TORRENT(Material.DIAMOND_SHOVEL, "Water Torrent", "blue", "AREA_OF_EFFECT", 150),
    RISING_TIDE(Material.COD, "Rising Tide", "blue", "TRANSPORT", 175),
    SEA_SHIELD(Material.HEART_OF_THE_SEA, "Sea Shield", "blue", "SHIELD", 200),
    VERDANT_SPORES(Material.LEATHER_HORSE_ARMOR, "Verdant Spores", "green", "BLAST", 0),
    POISON_DARTS(Material.WOODEN_HOE, "Poison Darts", "green", "MULTI_PROJECTILE", 125),
    AUTUMN_WINDS(Material.WOODEN_SHOVEL, "Autumn Winds", "green", "AREA_OF_EFFECT", 150),
    VINE_GRAB(Material.JUNGLE_SAPLING, "Vine Grab", "green", "TRANSPORT", 175),
    NATURES_AEGIS(Material.LIME_SHULKER_BOX, "Natures Aegis", "green", "SHIELD", 200),
    SERRATED_EARTH(Material.MOSSY_COBBLESTONE_WALL, "Serrated Earth", "dark_green", "BLAST", 0),
    LANDSLIDE(Material.COARSE_DIRT, "Landslide", "dark_green", "MULTI_PROJECTILE", 200),
    BURROW(Material.STONE_PICKAXE, "Burrow", "dark_green", "TRANSPORT", 300),
    SEISMIC_SMASH(Material.STONE_AXE, "Seismic Smash", "dark_green", "CONTACT", 400),
    ROCK_BODY(Material.NETHERITE_CHESTPLATE, "Rock Body", "dark_green", "AURA", 500),
    LIGHTNING_BOLT(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, "Lightning Bolt", "aqua", "BLAST", 0),
    FLASH(Material.TIPPED_ARROW, "Flash", "aqua", "TRANSPORT", 200),
    SEISMIC_SHOCK(Material.DIAMOND_SHOVEL, "Seismic Shock", "aqua", "AREA_OF_EFFECT", 300),
    LIGHTNING_CHAIN(Material.DIAMOND_HOE, "Lightning Chain", "aqua", "MULTI_PROJECTILE", 400),
    GALVANISE(Material.DIAMOND_CHESTPLATE, "Galvanise", "aqua", "AURA", 500),
    TWISTED_FLURRY(Material.IRON_HOE, "Twisted Flurry", "white", "MULTI_PROJECTILE", 0),
    FROSTBITE(Material.IRON_AXE, "Frostbite", "white", "CONTACT", 200),
    WINTERS_HEAVE(Material.WHITE_CONCRETE_POWDER, "Winters Heave", "white", "BLAST", 300),
    AVALANCHE(Material.IRON_SHOVEL, "Avalanche", "white", "AREA_OF_EFFECT", 400),
    GLACIAL_ARMAMENT(Material.PACKED_ICE, "Glacial Armament", "white", "SHIELD", 500),
    METEOR_BELT(Material.STONE_HOE, "Meteor Belt", "gray", "MULTI_PROJECTILE", 0),
    BLACK_HOLE(Material.DRAGON_EGG, "Black Hole", "gray", "BLAST", 400),
    STELLAR_SLAM(Material.QUARTZ, "Stellar Slam", "gray", "TRANSPORT", 600),
    AETHERS_WRATH(Material.STONE_SHOVEL, "Aethers Wrath", "gray", "AREA_OF_EFFECT", 800),
    COSMIC_SMASH(Material.STONE_AXE, "Cosmic Smash", "gray", "CONTACT", 1000),
    SCATTER_BONES(Material.BONE, "Scatter Bones", "dark_purple", "MULTI_PROJECTILE", 0),
    SOUL_DRAIN(Material.FLINT_AND_STEEL, "Soul Drain", "dark_purple", "CONTACT", 400),
    SEEKING_SKULL(Material.PLAYER_HEAD, "Seeking Skull", "dark_purple", "BLAST", 600), //TODO player head needs texture
    GASHING_FOSSILS(Material.BONE_BLOCK, "Gashing Fossils", "dark_purple", "AREA_OF_EFFECT", 800),
    PHANTOMS_CURSE(Material.ELYTRA, "Phantoms Curse", "dark_purple", "TRANSPORT", 1000),
    DARTS_OF_TIME(Material.SPECTRAL_ARROW, "Darts Of Time", "yellow", "MULTI_PROJECTILE", 0),
    ESCAPE_THROUGH_TIME(Material.ARMOR_STAND ,"Escape through Time", "yellow", "TRANSPORT", 400),
    DEATHLY_HOUR(Material.CLOCK, "Deathly Hour", "yellow", "AREA_OF_EFFECT", 600),
    CHRONOPUNCH(Material.GOLDEN_AXE, "Chronopunch", "yellow", "CONTACT", 800),
    TEMPORAL_ILLUSION(Material.TOTEM_OF_UNDYING, "Temporal Illusion", "yellow", "AURA", 1000),
    CATAPULT(Material.ANVIL, "Catapult", "dark_gray", "BLAST", 0),
    ALLOYED_BARRIER(Material.SPRUCE_DOOR, "Alloyed Barrier", "dark_gray", "SHIELD", 600),
    METARANG(Material.IRON_TRAPDOOR, "Metarang", "dark_gray", "MULTI_PROJECTILE", 900),
    RAZOR_SPIN(Material.IRON_SWORD, "Razor Spin", "dark_gray", "TRANSPORT", 1200),
    ANCIENT_TEMPER(Material.IRON_CHESTPLATE, "Ancient Temper", "dark_gray", "AURA", 1500),
    FIERY_MISSILE(Material.TNT, "Fiery Missile", "gold", "BLAST", 0),
    LANDMINES(Material.ACACIA_PRESSURE_PLATE, "Landmines", "gold", "MULTI_PROJECTILE", 600),
    BLAST_OFF(Material.PISTON, "Blast off", "gold", "AREA_OF_EFFECT", 900),
    FLYING_BLITZ(Material.TNT_MINECART, "Flying Blitz", "gold", "TRANSPORT", 1200),
    BOMB_HEAD(Material.GOLDEN_PICKAXE, "Bomb head", "gold", "CONTACT", 1500);
    
    private final ItemStack displayItem;
    private final ItemStack useItem;
    private final int price;


    public boolean playerCanBuy(@NotNull PlayerSessionData sessionData) {
        return sessionData.getGold().getCurrency() >= price;
    }


    SpellEnum(@NotNull Material material, @NotNull String name, @NotNull String color, @NotNull String spellType, int price) {
        String miniMessageName = "<" + color + "><bold>" + name;
        displayItem = Item.create(material, miniMessageName, 1);
        useItem = Item.create(material, miniMessageName, 1,
                new NamespacedKey[]{PersistentDataKeys.spellNameKey, PersistentDataKeys.spellTypeKey},
                new String[]{name(), spellType});
        this.price = price;
    }

    SpellEnum(@NotNull ItemStack displayItem, @NotNull ItemStack useItem, int price) {
        this.displayItem = displayItem;
        this.useItem = useItem;
        this.price = price;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public ItemStack getUseItem() {
        return useItem;
    }

    public int getPrice() {
        return price;
    }
}
