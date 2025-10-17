package com.tom.createores.kubejs;

import net.minecraft.world.item.crafting.Ingredient;

import com.simibubi.create.foundation.fluid.FluidIngredient;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.IngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.component.TimeComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeFunctionInstance;
import dev.latvian.mods.kubejs.util.TickDuration;

@SuppressWarnings("unchecked")
public abstract class ExcavatingRecipeJS<T extends ExcavatingRecipeJS<T>> extends KubeRecipe {
	public static final RecipeKey<Ingredient> DRILL = IngredientComponent.INGREDIENT.inputKey("drill").optional(Ingredient.of(CreateOreExcavation.DRILL_TAG));
	public static final RecipeKey<Integer> PRIORITY = NumberComponent.INT.range(0, Integer.MAX_VALUE).otherKey("priority").optional(0);
	public static final RecipeKey<FluidIngredient> FLUID = FluidIngredientJS.INSTANCE.inputKey("fluid").defaultOptional();
	public static final RecipeKey<Integer> STRESS = NumberComponent.INT.range(0, Integer.MAX_VALUE).otherKey("stress").optional(256);
	public static final RecipeKey<TickDuration> TICKS = TimeComponent.TICKS.otherKey("ticks");
	public static final RecipeKey<String> VEIN_ID = StringComponent.ID.inputKey("veinId");

	public static <T extends ExcavatingRecipeJS<T>> RecipeSchema addFuncs(RecipeSchema in) {
		return in.function(new RecipeFunctionInstance("drill", KubeJSUtil.wrapFunc(IngredientComponent.INGREDIENT.instance(), ExcavatingRecipeJS<T>::drill))).
				function(new RecipeFunctionInstance("stress", KubeJSUtil.wrapFunc(NumberComponent.intRange(0, Integer.MAX_VALUE), ExcavatingRecipeJS<T>::stress))).
				function(new RecipeFunctionInstance("priority", KubeJSUtil.wrapFunc(NumberComponent.INT, ExcavatingRecipeJS<T>::priority))).
				function(new RecipeFunctionInstance("fluid", KubeJSUtil.wrapFunc(FluidIngredientJS.INSTANCE, ExcavatingRecipeJS<T>::fluid)))
				;
	}

	@Override
	public void initValues(boolean created) {
		super.initValues(created);
		if(created) {
			setValue(DRILL, Ingredient.of(CreateOreExcavation.DRILL_TAG));
			setValue(STRESS, 256);
			setValue(PRIORITY, 0);
		}
	}

	public T drill(Ingredient drill) {
		setValue(DRILL, drill);
		return (T) this;
	}

	public T stress(int stress) {
		setValue(STRESS, stress);
		return (T) this;
	}

	public T fluid(FluidIngredient fluid) {
		setValue(FLUID, fluid);
		return (T) this;
	}

	public T priority(int priority) {
		setValue(PRIORITY, priority);
		return (T) this;
	}
}
