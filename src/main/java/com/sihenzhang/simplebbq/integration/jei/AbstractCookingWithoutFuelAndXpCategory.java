package com.sihenzhang.simplebbq.integration.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sihenzhang.simplebbq.util.I18nUtils;
import com.sihenzhang.simplebbq.util.RecipeUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCookingWithoutFuelAndXpCategory<T extends AbstractCookingRecipe> implements IRecipeCategory<T> {
    private final IDrawableAnimated animatedFlame;
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    private final int defaultCookingTime;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    public AbstractCookingWithoutFuelAndXpCategory(IGuiHelper guiHelper, IDrawable icon, String categoryKey, int defaultCookingTime) {
        this.animatedFlame = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(ModIntegrationJei.RECIPE_GUI_VANILLA, 82, 114, 14, 14), 300, IDrawableAnimated.StartDirection.TOP, true);
        this.background = guiHelper.drawableBuilder(ModIntegrationJei.RECIPE_GUI_VANILLA, 0, 186, 82, 34).addPadding(0, 10, 0, 0).build();
        this.icon = icon;
        this.title = I18nUtils.createIntegrationComponent(ModIntegrationJei.MOD_ID, categoryKey);
        this.defaultCookingTime = defaultCookingTime;
        this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<>() {
            @Override
            public IDrawableAnimated load(Integer cookingTime) {
                return guiHelper.drawableBuilder(ModIntegrationJei.RECIPE_GUI_VANILLA, 82, 128, 24, 17).buildAnimated(cookingTime, IDrawableAnimated.StartDirection.LEFT, false);
            }
        });
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
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
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9).addItemStack(RecipeUtils.getResultItem(recipe));
    }

    @Override
    public void draw(T recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics stack, double mouseX, double mouseY) {
        animatedFlame.draw(stack, 1, 20);

        var cookingTime = recipe.getCookingTime();

        this.getArrow(cookingTime).draw(stack, 24, 8);

        drawCookingTime(cookingTime, stack);
    }

    protected IDrawableAnimated getArrow(int cookingTime) {
        if (cookingTime <= 0) {
            cookingTime = defaultCookingTime;
        }
        return this.cachedArrows.getUnchecked(cookingTime);
    }

    protected void drawCookingTime(int cookingTime, GuiGraphics guiGraphics) {
        if (cookingTime <= 0) {
            return;
        }
        var cookingTimeSeconds = cookingTime / 20;
        var timeText = I18nUtils.createComponent("gui", ModIntegrationJei.MOD_ID, "category.smelting.time.seconds", cookingTimeSeconds);
        var fontRenderer = Minecraft.getInstance().font;
        var stringWidth = fontRenderer.width(timeText);
        guiGraphics.drawString(fontRenderer, timeText, background.getWidth() - stringWidth, 35, 0xFF808080);
    }
}
