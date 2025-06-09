package com.tom.createores.kubejs;

import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import com.mojang.serialization.Codec;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum PlacementJS implements RecipeComponent<RandomSpreadStructurePlacementJS> {
	INSTANCE;

	public static final TypeInfo TYPE = TypeInfo.of(RandomSpreadStructurePlacementJS.class);

	@Override
	public Codec<RandomSpreadStructurePlacementJS> codec() {
		return RandomSpreadStructurePlacement.CODEC.codec().xmap(RandomSpreadStructurePlacementJS::new, RandomSpreadStructurePlacementJS::map);
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE;
	}

	@Override
	public String toString() {
		return "coe:placement";
	}
}
