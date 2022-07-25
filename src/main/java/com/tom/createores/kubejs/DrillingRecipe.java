package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import com.google.gson.JsonArray;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

public class DrillingRecipe extends RecipeJS {

	@Override
	public void create(ListJS args) {
		inputItems.add(IngredientJS.of("#" + CreateOreExcavation.DRILL_TAG.location()));
		outputItems.addAll(parseResultItemList(args.get(0)));
		Component name = Component.Serializer.fromJson(args.get(1).toString());
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

	public DrillingRecipe fluid(FluidStackJS fluid) {
		json.add("fluid", fluid.toJson());
		save();
		return this;
	}

	public DrillingRecipe stress(int stress) {
		json.addProperty("stress", stress);
		save();
		return this;
	}

	public DrillingRecipe biomeWhitelist(String tag) {
		json.addProperty("biomeWhitelist", tag);
		save();
		return this;
	}

	public DrillingRecipe biomeBlacklist(String tag) {
		json.addProperty("biomeBlacklist", tag);
		save();
		return this;
	}

	public DrillingRecipe drill(IngredientJS drill) {
		inputItems.set(0, drill);
		serializeInputs = true;
		return this;
	}
}
