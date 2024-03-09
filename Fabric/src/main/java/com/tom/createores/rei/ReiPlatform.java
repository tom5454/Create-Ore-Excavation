package com.tom.createores.rei;

import java.util.List;

import com.simibubi.create.compat.rei.category.CreateRecipeCategory;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

public class ReiPlatform {

	public static dev.architectury.fluid.FluidStack wrapFluid(FluidStack output) {
		return CreateRecipeCategory.convertToREIFluid(output);
	}

	public static List<dev.architectury.fluid.FluidStack> wrapFluid(List<FluidStack> output) {
		return CreateRecipeCategory.convertToREIFluids(output);
	}

}
