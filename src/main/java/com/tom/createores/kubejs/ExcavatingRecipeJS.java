package com.tom.createores.kubejs;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

@SuppressWarnings("unchecked")
public abstract class ExcavatingRecipeJS<T extends ExcavatingRecipeJS<T>> extends RecipeJS {

	@Override
	public void create(ListJS arg0) {
		json.addProperty("amountMin", 1);
		json.addProperty("amountMax", 2);
	}

	public T biomeWhitelist(String tag) {
		json.addProperty("biomeWhitelist", tag);
		save();
		return (T) this;
	}

	public T biomeBlacklist(String tag) {
		json.addProperty("biomeBlacklist", tag);
		save();
		return (T) this;
	}

	public T drill(IngredientJS drill) {
		inputItems.set(0, drill);
		serializeInputs = true;
		return (T) this;
	}

	public T stress(int stress) {
		json.addProperty("stress", stress);
		save();
		return (T) this;
	}

	public T alwaysInfinite() {
		json.addProperty("neverFinite", true);
		json.remove("alwaysFinite");
		save();
		return (T) this;
	}

	public T alwaysFinite() {
		json.addProperty("alwaysFinite", true);
		json.remove("neverFinite");
		save();
		return (T) this;
	}

	public T defaultFinite() {
		json.remove("neverFinite");
		json.remove("alwaysFinite");
		save();
		return (T) this;
	}

	public T veinSize(float min, float max) {
		json.addProperty("amountMin", min);
		json.addProperty("amountMax", max);
		save();
		return (T) this;
	}
}
