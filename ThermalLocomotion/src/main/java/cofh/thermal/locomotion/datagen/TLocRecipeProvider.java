package cofh.thermal.locomotion.datagen;

import cofh.core.datagen.RecipeProviderCoFH;
import cofh.core.util.references.ItemTagsCoFH;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static cofh.core.util.constants.Constants.ID_THERMAL;
import static cofh.thermal.core.ThermalCore.BLOCKS;
import static cofh.thermal.core.ThermalCore.ITEMS;
import static cofh.thermal.core.init.TCoreIDs.*;
import static cofh.thermal.locomotion.init.TLocIDs.*;

public class TLocRecipeProvider extends RecipeProviderCoFH {

    public TLocRecipeProvider(DataGenerator generatorIn) {

        super(generatorIn, ID_THERMAL);
    }

    @Override
    public String getName() {

        return "Thermal Locomotion: Recipes";
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

        registerRailRecipes(consumer);

        ShapedRecipeBuilder.shapedRecipe(ITEMS.get(ID_UNDERWATER_CART))
                .key('C', Items.MINECART)
                .key('G', Tags.Items.GLASS)
                .key('P', Items.PRISMARINE)
                .patternLine("GGG")
                .patternLine("GCG")
                .patternLine("PPP")
                .addCriterion("has_" + name(Items.MINECART), hasItem(Items.MINECART))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ITEMS.get(ID_PHYTO_TNT_CART))
                .key('A', ITEMS.get(ID_PHYTO_TNT))
                .key('B', Items.MINECART)
                .patternLine("A")
                .patternLine("B")
                .addCriterion("has_" + name(Items.MINECART), hasItem(Items.MINECART))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ITEMS.get(ID_FIRE_TNT_CART))
                .key('A', ITEMS.get(ID_FIRE_TNT))
                .key('B', Items.MINECART)
                .patternLine("A")
                .patternLine("B")
                .addCriterion("has_" + name(Items.MINECART), hasItem(Items.MINECART))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ITEMS.get(ID_EARTH_TNT_CART))
                .key('A', ITEMS.get(ID_EARTH_TNT))
                .key('B', Items.MINECART)
                .patternLine("A")
                .patternLine("B")
                .addCriterion("has_" + name(Items.MINECART), hasItem(Items.MINECART))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ITEMS.get(ID_ICE_TNT_CART))
                .key('A', ITEMS.get(ID_ICE_TNT))
                .key('B', Items.MINECART)
                .patternLine("A")
                .patternLine("B")
                .addCriterion("has_" + name(Items.MINECART), hasItem(Items.MINECART))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ITEMS.get(ID_LIGHTNING_TNT_CART))
                .key('A', ITEMS.get(ID_LIGHTNING_TNT))
                .key('B', Items.MINECART)
                .patternLine("A")
                .patternLine("B")
                .addCriterion("has_" + name(Items.MINECART), hasItem(Items.MINECART))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ITEMS.get(ID_NUKE_TNT_CART))
                .key('A', ITEMS.get(ID_NUKE_TNT))
                .key('B', Items.MINECART)
                .patternLine("A")
                .patternLine("B")
                .addCriterion("has_" + name(Items.MINECART), hasItem(Items.MINECART))
                .build(consumer);
    }

    // region HELPERS
    private void registerRailRecipes(Consumer<IFinishedRecipe> consumer) {

        String rail = name(Items.RAIL);
        String activatorRail = name(Items.ACTIVATOR_RAIL);
        String detectorRail = name(Items.DETECTOR_RAIL);
        String poweredRail = name(Items.POWERED_RAIL);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_CROSSOVER_RAIL))
                .key('I', Items.STICK)
                .key('X', Blocks.RAIL)
                .patternLine("XI")
                .patternLine("IX")
                .addCriterion("has_" + rail, hasItem(Items.RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_PRISMARINE_RAIL), 6)
                .key('C', Items.PRISMARINE_CRYSTALS)
                .key('S', Items.PRISMARINE_SHARD)
                .key('X', Blocks.RAIL)
                .patternLine("XSX")
                .patternLine("XCX")
                .patternLine("XSX")
                .addCriterion("has_" + rail, hasItem(Items.RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_PRISMARINE_CROSSOVER_RAIL))
                .key('I', Items.STICK)
                .key('X', BLOCKS.get(ID_PRISMARINE_RAIL))
                .patternLine("XI")
                .patternLine("IX")
                .addCriterion("has_" + name(BLOCKS.get(ID_PRISMARINE_RAIL)), hasItem(ITEMS.get(ID_PRISMARINE_RAIL)))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_PRISMARINE_ACTIVATOR_RAIL), 6)
                .key('C', Items.PRISMARINE_CRYSTALS)
                .key('S', Items.PRISMARINE_SHARD)
                .key('X', Blocks.ACTIVATOR_RAIL)
                .patternLine("XSX")
                .patternLine("XCX")
                .patternLine("XSX")
                .addCriterion("has_" + activatorRail, hasItem(Items.ACTIVATOR_RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_PRISMARINE_DETECTOR_RAIL), 6)
                .key('C', Items.PRISMARINE_CRYSTALS)
                .key('S', Items.PRISMARINE_SHARD)
                .key('X', Blocks.DETECTOR_RAIL)
                .patternLine("XSX")
                .patternLine("XCX")
                .patternLine("XSX")
                .addCriterion("has_" + detectorRail, hasItem(Items.DETECTOR_RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_PRISMARINE_POWERED_RAIL), 6)
                .key('C', Items.PRISMARINE_CRYSTALS)
                .key('S', Items.PRISMARINE_SHARD)
                .key('X', Blocks.POWERED_RAIL)
                .patternLine("XSX")
                .patternLine("XCX")
                .patternLine("XSX")
                .addCriterion("has_" + poweredRail, hasItem(Items.POWERED_RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_LUMIUM_RAIL), 6)
                .key('I', ItemTagsCoFH.INGOTS_LUMIUM)
                .key('X', Blocks.RAIL)
                .patternLine("XIX")
                .patternLine("XIX")
                .patternLine("XIX")
                .addCriterion("has_" + rail, hasItem(Items.RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_LUMIUM_CROSSOVER_RAIL))
                .key('I', Items.STICK)
                .key('X', BLOCKS.get(ID_LUMIUM_RAIL))
                .patternLine("XI")
                .patternLine("IX")
                .addCriterion("has_" + name(BLOCKS.get(ID_LUMIUM_RAIL)), hasItem(ITEMS.get(ID_LUMIUM_RAIL)))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_LUMIUM_ACTIVATOR_RAIL), 6)
                .key('I', ItemTagsCoFH.INGOTS_LUMIUM)
                .key('X', Blocks.ACTIVATOR_RAIL)
                .patternLine("XIX")
                .patternLine("XIX")
                .patternLine("XIX")
                .addCriterion("has_" + activatorRail, hasItem(Items.ACTIVATOR_RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_LUMIUM_DETECTOR_RAIL), 6)
                .key('I', ItemTagsCoFH.INGOTS_LUMIUM)
                .key('X', Blocks.DETECTOR_RAIL)
                .patternLine("XIX")
                .patternLine("XIX")
                .patternLine("XIX")
                .addCriterion("has_" + detectorRail, hasItem(Items.DETECTOR_RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(BLOCKS.get(ID_LUMIUM_POWERED_RAIL), 6)
                .key('I', ItemTagsCoFH.INGOTS_LUMIUM)
                .key('X', Blocks.POWERED_RAIL)
                .patternLine("XIX")
                .patternLine("XIX")
                .patternLine("XIX")
                .addCriterion("has_" + poweredRail, hasItem(Items.POWERED_RAIL))
                .build(consumer);
    }
    // endregion
}
