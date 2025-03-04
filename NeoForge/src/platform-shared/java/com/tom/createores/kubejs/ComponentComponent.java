package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import com.mojang.serialization.Codec;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum ComponentComponent implements RecipeComponent<Component> {
	INSTANCE
	;

	public static final TypeInfo COMPONENT = TypeInfo.of(Component.class);

	@Override
	public Codec<Component> codec() {
		return ComponentSerialization.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return COMPONENT;
	}
}
