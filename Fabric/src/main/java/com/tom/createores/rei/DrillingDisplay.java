package com.tom.createores.rei;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import com.tom.createores.recipe.DrillingRecipe;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class DrillingDisplay extends CreateDisplay<DrillingRecipe> {

	public DrillingDisplay(DrillingRecipe recipe) {
		super(recipe, REIPlugin.DRILLING,
				getInputs(recipe),
				getOutputs(recipe)
				);
	}

	private static List<EntryIngredient> getInputs(DrillingRecipe recipe) {
		List<EntryIngredient> input = new ArrayList<>();
		input.add(EntryIngredients.ofIngredient(recipe.drill));
		if(recipe.drillingFluid != FluidIngredient.EMPTY)
			input.add(EntryIngredients.of(VanillaEntryTypes.FLUID, ReiPlatform.wrapFluid(recipe.drillingFluid.getMatchingFluidStacks())));
		return input;
	}

	private static List<EntryIngredient> getOutputs(DrillingRecipe recipe) {
		List<EntryIngredient> input = new ArrayList<>();
		for (ProcessingOutput p : recipe.output) {
			input.add(EntryIngredients.of(p.getStack()));
		}
		return input;
	}
}
