package com.tom.createores.jei;

import java.util.List;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import mezz.jei.api.fabric.constants.FabricTypes;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.ingredients.IIngredientType;

public class JeiPlatform {
	public static final IIngredientType<IJeiFluidIngredient> FLUID_STACK;

	static {
		try {
			FLUID_STACK = (IIngredientType<IJeiFluidIngredient>) FabricTypes.class.getDeclaredField("FLUID_STACK").get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException("FabricTypes.FLUID_STACK not found", e);
		}
	}

	public static IJeiFluidIngredient wrapFluid(FluidStack f) {
		return CreateRecipeCategory.toJei(f);
	}

	public static List<IJeiFluidIngredient> wrapFluid(List<FluidStack> f) {
		return CreateRecipeCategory.toJei(f);
	}
}
