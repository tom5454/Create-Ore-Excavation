package com.tom.createores.emi;

import java.util.ArrayList;

import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.ExtractorRecipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;

public class ExtractingEmiRecipe extends CreateEmiRecipe<ExtractorRecipe> {

	public ExtractingEmiRecipe(ExtractorRecipe recipe) {
		super(EMIPlugin.EXTRACTING, recipe, 134, 110);
		ResourceLocation rid = recipe.getId();
		this.id = new ResourceLocation("emi", CreateOreExcavation.MODID + "/extracting/" + rid.getNamespace() + "/" + rid.getPath());
		input = new ArrayList<>();
		input.add(EmiIngredient.of(recipe.drill));
		output = new ArrayList<>();
		output.add(fluidStack(recipe.output));
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addSlot(widgets, EmiIngredient.of(recipe.drill), 29, 6);

		addSlot(widgets, output.get(0), width / 2 - 8, 78);

		widgets.add(new RecipeTooltipWidget(recipe));

		AnimatedBlock.addBlock(Registration.EXTRACTOR_BLOCK.getDefaultState(), widgets, 41, 55);
	}
}
