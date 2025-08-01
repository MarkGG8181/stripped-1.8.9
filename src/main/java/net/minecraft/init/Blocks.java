package net.minecraft.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDaylightDetector;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSugarcane;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.util.ResourceLocation;

public class Blocks
{
    public static final Block air;
    public static final Block stone;
    public static final BlockGrass grass;
    public static final Block dirt;
    public static final Block cobblestone;
    public static final Block planks;
    public static final Block sapling;
    public static final Block bedrock;
    public static final BlockDynamicLiquid flowing_water;
    public static final BlockStaticLiquid water;
    public static final BlockDynamicLiquid flowing_lava;
    public static final BlockStaticLiquid lava;
    public static final BlockSand sand;
    public static final Block gravel;
    public static final Block gold_ore;
    public static final Block iron_ore;
    public static final Block coal_ore;
    public static final Block log;
    public static final Block log2;
    public static final BlockLeaves leaves;
    public static final BlockLeaves leaves2;
    public static final Block sponge;
    public static final Block glass;
    public static final Block lapis_ore;
    public static final Block lapis_block;
    public static final Block dispenser;
    public static final Block sandstone;
    public static final Block noteblock;
    public static final Block bed;
    public static final Block golden_rail;
    public static final Block detector_rail;
    public static final BlockPistonBase sticky_piston;
    public static final Block web;
    public static final BlockTallGrass tallgrass;
    public static final BlockDeadBush deadbush;
    public static final BlockPistonBase piston;
    public static final BlockPistonExtension piston_head;
    public static final Block wool;
    public static final BlockPistonMoving piston_extension;
    public static final BlockFlower yellow_flower;
    public static final BlockFlower red_flower;
    public static final BlockBush brown_mushroom;
    public static final BlockBush red_mushroom;
    public static final Block gold_block;
    public static final Block iron_block;
    public static final BlockSlab double_stone_slab;
    public static final BlockSlab stone_slab;
    public static final Block brick_block;
    public static final Block tnt;
    public static final Block bookshelf;
    public static final Block mossy_cobblestone;
    public static final Block obsidian;
    public static final Block torch;
    public static final BlockFire fire;
    public static final Block mob_spawner;
    public static final Block oak_stairs;
    public static final BlockChest chest;
    public static final BlockRedstoneWire redstone_wire;
    public static final Block diamond_ore;
    public static final Block diamond_block;
    public static final Block crafting_table;
    public static final Block wheat;
    public static final Block farmland;
    public static final Block furnace;
    public static final Block lit_furnace;
    public static final Block standing_sign;
    public static final Block oak_door;
    public static final Block spruce_door;
    public static final Block birch_door;
    public static final Block jungle_door;
    public static final Block acacia_door;
    public static final Block dark_oak_door;
    public static final Block ladder;
    public static final Block rail;
    public static final Block stone_stairs;
    public static final Block wall_sign;
    public static final Block lever;
    public static final Block stone_pressure_plate;
    public static final Block iron_door;
    public static final Block wooden_pressure_plate;
    public static final Block redstone_ore;
    public static final Block lit_redstone_ore;
    public static final Block unlit_redstone_torch;
    public static final Block redstone_torch;
    public static final Block stone_button;
    public static final Block snow_layer;
    public static final Block ice;
    public static final Block snow;
    public static final BlockCactus cactus;
    public static final Block clay;
    public static final BlockSugarcane sugarcane;
    public static final Block jukebox;
    public static final Block oak_fence;
    public static final Block spruce_fence;
    public static final Block birch_fence;
    public static final Block jungle_fence;
    public static final Block dark_oak_fence;
    public static final Block acacia_fence;
    public static final Block pumpkin;
    public static final Block netherrack;
    public static final Block soul_sand;
    public static final Block glowstone;
    public static final BlockPortal portal;
    public static final Block lit_pumpkin;
    public static final Block cake;
    public static final BlockRedstoneRepeater unpowered_repeater;
    public static final BlockRedstoneRepeater powered_repeater;
    public static final Block trapdoor;
    public static final Block monster_egg;
    public static final Block stonebrick;
    public static final Block brown_mushroom_block;
    public static final Block red_mushroom_block;
    public static final Block iron_bars;
    public static final Block glass_pane;
    public static final Block melon_block;
    public static final Block pumpkin_stem;
    public static final Block melon_stem;
    public static final Block vine;
    public static final Block oak_fence_gate;
    public static final Block spruce_fence_gate;
    public static final Block birch_fence_gate;
    public static final Block jungle_fence_gate;
    public static final Block dark_oak_fence_gate;
    public static final Block acacia_fence_gate;
    public static final Block brick_stairs;
    public static final Block stone_brick_stairs;
    public static final BlockMycelium mycelium;
    public static final Block waterlily;
    public static final Block nether_brick;
    public static final Block nether_brick_fence;
    public static final Block nether_brick_stairs;
    public static final Block nether_wart;
    public static final Block enchanting_table;
    public static final Block brewing_stand;
    public static final BlockCauldron cauldron;
    public static final Block end_portal;
    public static final Block end_portal_frame;
    public static final Block end_stone;
    public static final Block dragon_egg;
    public static final Block redstone_lamp;
    public static final Block lit_redstone_lamp;
    public static final BlockSlab double_wooden_slab;
    public static final BlockSlab wooden_slab;
    public static final Block cocoa;
    public static final Block sandstone_stairs;
    public static final Block emerald_ore;
    public static final Block ender_chest;
    public static final BlockTripWireHook tripwire_hook;
    public static final Block tripwire;
    public static final Block emerald_block;
    public static final Block spruce_stairs;
    public static final Block birch_stairs;
    public static final Block jungle_stairs;
    public static final Block command_block;
    public static final BlockBeacon beacon;
    public static final Block cobblestone_wall;
    public static final Block flower_pot;
    public static final Block carrots;
    public static final Block potatoes;
    public static final Block wooden_button;
    public static final BlockSkull skull;
    public static final Block anvil;
    public static final Block trapped_chest;
    public static final Block light_weighted_pressure_plate;
    public static final Block heavy_weighted_pressure_plate;
    public static final BlockRedstoneComparator unpowered_comparator;
    public static final BlockRedstoneComparator powered_comparator;
    public static final BlockDaylightDetector daylight_detector;
    public static final BlockDaylightDetector daylight_detector_inverted;
    public static final Block redstone_block;
    public static final Block quartz_ore;
    public static final BlockHopper hopper;
    public static final Block quartz_block;
    public static final Block quartz_stairs;
    public static final Block activator_rail;
    public static final Block dropper;
    public static final Block stained_hardened_clay;
    public static final Block barrier;
    public static final Block iron_trapdoor;
    public static final Block hay_block;
    public static final Block carpet;
    public static final Block hardened_clay;
    public static final Block coal_block;
    public static final Block packed_ice;
    public static final Block acacia_stairs;
    public static final Block dark_oak_stairs;
    public static final Block slime_block;
    public static final BlockDoublePlant double_plant;
    public static final BlockStainedGlass stained_glass;
    public static final BlockStainedGlassPane stained_glass_pane;
    public static final Block prismarine;
    public static final Block sea_lantern;
    public static final Block standing_banner;
    public static final Block wall_banner;
    public static final Block red_sandstone;
    public static final Block red_sandstone_stairs;
    public static final BlockSlab double_stone_slab2;
    public static final BlockSlab stone_slab2;

    /**
     * Returns the Block in the blockRegistry with the specified name.
     *  
     * @param blockName The block's name to register
     */
    private static Block getRegisteredBlock(String blockName)
    {
        return (Block)Block.blockRegistry.getObject(new ResourceLocation(blockName));
    }

    static
    {
        if (!Bootstrap.isRegistered())
        {
            throw new RuntimeException("Accessed Blocks before Bootstrap!");
        }
        else
        {
            air = getRegisteredBlock("air");
            stone = getRegisteredBlock("stone");
            grass = (BlockGrass)getRegisteredBlock("grass");
            dirt = getRegisteredBlock("dirt");
            cobblestone = getRegisteredBlock("cobblestone");
            planks = getRegisteredBlock("planks");
            sapling = getRegisteredBlock("sapling");
            bedrock = getRegisteredBlock("bedrock");
            flowing_water = (BlockDynamicLiquid)getRegisteredBlock("flowing_water");
            water = (BlockStaticLiquid)getRegisteredBlock("water");
            flowing_lava = (BlockDynamicLiquid)getRegisteredBlock("flowing_lava");
            lava = (BlockStaticLiquid)getRegisteredBlock("lava");
            sand = (BlockSand)getRegisteredBlock("sand");
            gravel = getRegisteredBlock("gravel");
            gold_ore = getRegisteredBlock("gold_ore");
            iron_ore = getRegisteredBlock("iron_ore");
            coal_ore = getRegisteredBlock("coal_ore");
            log = getRegisteredBlock("log");
            log2 = getRegisteredBlock("log2");
            leaves = (BlockLeaves)getRegisteredBlock("leaves");
            leaves2 = (BlockLeaves)getRegisteredBlock("leaves2");
            sponge = getRegisteredBlock("sponge");
            glass = getRegisteredBlock("glass");
            lapis_ore = getRegisteredBlock("lapis_ore");
            lapis_block = getRegisteredBlock("lapis_block");
            dispenser = getRegisteredBlock("dispenser");
            sandstone = getRegisteredBlock("sandstone");
            noteblock = getRegisteredBlock("noteblock");
            bed = getRegisteredBlock("bed");
            golden_rail = getRegisteredBlock("golden_rail");
            detector_rail = getRegisteredBlock("detector_rail");
            sticky_piston = (BlockPistonBase)getRegisteredBlock("sticky_piston");
            web = getRegisteredBlock("web");
            tallgrass = (BlockTallGrass)getRegisteredBlock("tallgrass");
            deadbush = (BlockDeadBush)getRegisteredBlock("deadbush");
            piston = (BlockPistonBase)getRegisteredBlock("piston");
            piston_head = (BlockPistonExtension)getRegisteredBlock("piston_head");
            wool = getRegisteredBlock("wool");
            piston_extension = (BlockPistonMoving)getRegisteredBlock("piston_extension");
            yellow_flower = (BlockFlower)getRegisteredBlock("yellow_flower");
            red_flower = (BlockFlower)getRegisteredBlock("red_flower");
            brown_mushroom = (BlockBush)getRegisteredBlock("brown_mushroom");
            red_mushroom = (BlockBush)getRegisteredBlock("red_mushroom");
            gold_block = getRegisteredBlock("gold_block");
            iron_block = getRegisteredBlock("iron_block");
            double_stone_slab = (BlockSlab)getRegisteredBlock("double_stone_slab");
            stone_slab = (BlockSlab)getRegisteredBlock("stone_slab");
            brick_block = getRegisteredBlock("brick_block");
            tnt = getRegisteredBlock("tnt");
            bookshelf = getRegisteredBlock("bookshelf");
            mossy_cobblestone = getRegisteredBlock("mossy_cobblestone");
            obsidian = getRegisteredBlock("obsidian");
            torch = getRegisteredBlock("torch");
            fire = (BlockFire)getRegisteredBlock("fire");
            mob_spawner = getRegisteredBlock("mob_spawner");
            oak_stairs = getRegisteredBlock("oak_stairs");
            chest = (BlockChest)getRegisteredBlock("chest");
            redstone_wire = (BlockRedstoneWire)getRegisteredBlock("redstone_wire");
            diamond_ore = getRegisteredBlock("diamond_ore");
            diamond_block = getRegisteredBlock("diamond_block");
            crafting_table = getRegisteredBlock("crafting_table");
            wheat = getRegisteredBlock("wheat");
            farmland = getRegisteredBlock("farmland");
            furnace = getRegisteredBlock("furnace");
            lit_furnace = getRegisteredBlock("lit_furnace");
            standing_sign = getRegisteredBlock("standing_sign");
            oak_door = getRegisteredBlock("wooden_door");
            spruce_door = getRegisteredBlock("spruce_door");
            birch_door = getRegisteredBlock("birch_door");
            jungle_door = getRegisteredBlock("jungle_door");
            acacia_door = getRegisteredBlock("acacia_door");
            dark_oak_door = getRegisteredBlock("dark_oak_door");
            ladder = getRegisteredBlock("ladder");
            rail = getRegisteredBlock("rail");
            stone_stairs = getRegisteredBlock("stone_stairs");
            wall_sign = getRegisteredBlock("wall_sign");
            lever = getRegisteredBlock("lever");
            stone_pressure_plate = getRegisteredBlock("stone_pressure_plate");
            iron_door = getRegisteredBlock("iron_door");
            wooden_pressure_plate = getRegisteredBlock("wooden_pressure_plate");
            redstone_ore = getRegisteredBlock("redstone_ore");
            lit_redstone_ore = getRegisteredBlock("lit_redstone_ore");
            unlit_redstone_torch = getRegisteredBlock("unlit_redstone_torch");
            redstone_torch = getRegisteredBlock("redstone_torch");
            stone_button = getRegisteredBlock("stone_button");
            snow_layer = getRegisteredBlock("snow_layer");
            ice = getRegisteredBlock("ice");
            snow = getRegisteredBlock("snow");
            cactus = (BlockCactus)getRegisteredBlock("cactus");
            clay = getRegisteredBlock("clay");
            sugarcane = (BlockSugarcane)getRegisteredBlock("sugarcane");
            jukebox = getRegisteredBlock("jukebox");
            oak_fence = getRegisteredBlock("fence");
            spruce_fence = getRegisteredBlock("spruce_fence");
            birch_fence = getRegisteredBlock("birch_fence");
            jungle_fence = getRegisteredBlock("jungle_fence");
            dark_oak_fence = getRegisteredBlock("dark_oak_fence");
            acacia_fence = getRegisteredBlock("acacia_fence");
            pumpkin = getRegisteredBlock("pumpkin");
            netherrack = getRegisteredBlock("netherrack");
            soul_sand = getRegisteredBlock("soul_sand");
            glowstone = getRegisteredBlock("glowstone");
            portal = (BlockPortal)getRegisteredBlock("portal");
            lit_pumpkin = getRegisteredBlock("lit_pumpkin");
            cake = getRegisteredBlock("cake");
            unpowered_repeater = (BlockRedstoneRepeater)getRegisteredBlock("unpowered_repeater");
            powered_repeater = (BlockRedstoneRepeater)getRegisteredBlock("powered_repeater");
            trapdoor = getRegisteredBlock("trapdoor");
            monster_egg = getRegisteredBlock("monster_egg");
            stonebrick = getRegisteredBlock("stonebrick");
            brown_mushroom_block = getRegisteredBlock("brown_mushroom_block");
            red_mushroom_block = getRegisteredBlock("red_mushroom_block");
            iron_bars = getRegisteredBlock("iron_bars");
            glass_pane = getRegisteredBlock("glass_pane");
            melon_block = getRegisteredBlock("melon_block");
            pumpkin_stem = getRegisteredBlock("pumpkin_stem");
            melon_stem = getRegisteredBlock("melon_stem");
            vine = getRegisteredBlock("vine");
            oak_fence_gate = getRegisteredBlock("fence_gate");
            spruce_fence_gate = getRegisteredBlock("spruce_fence_gate");
            birch_fence_gate = getRegisteredBlock("birch_fence_gate");
            jungle_fence_gate = getRegisteredBlock("jungle_fence_gate");
            dark_oak_fence_gate = getRegisteredBlock("dark_oak_fence_gate");
            acacia_fence_gate = getRegisteredBlock("acacia_fence_gate");
            brick_stairs = getRegisteredBlock("brick_stairs");
            stone_brick_stairs = getRegisteredBlock("stone_brick_stairs");
            mycelium = (BlockMycelium)getRegisteredBlock("mycelium");
            waterlily = getRegisteredBlock("waterlily");
            nether_brick = getRegisteredBlock("nether_brick");
            nether_brick_fence = getRegisteredBlock("nether_brick_fence");
            nether_brick_stairs = getRegisteredBlock("nether_brick_stairs");
            nether_wart = getRegisteredBlock("nether_wart");
            enchanting_table = getRegisteredBlock("enchanting_table");
            brewing_stand = getRegisteredBlock("brewing_stand");
            cauldron = (BlockCauldron)getRegisteredBlock("cauldron");
            end_portal = getRegisteredBlock("end_portal");
            end_portal_frame = getRegisteredBlock("end_portal_frame");
            end_stone = getRegisteredBlock("end_stone");
            dragon_egg = getRegisteredBlock("dragon_egg");
            redstone_lamp = getRegisteredBlock("redstone_lamp");
            lit_redstone_lamp = getRegisteredBlock("lit_redstone_lamp");
            double_wooden_slab = (BlockSlab)getRegisteredBlock("double_wooden_slab");
            wooden_slab = (BlockSlab)getRegisteredBlock("wooden_slab");
            cocoa = getRegisteredBlock("cocoa");
            sandstone_stairs = getRegisteredBlock("sandstone_stairs");
            emerald_ore = getRegisteredBlock("emerald_ore");
            ender_chest = getRegisteredBlock("ender_chest");
            tripwire_hook = (BlockTripWireHook)getRegisteredBlock("tripwire_hook");
            tripwire = getRegisteredBlock("tripwire");
            emerald_block = getRegisteredBlock("emerald_block");
            spruce_stairs = getRegisteredBlock("spruce_stairs");
            birch_stairs = getRegisteredBlock("birch_stairs");
            jungle_stairs = getRegisteredBlock("jungle_stairs");
            command_block = getRegisteredBlock("command_block");
            beacon = (BlockBeacon)getRegisteredBlock("beacon");
            cobblestone_wall = getRegisteredBlock("cobblestone_wall");
            flower_pot = getRegisteredBlock("flower_pot");
            carrots = getRegisteredBlock("carrots");
            potatoes = getRegisteredBlock("potatoes");
            wooden_button = getRegisteredBlock("wooden_button");
            skull = (BlockSkull)getRegisteredBlock("skull");
            anvil = getRegisteredBlock("anvil");
            trapped_chest = getRegisteredBlock("trapped_chest");
            light_weighted_pressure_plate = getRegisteredBlock("light_weighted_pressure_plate");
            heavy_weighted_pressure_plate = getRegisteredBlock("heavy_weighted_pressure_plate");
            unpowered_comparator = (BlockRedstoneComparator)getRegisteredBlock("unpowered_comparator");
            powered_comparator = (BlockRedstoneComparator)getRegisteredBlock("powered_comparator");
            daylight_detector = (BlockDaylightDetector)getRegisteredBlock("daylight_detector");
            daylight_detector_inverted = (BlockDaylightDetector)getRegisteredBlock("daylight_detector_inverted");
            redstone_block = getRegisteredBlock("redstone_block");
            quartz_ore = getRegisteredBlock("quartz_ore");
            hopper = (BlockHopper)getRegisteredBlock("hopper");
            quartz_block = getRegisteredBlock("quartz_block");
            quartz_stairs = getRegisteredBlock("quartz_stairs");
            activator_rail = getRegisteredBlock("activator_rail");
            dropper = getRegisteredBlock("dropper");
            stained_hardened_clay = getRegisteredBlock("stained_hardened_clay");
            barrier = getRegisteredBlock("barrier");
            iron_trapdoor = getRegisteredBlock("iron_trapdoor");
            hay_block = getRegisteredBlock("hay_block");
            carpet = getRegisteredBlock("carpet");
            hardened_clay = getRegisteredBlock("hardened_clay");
            coal_block = getRegisteredBlock("coal_block");
            packed_ice = getRegisteredBlock("packed_ice");
            acacia_stairs = getRegisteredBlock("acacia_stairs");
            dark_oak_stairs = getRegisteredBlock("dark_oak_stairs");
            slime_block = getRegisteredBlock("slime");
            double_plant = (BlockDoublePlant)getRegisteredBlock("double_plant");
            stained_glass = (BlockStainedGlass)getRegisteredBlock("stained_glass");
            stained_glass_pane = (BlockStainedGlassPane)getRegisteredBlock("stained_glass_pane");
            prismarine = getRegisteredBlock("prismarine");
            sea_lantern = getRegisteredBlock("sea_lantern");
            standing_banner = getRegisteredBlock("standing_banner");
            wall_banner = getRegisteredBlock("wall_banner");
            red_sandstone = getRegisteredBlock("red_sandstone");
            red_sandstone_stairs = getRegisteredBlock("red_sandstone_stairs");
            double_stone_slab2 = (BlockSlab)getRegisteredBlock("double_stone_slab2");
            stone_slab2 = (BlockSlab)getRegisteredBlock("stone_slab2");
        }
    }
}
