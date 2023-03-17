package me.chriss99.spellbend.harddata;

import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.util.ItemBuilder;
import me.chriss99.spellbend.util.PersistentData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public enum SpellEnum {
    MAGMA_BURST(Material.GOLDEN_HOE, "Magma Burst", "red", "MULTI_PROJECTILE", 0,
            "<gold>User casts a <yellow>rapid burst",
            "<yellow>of magma <gold>that deals <yellow>low",
            "<yellow>damage."),
    EMBER_BLAST(Material.GOLDEN_HORSE_ARMOR, "Ember Blast", "red", "BLAST", 125,
            "<gold>User casts a <yellow>slow-moving",
            "<yellow>fireball <gold>that deals <yellow>high",
            "<yellow>damage <gold>in its radius."),
    BLAZING_SPIN(Material.GOLDEN_SWORD, "Blazing Spin", "red", "SHIELD", 150,
            "<gold>User raises a <yellow>fiery column",
            "<gold>at their cursor, dealing <yellow>medium",
            "<yellow>damage <gold>and <yellow>stunning <gold>enemies."),
    SCORCHING_COLUMN(Material.GOLDEN_SHOVEL, "Scorching Column", "red", "AREA_OF_EFFECT", 175,
            "<gold>User <yellow>spins on their axis <gold>and",
            "<gold>deals <yellow>high damage",
            "<gold>in their radius."),
    FIERY_RAGE(Material.CAMPFIRE, "Fiery Rage", "red", "AURA",200,
            "<gold>User <yellow>propels themselves",
            "<gold>into the air and harnesses",
            "<yellow>damage boost <gold>for all",
            "<yellow>spells<gold>, alongside a small",
            "<yellow>speed boost<gold>."),
    HYDRO_BLAST(Material.DIAMOND_HORSE_ARMOR, "Hydro Blast", "blue", "BLAST", 0,
            "<gold>User fires a <yellow>brisk water",
            "<yellow>pulse <gold>that deals <yellow>medium",
            "<yellow>damage <gold>and <yellow>briefly stuns",
            "<yellow>enemies<gold>."),
    WATER_SPRAY(Material.DIAMOND_HOE, "Water Spray", "blue", "MULTI_PROJECTILE", 125,
            "<gold>User casts a <yellow>fast stream",
            "<yellow>of water <gold>that deals <yellow>low",
            "<yellow>damage<gold>."),
    WATER_TORRENT(Material.DIAMOND_SHOVEL, "Water Torrent", "blue", "AREA_OF_EFFECT", 150,
            "<gold>User summons a <yellow>water",
            "<yellow>tornado <gold>around them,",
            "<yellow>sweeping attackers up <gold>and",
            "<gold>dealing <yellow>medium damage."),
    RISING_TIDE(Material.COD, "Rising Tide", "blue", "TRANSPORT", 175,
            "<gold>User enters a <yellow>large bubble",
            "<gold>that <yellow>reduces damage <gold>taken",
            "<gold>for a small period."),
    SEA_SHIELD(Material.HEART_OF_THE_SEA, "Sea Shield", "blue", "SHIELD", 200,
            "<gold>User summons a <yellow>shoal of fish",
            "<gold>that <yellow>moves the player",
            "<gold>and <yellow>flings nearby foes<gold>."),
    VERDANT_SPORES(Material.LEATHER_HORSE_ARMOR, "Verdant Spores", "green", "BLAST", 0,
            "<gold>User fires <yellow>fast-moving",
            "<yellow>orbs <gold>that <yellow>multiply <gold>as they",
            "<gold>travel, capable of <yellow>high damage."),
    POISON_DARTS(Material.WOODEN_HOE, "Poison Darts", "green", "MULTI_PROJECTILE", 125,
            "<gold>User casts a <yellow>plethora of",
            "<yellow>poisonous darts <gold>that inflict",
            "<yellow>low damage<gold>."),
    AUTUMN_WINDS(Material.WOODEN_SHOVEL, "Autumn Winds", "green", "AREA_OF_EFFECT", 150,
            "<gold>User casts a <yellow>leafy tornado",
            "<gold>that <yellow>hurls attackers up",
            "<gold>and deals <yellow>medium damage<gold>."),
    VINE_GRAB(Material.JUNGLE_SAPLING, "Vine Grab", "green", "TRANSPORT", 175,
            "<gold>User shoots a <yellow>long-sprawling vine",
            "<gold>that <yellow>draws opponents towards",
            "<gold>the user, dealing <yellow>low damage<gold>."),
    NATURES_AEGIS(Material.LIME_SHULKER_BOX, "Natures Aegis", "green", "SHIELD", 200,
            "<gold>User enters a <yellow>thorny shield",
            "<gold>that <yellow>deflects oncoming",
            "<yellow>foes<gold>, dealing <yellow>low damage."),
    SERRATED_EARTH(Material.MOSSY_COBBLESTONE_WALL, "Serrated Earth", "dark_green", "BLAST", 0,
            "<gold>User raises <yellow>medium-damage",
            "<yellow>spikes <gold>from the earth in",
            "<gold>the direction of their cursor",
            "<gold>that inflict a <yellow>short stun."),
    LANDSLIDE(Material.COARSE_DIRT, "Landslide", "dark_green", "MULTI_PROJECTILE", 200,
            "<gold>User hurls <yellow>medium-damage",
            "<yellow>dirt <gold>from behind them,",
            "<gold>trampling unsuspecting foes."),
    BURROW(Material.STONE_PICKAXE, "Burrow", "dark_green", "TRANSPORT", 300,
            "<gold>User <yellow>burrows underground",
            "<gold>and <yellow>sprouts up at will<gold>,",
            "<gold>dealing <yellow>low damage<gold>."),
    SEISMIC_SMASH(Material.STONE_AXE, "Seismic Smash", "dark_green", "CONTACT", 400,
            "<gold>User <yellow>pummels nearby foes",
            "<gold>with a <yellow>reinforced fist<gold>,",
            "<gold>dealing <yellow>high damage <gold>and",
            "<yellow>strong knockback<gold>."),
    ROCK_BODY(Material.NETHERITE_CHESTPLATE, "Rock Body", "dark_green", "AURA", 500,
            "<gold>User becomes <yellow>geologically",
            "<yellow>reinforced <gold>and <yellow>reduces damage",
            "<yellow>intake<gold>, sacrificing speed."),
    LIGHTNING_BOLT(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, "Lightning Bolt", "aqua", "BLAST", 0,
            "<gold>User <yellow>strikes lightning",
            "<gold>through a <yellow>lightspeed blast<gold>,",
            "<gold>dealing <yellow>medium damage<gold>."),
    FLASH(Material.TIPPED_ARROW, "Flash", "aqua", "TRANSPORT", 200,
            "<gold>User <yellow>warps a short",
            "<yellow>distance <gold>to evade or",
            "<gold>approach their target."),
    SEISMIC_SHOCK(Material.DIAMOND_SHOVEL, "Seismic Shock", "aqua", "AREA_OF_EFFECT", 300,
            "<gold>User emits <yellow>shockwaves <gold>that",
            "<gold>inflict <yellow>medium damage <gold>and",
            "<yellow>stun <gold>nearby foes."),
    LIGHTNING_CHAIN(Material.DIAMOND_HOE, "Lightning Chain", "aqua", "MULTI_PROJECTILE", 400,
            "<gold>User creates a <yellow>row of",
            "<yellow>lightning <gold>that deals <yellow>medium",
            "<yellow>damage<gold>."),
    GALVANISE(Material.DIAMOND_CHESTPLATE, "Galvanise", "aqua", "AURA", 500,
            "<gold>User becomes <yellow>charged",
            "<yellow>with electricity <gold>and <yellow>shocks",
            "<gold>nearby enemies."),
    TWISTED_FLURRY(Material.IRON_HOE, "Twisted Flurry", "white", "MULTI_PROJECTILE", 0,
            "<gold>User hurls a spiral of",
            "<yellow>snowballs <gold>that deal <yellow>low",
            "<yellow>damage<gold>."),
    FROSTBITE(Material.IRON_AXE, "Frostbite", "white", "CONTACT", 200,
            "<gold>User <yellow>encases <gold>a nearby",
            "<gold>enemy in <yellow>ice<gold>, dealing",
            "<yellow>low damage."),
    WINTERS_HEAVE(Material.WHITE_CONCRETE_POWDER, "Winters Heave", "white", "BLAST", 300,
            "<gold>User tosses a <yellow>giant",
            "<yellow>snowball <gold>that <yellow>freezes",
            "<yellow>nearby foes<gold>, dealing",
            "<yellow>low damage<gold>."),
    AVALANCHE(Material.IRON_SHOVEL, "Avalanche", "white", "AREA_OF_EFFECT", 400,
            "<gold>User <yellow>crushes <gold>foes at",
            "<gold>their cursor with <yellow>ice<gold>,",
            "<gold>dealing <yellow>medium damage<gold>."),
    GLACIAL_ARMAMENT(Material.PACKED_ICE, "Glacial Armament", "white", "SHIELD", 500,
            "<gold>User surrounds themself",
            "<gold>with <yellow>blocks of ice<gold>,",
            "<yellow>absorbing damage <gold>and",
            "<yellow>shattering <gold>on enemies."),
    METEOR_BELT(Material.STONE_HOE, "Meteor Belt", "gray", "MULTI_PROJECTILE", 0,
            "<gold>User charges a <yellow>belt",
            "<yellow>of meteors <gold>that strike",
            "<gold>from the sky, dealing",
            "<yellow>low damage<gold>."),
    BLACK_HOLE(Material.DRAGON_EGG, "Black Hole", "gray", "BLAST", 400,
            "<gold>User creates a <yellow>stellar",
            "<yellow>vacuum <gold>that <yellow>attracts players<gold>,",
            "<gold>exploding and dealing",
            "<yellow>medium damage<gold>."),
    STELLAR_SLAM(Material.QUARTZ, "Stellar Slam", "gray", "TRANSPORT", 600,
            "<gold>User <yellow>rockets <gold>into the sky",
            "<gold>and <yellow>crashes <gold>at their",
            "<gold>cursor, dealing <yellow>medium",
            "<yellow>damage<gold>."),
    AETHERS_WRATH(Material.STONE_SHOVEL, "Aethers Wrath", "gray", "AREA_OF_EFFECT", 800,
            "<gold>User summons three giant",
            "<yellow>asteroids <gold>at their cursor,",
            "<gold>dealing <yellow>medium damage<gold>."),
    COSMIC_SMASH(Material.STONE_AXE, "Cosmic Smash", "gray", "CONTACT", 1000,
            "<gold>User <yellow>charges towards their foe",
            "<gold>and <yellow>pounds <gold>them into",
            "<gold>the surface ahead, dealing",
            "<yellow>high damage<gold>."),
    SCATTER_BONES(Material.BONE, "Scatter Bones", "dark_purple", "MULTI_PROJECTILE", 0,
            "<gold>User hurls <yellow>rows <gold>of",
            "<yellow>explosive bones <gold>that deal",
            "<yellow>high damage<gold>."),
    SOUL_DRAIN(Material.FLINT_AND_STEEL, "Soul Drain", "dark_purple", "CONTACT", 400,
            "<gold>User <yellow>bites <gold>their victim,",
            "<gold>dealing <yellow>medium damage <gold>and",
            "<yellow>leeching health <gold>from lost blood."),
    SEEKING_SKULL(Material.PLAYER_HEAD, "Seeking Skull", "dark_purple", "BLAST", 600, //TODO player head needs texture
            "<gold>User casts a <yellow>homing",
            "<yellow>skull <gold>that follows the",
            "<yellow>player's direction<gold>,",
            "<gold>dealing <yellow>high damage<gold>."),
    GASHING_FOSSILS(Material.BONE_BLOCK, "Gashing Fossils", "dark_purple", "AREA_OF_EFFECT", 800,
            "<gold>User pulls the <yellow>bones",
            "<gold>of ancient <yellow>fossils <gold>from",
            "<gold>the earth, dealing <yellow>high damage",
            "<gold>and <yellow>prolonged stuns<gold>."),
    PHANTOMS_CURSE(Material.ELYTRA, "Phantoms Curse", "dark_purple", "TRANSPORT", 1000,
            "<gold>User morphs into a",
            "<yellow>flying phantom<gold>, dealing",
            "<yellow>medium damage <gold>to those",
            "<gold>around themself."),
    DARTS_OF_TIME(Material.SPECTRAL_ARROW, "Darts Of Time", "yellow", "MULTI_PROJECTILE", 0,
            "<gold>User charges a <yellow>row of",
            "<yellow>lightspeed bullets <gold>that",
            "<yellow>shoot on left-click<gold>,",
            "<gold>dealing <yellow>medium damage<gold>."),
    ESCAPE_THROUGH_TIME(Material.ARMOR_STAND ,"Escape through Time", "yellow", "TRANSPORT", 400,
            "<gold>User <yellow>marks a location <gold>in",
            "<gold>time, which can be <yellow>warped",
            "<yellow>to <gold>on right-click."),
    DEATHLY_HOUR(Material.CLOCK, "Deathly Hour", "yellow", "AREA_OF_EFFECT", 600,
            "<gold>User unearths a <yellow>giant clock",
            "<gold>at their cursor, dealing",
            "<yellow>medium damage <gold>and <yellow>slowing",
            "<gold>nearby foes."),
    CHRONOPUNCH(Material.GOLDEN_AXE, "Chronopunch", "yellow", "CONTACT", 800,
            "<gold>User <yellow>grabs the facing foe",
            "<gold>and <yellow>tilts on their axis<gold>,",
            "<gold>releasing at will and",
            "<gold>dealing <yellow>high damage <gold>and <yellow>knockback<gold>."),
    TEMPORAL_ILLUSION(Material.TOTEM_OF_UNDYING, "Temporal Illusion", "yellow", "AURA", 1000,
            "<gold>User summons a <yellow>clone",
            "<gold>beside them that <yellow>augments",
            "<yellow>damage output<gold>, <yellow>slowing",
            "<gold>damaged players."),
    CATAPULT(Material.ANVIL, "Catapult", "#808080", "BLAST", 0,
            "<gold>User charges and hurls",
            "<gold>a <yellow>flying anvil<gold>, dealing",
            "<yellow>high damage <gold>and",
            "<yellow>knockback<gold>."),
    ALLOYED_BARRIER(Material.SPRUCE_DOOR, "Alloyed Barrier", "#808080", "SHIELD", 600,
            "<gold>User forms a <yellow>row of shields",
            "<gold>that <yellow>block projectiles <gold>and",
            "<gold>deal <yellow>low damage <gold>to",
            "<gold>oncoming foes."),
    METARANG(Material.IRON_TRAPDOOR, "Metarang", "#808080", "MULTI_PROJECTILE", 900,
            "<gold>User flings a <yellow>trio of iron",
            "<yellow>boomerangs <gold>that deal <yellow>medium",
            "<yellow>damage <gold>and <yellow>return",
            "<yellow>to the caster<gold>."),
    RAZOR_SPIN(Material.IRON_SWORD, "Razor Spin", "#808080", "TRANSPORT", 1200,
            "<gold>User <yellow>dashes <gold>into the air,",
            "<gold>dealing <yellow>medium damage <gold>and",
            "<yellow>knockback <gold>to nearby foes."),
    ANCIENT_TEMPER(Material.IRON_CHESTPLATE, "Ancient Temper", "#808080", "AURA", 1500,
            "<gold>User rises <yellow>metal <gold>from beneath",
            "<gold>them and <yellow>transforms <gold>into an",
            "<yellow>iron golem<gold>, increasing <yellow>speed",
            "<gold>and <yellow>flinging opponents<gold>."),
    FIERY_MISSILE(Material.TNT, "Fiery Missile", "gold", "BLAST", 0,
            "<gold>User charges a <yellow>missile",
            "<gold>that <yellow>increases in damage <gold>and",
            "<yellow>speed <gold>as it travels, dealing",
            "<yellow>medium damage<gold>."),
    LANDMINES(Material.ACACIA_PRESSURE_PLATE, "Landmines", "gold", "MULTI_PROJECTILE", 600,
            "<gold>User plants <yellow>pressure plates",
            "<gold>in the ground, dealing <yellow>low",
            "<yellow>damage <gold>to oblivious",
            "<gold>bypassers."),
    BLAST_OFF(Material.PISTON, "Blast off", "gold", "AREA_OF_EFFECT", 900,
            "<gold>User <yellow>soars into the air",
            "<gold>out of combat, <yellow>flinging nearby",
            "<yellow>enemies <gold>and dealing <yellow>medium",
            "<yellow>damage<gold>."),
    FLYING_BLITZ(Material.TNT_MINECART, "Flying Blitz", "gold", "TRANSPORT", 1200,
            "<gold>User hurls themselves into",
            "<gold>a <yellow>giant plane<gold>, releasing",
            "<yellow>medium damage bombs <gold>on",
            "<gold>groundlings below."),
    BOMB_HEAD(Material.GOLDEN_PICKAXE, "Bomb head", "gold", "CONTACT", 1500,
            "<gold>User places a <yellow>ticking TNT",
            "<gold>on an opponent's <yellow>head<gold>, dealing",
            "<yellow>high damage <gold>and <yellow>flaming",
            "<gold>nearby players.");
    
    private final ItemStack displayItem;
    private final ItemStack useItem;
    private final int price;


    public boolean playerCanBuy(@NotNull PlayerSessionData sessionData) {
        return sessionData.getGold().getCurrency() >= price;
    }


    SpellEnum(@NotNull Material material, @NotNull String name, @NotNull String color, @NotNull String spellType, int price, @NotNull String... miniMessageExplanation) {
        String miniMessageName = "<" + color + "><bold>" + name;
        LinkedList<String> miniMessageLore = new LinkedList<>(List.of("<dark_gray>----------------"));
        miniMessageLore.addAll(List.of(miniMessageExplanation));

        displayItem = new ItemBuilder(material)
                .setMiniMessageDisplayName(miniMessageName)
                .setMiniMessageLore(miniMessageLore)
                .setCustomModelData(1)
                .hideAllFlags()
                .build();
        useItem = new ItemBuilder(material)
                .setMiniMessageDisplayName(miniMessageName)
                .setMiniMessageLore(miniMessageLore)
                .setCustomModelData(1)
                .hideAllFlags()
                .addPersistentData(
                        new PersistentData<>(PersistentDataKeys.spellNameKey, PersistentDataType.STRING, name()),
                        new PersistentData<>(PersistentDataKeys.spellTypeKey, PersistentDataType.STRING, spellType))
                .build();
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
