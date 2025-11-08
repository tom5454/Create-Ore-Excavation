package com.tom.createores.kubejs;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

import com.mojang.serialization.Codec;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.util.FluidIngredient;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

public enum FluidIngredientJS implements RecipeComponent<FluidIngredient> {
	INSTANCE;

	public static final TypeInfo TYPE_INFO = TypeInfo.of(FluidIngredient.class);
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.unit(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "fluid_ingredient"), INSTANCE);

	@Override
	public Codec<FluidIngredient> codec() {
		return FluidIngredient.CODEC;
	}

	@Override
	public TypeInfo typeInfo() {
		return TYPE_INFO;
	}

	static FluidIngredient wrap(Context registries, Object o) {
		var ingr = FluidWrapper.wrapSizedIngredient(registries, o);
		if (ingr.ingredient() instanceof TagFluidIngredient f) {
			return FluidIngredient.fromTag(f.tag(), ingr.amount());
		}
		FluidStack[] fluids = ingr.ingredient().getStacks();
		if (fluids.length == 1) {
			return FluidIngredient.fromFluidStack(fluids[0].copyWithAmount(ingr.amount()));
		} else {
			throw new RuntimeException("Can't use multiple fluids in fluid ingredient");
		}
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}
}
