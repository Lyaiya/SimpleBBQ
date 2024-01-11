package com.sihenzhang.simplebbq.data;

import com.sihenzhang.simplebbq.SimpleBBQ;
import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import com.sihenzhang.simplebbq.data.recipes.GrillCookingRecipeBuilder;
import com.sihenzhang.simplebbq.data.recipes.SeasoningRecipeBuilder;
import com.sihenzhang.simplebbq.data.recipes.SkeweringRecipeBuilder;
import com.sihenzhang.simplebbq.tag.SimpleBBQItemTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SimpleBBQRecipeProvider extends RecipeProvider {
    public SimpleBBQRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        // crafting recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SimpleBBQRegistry.GRILL_BLOCK.get())
                .define('#', Blocks.IRON_TRAPDOOR)
                .define('X', Tags.Items.INGOTS_IRON)
                .define('I', Tags.Items.RODS_WOODEN)
                .pattern("X#X")
                .pattern("I I")
                .pattern("I I")
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(consumer);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SimpleBBQRegistry.SKEWERING_TABLE_BLOCK.get())
                .define('#', ItemTags.PLANKS)
                .define('_', Blocks.SMOOTH_STONE_SLAB)
                .pattern("__")
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_stone_slab", has(Blocks.SMOOTH_STONE_SLAB))
                .save(consumer);

        // skewering recipe
        skeweringRecipe(consumer, Items.BEEF, SimpleBBQRegistry.BEEF_SKEWER.get());
        skeweringRecipe(consumer, Items.CHICKEN, SimpleBBQRegistry.CHICKEN_SKEWER.get());
        skeweringRecipe(consumer, Items.MUTTON, SimpleBBQRegistry.MUTTON_SKEWER.get());
        skeweringRecipe(consumer, Items.PORKCHOP, SimpleBBQRegistry.PORK_SKEWER.get());
        skeweringRecipe(consumer, Items.RABBIT, SimpleBBQRegistry.RABBIT_SKEWER.get());
        skeweringRecipe(consumer, Items.COD, SimpleBBQRegistry.COD_SKEWER.get());
        skeweringRecipe(consumer, Items.SALMON, SimpleBBQRegistry.SALMON_SKEWER.get());
        skeweringRecipe(consumer, Items.BREAD, SimpleBBQRegistry.BREAD_SLICE_SKEWER.get());
        skeweringRecipe(consumer, Items.BROWN_MUSHROOM, SimpleBBQRegistry.MUSHROOM_SKEWER.get());
        skeweringRecipe(consumer, Items.POTATO, SimpleBBQRegistry.POTATO_SKEWER.get());

        // skewer cooking recipe
        grillCookingRecipe(consumer, SimpleBBQRegistry.BEEF_SKEWER.get(), SimpleBBQRegistry.COOKED_BEEF_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.CHICKEN_SKEWER.get(), SimpleBBQRegistry.COOKED_CHICKEN_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.MUTTON_SKEWER.get(), SimpleBBQRegistry.COOKED_MUTTON_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.PORK_SKEWER.get(), SimpleBBQRegistry.COOKED_PORK_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.RABBIT_SKEWER.get(), SimpleBBQRegistry.COOKED_RABBIT_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.COD_SKEWER.get(), SimpleBBQRegistry.COOKED_COD_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.SALMON_SKEWER.get(), SimpleBBQRegistry.COOKED_SALMON_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.BREAD_SLICE_SKEWER.get(), SimpleBBQRegistry.TOAST_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.MUSHROOM_SKEWER.get(), SimpleBBQRegistry.ROASTED_MUSHROOM_SKEWER.get(), 300);
        grillCookingRecipe(consumer, SimpleBBQRegistry.POTATO_SKEWER.get(), SimpleBBQRegistry.BAKED_POTATO_SKEWER.get(), 300);

        // seasoning recipe
        seasoningRecipe(consumer, Ingredient.of(SimpleBBQItemTags.CAN_BE_SEASONED_BY_HONEY), Items.HONEY_BOTTLE, "honey");
        seasoningRecipe(consumer, Ingredient.of(SimpleBBQItemTags.CAN_BE_SEASONED_BY_CHILI_POWDER), SimpleBBQRegistry.CHILI_POWDER.get());
        seasoningRecipe(consumer, Ingredient.of(SimpleBBQItemTags.CAN_BE_SEASONED_BY_CUMIN), SimpleBBQRegistry.CUMIN.get());
        seasoningRecipe(consumer, Ingredient.of(SimpleBBQItemTags.CAN_BE_SEASONED_BY_SALT_AND_PEPPER), SimpleBBQRegistry.SALT_AND_PEPPER.get());
    }

    protected static void grillCookingRecipe(Consumer<FinishedRecipe> consumer, ItemLike pIngredient, ItemLike pResult, int pCookingTime) {
        GrillCookingRecipeBuilder.cooking(Ingredient.of(pIngredient), pResult, pCookingTime).save(consumer, getSimpleRecipeName("grill_cooking", pResult));
    }

    protected static void skeweringRecipe(Consumer<FinishedRecipe> consumer, ItemLike pIngredient, int pIngredientCount, ItemLike pResult, int pResultCount) {
        SkeweringRecipeBuilder.skewering(Ingredient.of(pIngredient), pIngredientCount, pResult, pResultCount).save(consumer, getSimpleRecipeName("skewering", pResult));
    }

    protected static void skeweringRecipe(Consumer<FinishedRecipe> consumer, ItemLike pIngredient, int pIngredientCount, ItemLike pResult) {
        SkeweringRecipeBuilder.skewering(Ingredient.of(pIngredient), pIngredientCount, pResult).save(consumer, getSimpleRecipeName("skewering", pResult));
    }

    protected static void skeweringRecipe(Consumer<FinishedRecipe> consumer, ItemLike pIngredient, ItemLike pResult, int pResultCount) {
        SkeweringRecipeBuilder.skewering(Ingredient.of(pIngredient), pResult, pResultCount).save(consumer, getSimpleRecipeName("skewering", pResult));
    }

    protected static void skeweringRecipe(Consumer<FinishedRecipe> consumer, ItemLike pIngredient, ItemLike pResult) {
        SkeweringRecipeBuilder.skewering(Ingredient.of(pIngredient), pResult).save(consumer, getSimpleRecipeName("skewering", pResult));
    }

    protected static void seasoningRecipe(Consumer<FinishedRecipe> consumer, Ingredient pIngredient, ItemLike pSeasoning) {
        SeasoningRecipeBuilder.seasoning(pIngredient, Ingredient.of(pSeasoning), getItemName(pSeasoning)).save(consumer, getSimpleRecipeName("seasoning", getItemName(pSeasoning)));
    }

    protected static void seasoningRecipe(Consumer<FinishedRecipe> consumer, Ingredient pIngredient, ItemLike pSeasoning, String name) {
        SeasoningRecipeBuilder.seasoning(pIngredient, Ingredient.of(pSeasoning), name).save(consumer, getSimpleRecipeName("seasoning", name));
    }

    protected static String getSimpleRecipeName(ItemLike pItemLike) {
        return getSimpleRecipeName(getItemName(pItemLike));
    }

    protected static String getSimpleRecipeName(String name) {
        return SimpleBBQ.MOD_ID + ":" + name;
    }

    protected static String getSimpleRecipeName(String pRecipeType, ItemLike pItemLike) {
        return getSimpleRecipeName(pRecipeType, getItemName(pItemLike));
    }

    protected static String getSimpleRecipeName(String pRecipeType, String name) {
        return SimpleBBQ.MOD_ID + ":" + pRecipeType + "/" + name;
    }
}
