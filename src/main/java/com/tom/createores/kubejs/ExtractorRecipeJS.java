package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;

public class ExtractorRecipeJS extends ExcavatingRecipeJS<ExtractorRecipeJS> {
	private FluidStackJS fluid;

	@Override
	public void create(RecipeArguments args) {
		super.create(args);
		fluid = FluidStackJS.of(args.get(0));
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
		super.deserialize();
		fluid = FluidStackJS.fromJson(json.get("output"));
	}

	@Override
	public void serialize() {
		super.serialize();
		if(serializeOutputs)json.add("output", fluid.toJson());
	}
}
