package com.tom.createores.jei;

import java.util.ArrayList;
import java.util.List;

import net.createmod.catnip.layout.LayoutHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;

public class DrillingCategory extends ExcavatingCategory<DrillingRecipe> {

	public DrillingCategory() {
		block = new AnimatedBlock(Registration.DRILL_BLOCK.getDefaultState(), 11);
		icon = new DoubleItemIcon(Registration.DRILL_BLOCK::asStack, () -> new ItemStack(Registration.NORMAL_DRILL_ITEM.get()));
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.coe.recipe.drilling");
	}

	@Override
	public RecipeType<DrillingRecipe> getRecipeType() {
		return JEIHandler.DRILLING;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DrillingRecipe recipe, IFocusGroup focuses) {
		super.setRecipe(builder, recipe, focuses);

		int xOffset = getBackground().getWidth() / 2;
		int yOffset = 86;

		layoutOutput(recipe).forEach(layoutEntry -> builder
				.addSlot(RecipeIngredientRole.OUTPUT, (xOffset) + layoutEntry.posX() + 1, yOffset + layoutEntry.posY() + 1)
				.setBackground(CreateRecipeCategory.getRenderedSlot(layoutEntry.output()), -1, -1)
				.addItemStack(layoutEntry.output().getStack())
				.addRichTooltipCallback(CreateRecipeCategory.addStochasticTooltip(layoutEntry.output()))
				);
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
