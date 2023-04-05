package com.tom.createores.emi;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.ponder.ui.LayoutHelper;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

public class DrillingEmiRecipe extends CreateEmiRecipe<DrillingRecipe> {

	public DrillingEmiRecipe(DrillingRecipe recipe) {
		super(EMIPlugin.DRILLING, recipe, 134, 110);
		ResourceLocation rid = recipe.getId();
		this.id = new ResourceLocation("emi", CreateOreExcavation.MODID + "/drilling/" + rid.getNamespace() + "/" + rid.getPath());
		input = new ArrayList<>();
		input.add(EmiIngredient.of(recipe.drill));
		if(recipe.drillingFluid != FluidIngredient.EMPTY)
			input.add(fluidStack(recipe.drillingFluid.getMatchingFluidStacks().get(0)));
		output = recipe.output.stream().map(ProcessingOutput::getStack).map(EmiStack::of).toList();
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addSlot(widgets, EmiIngredient.of(recipe.drill), 29, 6);

		if(recipe.drillingFluid != FluidIngredient.EMPTY)
			addSlot(widgets, input.get(1), 29 + 18, 6);

		int xOffset = 134 / 2;
		int yOffset = 86;
		layoutOutput(recipe).forEach(layoutEntry -> {
			addChancedSlot(widgets, EmiStack.of(layoutEntry.output.getStack()), (xOffset) + layoutEntry.posX() + 1, yOffset + layoutEntry.posY() + 1, layoutEntry.output.getChance());
		});

		widgets.add(new RecipeTooltipWidget(recipe));

		AnimatedBlock.addBlock(Registration.DRILL_BLOCK.getDefaultState(), widgets, 41, 55);
	}

	private List<LayoutEntry> layoutOutput(DrillingRecipe recipe) {
		int size = recipe.getOutput().size();
		List<LayoutEntry> positions = new ArrayList<>(size);

		LayoutHelper layout = LayoutHelper.centeredHorizontal(size, 1, 18, 18, 1);
		for (ProcessingOutput result : recipe.getOutput()) {
			positions.add(new LayoutEntry(result, layout.getX(), layout.getY()));
			layout.next();
		}

		return positions;
	}

	private record LayoutEntry(
			ProcessingOutput output,
			int posX,
			int posY
			) {}
}
