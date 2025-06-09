package com.tom.createores.kubejs;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum ProcessingOutputJS implements RecipeComponent<ProcessingOutput> {
	INSTANCE;

	public static final TypeInfo TYPE = TypeInfo.of(ProcessingOutput.class);

	@Override
	public Codec<ProcessingOutput> codec() {
		return ProcessingOutput.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE;
	}

	@Override
	public String toString() {
		return "coe:output_stack";
	}

	static ProcessingOutput wrap(RegistryAccessContainer registries, Object o) {
		return new ProcessingOutput(ItemStackJS.wrap(registries, o), 1f);
	}
}
