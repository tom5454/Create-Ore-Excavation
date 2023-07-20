package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public class ExtractorRecipeJS extends ExcavatingRecipeJS<ExtractorRecipeJS> {
	public static final RecipeKey<OutputFluid> RESULT = FluidComponents.OUTPUT.key("output");
	public static final RecipeKey<Component> NAME = ComponentComponent.INSTANCE.key("name");
	public static final RecipeKey<Integer> WEIGHT = NumberComponent.intRange(1, Integer.MAX_VALUE).key("weight");
	public static final RecipeKey<Integer> TICKS = NumberComponent.intRange(1, Integer.MAX_VALUE).key("ticks");

	public static final RecipeSchema SCHEMA = new RecipeSchema(ExtractorRecipeJS.class, ExtractorRecipeJS::new, RESULT, NAME, WEIGHT, TICKS);
}
