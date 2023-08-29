package com.tom.createores.rei;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import com.simibubi.create.compat.rei.display.CreateDisplay;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.VeinRecipe;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;

public class REIPlugin implements REIClientPlugin {
	public static final CategoryIdentifier<CreateDisplay<DrillingRecipe>> DRILLING = CategoryIdentifier.of(CreateOreExcavation.MODID, "drilling");
	public static final CategoryIdentifier<CreateDisplay<ExtractorRecipe>> EXTRACTING = CategoryIdentifier.of(CreateOreExcavation.MODID, "extractor");
	public static final CategoryIdentifier<CreateDisplay<VeinRecipe>> VEINS = CategoryIdentifier.of(CreateOreExcavation.MODID, "vein");

	public static final EntryType<VeinRecipe> VEIN_TYPE = EntryType.deferred(new ResourceLocation(CreateOreExcavation.MODID, "vein"));

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new DrillingCategory());
		registry.add(new ExtractingCategory());
		registry.add(new VeinCategory());

		registry.addWorkstations(DRILLING, EntryStacks.of(Registration.DRILL_BLOCK.asStack()));
		registry.addWorkstations(EXTRACTING, EntryStacks.of(Registration.EXTRACTOR_BLOCK.asStack()));
		registry.addWorkstations(VEINS, EntryStacks.of(Registration.VEIN_FINDER_ITEM.asStack()));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		consumeAllRecipes(r -> {
			if(r instanceof ExtractorRecipe e)registry.add(new ExtractingDisplay(e), e);
			else if(r instanceof DrillingRecipe e)registry.add(new DrillingDisplay(e), e);
			else if(r instanceof VeinRecipe e)registry.add(new VeinDisplay(e), e);
		});
	}

	@Override
	public void registerEntries(EntryRegistry registry) {
		registry.addEntries(Minecraft.getInstance().level.getRecipeManager()
				.getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()).stream().map(e -> EntryStack.of(VEIN_TYPE, e)).toList());
	}

	@Override
	public void registerEntryTypes(EntryTypeRegistry registry) {
		registry.register(VEIN_TYPE, new VeinDefinition());
	}

	public static void consumeAllRecipes(Consumer<Recipe<?>> consumer) {
		Minecraft.getInstance().level.getRecipeManager()
		.getRecipes()
		.forEach(consumer);
	}
}
