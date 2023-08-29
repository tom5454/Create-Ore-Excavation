package com.tom.createores.emi;

import java.util.ArrayList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.ExtractorRecipe;

import dev.emi.emi.api.widget.WidgetHolder;

public class ExtractingEmiRecipe extends ExcavatingEmiRecipe<ExtractorRecipe> {

	public ExtractingEmiRecipe(ExtractorRecipe recipe) {
		super(EMIPlugin.EXTRACTING, recipe);
		ResourceLocation rid = recipe.getId();
		this.id = new ResourceLocation("emi", CreateOreExcavation.MODID + "/extracting/" + rid.getNamespace() + "/" + rid.getPath());
		output = new ArrayList<>();
		output.add(fluidStack(recipe.output));
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		super.addWidgets(widgets);

		addSlot(widgets, output.get(0), width / 2 - 8, 78);
	}

	@Override
	protected BlockState getBlock() {
		return Registration.EXTRACTOR_BLOCK.getDefaultState();
	}
}
