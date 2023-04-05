package com.tom.createores.jei;

import java.util.List;

import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.ingredients.IIngredientType;

public class JeiPlatform {
	public static final IIngredientType<FluidStack> FLUID_STACK = ForgeTypes.FLUID_STACK;

	public static FluidStack wrapFluid(FluidStack f) {
		return f;
	}

	public static List<FluidStack> wrapFluid(List<FluidStack> f) {
		return f;
	}
}
