package com.tom.createores.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import com.tom.createores.Registration;
import com.tom.createores.recipe.ExtractorRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

public class ExtractingCategory extends ExcavatingCategory<ExtractorRecipe> {

	public ExtractingCategory() {
		block = new AnimatedBlock(Registration.EXTRACTOR_BLOCK.getDefaultState(), 11);
		icon = new DoubleItemIcon(Registration.EXTRACTOR_BLOCK::asStack, () -> new ItemStack(Items.BUCKET));
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.coe.recipe.extracting");
	}

	@Override
	public RecipeType<RecipeHolder<ExtractorRecipe>> getRecipeType() {
		return JEIRecipes.EXTRACTING;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ExtractorRecipe> recipe, IFocusGroup focuses) {
		super.setRecipe(builder, recipe, focuses);

		int xOffset = getWidth() / 2;
		int yOffset = 86;

		CreateRecipeCategory.addFluidSlot(builder, xOffset - 8, yOffset - 8, recipe.value().getOutput());
	}
}
