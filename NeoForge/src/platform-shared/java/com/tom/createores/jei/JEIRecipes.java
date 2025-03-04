package com.tom.createores.jei;

import net.minecraft.world.item.crafting.RecipeHolder;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.VeinRecipe;

import mezz.jei.api.recipe.RecipeType;

public class JEIRecipes {
	public static final RecipeType<RecipeHolder<DrillingRecipe>> DRILLING = RecipeType.createFromVanilla(CreateOreExcavation.DRILLING_RECIPES.getRecipeType());
	public static final RecipeType<RecipeHolder<ExtractorRecipe>> EXTRACTING = RecipeType.createFromVanilla(CreateOreExcavation.EXTRACTING_RECIPES.getRecipeType());
	public static final RecipeType<RecipeHolder<VeinRecipe>> VEINS = RecipeType.createFromVanilla(CreateOreExcavation.VEIN_RECIPES.getRecipeType());
}
