package com.tom.createores.kubejs;

import java.util.List;

import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.tom.createores.CreateOreExcavation;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public class DrillingRecipeJS extends ExcavatingRecipeJS<DrillingRecipeJS> {
	public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "drilling"), DrillingRecipeJS.class, DrillingRecipeJS::new);

	public static final RecipeKey<List<ProcessingOutput>> OUTPUT = ProcessingOutputJS.instance().asListOrSelf().outputKey("output");

	public static final RecipeSchema SCHEMA = addFuncs(new RecipeSchema(OUTPUT, TICKS, VEIN_ID, DRILL, PRIORITY, FLUID, STRESS).
			constructor(OUTPUT, VEIN_ID, TICKS).
			factory(RECIPE_FACTORY));
}
