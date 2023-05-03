package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.ComponentWrapper;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;

public class KubeJSExcavation extends KubeJSPlugin {

	@Override
	public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
		event.register(CreateOreExcavation.DRILLING_RECIPES.getId(), DrillingRecipeJS::new);
		event.register(CreateOreExcavation.EXTRACTING_RECIPES.getId(), ExtractorRecipeJS::new);
		CreateOreExcavation.LOGGER.info("Loaded KubeJS integration");
	}

	public static Component parseComponent(Object comp) {
		if(comp instanceof String) {
			try {
				return Component.Serializer.fromJson((String) comp);
			} catch (Exception e) {
			}
		}
		return ComponentWrapper.of(comp);
	}
}
