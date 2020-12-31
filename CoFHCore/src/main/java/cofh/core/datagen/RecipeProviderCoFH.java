package cofh.core.datagen;

import cofh.core.registries.DeferredRegisterCoFH;
import cofh.core.util.FlagManager;
import cofh.core.util.FlagRecipeCondition;
import net.minecraft.advancements.criterion.*;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static cofh.core.util.constants.Constants.ID_FORGE;

public class RecipeProviderCoFH extends RecipeProvider implements IConditionBuilder {

    protected final String modid;
    // private FeatureManager manager;

    public RecipeProviderCoFH(DataGenerator generatorIn, String modid) {

        super(generatorIn);
        this.modid = modid;
    }

    // TODO: Finish adding this to fully support modular features.
    //    protected void generateFlaggedRecipe(ResourceLocation id, Consumer<Consumer<IFinishedRecipe>> callable, String flag, Consumer<IFinishedRecipe> consumer) {
    //
    //        ConditionalRecipe.builder()
    //                .addCondition(
    //                        flagEnabled(manager, flag)
    //                )
    //                .addRecipe(callable)
    //                .build(consumer, id);
    //    }
    //
    //    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    //
    //        ResourceLocation ID = new ResourceLocation("data_gen_test", "conditional");
    //
    //        ConditionalRecipe.builder()
    //                .addCondition(
    //                        and(
    //                                not(modLoaded("minecraft")),
    //                                itemExists("minecraft", "dirt"),
    //                                FALSE()
    //                        )
    //                )
    //                .addRecipe(
    //                        ShapedRecipeBuilder.shapedRecipe(Blocks.DIAMOND_BLOCK, 64)
    //                                .patternLine("XXX")
    //                                .patternLine("XXX")
    //                                .patternLine("XXX")
    //                                .key('X', Blocks.DIRT)
    //                                .setGroup("")
    //                                .addCriterion("has_dirt", hasItem(Blocks.DIRT)) //Doesn't actually print... TODO: nested/conditional advancements?
    //                                ::build
    //                )
    //                .setAdvancement(ID,
    //                        ConditionalAdvancement.builder()
    //                                .addCondition(
    //                                        and(
    //                                                not(modLoaded("minecraft")),
    //                                                itemExists("minecraft", "dirt"),
    //                                                FALSE()
    //                                        )
    //                                )
    //                                .addAdvancement(
    //                                        Advancement.Builder.builder()
    //                                                .withParentId(new ResourceLocation("minecraft", "root"))
    //                                                .withDisplay(Blocks.DIAMOND_BLOCK,
    //                                                        new StringTextComponent("Dirt2Diamonds"),
    //                                                        new StringTextComponent("The BEST crafting recipe in the game!"),
    //                                                        null, FrameType.TASK, false, false, false
    //                                                )
    //                                                .withRewards(AdvancementRewards.Builder.recipe(ID))
    //                                                .withCriterion("has_dirt", hasItem(Blocks.DIRT))
    //                                )
    //                )
    //                .build(consumer, ID);
    //    }

    @SafeVarargs
    protected final Ingredient fromTags(ITag.INamedTag<Item>... tagsIn) {

        List<Ingredient> ingredients = new ArrayList<>(tagsIn.length);
        for (ITag.INamedTag<Item> tag : tagsIn) {
            ingredients.add(Ingredient.fromTag(tag));
        }
        return new CompoundIngredientCoFH(ingredients);
    }

    // region HELPERS
    protected void generatePackingRecipe(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapedRecipeBuilder.shapedRecipe(storage)
                .key('#', individual)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###")
                .addCriterion("has_at_least_9_" + individualName, hasItem(MinMaxBounds.IntBound.atLeast(9), individual))
                .build(consumer, this.modid + ":storage/" + storageName + suffix);
    }

    protected void generatePackingRecipe(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, ITag.INamedTag<Item> tag, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapedRecipeBuilder.shapedRecipe(storage)
                .key('I', individual)
                .key('#', tag)
                .patternLine("###")
                .patternLine("#I#")
                .patternLine("###")
                .addCriterion("has_at_least_9_" + individualName, hasItem(MinMaxBounds.IntBound.atLeast(9), individual))
                .build(consumer, this.modid + ":storage/" + storageName + suffix);
    }

    protected void generateUnpackingRecipe(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, String suffix) {

        String storageName = name(storage);
        String individualName = name(individual);

        ShapelessRecipeBuilder.shapelessRecipe(individual, 9)
                .addIngredient(storage)
                .addCriterion("has_at_least_9_" + individualName, hasItem(MinMaxBounds.IntBound.atLeast(9), individual))
                .addCriterion("has_" + storageName, hasItem(storage))
                .build(consumer, this.modid + ":storage/" + individualName + suffix);
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, String packingSuffix, String unpackingSuffix) {

        generatePackingRecipe(consumer, storage, individual, packingSuffix);
        generateUnpackingRecipe(consumer, storage, individual, unpackingSuffix);
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, ITag.INamedTag<Item> tag, String packingSuffix, String unpackingSuffix) {

        generatePackingRecipe(consumer, storage, individual, tag, packingSuffix);
        generateUnpackingRecipe(consumer, storage, individual, unpackingSuffix);
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual, ITag.INamedTag<Item> tag) {

        generateStorageRecipes(consumer, storage, individual, tag, "", "_from_block");
    }

    protected void generateStorageRecipes(Consumer<IFinishedRecipe> consumer, Item storage, Item individual) {

        generateStorageRecipes(consumer, storage, individual, "", "_from_block");
    }

    protected void generateTypeRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String type) {

        Item ingot = reg.get(type + "_ingot");
        Item gem = reg.get(type);
        Item block = reg.get(type + "_block");
        Item nugget = reg.get(type + "_nugget");

        ITag.INamedTag<Item> ingotTag = forgeTag("ingots/" + type);
        ITag.INamedTag<Item> gemTag = forgeTag("gems/" + type);
        ITag.INamedTag<Item> nuggetTag = forgeTag("nuggets/" + type);

        if (block != null) {
            if (ingot != null) {
                generateStorageRecipes(consumer, block, ingot, ingotTag, "", "_from_block");
            } else if (gem != null) {
                generateStorageRecipes(consumer, block, gem, gemTag, "", "_from_block");
            }
        }
        if (nugget != null) {
            if (ingot != null) {
                generateStorageRecipes(consumer, ingot, nugget, nuggetTag, "_from_nuggets", "_from_ingot");
            } else if (gem != null) {
                generateStorageRecipes(consumer, gem, nugget, nuggetTag, "_from_nuggets", "_from_gem");
            }
        }
        generateGearRecipe(reg, consumer, type);
    }

    protected void generateGearRecipe(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String type) {

        Item gear = reg.get(type + "_gear");
        if (gear == null) {
            return;
        }
        Item ingot = reg.get(type + "_ingot");
        Item gem = reg.get(type);

        ITag.INamedTag<Item> ingotTag = forgeTag("ingots/" + type);
        ITag.INamedTag<Item> gemTag = forgeTag("gems/" + type);

        if (ingot != null) {
            ShapedRecipeBuilder.shapedRecipe(gear)
                    .key('#', ingotTag)
                    .key('i', Tags.Items.NUGGETS_IRON)
                    .patternLine(" # ")
                    .patternLine("#i#")
                    .patternLine(" # ")
                    .addCriterion("has_" + name(ingot), hasItem(ingotTag))
                    .build(consumer, this.modid + ":parts/" + name(gear));
        }
        if (gem != null) {
            ShapedRecipeBuilder.shapedRecipe(gear)
                    .key('#', gemTag)
                    .key('i', Tags.Items.NUGGETS_IRON)
                    .patternLine(" # ")
                    .patternLine("#i#")
                    .patternLine(" # ")
                    .addCriterion("has_" + name(gem), hasItem(gemTag))
                    .build(consumer, this.modid + ":parts/" + name(gear));
        }
    }

    protected void generateGearRecipe(Consumer<IFinishedRecipe> consumer, Item gear, Item material, ITag.INamedTag<Item> tag) {

        if (gear == null || material == null || tag == null) {
            return;
        }
        ShapedRecipeBuilder.shapedRecipe(gear)
                .key('#', tag)
                .key('i', Tags.Items.NUGGETS_IRON)
                .patternLine(" # ")
                .patternLine("#i#")
                .patternLine(" # ")
                .addCriterion("has_" + name(material), hasItem(tag))
                .build(consumer, this.modid + ":parts/" + name(gear));
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String material, float xp) {

        generateSmeltingAndBlastingRecipes(reg, consumer, material, xp, "smelting");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, String material, float xp, String folder) {

        Item ore = reg.get(material + "_ore");
        Item ingot = reg.get(material + "_ingot");
        Item gem = reg.get(material);
        Item dust = reg.get(material + "_dust");

        if (ingot != null) {
            if (dust != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, dust, ingot, 0, folder, "_dust");
            }
            if (ore != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, ore, ingot, xp, folder, "_ore");
            }
        } else if (gem != null) {
            if (ore != null) {
                generateSmeltingAndBlastingRecipes(reg, consumer, ore, gem, xp, folder, "_ore");
            }
        }
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp) {

        generateSmeltingAndBlastingRecipes(reg, consumer, input, output, xp, "", "");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp, String folder) {

        generateSmeltingAndBlastingRecipes(reg, consumer, input, output, xp, folder, "");
    }

    protected void generateSmeltingAndBlastingRecipes(DeferredRegisterCoFH<Item> reg, Consumer<IFinishedRecipe> consumer, Item input, Item output, float xp, String folder, String suffix) {

        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, xp, 200)
                .addCriterion("has_" + name(input), hasItem(input))
                .build(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_smelting");

        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, 100)
                .addCriterion("has_" + name(input), hasItem(input))
                .build(consumer, this.modid + ":" + folder + "/" + name(output) + "_from" + suffix + "_blasting");
    }

    // TODO: Change if Mojang implements some better defaults...
    public InventoryChangeTrigger.Instance hasItem(MinMaxBounds.IntBound amount, IItemProvider itemIn) {

        return hasItem(new ItemPredicate(null, itemIn.asItem(), amount, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.enchantments, EnchantmentPredicate.enchantments, null, NBTPredicate.ANY)); // ItemPredicate.Builder.create().item(itemIn).count(amount).build());
    }

    protected static Tags.IOptionalNamedTag<Item> forgeTag(String name) {

        return ItemTags.createOptional(new ResourceLocation(ID_FORGE, name));
    }

    protected static String name(Block block) {

        return block.getRegistryName() == null ? "" : block.getRegistryName().getPath();
    }

    protected static String name(Item item) {

        return item.getRegistryName() == null ? "" : item.getRegistryName().getPath();
    }
    // endregion

    // region CONDITIONS
    protected ICondition flagEnabled(FlagManager manager, String flag) {

        return new FlagRecipeCondition(manager, flag);
    }
    // endregion

    protected static class CompoundIngredientCoFH extends CompoundIngredient {

        public CompoundIngredientCoFH(List<Ingredient> children) {

            super(children);
        }

    }

}
