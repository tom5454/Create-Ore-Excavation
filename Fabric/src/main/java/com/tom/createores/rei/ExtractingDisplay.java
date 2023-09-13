package com.tom.createores.rei;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import com.tom.createores.recipe.ExtractorRecipe;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class ExtractingDisplay extends CreateDisplay<ExtractorRecipe> {

	public ExtractingDisplay(ExtractorRecipe recipe) {
		super(recipe, REIPlugin.EXTRACTING,
				getInputs(recipe),
				List.of(EntryIngredients.of(ReiPlatform.wrapFluid(recipe.output)))
				);
	}

	private static List<EntryIngredient> getInputs(ExtractorRecipe recipe) {
		List<EntryIngredient> input = new ArrayList<>();
		input.add(EntryIngredients.ofIngredient(recipe.drill));
		if(recipe.drillingFluid != FluidIngredient.EMPTY)
			input.add(EntryIngredients.of(VanillaEntryTypes.FLUID, ReiPlatform.wrapFluid(recipe.drillingFluid.getMatchingFluidStacks())));
		return input;
	}
}
