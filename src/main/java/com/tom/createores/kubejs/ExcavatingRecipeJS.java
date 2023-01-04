package com.tom.createores.kubejs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.recipe.IngredientMatch;
import dev.latvian.mods.kubejs.recipe.ItemInputTransformer;
import dev.latvian.mods.kubejs.recipe.ItemOutputTransformer;
import dev.latvian.mods.kubejs.recipe.RecipeArguments;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

@SuppressWarnings("unchecked")
public abstract class ExcavatingRecipeJS<T extends ExcavatingRecipeJS<T>> extends RecipeJS {
	private Ingredient drill;

	@Override
	public void create(RecipeArguments arg0) {
		drill = Ingredient.of(CreateOreExcavation.DRILL_TAG);
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

	public T drill(Ingredient drill) {
		this.drill = drill;
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

	@Override
	public void deserialize() {
		drill = parseItemInput(json.get("drill"), "drill");
	}

	@Override
	public void serialize() {
		if(serializeInputs)json.add("drill", drill.toJson());
	}

	@Override
	public boolean hasInput(IngredientMatch var1) {
		return false;
	}

	@Override
	public boolean replaceInput(IngredientMatch var1, Ingredient var2, ItemInputTransformer var3) {
		return false;
	}

	@Override
	public boolean hasOutput(IngredientMatch var1) {
		return false;
	}

	@Override
	public boolean replaceOutput(IngredientMatch var1, ItemStack var2, ItemOutputTransformer var3) {
		return false;
	}
}
