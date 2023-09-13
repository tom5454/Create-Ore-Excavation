package com.tom.createores.kubejs;

import net.minecraft.world.item.crafting.Ingredient;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;

@SuppressWarnings("unchecked")
public abstract class ExcavatingRecipeJS<T extends ExcavatingRecipeJS<T>> extends RecipeJS {
	private InputItem drill;

	@Override
	public void initValues(boolean created) {
		if(created) {
			drill = InputItem.of(Ingredient.of(CreateOreExcavation.DRILL_TAG));
		}
		super.initValues(created);
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

	public T fluid(FluidStackJS fluid) {
		json.add("fluid", fluid.toJson());
		save();
		return (T) this;
	}

	public T priority(int priority) {
		json.addProperty("priority", priority);
		save();
		return (T) this;
	}
}
