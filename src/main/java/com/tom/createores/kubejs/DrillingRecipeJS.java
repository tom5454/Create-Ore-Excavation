package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import com.google.gson.JsonArray;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.util.ListJS;

public class DrillingRecipeJS extends ExcavatingRecipeJS<DrillingRecipeJS> {

	@Override
	public void create(ListJS args) {
		super.create(args);
		inputItems.add(IngredientJS.of("#" + CreateOreExcavation.DRILL_TAG.location()));
		outputItems.addAll(parseResultItemList(args.get(0)));
		Component name = KubeJSExcavation.parseComponent(args.get(1));
		int weight = ((Number) args.get(2)).intValue();
		int ticks = ((Number) args.get(3)).intValue();

		if(weight < 1)throw new RecipeExceptionJS("Weight must be higher than 0");
		if(ticks < 1)throw new RecipeExceptionJS("Ticks must be higher than 0");

		json.addProperty("weight", weight);
		json.addProperty("ticks", ticks);
		json.addProperty("name", Component.Serializer.toJson(name));
	}

	@Override
	public void deserialize() {
		inputItems.add(IngredientJS.ingredientFromRecipeJson(json.get("drill")));
		outputItems.addAll(parseResultItemList(json.get("output")));
	}

	@Override
	public void serialize() {
		if (serializeInputs) {
			json.add("drill", inputItems.get(0).toJson());
		}
		if (serializeOutputs) {
			var results = new JsonArray();

			for (var out : outputItems) {
				results.add(out.toResultJson());
			}

			json.add("output", results);
		}
	}

	public DrillingRecipeJS fluid(FluidStackJS fluid) {
		json.add("fluid", fluid.toJson());
		save();
		return this;
	}
}
