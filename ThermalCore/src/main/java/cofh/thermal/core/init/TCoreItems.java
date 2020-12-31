package cofh.thermal.core.init;

import cofh.core.entity.AbstractGrenadeEntity;
import cofh.core.item.*;
import cofh.core.util.constants.ToolTypes;
import cofh.core.util.helpers.AugmentDataHelper;
import cofh.thermal.core.common.ThermalItemGroups;
import cofh.thermal.core.entity.item.*;
import cofh.thermal.core.entity.projectile.*;
import cofh.thermal.core.item.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import static cofh.core.util.constants.Constants.FALSE;
import static cofh.core.util.constants.Constants.TRUE;
import static cofh.core.util.constants.NBTTags.*;
import static cofh.thermal.core.ThermalCore.BLOCKS;
import static cofh.thermal.core.ThermalCore.ITEMS;
import static cofh.thermal.core.common.ThermalFlags.*;
import static cofh.thermal.core.common.ThermalItemGroups.THERMAL_ITEMS;
import static cofh.thermal.core.common.ThermalItemGroups.THERMAL_TOOLS;
import static cofh.thermal.core.init.TCoreIDs.*;
import static cofh.thermal.core.init.TCoreReferences.*;
import static cofh.thermal.core.util.RegistrationHelper.*;

public class TCoreItems {

    private TCoreItems() {

    }

    public static void register() {

        registerResources();
        registerParts();

        registerUpgradeAugments();
        registerStorageAugments();
        registerMachineAugments();
        registerDynamoAugments();
        registerAreaAugments();
        registerPotionAugments();

        registerMaterials();
        registerTools();
        registerArmor();

        registerSpawnEggs();
    }

    public static void setup() {

        DetonatorItem.registerTNT(Blocks.TNT, TNTEntity::new);

        DetonatorItem.registerTNT(BLOCKS.get(ID_PHYTO_TNT), PhytoTNTEntity::new);

        DetonatorItem.registerTNT(BLOCKS.get(ID_FIRE_TNT), FireTNTEntity::new);
        DetonatorItem.registerTNT(BLOCKS.get(ID_EARTH_TNT), EarthTNTEntity::new);
        DetonatorItem.registerTNT(BLOCKS.get(ID_ICE_TNT), IceTNTEntity::new);
        DetonatorItem.registerTNT(BLOCKS.get(ID_LIGHTNING_TNT), LightningTNTEntity::new);

        DetonatorItem.registerTNT(BLOCKS.get(ID_NUKE_TNT), NukeTNTEntity::new);

        ((DivingArmorItem) ITEMS.get(ID_DIVING_HELMET)).setup();
        ((DivingArmorItem) ITEMS.get(ID_DIVING_CHESTPLATE)).setup();
        ((DivingArmorItem) ITEMS.get(ID_DIVING_LEGGINGS)).setup();
        ((DivingArmorItem) ITEMS.get(ID_DIVING_BOOTS)).setup();
    }

    // region HELPERS
    private static void registerResources() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("sawdust", group);
        registerItem("coal_coke", () -> new ItemCoFH(new Item.Properties().group(group)).setBurnTime(3200));
        registerItem("bitumen", () -> new ItemCoFH(new Item.Properties().group(group)).setBurnTime(1600));
        registerItem("tar", () -> new ItemCoFH(new Item.Properties().group(group)).setBurnTime(800));
        registerItem("rosin", () -> new ItemCoFH(new Item.Properties().group(group)).setBurnTime(800));
        registerItem("rubber", group);
        registerItem("cured_rubber", group);
        registerItem("slag", group);
        registerItem("rich_slag", group);

        registerItem("basalz_rod", group);
        registerItem("basalz_powder", group);
        registerItem("blitz_rod", group);
        registerItem("blitz_powder", group);
        registerItem("blizz_rod", group);
        registerItem("blizz_powder", group);

        registerItem("beekeeper_fabric", () -> new ItemCoFH(new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_BEEKEEPER_ARMOR)));
        registerItem("diving_fabric", () -> new ItemCoFH(new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_DIVING_ARMOR)));
        registerItem("hazmat_fabric", () -> new ItemCoFH(new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_HAZMAT_ARMOR)));

        registerItem("apatite", group);
        registerItem("apatite_dust", group);
        registerItem("cinnabar", group);
        registerItem("cinnabar_dust", group);
        registerItem("niter", group);
        registerItem("niter_dust", group);
        registerItem("sulfur", () -> new ItemCoFH(new Item.Properties().group(group)).setBurnTime(1200));
        registerItem("sulfur_dust", () -> new ItemCoFH(new Item.Properties().group(group)).setBurnTime(1200));
    }

    private static void registerParts() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("redstone_servo", group);
        registerItem("rf_coil", group);

        registerItem("drill_head", () -> new ItemCoFH(new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_TOOL_COMPONENTS)));
        registerItem("saw_blade", () -> new ItemCoFH(new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_TOOL_COMPONENTS)));
        registerItem("laser_diode", () -> new ItemCoFH(new Item.Properties().group(group)).setShowInGroups(FALSE));//.setShowInGroups(getFeature(FLAG_TOOL_COMPONENTS))); // TODO: Implement
    }

    private static void registerMaterials() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("ender_pearl_dust", group);

        registerMetalSet("iron", group, Rarity.COMMON, TRUE, true);
        registerMetalSet("gold", group, Rarity.COMMON, TRUE, true);

        registerGemSet("lapis", group, Rarity.COMMON, TRUE, true);
        registerGemSet("diamond", group, Rarity.COMMON, TRUE, true);
        registerGemSet("emerald", group, Rarity.COMMON, TRUE, true);
        registerGemSet("quartz", group, Rarity.COMMON, TRUE, true);

        Rarity rarity = Rarity.UNCOMMON;

        registerMetalSet("copper", group, getFlag(FLAG_RESOURCE_COPPER));
        registerMetalSet("tin", group, getFlag(FLAG_RESOURCE_TIN));
        registerMetalSet("lead", group, getFlag(FLAG_RESOURCE_LEAD));
        registerMetalSet("silver", group, getFlag(FLAG_RESOURCE_SILVER));
        registerMetalSet("nickel", group, getFlag(FLAG_RESOURCE_NICKEL));

        registerMetalSet("bronze", group, getFlag(FLAG_RESOURCE_BRONZE));
        registerMetalSet("electrum", group, getFlag(FLAG_RESOURCE_ELECTRUM));
        registerMetalSet("invar", group, getFlag(FLAG_RESOURCE_INVAR));
        registerMetalSet("constantan", group, getFlag(FLAG_RESOURCE_CONSTANTAN));

        registerMetalSet("signalum", group, rarity);
        registerMetalSet("lumium", group, rarity);
        registerMetalSet("enderium", group, rarity);

        registerGemSet("ruby", group, getFlag(FLAG_RESOURCE_RUBY));
        registerGemSet("sapphire", group, getFlag(FLAG_RESOURCE_SAPPHIRE));
    }

    private static void registerTools() {

        ItemGroup group = ThermalItemGroups.THERMAL_TOOLS;

        registerItem("wrench", () -> new WrenchItem(new Item.Properties().maxStackSize(1).group(group).addToolType(ToolTypes.WRENCH, 1)));
        registerItem("redprint", () -> new RedprintItem(new Item.Properties().maxStackSize(1).group(group)));
        // TODO: Bring back w/ 1.2.
        // registerItem("xp_crystal", () -> new XPCrystalItem(new Item.Properties().maxStackSize(1).group(group), MB_PER_XP * 10000));
        registerItem("rf_potato", () -> new EnergyContainerItem(new Item.Properties().maxStackSize(1).group(group), 100000, 40));
        registerItem("lock", () -> new LockItem(new Item.Properties().group(group)));
        registerItem("phytogro", () -> new PhytoGroItem(new Item.Properties().group(group)));

        registerItem("earth_charge", () -> new EarthChargeItem(new Item.Properties().group(group)));
        registerItem("ice_charge", () -> new IceChargeItem(new Item.Properties().group(group)));
        registerItem("lightning_charge", () -> new LightningChargeItem(new Item.Properties().group(group)));

        registerItem("detonator", () -> new DetonatorItem(new Item.Properties().group(group)));

        registerItem("explosive_grenade", () -> new GrenadeItem(new GrenadeItem.IGrenadeFactory<AbstractGrenadeEntity>() {

            @Override
            public AbstractGrenadeEntity createGrenade(World world, LivingEntity living) {

                return new ExplosiveGrenadeEntity(world, living);
            }

            @Override
            public AbstractGrenadeEntity createGrenade(World world, double posX, double posY, double posZ) {

                return new ExplosiveGrenadeEntity(world, posX, posY, posZ);
            }

        }, new Item.Properties().group(group).maxStackSize(16)));
        registerItem("phyto_grenade", () -> new GrenadeItem(new GrenadeItem.IGrenadeFactory<AbstractGrenadeEntity>() {

            @Override
            public AbstractGrenadeEntity createGrenade(World world, LivingEntity living) {

                return new PhytoGrenadeEntity(world, living);
            }

            @Override
            public AbstractGrenadeEntity createGrenade(World world, double posX, double posY, double posZ) {

                return new PhytoGrenadeEntity(world, posX, posY, posZ);
            }

        }, new Item.Properties().group(group).maxStackSize(16)).setShowInGroups(getFlag(FLAG_PHYTOGRO_EXPLOSIVES)));

        registerItem("fire_grenade", () -> new GrenadeItem(new GrenadeItem.IGrenadeFactory<AbstractGrenadeEntity>() {

            @Override
            public AbstractGrenadeEntity createGrenade(World world, LivingEntity living) {

                return new FireGrenadeEntity(world, living);
            }

            @Override
            public AbstractGrenadeEntity createGrenade(World world, double posX, double posY, double posZ) {

                return new FireGrenadeEntity(world, posX, posY, posZ);
            }

        }, new Item.Properties().group(group).maxStackSize(16)).setShowInGroups(getFlag(FLAG_ELEMENTAL_EXPLOSIVES)));
        registerItem("earth_grenade", () -> new GrenadeItem(new GrenadeItem.IGrenadeFactory<AbstractGrenadeEntity>() {

            @Override
            public AbstractGrenadeEntity createGrenade(World world, LivingEntity living) {

                return new EarthGrenadeEntity(world, living);
            }

            @Override
            public AbstractGrenadeEntity createGrenade(World world, double posX, double posY, double posZ) {

                return new EarthGrenadeEntity(world, posX, posY, posZ);
            }

        }, new Item.Properties().group(group).maxStackSize(16)).setShowInGroups(getFlag(FLAG_ELEMENTAL_EXPLOSIVES)));
        registerItem("ice_grenade", () -> new GrenadeItem(new GrenadeItem.IGrenadeFactory<AbstractGrenadeEntity>() {

            @Override
            public AbstractGrenadeEntity createGrenade(World world, LivingEntity living) {

                return new IceGrenadeEntity(world, living);
            }

            @Override
            public AbstractGrenadeEntity createGrenade(World world, double posX, double posY, double posZ) {

                return new IceGrenadeEntity(world, posX, posY, posZ);
            }

        }, new Item.Properties().group(group).maxStackSize(16)).setShowInGroups(getFlag(FLAG_ELEMENTAL_EXPLOSIVES)));
        registerItem("lightning_grenade", () -> new GrenadeItem(new GrenadeItem.IGrenadeFactory<AbstractGrenadeEntity>() {

            @Override
            public AbstractGrenadeEntity createGrenade(World world, LivingEntity living) {

                return new LightningGrenadeEntity(world, living);
            }

            @Override
            public AbstractGrenadeEntity createGrenade(World world, double posX, double posY, double posZ) {

                return new LightningGrenadeEntity(world, posX, posY, posZ);
            }

        }, new Item.Properties().group(group).maxStackSize(16)).setShowInGroups(getFlag(FLAG_ELEMENTAL_EXPLOSIVES)));

        registerItem("nuke_grenade", () -> new GrenadeItem(new GrenadeItem.IGrenadeFactory<AbstractGrenadeEntity>() {

            @Override
            public AbstractGrenadeEntity createGrenade(World world, LivingEntity living) {

                return new NukeGrenadeEntity(world, living);
            }

            @Override
            public AbstractGrenadeEntity createGrenade(World world, double posX, double posY, double posZ) {

                return new NukeGrenadeEntity(world, posX, posY, posZ);
            }

        }, new Item.Properties().group(group).rarity(Rarity.UNCOMMON).maxStackSize(16)).setShowInGroups(getFlag(FLAG_NUCLEAR_EXPLOSIVES)));
    }

    private static void registerArmor() {

        ItemGroup group = THERMAL_TOOLS;

        ITEMS.register(ID_BEEKEEPER_HELMET, () -> new BeekeeperArmorItem(BEEKEEPER, EquipmentSlotType.HEAD, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_BEEKEEPER_ARMOR)));
        ITEMS.register(ID_BEEKEEPER_CHESTPLATE, () -> new BeekeeperArmorItem(BEEKEEPER, EquipmentSlotType.CHEST, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_BEEKEEPER_ARMOR)));
        ITEMS.register(ID_BEEKEEPER_LEGGINGS, () -> new BeekeeperArmorItem(BEEKEEPER, EquipmentSlotType.LEGS, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_BEEKEEPER_ARMOR)));
        ITEMS.register(ID_BEEKEEPER_BOOTS, () -> new BeekeeperArmorItem(BEEKEEPER, EquipmentSlotType.FEET, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_BEEKEEPER_ARMOR)));

        ITEMS.register(ID_DIVING_HELMET, () -> new DivingArmorItem(DIVING, EquipmentSlotType.HEAD, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_DIVING_ARMOR)));
        ITEMS.register(ID_DIVING_CHESTPLATE, () -> new DivingArmorItem(DIVING, EquipmentSlotType.CHEST, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_DIVING_ARMOR)));
        ITEMS.register(ID_DIVING_LEGGINGS, () -> new DivingArmorItem(DIVING, EquipmentSlotType.LEGS, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_DIVING_ARMOR)));
        ITEMS.register(ID_DIVING_BOOTS, () -> new DivingArmorItem(DIVING, EquipmentSlotType.FEET, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_DIVING_ARMOR)));

        ITEMS.register(ID_HAZMAT_HELMET, () -> new HazmatArmorItem(HAZMAT, EquipmentSlotType.HEAD, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_HAZMAT_ARMOR)));
        ITEMS.register(ID_HAZMAT_CHESTPLATE, () -> new HazmatArmorItem(HAZMAT, EquipmentSlotType.CHEST, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_HAZMAT_ARMOR)));
        ITEMS.register(ID_HAZMAT_LEGGINGS, () -> new HazmatArmorItem(HAZMAT, EquipmentSlotType.LEGS, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_HAZMAT_ARMOR)));
        ITEMS.register(ID_HAZMAT_BOOTS, () -> new HazmatArmorItem(HAZMAT, EquipmentSlotType.FEET, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_HAZMAT_ARMOR)));
    }

    // region AUGMENTS
    private static void registerAreaAugments() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("area_radius_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_AREA_EFFECT)
                        .mod(TAG_AUGMENT_AREA_RADIUS, 1.0F)
                        .build()).setShowInGroups(getFlag(FLAG_AREA_AUGMENTS)));
    }

    private static void registerDynamoAugments() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("dynamo_output_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_DYNAMO)
                        .mod(TAG_AUGMENT_DYNAMO_PRODUCTION, 1.0F)
                        .mod(TAG_AUGMENT_DYNAMO_EFFICIENCY, 0.9F)
                        .build()).setShowInGroups(getFlag(FLAG_DYNAMO_AUGMENTS)));

        registerItem("dynamo_fuel_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_DYNAMO)
                        .mod(TAG_AUGMENT_DYNAMO_EFFICIENCY, 1.10F)
                        .build()).setShowInGroups(getFlag(FLAG_DYNAMO_AUGMENTS)));
    }

    private static void registerMachineAugments() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("machine_speed_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_MACHINE)
                        .mod(TAG_AUGMENT_MACHINE_POWER, 1.0F)
                        .mod(TAG_AUGMENT_MACHINE_ENERGY, 1.1F)
                        .build()).setShowInGroups(getFlag(FLAG_MACHINE_AUGMENTS)));

        registerItem("machine_output_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_MACHINE)
                        .mod(TAG_AUGMENT_MACHINE_SECONDARY, 0.15F)
                        .mod(TAG_AUGMENT_MACHINE_ENERGY, 1.25F)
                        .build()).setShowInGroups(getFlag(FLAG_MACHINE_AUGMENTS)));

        registerItem("machine_catalyst_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_MACHINE)
                        .mod(TAG_AUGMENT_MACHINE_CATALYST, 0.8F)
                        .mod(TAG_AUGMENT_MACHINE_ENERGY, 1.25F)
                        .build()).setShowInGroups(getFlag(FLAG_MACHINE_AUGMENTS)));

        registerItem("machine_cycle_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_MACHINE)
                        .mod(TAG_AUGMENT_FEATURE_RECYCLE, 1.0F)
                        .build()).setShowInGroups(getFlag(FLAG_MACHINE_AUGMENTS)));
    }

    private static void registerPotionAugments() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("potion_amplifier_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_POTION)
                        .mod(TAG_AUGMENT_POTION_AMPLIFIER, 1.0F)
                        .mod(TAG_AUGMENT_POTION_DURATION, -0.25F)
                        .build()).setShowInGroups(getFlag(FLAG_POTION_AUGMENTS)));

        registerItem("potion_duration_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_POTION)
                        .mod(TAG_AUGMENT_POTION_DURATION, 1.0F)
                        .build()).setShowInGroups(getFlag(FLAG_POTION_AUGMENTS)));
    }

    private static void registerStorageAugments() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;

        registerItem("rf_coil_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_RF)
                        .mod(TAG_AUGMENT_ENERGY_STORAGE, 4.0F)
                        .mod(TAG_AUGMENT_ENERGY_XFER, 4.0F)
                        .build()).setShowInGroups(getFlag(FLAG_STORAGE_AUGMENTS)));

        registerItem("rf_coil_storage_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_RF)
                        .mod(TAG_AUGMENT_ENERGY_STORAGE, 6.0F)
                        .mod(TAG_AUGMENT_ENERGY_XFER, 2.0F)
                        .build()).setShowInGroups(getFlag(FLAG_STORAGE_AUGMENTS)));

        registerItem("rf_coil_xfer_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_RF)
                        .mod(TAG_AUGMENT_ENERGY_STORAGE, 2.0F)
                        .mod(TAG_AUGMENT_ENERGY_XFER, 6.0F)
                        .build()).setShowInGroups(getFlag(FLAG_STORAGE_AUGMENTS)));

        registerItem("fluid_tank_augment", () -> new AugmentItem(new Item.Properties().group(group),
                AugmentDataHelper.builder()
                        .type(TAG_AUGMENT_TYPE_FLUID)
                        .mod(TAG_AUGMENT_FLUID_STORAGE, 4.0F)
                        .build()).setShowInGroups(getFlag(FLAG_STORAGE_AUGMENTS)));
    }

    private static void registerUpgradeAugments() {

        ItemGroup group = ThermalItemGroups.THERMAL_ITEMS;
        final float[] upgradeMods = new float[]{1.0F, 2.0F, 3.0F, 4.0F, 6.0F, 8.5F};
        // final float[] upgradeMods = new float[]{1.0F, 1.5F, 2.0F, 2.5F, 3.0F, 3.5F};

        for (int i = 1; i <= 3; ++i) {
            int tier = i;
            registerItem("upgrade_augment_" + i, () -> new AugmentItem(new Item.Properties().group(group),
                    AugmentDataHelper.builder()
                            .type(TAG_AUGMENT_TYPE_UPGRADE)
                            .mod(TAG_AUGMENT_BASE_MOD, upgradeMods[tier])
                            .build()).setShowInGroups(getFlag(FLAG_UPGRADE_AUGMENTS)));
        }
    }
    // endregion

    private static void registerSpawnEggs() {

        ItemGroup group = THERMAL_ITEMS;

        registerItem("basalz_spawn_egg", () -> new SpawnEggItemCoFH(() -> BASALZ_ENTITY, 0x363840, 0x080407, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_MOB_BASALZ)));
        registerItem("blizz_spawn_egg", () -> new SpawnEggItemCoFH(() -> BLIZZ_ENTITY, 0x7BD4FF, 0x0D6FD9, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_MOB_BLIZZ)));
        registerItem("blitz_spawn_egg", () -> new SpawnEggItemCoFH(() -> BLITZ_ENTITY, 0xECFEFC, 0xFFD46D, new Item.Properties().group(group)).setShowInGroups(getFlag(FLAG_MOB_BLITZ)));
    }
    // endregion

    public static final ArmorMaterialCoFH BEEKEEPER = new ArmorMaterialCoFH("thermal:beekeeper", 4, new int[]{1, 2, 3, 1}, 16, SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 0.0F, 0.0F, () -> Ingredient.fromItems(ITEMS.get("beekeeper_fabric")));
    public static final ArmorMaterialCoFH DIVING = new ArmorMaterialCoFH("thermal:diving", 12, new int[]{1, 4, 5, 2}, 20, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, () -> Ingredient.fromItems(ITEMS.get("diving_fabric")));
    public static final ArmorMaterialCoFH HAZMAT = new ArmorMaterialCoFH("thermal:hazmat", 6, new int[]{1, 4, 5, 2}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.fromItems(ITEMS.get("hazmat_fabric")));

}
