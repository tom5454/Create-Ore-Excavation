package com.tom.createores.kubejs;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public class ExtractorRecipeJS extends ExcavatingRecipeJS<ExtractorRecipeJS> {
	public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "extractor"), ExtractorRecipeJS.class, ExtractorRecipeJS::new);

	public static final RecipeKey<FluidStack> OUTPUT = FluidStackComponent.FLUID_STACK.outputKey("output");

	public static final RecipeSchema SCHEMA = addFuncs(new RecipeSchema(OUTPUT, TICKS, VEIN_ID, DRILL, PRIORITY, FLUID, STRESS).
			constructor(OUTPUT, VEIN_ID, TICKS).
			factory(RECIPE_FACTORY));

}
