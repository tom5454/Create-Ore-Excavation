package com.tom.createores.kubejs;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;

public class KubeJSExcavation extends KubeJSPlugin {

	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.register(CreateOreExcavation.DRILLING_RECIPES.getId(), DrillingRecipeJS.SCHEMA);
		event.register(CreateOreExcavation.EXTRACTING_RECIPES.getId(), ExtractorRecipeJS.SCHEMA);
		event.register(CreateOreExcavation.VEIN_RECIPES.getId(), VeinRecipeJS.SCHEMA);
		CreateOreExcavation.LOGGER.info("Loaded KubeJS integration");
	}

}
