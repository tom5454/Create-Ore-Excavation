package com.tom.createores.jei;

import java.util.List;

import net.neoforged.neoforge.fluids.FluidStack;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;

public class JeiPlatform {
	public static final IIngredientType<FluidStack> FLUID_STACK = NeoForgeTypes.FLUID_STACK;

	public static FluidStack wrapFluid(FluidStack f) {
		return f;
	}

	public static List<FluidStack> wrapFluid(List<FluidStack> f) {
		return f;
	}
}
