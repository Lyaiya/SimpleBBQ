package com.sihenzhang.simplebbq.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUtils {
    private RecipeUtils() {
    }

    public static ItemStack getResultItem(Recipe<?> recipe) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        } else {
            RegistryAccess registryAccess = level.registryAccess();
            return recipe.getResultItem(registryAccess);
        }
    }
}
