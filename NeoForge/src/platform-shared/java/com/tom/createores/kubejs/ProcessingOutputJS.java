package com.tom.createores.kubejs;

import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.create.recipe.ProcessingOutputRecipeComponent;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum ProcessingOutputJS implements RecipeComponent<ProcessingOutput> {
	INSTANCE;

	public static final TypeInfo TYPE_INFO = TypeInfo.of(ProcessingOutput.class);
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.unit(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "output_stack"), INSTANCE);

	@Override
	public Codec<ProcessingOutput> codec() {
		return ProcessingOutput.CODEC_NEW;
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE_INFO;
	}

	@Override
	public String toString() {
		return "coe:output_stack";
	}

	static ProcessingOutput wrap(Context registries, Object o) {
		return new ProcessingOutput(ItemWrapper.wrap(registries, o), 1f);
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	public static RecipeComponent<ProcessingOutput> instance() {
		return CreateOreExcavation.kubeJSCreate ? ProcessingOutputRecipeComponent.TYPE.instance() : INSTANCE;
	}
}
