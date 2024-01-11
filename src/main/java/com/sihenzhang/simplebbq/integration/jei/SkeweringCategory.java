package com.sihenzhang.simplebbq.integration.jei;

import com.sihenzhang.simplebbq.SimpleBBQ;
import com.sihenzhang.simplebbq.SimpleBBQRegistry;
import com.sihenzhang.simplebbq.recipe.SkeweringRecipe;
import com.sihenzhang.simplebbq.tag.SimpleBBQItemTags;
import com.sihenzhang.simplebbq.util.I18nUtils;
import com.sihenzhang.simplebbq.util.RecipeUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class SkeweringCategory implements IRecipeCategory<SkeweringRecipe> {
    public static final RecipeType<SkeweringRecipe> RECIPE_TYPE = RecipeType.create(SimpleBBQ.MOD_ID, "skewering", SkeweringRecipe.class);
    private final IDrawable background;
    private final IDrawable icon;

    public SkeweringCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(ModIntegrationJei.RECIPE_GUI_VANILLA, 0, 168, 125, 18);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, SimpleBBQRegistry.SKEWERING_TABLE_BLOCK_ITEM.get().getDefaultInstance());
    }

    @Override
    public @NotNull RecipeType<SkeweringRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return I18nUtils.createIntegrationComponent(ModIntegrationJei.MOD_ID, "category.skewering");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SkeweringRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 1).addIngredients(Ingredient.of(SimpleBBQItemTags.SKEWER));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1).addItemStack(RecipeUtils.getResultItem(recipe));
    }
}
