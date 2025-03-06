package com.tom.createores.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.VeinRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;

public abstract class ExcavatingCategory<T extends ExcavatingRecipe> implements IRecipeCategory<RecipeHolder<T>> {
	protected AnimatedBlock block;
	protected IDrawable icon;

	public ExcavatingCategory() {
	}

	@Override
	public int getWidth() {
		return 177;
	}

	@Override
	public int getHeight() {
		return 100;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<T> recipe, IFocusGroup focuses) {
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();

		builder
		.addSlot(RecipeIngredientRole.INPUT, 51, 3)
		.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
		.addIngredients(recipe.value().getDrill());

		mngr.byKey(recipe.value().veinId).ifPresent(rec -> {
			if(rec.value() instanceof VeinRecipe r)
				builder
				.addSlot(RecipeIngredientRole.CATALYST, 100, 3)
				.addIngredient(VeinIngredient.VEIN, new Vein((RecipeHolder) rec));
		});

		if(recipe.value().getDrillingFluid() != FluidIngredient.EMPTY) {
			builder
			.addSlot(RecipeIngredientRole.INPUT, 51 + 18, 3)
			.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
			.addIngredients(JeiPlatform.FLUID_STACK, JeiPlatform.wrapFluid(recipe.value().getDrillingFluid().getMatchingFluidStacks()))
			.setFluidRenderer(recipe.value().getDrillingFluid().getRequiredAmount(), false, 16, 16);
			//.addRichTooltipCallback(CreateRecipeCategory.addFluidTooltip(recipe.value().getDrillingFluid().getRequiredAmount()));
		}
	}

	@Override
	public void draw(RecipeHolder<T> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		block.draw(stack, 48, 35);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<T> recipe, IRecipeSlotsView recipeSlotsView,
			double mouseX, double mouseY) {
		if (mouseX > 40 && mouseX < 80 && mouseY > 25 && mouseY < 60) {
			tooltip.add(Component.translatable("tooltip.coe.processTime", recipe.value().getTicks()));
		}
	}
}
