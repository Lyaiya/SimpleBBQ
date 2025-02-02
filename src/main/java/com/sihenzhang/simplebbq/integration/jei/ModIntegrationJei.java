package com.sihenzhang.simplebbq.integration.jei;

import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import com.sihenzhang.simplebbq.util.I18nUtils;
import com.sihenzhang.simplebbq.util.RLUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class ModIntegrationJei implements IModPlugin {
    public static final String MOD_ID = "jei";
    public static final ResourceLocation RECIPE_GUI_VANILLA = RLUtils.createRL(MOD_ID, "textures/jei/gui/gui_vanilla.png");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return RLUtils.createRL("simple_bbq");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new GrillCookingCategory(guiHelper));
        registration.addRecipeCategories(new CampfireCookingOnGrillCategory(guiHelper));
        registration.addRecipeCategories(new SeasoningCategory(guiHelper));
        registration.addRecipeCategories(new SkeweringCategory(guiHelper));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var recipeManager = level.getRecipeManager();
        registration.addRecipes(GrillCookingCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(SimpleBBQRegistry.GRILL_COOKING_RECIPE_TYPE.get()));
        registration.addRecipes(CampfireCookingOnGrillCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(RecipeType.CAMPFIRE_COOKING));
        registration.addRecipes(SeasoningCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(SimpleBBQRegistry.SEASONING_RECIPE_TYPE.get()));
        registration.addRecipes(SkeweringCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(SimpleBBQRegistry.SKEWERING_RECIPE_TYPE.get()));

        registration.addIngredientInfo(
                List.of(
                        SimpleBBQRegistry.CHILI_POWDER.get().getDefaultInstance(),
                        SimpleBBQRegistry.CUMIN.get().getDefaultInstance(),
                        SimpleBBQRegistry.SALT_AND_PEPPER.get().getDefaultInstance()
                ),
                VanillaTypes.ITEM_STACK,
                I18nUtils.createIntegrationComponent(MOD_ID, "description.seasoning")
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(SimpleBBQRegistry.GRILL_BLOCK_ITEM.get().getDefaultInstance(), GrillCookingCategory.RECIPE_TYPE, CampfireCookingOnGrillCategory.RECIPE_TYPE, SeasoningCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(SimpleBBQRegistry.SKEWERING_TABLE_BLOCK_ITEM.get().getDefaultInstance(), SkeweringCategory.RECIPE_TYPE);
    }
}
