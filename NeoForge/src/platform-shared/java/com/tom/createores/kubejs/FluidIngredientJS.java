package com.tom.createores.kubejs;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum FluidIngredientJS implements RecipeComponent<FluidIngredient> {
	INSTANCE;

	public static final TypeInfo TYPE = TypeInfo.of(FluidIngredient.class);

	@Override
	public Codec<FluidIngredient> codec() {
		return FluidIngredient.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE;
	}

	static FluidIngredient wrap(RegistryAccessContainer registries, Object o) {
		var ingr = FluidWrapper.wrapSizedIngredient(registries, o);
		if (ingr.ingredient() instanceof TagFluidIngredient f) {
			return FluidIngredient.fromTag(f.tag(), ingr.amount());
		}
		FluidStack[] fluids = ingr.ingredient().getStacks();
		if (fluids.length == 1) {
			return FluidIngredient.fromFluidStack(fluids[0]);
		} else {
			throw new RuntimeException("Can't use multiple fluids in fluid ingredient");
		}
	}
}
