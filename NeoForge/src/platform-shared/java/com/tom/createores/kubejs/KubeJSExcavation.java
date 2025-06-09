package com.tom.createores.kubejs;

import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.FrequencyReductionMethod;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.util.ThreeState;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeFactoryRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry.RegistriesFromFunction;

public class KubeJSExcavation implements KubeJSPlugin {

	@Override
	public void registerRecipeFactories(RecipeFactoryRegistry registry) {
		registry.register(DrillingRecipeJS.RECIPE_FACTORY);
		registry.register(ExtractorRecipeJS.RECIPE_FACTORY);
		registry.register(VeinRecipeJS.RECIPE_FACTORY);
	}

	@Override
	public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
		registry.register(CreateOreExcavation.VEIN_RECIPES.getId(), VeinRecipeJS.SCHEMA);
		registry.register(CreateOreExcavation.DRILLING_RECIPES.getId(), DrillingRecipeJS.SCHEMA);
		registry.register(CreateOreExcavation.EXTRACTING_RECIPES.getId(), ExtractorRecipeJS.SCHEMA);
	}

	@Override
	public void registerRecipeComponents(RecipeComponentFactoryRegistry registry) {
		registry.register(ComponentComponent.INSTANCE);
		registry.register(ProcessingOutputJS.INSTANCE);
		registry.register(PlacementJS.INSTANCE);
		registry.register(FluidIngredientJS.INSTANCE);
	}

	@Override
	public void registerTypeWrappers(TypeWrapperRegistry registry) {
		registry.register(ProcessingOutput.class, (RegistriesFromFunction<ProcessingOutput>) ProcessingOutputJS::wrap);
		registry.register(FluidIngredient.class, (RegistriesFromFunction<FluidIngredient>) FluidIngredientJS::wrap);
		registry.registerEnumFromStringCodec(ThreeState.class, ThreeState.CODEC);
		registry.registerEnumFromStringCodec(RandomSpreadType.class, RandomSpreadType.CODEC);
		registry.registerEnumFromStringCodec(FrequencyReductionMethod.class, FrequencyReductionMethod.CODEC);
	}

	@Override
	public void registerBindings(BindingRegistry bindings) {
		bindings.add("coeutil", COEUtil.class);
	}
}
