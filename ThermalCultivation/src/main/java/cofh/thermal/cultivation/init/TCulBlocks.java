package cofh.thermal.cultivation.init;

import cofh.core.block.Block4Way;
import cofh.core.block.CakeBlockCoFH;
import cofh.core.block.TileBlockActive4Way;
import cofh.core.block.crops.AttachedStemBlockCoFH;
import cofh.core.block.crops.StemBlockCoFH;
import cofh.core.util.ProxyUtils;
import cofh.thermal.core.block.SoilBlock;
import cofh.thermal.core.block.TilledSoilBlock;
import cofh.thermal.core.common.ThermalConfig;
import cofh.thermal.cultivation.block.FrostMelonBlock;
import cofh.thermal.cultivation.inventory.container.device.DeviceSoilInfuserContainer;
import cofh.thermal.cultivation.tileentity.DeviceSoilInfuserTile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;

import java.util.function.IntSupplier;

import static cofh.core.util.constants.Constants.ACTIVE;
import static cofh.core.util.constants.Constants.CHARGED;
import static cofh.core.util.helpers.BlockHelper.lightValue;
import static cofh.thermal.core.ThermalCore.*;
import static cofh.thermal.core.common.ThermalAugmentRules.DEVICE_VALIDATOR;
import static cofh.thermal.core.util.RegistrationHelper.*;
import static cofh.thermal.cultivation.init.TCulIDs.*;
import static cofh.thermal.cultivation.init.TCulReferences.DEVICE_SOIL_INFUSER_BLOCK;
import static net.minecraft.block.AbstractBlock.Properties.create;

public class TCulBlocks {

    private TCulBlocks() {

    }

    public static void register() {

        registerPlants();
        registerFoods();
        registerStorage();
        registerMisc();

        registerTileBlocks();
        registerTileContainers();
        registerTileEntities();
    }

    public static void setup() {

        FireBlock fire = (FireBlock) Blocks.FIRE;

        fire.setFireInfo(BLOCKS.get(block(ID_BARLEY)), 60, 20);
        fire.setFireInfo(BLOCKS.get(block(ID_CORN)), 60, 20);
    }

    // region HELPERS
    private static void registerPlants() {

        // ANNUAL
        registerAnnual(ID_BARLEY);
        registerTallAnnual(ID_CORN);
        registerAnnual(ID_ONION);
        registerAnnual(ID_RADISH);
        registerAnnual(ID_RICE);
        registerAnnual(ID_SADIROOT);
        registerAnnual(ID_SPINACH);

        // PERENNIAL
        registerPerennial(ID_BELL_PEPPER);
        registerPerennial(ID_EGGPLANT);
        registerPerennial(ID_GREEN_BEAN);
        registerPerennial(ID_PEANUT);
        registerPerennial(ID_STRAWBERRY);
        registerPerennial(ID_TOMATO);

        registerPerennial(ID_COFFEE, 6);
        // registerTallPerennial(ID_HOPS);
        registerPerennial(ID_TEA);

        // STEM
        registerBlock(ID_FROST_MELON, () -> new FrostMelonBlock(create(Material.GOURD, MaterialColor.CYAN).tickRandomly().hardnessAndResistance(1.0F).harvestTool(ToolType.AXE).sound(SoundType.SNOW)), Rarity.UNCOMMON);
        registerBlockOnly(ID_FROST_MELON_STEM, () -> new StemBlockCoFH(create(Material.PLANTS).tickRandomly().doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.WOOD)).crop(BLOCKS.getSup(ID_FROST_MELON)).seed(ITEMS.getSup(seeds(ID_FROST_MELON))));
        registerBlockOnly(ID_FROST_MELON_STEM_ATTACHED, () -> new AttachedStemBlockCoFH(create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0.0F).sound(SoundType.STEM)).crop(BLOCKS.getSup(ID_FROST_MELON)).seed(ITEMS.getSup(seeds(ID_FROST_MELON))));
    }

    private static void registerFoods() {

        registerBlock(ID_CHOCOLATE_CAKE, () -> new CakeBlockCoFH(create(Material.CAKE).hardnessAndResistance(0.5F).sound(SoundType.CLOTH), TCulFoods.CHOCOLATE_CAKE));
        registerBlock(ID_SPICE_CAKE, () -> new CakeBlockCoFH(create(Material.CAKE).hardnessAndResistance(0.5F).sound(SoundType.CLOTH), TCulFoods.SPICE_CAKE));
    }

    private static void registerStorage() {

        registerBlock(block(ID_BARLEY), () -> new HayBlock(create(Material.ORGANIC, MaterialColor.GOLD).hardnessAndResistance(0.5F).sound(SoundType.PLANT).harvestTool(ToolType.HOE)));
        registerBlock(block(ID_CORN), () -> new Block(create(Material.WOOD, MaterialColor.YELLOW).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_ONION), () -> new Block(create(Material.WOOD, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_RADISH), () -> new Block(create(Material.WOOD, MaterialColor.RED_TERRACOTTA).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_SADIROOT), () -> new Block(create(Material.WOOD, MaterialColor.GREEN_TERRACOTTA).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_SPINACH), () -> new Block(create(Material.WOOD, MaterialColor.GREEN).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));

        registerBlock(block(ID_BELL_PEPPER), () -> new Block(create(Material.WOOD, MaterialColor.RED_TERRACOTTA).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_EGGPLANT), () -> new Block(create(Material.WOOD, MaterialColor.PURPLE_TERRACOTTA).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_GREEN_BEAN), () -> new Block(create(Material.WOOD, MaterialColor.GREEN).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_HOPS), () -> new Block(create(Material.WOOD, MaterialColor.GREEN).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_STRAWBERRY), () -> new Block(create(Material.WOOD, MaterialColor.RED_TERRACOTTA).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));
        registerBlock(block(ID_TOMATO), () -> new Block(create(Material.WOOD, MaterialColor.RED).hardnessAndResistance(1.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE)));

        registerBlock(block(ID_RICE), () -> new Block4Way(create(Material.WOOL, MaterialColor.WHITE_TERRACOTTA).hardnessAndResistance(0.5F).sound(SoundType.WART)));

        registerBlock(block(ID_COFFEE), () -> new Block4Way(create(Material.WOOL, MaterialColor.RED_TERRACOTTA).hardnessAndResistance(0.5F).sound(SoundType.WART)));
        registerBlock(block(ID_PEANUT), () -> new Block4Way(create(Material.WOOL, MaterialColor.BROWN_TERRACOTTA).hardnessAndResistance(0.5F).sound(SoundType.WART)));
        registerBlock(block(ID_TEA), () -> new Block4Way(create(Material.WOOL, MaterialColor.GREEN_TERRACOTTA).hardnessAndResistance(0.5F).sound(SoundType.WART)));
    }

    private static void registerMisc() {

        registerBlock(ID_PHYTOSOIL, () -> new SoilBlock(create(Material.EARTH).tickRandomly().hardnessAndResistance(0.8F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND).setLightLevel((state) -> state.get(CHARGED) > 0 ? 7 : 0)));
        registerBlock(ID_PHYTOSOIL_TILLED, () -> new TilledSoilBlock(create(Material.EARTH).tickRandomly().hardnessAndResistance(0.8F).harvestTool(ToolType.SHOVEL).sound(SoundType.GROUND).setLightLevel((state) -> state.get(CHARGED) > 0 ? 7 : 0)).dirt(BLOCKS.getSup(ID_PHYTOSOIL)));
    }

    private static void registerTileBlocks() {

        IntSupplier deviceAugs = () -> ThermalConfig.deviceAugments;

        registerAugBlock(ID_DEVICE_SOIL_INFUSER, () -> new TileBlockActive4Way(create(Material.WOOD).sound(SoundType.SCAFFOLDING).hardnessAndResistance(2.5F).harvestTool(ToolType.AXE).setLightLevel(lightValue(ACTIVE, 10)), DeviceSoilInfuserTile::new), deviceAugs, DEVICE_VALIDATOR);
    }

    private static void registerTileContainers() {

        CONTAINERS.register(ID_DEVICE_SOIL_INFUSER, () -> IForgeContainerType.create((windowId, inv, data) -> new DeviceSoilInfuserContainer(windowId, ProxyUtils.getClientWorld(), data.readBlockPos(), inv, ProxyUtils.getClientPlayer())));
    }

    private static void registerTileEntities() {

        TILE_ENTITIES.register(ID_DEVICE_SOIL_INFUSER, () -> TileEntityType.Builder.create(DeviceSoilInfuserTile::new, DEVICE_SOIL_INFUSER_BLOCK).build(null));
    }
    // endregion
}
