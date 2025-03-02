package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;

public enum ComponentComponent implements RecipeComponent<Component> {
	INSTANCE
	;

	@Override
	public Class<?> componentClass() {
		return Component.class;
	}

	@Override
	public JsonElement write(RecipeJS recipe, Component value) {
		return new JsonPrimitive(Component.Serializer.toJson(value));
	}

	@Override
	public Component read(RecipeJS recipe, Object comp) {
		if(comp instanceof String) {
			try {
				return Component.Serializer.fromJson((String) comp);
			} catch (Exception e) {
			}
		}
		return TextWrapper.of(comp);
	}

}
