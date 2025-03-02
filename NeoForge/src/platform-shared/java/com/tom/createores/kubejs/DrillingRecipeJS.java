package com.tom.createores.kubejs;

import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public class DrillingRecipeJS extends ExcavatingRecipeJS<DrillingRecipeJS> {
	public static final RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("output");
	public static final RecipeKey<String> VEIN_NAME = StringComponent.ID.key("vein_id");
	public static final RecipeKey<Integer> TICKS = NumberComponent.intRange(1, Integer.MAX_VALUE).key("ticks");

	public static final RecipeSchema SCHEMA = new RecipeSchema(DrillingRecipeJS.class, DrillingRecipeJS::new, RESULTS, VEIN_NAME, TICKS);
}
