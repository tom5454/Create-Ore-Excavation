package com.tom.createores.kubejs;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;

public class KubeJSExcavation implements KubeJSPlugin {

	@Override
	public void registerRecipeSchemas(RecipeSchemaRegistry event) {
		event.register(CreateOreExcavation.DRILLING_RECIPES.getId(), DrillingRecipeJS.SCHEMA);
		event.register(CreateOreExcavation.EXTRACTING_RECIPES.getId(), ExtractorRecipeJS.SCHEMA);
		event.register(CreateOreExcavation.VEIN_RECIPES.getId(), VeinRecipeJS.SCHEMA);
		CreateOreExcavation.LOGGER.info("Loaded KubeJS integration");
	}

	@Override
	public void registerRecipeComponents(RecipeComponentFactoryRegistry registry) {
		registry.register(ComponentComponent.INSTANCE);
	}
}
