package cofh.thermal.cultivation.datagen;

import cofh.core.datagen.RecipeProviderCoFH;
import cofh.core.registries.DeferredRegisterCoFH;
import cofh.core.util.references.ItemTagsCoFH;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static cofh.core.util.constants.Constants.ID_THERMAL;
import static cofh.thermal.core.ThermalCore.ITEMS;
import static cofh.thermal.core.util.RegistrationHelper.block;
import static cofh.thermal.core.util.RegistrationHelper.seeds;
import static cofh.thermal.cultivation.init.TCulIDs.*;

public class TCulRecipeProvider extends RecipeProviderCoFH {

    public TCulRecipeProvider(DataGenerator generatorIn) {

        super(generatorIn, ID_THERMAL);
    }

    @Override
    public String getName() {

        return "Thermal Cultivation: Recipes";
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

        DeferredRegisterCoFH<Item> reg = ITEMS;

        Item redstoneServo = reg.get("redstone_servo");
        Item rfCoil = reg.get("rf_coil");

        ShapedRecipeBuilder.shapedRecipe(reg.get("watering_can"))
                .key('B', Items.BUCKET)
                .key('C', ItemTagsCoFH.INGOTS_COPPER)
                .patternLine("C  ")
                .patternLine("CBC")
                .patternLine(" C ")
                .addCriterion("has_bucket", hasItem(Items.BUCKET))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reg.get(ID_PHYTOSOIL))
                .key('C', Items.CHARCOAL)
                .key('P', reg.get("phytogro"))
                .key('X', Blocks.DIRT)
                .patternLine("CPC")
                .patternLine("PXP")
                .patternLine("CPC")
                .addCriterion("has_phytogro", hasItem(reg.get("phytogro")))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reg.get(ID_DEVICE_SOIL_INFUSER))
                .key('C', reg.get("phytogro"))
                .key('G', Tags.Items.GLASS)
                .key('P', rfCoil)
                .key('X', ItemTagsCoFH.GEARS_LUMIUM)
                .key('W', ItemTags.PLANKS)
                .patternLine("WXW")
                .patternLine("GCG")
                .patternLine("WPW")
                .addCriterion("has_rf_coil", hasItem(rfCoil))
                .build(consumer);

        ShapelessRecipeBuilder.shapelessRecipe(reg.get(seeds(ID_FROST_MELON)))
                .addIngredient(reg.get(ID_FROST_MELON_SLICE))
                .addCriterion("has_frost_melon", hasItem(reg.get(ID_FROST_MELON_SLICE)))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reg.get(ID_FROST_MELON))
                .key('M', reg.get(ID_FROST_MELON_SLICE))
                .patternLine("MMM")
                .patternLine("MMM")
                .patternLine("MMM")
                .addCriterion("has_frost_melon", hasItem(reg.get(ID_FROST_MELON_SLICE)))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reg.get(ID_CHOCOLATE_CAKE))
                .key('A', Items.MILK_BUCKET)
                .key('B', Items.COCOA_BEANS)
                .key('C', Items.WHEAT)
                .key('D', reg.get(ID_STRAWBERRY))
                .key('E', Items.EGG)
                .patternLine("ADA")
                .patternLine("BEB")
                .patternLine("CDC")
                .addCriterion("has_cocoa_beans", hasItem(Items.COCOA_BEANS))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reg.get(ID_SPICE_CAKE))
                .key('A', Items.MILK_BUCKET)
                .key('B', Items.HONEY_BOTTLE)
                .key('C', Items.WHEAT)
                .key('D', reg.get(ID_SADIROOT))
                .key('E', Items.EGG)
                .patternLine("ADA")
                .patternLine("BEB")
                .patternLine("CDC")
                .addCriterion("has_sadiroot", hasItem(reg.get(ID_SADIROOT)))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(reg.get(ID_XP_STEW))
                .key('A', reg.get(ID_BARLEY))
                .key('B', Items.BOWL)
                .key('C', reg.get(ID_SPINACH))
                .key('D', reg.get(ID_EGGPLANT))
                .key('E', Items.EXPERIENCE_BOTTLE)
                .patternLine("CDC")
                .patternLine("AEA")
                .patternLine(" B ")
                .addCriterion("has_eggplant", hasItem(reg.get(ID_EGGPLANT)))
                .build(consumer);

        generateStorageRecipes(consumer, reg.get(block(ID_BARLEY)), reg.get(ID_BARLEY), forgeTag("crops/barley"));
        generateStorageRecipes(consumer, reg.get(block(ID_BELL_PEPPER)), reg.get(ID_BELL_PEPPER), forgeTag("crops/bell_pepper"));
        generateStorageRecipes(consumer, reg.get(block(ID_COFFEE)), reg.get(ID_COFFEE), forgeTag("crops/coffee"));
        generateStorageRecipes(consumer, reg.get(block(ID_CORN)), reg.get(ID_CORN), forgeTag("crops/corn"));
        generateStorageRecipes(consumer, reg.get(block(ID_EGGPLANT)), reg.get(ID_EGGPLANT), forgeTag("crops/eggplant"));
        generateStorageRecipes(consumer, reg.get(block(ID_GREEN_BEAN)), reg.get(ID_GREEN_BEAN), forgeTag("crops/green_bean"));
        generateStorageRecipes(consumer, reg.get(block(ID_HOPS)), reg.get(ID_HOPS), forgeTag("crops/hops"));
        generateStorageRecipes(consumer, reg.get(block(ID_ONION)), reg.get(ID_ONION), forgeTag("crops/onion"));
        generateStorageRecipes(consumer, reg.get(block(ID_PEANUT)), reg.get(ID_PEANUT), forgeTag("crops/peanut"));
        generateStorageRecipes(consumer, reg.get(block(ID_RADISH)), reg.get(ID_RADISH), forgeTag("crops/radish"));
        generateStorageRecipes(consumer, reg.get(block(ID_RICE)), reg.get(ID_RICE), forgeTag("crops/rice"));
        generateStorageRecipes(consumer, reg.get(block(ID_SADIROOT)), reg.get(ID_SADIROOT), forgeTag("crops/sadiroot"));
        generateStorageRecipes(consumer, reg.get(block(ID_SPINACH)), reg.get(ID_SPINACH), forgeTag("crops/spinach"));
        generateStorageRecipes(consumer, reg.get(block(ID_STRAWBERRY)), reg.get(ID_STRAWBERRY), forgeTag("crops/strawberry"));
        generateStorageRecipes(consumer, reg.get(block(ID_TEA)), reg.get(ID_TEA), forgeTag("crops/tea"));
        generateStorageRecipes(consumer, reg.get(block(ID_TOMATO)), reg.get(ID_TOMATO), forgeTag("crops/tomato"));
    }

}
