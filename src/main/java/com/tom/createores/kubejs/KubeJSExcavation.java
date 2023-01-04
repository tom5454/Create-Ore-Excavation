package com.tom.createores.kubejs;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;

public class KubeJSExcavation extends KubeJSPlugin {

	@Override
	public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
		event.register(CreateOreExcavation.DRILLING_RECIPES.getId(), DrillingRecipeJS::new);
		event.register(CreateOreExcavation.EXTRACTING_RECIPES.getId(), ExtractorRecipeJS::new);
		CreateOreExcavation.LOGGER.info("Loaded KubeJS integration");
	}
}
