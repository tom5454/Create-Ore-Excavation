package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public class DrillingRecipeJS extends ExcavatingRecipeJS<DrillingRecipeJS> {
	public static final RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("output");
	public static final RecipeKey<Component> NAME = ComponentComponent.INSTANCE.key("name");
	public static final RecipeKey<Integer> WEIGHT = NumberComponent.intRange(1, Integer.MAX_VALUE).key("weight");
	public static final RecipeKey<Integer> TICKS = NumberComponent.intRange(1, Integer.MAX_VALUE).key("ticks");

	public static final RecipeSchema SCHEMA = new RecipeSchema(DrillingRecipeJS.class, DrillingRecipeJS::new, RESULTS, NAME, WEIGHT, TICKS);

	public DrillingRecipeJS fluid(FluidStackJS fluid) {
		json.add("fluid", fluid.toJson());
		save();
		return this;
	}
}
