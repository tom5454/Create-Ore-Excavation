package com.tom.createores.rei;

import java.util.Collections;
import java.util.List;

import com.simibubi.create.compat.rei.display.CreateDisplay;

import com.tom.createores.recipe.VeinRecipe;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;

public class VeinDisplay extends CreateDisplay<VeinRecipe> {

	public VeinDisplay(VeinRecipe recipe) {
		super(recipe, REIPlugin.VEINS,
				Collections.emptyList(),
				List.of(EntryIngredient.of(EntryStack.of(REIPlugin.VEIN_TYPE, recipe)))
				);
	}

}
