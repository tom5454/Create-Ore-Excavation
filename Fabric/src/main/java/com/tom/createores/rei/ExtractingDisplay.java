package com.tom.createores.rei;

import java.util.List;

import com.simibubi.create.compat.rei.display.CreateDisplay;

import com.tom.createores.recipe.ExtractorRecipe;

import me.shedaniel.rei.api.common.util.EntryIngredients;

public class ExtractingDisplay extends CreateDisplay<ExtractorRecipe> {

	public ExtractingDisplay(ExtractorRecipe recipe) {
		super(recipe, REIPlugin.EXTRACTING,
				List.of(EntryIngredients.ofIngredient(recipe.drill)),
				List.of(EntryIngredients.of(ReiPlatform.wrapFluid(recipe.output)))
				);
	}

}
