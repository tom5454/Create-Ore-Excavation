package com.tom.createores.kubejs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import com.mojang.serialization.Codec;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum PlacementJS implements RecipeComponent<RandomSpreadStructurePlacementJS> {
	INSTANCE;

	public static final TypeInfo TYPE_INFO = TypeInfo.of(RandomSpreadStructurePlacementJS.class);
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.unit(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "placement"), INSTANCE);

	@Override
	public Codec<RandomSpreadStructurePlacementJS> codec() {
		return RandomSpreadStructurePlacement.CODEC.codec().xmap(RandomSpreadStructurePlacementJS::new, RandomSpreadStructurePlacementJS::map);
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE_INFO;
	}

	@Override
	public String toString() {
		return "coe:placement";
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}
}
