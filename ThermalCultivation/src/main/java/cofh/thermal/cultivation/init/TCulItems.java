package cofh.thermal.cultivation.init;

import cofh.core.item.BlockNamedItemCoFH;
import cofh.core.item.ItemCoFH;
import cofh.thermal.cultivation.item.WateringCanItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;

import static cofh.thermal.core.ThermalCore.BLOCKS;
import static cofh.thermal.core.ThermalCore.ITEMS;
import static cofh.thermal.core.common.ThermalItemGroups.THERMAL_ITEMS;
import static cofh.thermal.core.common.ThermalItemGroups.THERMAL_TOOLS;
import static cofh.thermal.core.util.RegistrationHelper.registerCropAndSeed;
import static cofh.thermal.core.util.RegistrationHelper.seeds;
import static cofh.thermal.cultivation.init.TCulFoods.*;
import static cofh.thermal.cultivation.init.TCulIDs.*;

public class TCulItems {

    private TCulItems() {

    }

    public static void register() {

        registerCrops();
        registerFoods();
        registerTools();
    }

    private static void registerCrops() {

        // ANNUAL
        registerCropAndSeed(ID_BARLEY);
        registerCropAndSeed(ID_CORN, CORN);
        registerCropAndSeed(ID_ONION, ONION);
        registerCropAndSeed(ID_RADISH, RADISH);
        registerCropAndSeed(ID_RICE);
        registerCropAndSeed(ID_SADIROOT);
        registerCropAndSeed(ID_SPINACH, SPINACH);

        // PERENNIAL
        registerCropAndSeed(ID_BELL_PEPPER, BELL_PEPPER);
        registerCropAndSeed(ID_EGGPLANT, EGGPLANT);
        registerCropAndSeed(ID_GREEN_BEAN, GREEN_BEAN);
        registerCropAndSeed(ID_PEANUT, PEANUT);
        registerCropAndSeed(ID_STRAWBERRY, STRAWBERRY);
        registerCropAndSeed(ID_TOMATO, TOMATO);

        // BREWING
        registerCropAndSeed(ID_COFFEE, COFFEE_CHERRY);

        // TODO: Implement Hops
        // registerCropAndSeed(ID_HOPS);
        ITEMS.register(ID_HOPS, () -> new ItemCoFH(new Item.Properties().group(THERMAL_ITEMS)));
        ITEMS.register(seeds(ID_HOPS), () -> new ItemCoFH(new Item.Properties().group(THERMAL_ITEMS)));

        registerCropAndSeed(ID_TEA);

        // OTHER
        ITEMS.register(ID_FROST_MELON_SLICE, () -> new ItemCoFH(new Item.Properties().group(THERMAL_ITEMS).food(FROST_MELON_SLICE).rarity(Rarity.UNCOMMON)));
        ITEMS.register(seeds(ID_FROST_MELON), () -> new BlockNamedItemCoFH(BLOCKS.get(ID_FROST_MELON_STEM), new Item.Properties().group(THERMAL_ITEMS).rarity(Rarity.UNCOMMON)));
    }

    private static void registerFoods() {

        ItemGroup group = THERMAL_ITEMS;

        //        ITEMS.register("coffee", () -> new ItemCoFH(new Item.Properties().group(group).food(COFFEE)));
        //        ITEMS.register("dough", () -> new ItemCoFH(new Item.Properties().group(group).food(DOUGH)));
        //        ITEMS.register("flour", () -> new ItemCoFH(new Item.Properties().group(group)));
        ITEMS.register(ID_XP_STEW, () -> new ItemCoFH(new Item.Properties().maxStackSize(1).group(group).food(XP_STEW).rarity(Rarity.UNCOMMON)) {

            @Override
            public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {

                ItemStack itemstack = super.onItemUseFinish(stack, worldIn, entityLiving);
                return entityLiving instanceof PlayerEntity && ((PlayerEntity) entityLiving).abilities.isCreativeMode ? itemstack : new ItemStack(Items.BOWL);
            }
        });
    }

    private static void registerTools() {

        ItemGroup group = THERMAL_TOOLS;

        ITEMS.register("watering_can", () -> new WateringCanItem(new Item.Properties().maxStackSize(1).group(group), 8000));
    }

}
