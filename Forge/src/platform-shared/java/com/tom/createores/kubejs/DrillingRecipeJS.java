package com.tom.createores.kubejs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.google.gson.JsonArray;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;

public class DrillingRecipeJS extends ExcavatingRecipeJS<DrillingRecipeJS> {
	private List<ItemStack> outputItems = new ArrayList<>();

	@Override
	public void create(RecipeArguments args) {
		super.create(args);
		outputItems.addAll(parseItemOutputList(args.get(0)));
		Component name = KubeJSExcavation.parseComponent(args.get(1));
		int weight = ((Number) args.get(2)).intValue();
		int ticks = ((Number) args.get(3)).intValue();

		if(weight < 1)throw new RecipeExceptionJS("Weight must be higher than 0");
		if(ticks < 1)throw new RecipeExceptionJS("Ticks must be higher than 0");

		json.addProperty("weight", weight);
		json.addProperty("ticks", ticks);
		json.addProperty("name", Component.Serializer.toJson(name));
		serializeOutputs = true;
	}

	@Override
	public void deserialize() {
		super.deserialize();
		outputItems.addAll(parseItemOutputList(json.get("output")));
	}

	@Override
	public void serialize() {
		super.serialize();
		if (serializeOutputs) {
			var results = new JsonArray();

			for (var out : outputItems) {
				results.add(itemToJson(out));
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
