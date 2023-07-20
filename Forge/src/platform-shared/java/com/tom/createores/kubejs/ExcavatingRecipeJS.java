package com.tom.createores.kubejs;

import net.minecraft.world.item.crafting.Ingredient;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

@SuppressWarnings("unchecked")
public abstract class ExcavatingRecipeJS<T extends ExcavatingRecipeJS<T>> extends RecipeJS {
	private InputItem drill;

	@Override
	public void initValues(boolean created) {
		if(created) {
			drill = InputItem.of(Ingredient.of(CreateOreExcavation.DRILL_TAG));
			json.addProperty("amountMin", 1);
			json.addProperty("amountMax", 2);
		}
		super.initValues(created);
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

	public T drill(InputItem drill) {
		this.drill = drill;
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
	public void deserialize(boolean merge) {
		super.deserialize(merge);
		drill = readInputItem(json.get("drill"));
	}

	@Override
	public void serialize() {
		super.serialize();
		json.add("drill", drill.ingredient.toJson());
	}
}
