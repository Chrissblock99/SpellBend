package me.chriss99.spellbend.harddata;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.chriss99.spellbend.data.PlayerSessionData;
import me.chriss99.spellbend.spells.*;
import me.chriss99.spellbend.util.ItemBuilder;
import me.chriss99.spellbend.util.PersistentData;
import me.chriss99.spellbend.util.TriFunction;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public enum SpellEnum {
    MAGMA_BURST(Material.GOLDEN_HOE, "Magma Burst", "red", "MULTI_PROJECTILE", 0, 20, Test_Spell::new,
            "<gold>User casts a <yellow>rapid burst",
            "<yellow>of magma <gold>that deals <yellow>low",
            "<yellow>damage."),
    EMBER_BLAST(Material.GOLDEN_HORSE_ARMOR, "Ember Blast", "red", "BLAST", 125, 35, Ember_Blast::new,
            "<gold>User casts a <yellow>slow-moving",
            "<yellow>fireball <gold>that deals <yellow>high",
            "<yellow>damage <gold>in its radius."),
    BLAZING_SPIN(Material.GOLDEN_SWORD, "Blazing Spin", "red", "SHIELD", 150, 35, Test_Spell::new,
            "<gold>User raises a <yellow>fiery column",
            "<gold>at their cursor, dealing <yellow>medium",
            "<yellow>damage <gold>and <yellow>stunning <gold>enemies."),
    SCORCHING_COLUMN(Material.GOLDEN_SHOVEL, "Scorching Column", "red", "AREA_OF_EFFECT", 175, 25, Test_Spell::new,
            "<gold>User <yellow>spins on their axis <gold>and",
            "<gold>deals <yellow>high damage",
            "<gold>in their radius."),
    FIERY_RAGE(Material.CAMPFIRE, "Fiery Rage", "red", "AURA",200, 40, Fiery_Rage::new,
            "<gold>User <yellow>propels themselves",
            "<gold>into the air and harnesses",
            "<yellow>damage boost <gold>for all",
            "<yellow>spells<gold>, alongside a small",
            "<yellow>speed boost<gold>."),
    HYDRO_BLAST(Material.DIAMOND_HORSE_ARMOR, "Hydro Blast", "blue", "BLAST", 0, 25, Test_Spell::new,
            "<gold>User fires a <yellow>brisk water",
            "<yellow>pulse <gold>that deals <yellow>medium",
            "<yellow>damage <gold>and <yellow>briefly stuns",
            "<yellow>enemies<gold>."),
    WATER_SPRAY(Material.DIAMOND_HOE, "Water Spray", "blue", "MULTI_PROJECTILE", 125, 25, Test_Spell::new,
            "<gold>User casts a <yellow>fast stream",
            "<yellow>of water <gold>that deals <yellow>low",
            "<yellow>damage<gold>."),
    WATER_TORRENT(Material.DIAMOND_SHOVEL, "Water Torrent", "blue", "AREA_OF_EFFECT", 150, 30, Test_Spell::new,
            "<gold>User summons a <yellow>water",
            "<yellow>tornado <gold>around them,",
            "<yellow>sweeping attackers up <gold>and",
            "<gold>dealing <yellow>medium damage."),
    SEA_SHIELD(Material.HEART_OF_THE_SEA, "Sea Shield", "blue", "SHIELD", 175, 25, Test_Spell::new,
            "<gold>User enters a <yellow>large bubble",
            "<gold>that <yellow>reduces damage <gold>taken",
            "<gold>for a small period."),
    RISING_TIDE(Material.COD, "Rising Tide", "blue", "TRANSPORT", 200, 30, Test_Spell::new,
            "<gold>User summons a <yellow>shoal of fish",
            "<gold>that <yellow>moves the player",
            "<gold>and <yellow>flings nearby foes<gold>."),
    VERDANT_SPORES(Material.LEATHER_HORSE_ARMOR, "Verdant Spores", "green", "BLAST", 0, 30, Test_Spell::new,
            "<gold>User fires <yellow>fast-moving",
            "<yellow>orbs <gold>that <yellow>multiply <gold>as they",
            "<gold>travel, capable of <yellow>high damage."),
    POISON_DARTS(Material.WOODEN_HOE, "Poison Darts", "green", "MULTI_PROJECTILE", 125, 20, Test_Spell::new,
            "<gold>User casts a <yellow>plethora of",
            "<yellow>poisonous darts <gold>that inflict",
            "<yellow>low damage<gold>."),
    AUTUMN_WINDS(Material.WOODEN_SHOVEL, "Autumn Winds", "green", "AREA_OF_EFFECT", 150, 35, Test_Spell::new,
            "<gold>User casts a <yellow>leafy tornado",
            "<gold>that <yellow>hurls attackers up",
            "<gold>and deals <yellow>medium damage<gold>."),
    VINE_GRAB(Material.JUNGLE_SAPLING, "Vine Grab", "green", "TRANSPORT", 175, 20, Test_Spell::new,
            "<gold>User shoots a <yellow>long-sprawling vine",
            "<gold>that <yellow>draws opponents towards",
            "<gold>the user, dealing <yellow>low damage<gold>."),
    NATURES_AEGIS(Material.LIME_SHULKER_BOX, "Natures Aegis", "green", "SHIELD", 200, 30, Test_Spell::new,
            "<gold>User enters a <yellow>thorny shield",
            "<gold>that <yellow>deflects oncoming",
            "<yellow>foes<gold>, dealing <yellow>low damage."),
    SERRATED_EARTH(Material.MOSSY_COBBLESTONE_WALL, "Serrated Earth", "dark_green", "BLAST", 0, 35, Test_Spell::new,
            "<gold>User raises <yellow>medium-damage",
            "<yellow>spikes <gold>from the earth in",
            "<gold>the direction of their cursor",
            "<gold>that inflict a <yellow>short stun."),
    LANDSLIDE(Material.COARSE_DIRT, "Landslide", "dark_green", "MULTI_PROJECTILE", 200, 25, Test_Spell::new,
            "<gold>User hurls <yellow>medium-damage",
            "<yellow>dirt <gold>from behind them,",
            "<gold>trampling unsuspecting foes."),
    BURROW(Material.STONE_PICKAXE, "Burrow", "dark_green", "TRANSPORT", 300, 30, Test_Spell::new,
            "<gold>User <yellow>burrows underground",
            "<gold>and <yellow>sprouts up at will<gold>,",
            "<gold>dealing <yellow>low damage<gold>."),
    SEISMIC_SMASH(Material.STONE_AXE, "Seismic Smash", "dark_green", "CONTACT", 400, 35, Test_Spell::new,
            "<gold>User <yellow>pummels nearby foes",
            "<gold>with a <yellow>reinforced fist<gold>,",
            "<gold>dealing <yellow>high damage <gold>and",
            "<yellow>strong knockback<gold>."),
    ROCK_BODY(Material.NETHERITE_CHESTPLATE, "Rock Body", "dark_green", "AURA", 500, 40, Test_Spell::new,
            "<gold>User becomes <yellow>geologically",
            "<yellow>reinforced <gold>and <yellow>reduces damage",
            "<yellow>intake<gold>, sacrificing speed."),
    LIGHTNING_BOLT(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, "Lightning Bolt", "aqua", "BLAST", 0, 25, Test_Spell::new,
            "<gold>User <yellow>strikes lightning",
            "<gold>through a <yellow>lightspeed blast<gold>,",
            "<gold>dealing <yellow>medium damage<gold>."),
    FLASH(Material.TIPPED_ARROW, "Flash", "aqua", "TRANSPORT", 200, 10, Test_Spell::new, null,
            (item) -> {
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    meta.setColor(Color.fromRGB(126, 178, 202));
                    item.setItemMeta(meta);
                    return item;
                    },
            "<gold>User <yellow>warps a short",
            "<yellow>distance <gold>to evade or",
            "<gold>approach their target."),
    SEISMIC_SHOCK(Material.DIAMOND_SHOVEL, "Seismic Shock", "aqua", "AREA_OF_EFFECT", 300, 35, Seismic_Shock::new,
            Seismic_Shock::validatePlayerState, null,
            "<gold>User emits <yellow>shockwaves <gold>that",
            "<gold>inflict <yellow>medium damage <gold>and",
            "<yellow>stun <gold>nearby foes."),
    LIGHTNING_CHAIN(Material.DIAMOND_HOE, "Lightning Chain", "aqua", "MULTI_PROJECTILE", 400, 25, Test_Spell::new,
            "<gold>User creates a <yellow>row of",
            "<yellow>lightning <gold>that deals <yellow>medium",
            "<yellow>damage<gold>."),
    GALVANISE(Material.DIAMOND_CHESTPLATE, "Galvanise", "aqua", "AURA", 500, 35, Test_Spell::new,
            "<gold>User becomes <yellow>charged",
            "<yellow>with electricity <gold>and <yellow>shocks",
            "<gold>nearby enemies."),
    TWISTED_FLURRY(Material.IRON_HOE, "Twisted Flurry", "white", "MULTI_PROJECTILE", 0, 15, Test_Spell::new,
            "<gold>User hurls a spiral of",
            "<yellow>snowballs <gold>that deal <yellow>low",
            "<yellow>damage<gold>."),
    FROSTBITE(Material.IRON_AXE, "Frostbite", "white", "CONTACT", 200, 25, Test_Spell::new,
            "<gold>User <yellow>encases <gold>a nearby",
            "<gold>enemy in <yellow>ice<gold>, dealing",
            "<yellow>low damage."),
    WINTERS_HEAVE(Material.WHITE_CONCRETE_POWDER, "Winters Heave", "white", "BLAST", 300, 25, Test_Spell::new,
            "<gold>User tosses a <yellow>giant",
            "<yellow>snowball <gold>that <yellow>freezes",
            "<yellow>nearby foes<gold>, dealing",
            "<yellow>low damage<gold>."),
    AVALANCHE(Material.IRON_SHOVEL, "Avalanche", "white", "AREA_OF_EFFECT", 400, 35, Test_Spell::new,
            "<gold>User <yellow>crushes <gold>foes at",
            "<gold>their cursor with <yellow>ice<gold>,",
            "<gold>dealing <yellow>medium damage<gold>."),
    GLACIAL_ARMAMENT(Material.PACKED_ICE, "Glacial Armament", "white", "SHIELD", 500, 25, Test_Spell::new,
            "<gold>User surrounds themself",
            "<gold>with <yellow>blocks of ice<gold>,",
            "<yellow>absorbing damage <gold>and",
            "<yellow>shattering <gold>on enemies."),
    METEOR_BELT(Material.STONE_HOE, "Meteor Belt", "gray", "MULTI_PROJECTILE", 0, 25, Test_Spell::new,
            "<gold>User charges a <yellow>belt",
            "<yellow>of meteors <gold>that strike",
            "<gold>from the sky, dealing",
            "<yellow>low damage<gold>."),
    BLACK_HOLE(Material.DRAGON_EGG, "Black Hole", "gray", "BLAST", 400, 35, Test_Spell::new,
            "<gold>User creates a <yellow>stellar",
            "<yellow>vacuum <gold>that <yellow>attracts players<gold>,",
            "<gold>exploding and dealing",
            "<yellow>medium damage<gold>."),
    STELLAR_SLAM(Material.QUARTZ, "Stellar Slam", "gray", "TRANSPORT", 600, 25, Test_Spell::new,
            "<gold>User <yellow>rockets <gold>into the sky",
            "<gold>and <yellow>crashes <gold>at their",
            "<gold>cursor, dealing <yellow>medium",
            "<yellow>damage<gold>."),
    AETHERS_WRATH(Material.STONE_SHOVEL, "Aethers Wrath", "gray", "AREA_OF_EFFECT", 800, 30, Test_Spell::new,
            "<gold>User summons three giant",
            "<yellow>asteroids <gold>at their cursor,",
            "<gold>dealing <yellow>medium damage<gold>."),
    COSMIC_SMASH(Material.STONE_AXE, "Cosmic Smash", "gray", "CONTACT", 1000, 35, Test_Spell::new,
            "<gold>User <yellow>charges towards their foe",
            "<gold>and <yellow>pounds <gold>them into",
            "<gold>the surface ahead, dealing",
            "<yellow>high damage<gold>."),
    SCATTER_BONES(Material.BONE, "Scatter Bones", "dark_purple", "MULTI_PROJECTILE", 0, 30, Test_Spell::new,
            "<gold>User hurls <yellow>rows <gold>of",
            "<yellow>explosive bones <gold>that deal",
            "<yellow>high damage<gold>."),
    SOUL_DRAIN(Material.FLINT_AND_STEEL, "Soul Drain", "dark_purple", "CONTACT", 400, 30, Test_Spell::new,
            "<gold>User <yellow>bites <gold>their victim,",
            "<gold>dealing <yellow>medium damage <gold>and",
            "<yellow>leeching health <gold>from lost blood."),
    SEEKING_SKULL(Material.PLAYER_HEAD, "Seeking Skull", "dark_purple", "BLAST", 600, 30, Test_Spell::new, null,
            (item) -> {
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();

                try {
                    textures.setSkin(new URL("http://textures.minecraft.net/texture/71dd7ee7dc7fbcb5c1aee7a257917f034eeba1e093727d712dab0fc35fb0e38"));
                } catch (MalformedURLException e) {
                    Bukkit.getLogger().info("Seeking skull texture could not be generated!");
                    e.printStackTrace();
                }

                profile.setTextures(textures);
                meta.setPlayerProfile(profile);
                item.setItemMeta(meta);
                return item;
                },
            "<gold>User casts a <yellow>homing",
            "<yellow>skull <gold>that follows the",
            "<yellow>player's direction<gold>,",
            "<gold>dealing <yellow>high damage<gold>."),
    GASHING_FOSSILS(Material.BONE_BLOCK, "Gashing Fossils", "dark_purple", "AREA_OF_EFFECT", 800, 35, Test_Spell::new,
            "<gold>User pulls the <yellow>bones",
            "<gold>of ancient <yellow>fossils <gold>from",
            "<gold>the earth, dealing <yellow>high damage",
            "<gold>and <yellow>prolonged stuns<gold>."),
    PHANTOMS_CURSE(Material.ELYTRA, "Phantoms Curse", "dark_purple", "TRANSPORT", 1000, 40, Test_Spell::new,
            "<gold>User morphs into a",
            "<yellow>flying phantom<gold>, dealing",
            "<yellow>medium damage <gold>to those",
            "<gold>around themself."),
    DARTS_OF_TIME(Material.SPECTRAL_ARROW, "Darts Of Time", "yellow", "MULTI_PROJECTILE", 0, 25, Test_Spell::new,
            "<gold>User charges a <yellow>row of",
            "<yellow>lightspeed bullets <gold>that",
            "<yellow>shoot on left-click<gold>,",
            "<gold>dealing <yellow>medium damage<gold>."),
    ESCAPE_THROUGH_TIME(Material.ARMOR_STAND ,"Escape through Time", "yellow", "TRANSPORT", 400, 25, Escape_Through_Time::new,
            "<gold>User <yellow>marks a location <gold>in",
            "<gold>time, which can be <yellow>warped",
            "<yellow>to <gold>on right-click."),
    DEATHLY_HOUR(Material.CLOCK, "Deathly Hour", "yellow", "AREA_OF_EFFECT", 600, 30, Test_Spell::new,
            "<gold>User unearths a <yellow>giant clock",
            "<gold>at their cursor, dealing",
            "<yellow>medium damage <gold>and <yellow>slowing",
            "<gold>nearby foes."),
    CHRONOPUNCH(Material.GOLDEN_AXE, "Chronopunch", "yellow", "CONTACT", 800, 30, Test_Spell::new,
            "<gold>User <yellow>grabs the facing foe",
            "<gold>and <yellow>tilts on their axis<gold>,",
            "<gold>releasing at will and",
            "<gold>dealing <yellow>high damage <gold>and <yellow>knockback<gold>."),
    TEMPORAL_ILLUSION(Material.TOTEM_OF_UNDYING, "Temporal Illusion", "yellow", "AURA", 1000, 35, Test_Spell::new,
            "<gold>User summons a <yellow>clone",
            "<gold>beside them that <yellow>augments",
            "<yellow>damage output<gold>, <yellow>slowing",
            "<gold>damaged players."),
    CATAPULT(Material.ANVIL, "Catapult", "#808080", "BLAST", 0, 35, Test_Spell::new,
            "<gold>User charges and hurls",
            "<gold>a <yellow>flying anvil<gold>, dealing",
            "<yellow>high damage <gold>and",
            "<yellow>knockback<gold>."),
    ALLOYED_BARRIER(Material.SPRUCE_DOOR, "Alloyed Barrier", "#808080", "SHIELD", 600, 25, Test_Spell::new,
            "<gold>User forms a <yellow>row of shields",
            "<gold>that <yellow>block projectiles <gold>and",
            "<gold>deal <yellow>low damage <gold>to",
            "<gold>oncoming foes."),
    METARANG(Material.IRON_TRAPDOOR, "Metarang", "#808080", "MULTI_PROJECTILE", 900, 30, Test_Spell::new,
            "<gold>User flings a <yellow>trio of iron",
            "<yellow>boomerangs <gold>that deal <yellow>medium",
            "<yellow>damage <gold>and <yellow>return",
            "<yellow>to the caster<gold>."),
    RAZOR_SPIN(Material.IRON_SWORD, "Razor Spin", "#808080", "TRANSPORT", 1200, 25, Test_Spell::new,
            "<gold>User <yellow>dashes <gold>into the air,",
            "<gold>dealing <yellow>medium damage <gold>and",
            "<yellow>knockback <gold>to nearby foes."),
    ANCIENT_TEMPER(Material.IRON_CHESTPLATE, "Ancient Temper", "#808080", "AURA", 1500, 40, Test_Spell::new,
            "<gold>User rises <yellow>metal <gold>from beneath",
            "<gold>them and <yellow>transforms <gold>into an",
            "<yellow>iron golem<gold>, increasing <yellow>speed",
            "<gold>and <yellow>flinging opponents<gold>."),
    FIERY_MISSILE(Material.TNT, "Fiery Missile", "gold", "BLAST", 0, 35, Test_Spell::new,
            "<gold>User charges a <yellow>missile",
            "<gold>that <yellow>increases in damage <gold>and",
            "<yellow>speed <gold>as it travels, dealing",
            "<yellow>medium damage<gold>."),
    LANDMINES(Material.ACACIA_PRESSURE_PLATE, "Landmines", "gold", "MULTI_PROJECTILE", 600, 30, Test_Spell::new,
            "<gold>User plants <yellow>pressure plates",
            "<gold>in the ground, dealing <yellow>low",
            "<yellow>damage <gold>to oblivious",
            "<gold>bypassers."),
    BLAST_OFF(Material.PISTON, "Blast off", "gold", "AREA_OF_EFFECT", 900, 30, Test_Spell::new,
            Seismic_Shock::validatePlayerState, null,
            "<gold>User <yellow>soars into the air",
            "<gold>out of combat, <yellow>flinging nearby",
            "<yellow>enemies <gold>and dealing <yellow>medium",
            "<yellow>damage<gold>."),
    FLYING_BLITZ(Material.TNT_MINECART, "Flying Blitz", "gold", "TRANSPORT", 1200, 30, Test_Spell::new,
            "<gold>User hurls themselves into",
            "<gold>a <yellow>giant plane<gold>, releasing",
            "<yellow>medium damage bombs <gold>on",
            "<gold>groundlings below."),
    BOMB_HEAD(Material.GOLDEN_PICKAXE, "Bomb head", "gold", "CONTACT", 1500, 30, Test_Spell::new,
            "<gold>User places a <yellow>ticking TNT",
            "<gold>on an opponent's <yellow>head<gold>, dealing",
            "<yellow>high damage <gold>and <yellow>flaming",
            "<gold>nearby players.");

    private static final List<String> enumStringValues = Arrays.stream(SpellEnum.values()).map(Enum::toString).toList();

    public static boolean spellExists(@NotNull String spellName) {
        return enumStringValues.contains(spellName);
    }

    public static @Nullable SpellEnum spellEnumOf(@NotNull String spellName) {
        if (spellExists(spellName))
            return valueOf(spellName);
        return null;
    }


    private final ItemStack displayItem;
    private final ItemStack useItem;
    private final int price;

    private final int manaCost;
    private final String spellType;
    private final TriFunction<@NotNull Player, @NotNull String, @NotNull ItemStack, @NotNull Spell> spellBuilder;
    private final @Nullable Function<@NotNull Player, @Nullable Component> playerStateValidator;


    public boolean playerCanBuy(@NotNull PlayerSessionData sessionData) {
        return sessionData.getGold().getCurrency() >= price;
    }


    SpellEnum(@NotNull Material material, @NotNull String name, @NotNull String color, @NotNull String spellType, int price, int manaCost,
              @NotNull TriFunction<@NotNull Player, @NotNull String, @NotNull ItemStack, @NotNull Spell> spellBuilder, @NotNull String... miniMessageExplanation) {
        this(material, name, color, spellType, price, manaCost, spellBuilder, null, null, miniMessageExplanation);
    }

    SpellEnum(@NotNull Material material, @NotNull String name, @NotNull String color, @NotNull String spellType, int price, int manaCost,
              @NotNull TriFunction<@NotNull Player, @NotNull String, @NotNull ItemStack, @NotNull Spell> spellBuilder,
              @Nullable Function<@NotNull Player, @Nullable Component> playerStateValidator,
              @Nullable Function<ItemStack, ItemStack> itemModifier, @NotNull String... miniMessageExplanation) {

        String miniMessageName = "<" + color + "><bold>" + name;
        LinkedList<String> miniMessageLore = new LinkedList<>(List.of("<dark_gray>----------------"));
        miniMessageLore.addAll(List.of(miniMessageExplanation));
        miniMessageLore.add("<dark_gray>----------------");

        ItemStack item = new ItemBuilder(material)
                .setMiniMessageDisplayName(miniMessageName)
                .setMiniMessageLore(miniMessageLore)
                .setCustomModelData(1)
                .hideAllFlags()
                .build();
        if (itemModifier != null)
            item = itemModifier.apply(item);

        displayItem = item;
        useItem = new ItemBuilder(displayItem.clone())
                .addPersistentData(
                        new PersistentData<>(PersistentDataKeys.SPELL_NAME_KEY, PersistentDataType.STRING, name()),
                        new PersistentData<>(PersistentDataKeys.SPELL_TYPE_KEY, PersistentDataType.STRING, spellType),
                        new PersistentData<>(PersistentDataKeys.MANA_COST_KEY, PersistentDataType.INTEGER, manaCost))
                .build();
        this.price = price;

        this.manaCost = manaCost;
        this.spellType = spellType;
        this.spellBuilder = spellBuilder;
        this.playerStateValidator = playerStateValidator;
    }

    SpellEnum(@NotNull ItemStack displayItem, @NotNull ItemStack useItem, int price, int manaCost, @NotNull String spellType,
              @NotNull TriFunction<@NotNull Player, @NotNull String, @NotNull ItemStack, @NotNull Spell> spellBuilder,
              @Nullable Function<@NotNull Player, @Nullable Component> playerStateValidator) {
        this.displayItem = displayItem;
        this.useItem = useItem;
        this.price = price;

        this.manaCost = manaCost;
        this.spellType = spellType;
        this.spellBuilder = spellBuilder;
        this.playerStateValidator = playerStateValidator;
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

    public int getManaCost() {
        return manaCost;
    }

    public @NotNull String getSpellType() {
        return spellType;
    }

    public @NotNull TriFunction<@NotNull Player, @Nullable String, @NotNull ItemStack, @NotNull Spell> getSpellBuilder() {
        return spellBuilder;
    }

    public @Nullable Function<@NotNull Player, @Nullable Component> getPlayerStateValidator() {
        return playerStateValidator;
    }
}
