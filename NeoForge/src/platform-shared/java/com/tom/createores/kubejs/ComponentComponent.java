package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum ComponentComponent implements RecipeComponent<Component> {
	INSTANCE
	;

	public static final TypeInfo COMPONENT = TypeInfo.of(Component.class);
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.unit(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "component"), INSTANCE);

	@Override
	public Codec<Component> codec() {
		return ComponentSerialization.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return COMPONENT;
	}

	@Override
	public String toString() {
		return "component";
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}
}
