package com.sihenzhang.simplebbq.integration.jei;

import com.sihenzhang.simplebbq.SimpleBBQ;
import com.sihenzhang.simplebbq.SimpleBBQConfig;
import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import org.jetbrains.annotations.NotNull;

public class CampfireCookingOnGrillCategory extends AbstractCookingWithoutFuelAndXpCategory<CampfireCookingRecipe> {
    public static final RecipeType<CampfireCookingRecipe> RECIPE_TYPE = RecipeType.create(SimpleBBQ.MOD_ID, "campfire_cooking_on_grill", CampfireCookingRecipe.class);

    public CampfireCookingOnGrillCategory(IGuiHelper guiHelper) {
        super(guiHelper, new DrawableDoubleItemStack(SimpleBBQRegistry.GRILL_BLOCK_ITEM.get().getDefaultInstance(), Items.CAMPFIRE.getDefaultInstance()), "category.campfire_cooking_on_grill", 400);
    }

    @Override
    public @NotNull RecipeType<CampfireCookingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    protected IDrawableAnimated getArrow(int cookingTime) {
        return super.getArrow(Mth.clamp((int) (cookingTime * SimpleBBQConfig.CAMPFIRE_COOKING_ON_GRILL_COOKING_TIME_MODIFIER.get()), Math.min(SimpleBBQConfig.CAMPFIRE_COOKING_ON_GRILL_MINIMUM_COOKING_TIME.get(), cookingTime), cookingTime));
    }

    @Override
    protected void drawCookingTime(int cookingTime, GuiGraphics guiGraphics) {
        super.drawCookingTime(Mth.clamp((int) (cookingTime * SimpleBBQConfig.CAMPFIRE_COOKING_ON_GRILL_COOKING_TIME_MODIFIER.get()), Math.min(SimpleBBQConfig.CAMPFIRE_COOKING_ON_GRILL_MINIMUM_COOKING_TIME.get(), cookingTime), cookingTime), guiGraphics);
    }
}
