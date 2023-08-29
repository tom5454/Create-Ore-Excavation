package com.tom.createores.emi;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.VeinRecipe;

import dev.emi.emi.api.widget.WidgetHolder;

public class VeinEmiRecipe extends CreateEmiRecipe<VeinRecipe> {

	public VeinEmiRecipe(VeinRecipe recipe) {
		super(EMIPlugin.VEINS, recipe, 134, 110);
		ResourceLocation rid = recipe.getId();
		this.id = new ResourceLocation("emi", CreateOreExcavation.MODID + "/ore_vein/" + rid.getNamespace() + "/" + rid.getPath());
		input = new ArrayList<>();
		output = Collections.singletonList(new VeinEmiStack(recipe));
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addSlot(widgets, new VeinEmiStack(recipe), 50, 25);

		widgets.add(new VeinTooltipWidget(recipe));
	}
}
