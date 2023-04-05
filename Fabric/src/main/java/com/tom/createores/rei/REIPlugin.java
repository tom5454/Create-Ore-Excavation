package com.tom.createores.rei;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Recipe;

import com.simibubi.create.compat.rei.display.CreateDisplay;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REIPlugin implements REIClientPlugin {
	public static final CategoryIdentifier<CreateDisplay<DrillingRecipe>> DRILLING = CategoryIdentifier.of(CreateOreExcavation.MODID, "drilling");
	public static final CategoryIdentifier<CreateDisplay<ExtractorRecipe>> EXTRACTING = CategoryIdentifier.of(CreateOreExcavation.MODID, "extractor");

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new DrillingCategory());
		registry.add(new ExtractingCategory());

		registry.addWorkstations(DRILLING, EntryStacks.of(Registration.DRILL_BLOCK.asStack()));
		registry.addWorkstations(EXTRACTING, EntryStacks.of(Registration.EXTRACTOR_BLOCK.asStack()));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		consumeAllRecipes(r -> {
			if(r instanceof ExtractorRecipe e)registry.add(new ExtractingDisplay(e), e);
			else if(r instanceof DrillingRecipe e)registry.add(new DrillingDisplay(e), e);
		});
	}

	public static void consumeAllRecipes(Consumer<Recipe<?>> consumer) {
		Minecraft.getInstance().level.getRecipeManager()
		.getRecipes()
		.forEach(consumer);
	}
}
