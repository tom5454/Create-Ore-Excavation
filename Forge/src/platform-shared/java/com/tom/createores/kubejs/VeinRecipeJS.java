package com.tom.createores.kubejs;

import net.minecraft.network.chat.Component;

import com.google.gson.JsonObject;

import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public class VeinRecipeJS extends RecipeJS {
	public static final RecipeKey<OutputItem> ICON = ItemComponents.OUTPUT.key("icon");
	public static final RecipeKey<Component> NAME = ComponentComponent.INSTANCE.key("name");

	public static final RecipeSchema SCHEMA = new RecipeSchema(VeinRecipeJS.class, VeinRecipeJS::new, NAME, ICON);

	@Override
	public void initValues(boolean created) {
		if(created) {
			json.add("placement", new JsonObject());
			JsonObject p = json.getAsJsonObject("placement");
			p.addProperty("salt", 0);
			p.addProperty("separation", 8);
			p.addProperty("spacing", 64);
		}
		super.initValues(created);
	}

	public VeinRecipeJS alwaysInfinite() {
		json.addProperty("neverFinite", true);
		json.remove("alwaysFinite");
		save();
		return this;
	}

	public VeinRecipeJS alwaysFinite() {
		json.addProperty("alwaysFinite", true);
		json.remove("neverFinite");
		save();
		return this;
	}

	public VeinRecipeJS defaultFinite() {
		json.remove("neverFinite");
		json.remove("alwaysFinite");
		save();
		return this;
	}

	public VeinRecipeJS veinSize(float min, float max) {
		json.addProperty("amountMin", min);
		json.addProperty("amountMax", max);
		save();
		return this;
	}

	public VeinRecipeJS biomeWhitelist(String tag) {
		json.addProperty("biomeWhitelist", tag);
		save();
		return  this;
	}

	public VeinRecipeJS biomeBlacklist(String tag) {
		json.addProperty("biomeBlacklist", tag);
		save();
		return this;
	}

	public VeinRecipeJS placement(int spacing, int separation, int salt) {
		JsonObject p = json.getAsJsonObject("placement");
		p.addProperty("salt", salt);
		p.addProperty("separation", separation);
		p.addProperty("spacing", spacing);
		save();
		return this;
	}

	public VeinRecipeJS spread(String spread) {
		JsonObject p = json.getAsJsonObject("placement");
		p.addProperty("spread_type", spread);
		save();
		return this;
	}

	public VeinRecipeJS reduction(String freqReduction) {
		JsonObject p = json.getAsJsonObject("placement");
		p.addProperty("frequency_reduction_method", freqReduction);
		save();
		return this;
	}

	public VeinRecipeJS priority(int priority) {
		json.addProperty("priority", priority);
		save();
		return this;
	}
}
