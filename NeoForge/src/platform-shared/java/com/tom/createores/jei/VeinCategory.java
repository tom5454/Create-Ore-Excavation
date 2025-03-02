package com.tom.createores.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;

import com.tom.createores.Registration;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.BiomeTooltip;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class VeinCategory implements IRecipeCategory<VeinRecipe> {
	protected IDrawable background;
	protected IDrawable icon;
	protected IDrawable biomeWIcon, biomeBIcon;

	public VeinCategory() {
		icon = new ItemIcon(() -> new ItemStack(Registration.NORMAL_DRILL_ITEM.get()));
		background = new EmptyBackground(177, 100);
		biomeWIcon = new ItemIcon(() -> new ItemStack(Items.OAK_SAPLING));
		biomeBIcon = new DoubleItemIcon(() -> new ItemStack(Items.OAK_SAPLING), () -> new ItemStack(Items.BARRIER));
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, VeinRecipe recipe, IFocusGroup focuses) {
		builder
		.addSlot(RecipeIngredientRole.OUTPUT, 50, 25)
		.addIngredient(VeinIngredient.VEIN, recipe);
	}

	@Override
	public void draw(VeinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		biomeWIcon.draw(stack, 100, 5);
		biomeBIcon.draw(stack, 100, 25);
	}

	@Override
	public List<Component> getTooltipStrings(VeinRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> tooltip = new ArrayList<>();
		if(mouseX > 100 && mouseX < 118) {
			if(mouseY > 5 && mouseY < 23) {
				tooltip.add(Component.translatable("tooltip.coe.biome.whitelist"));
				BiomeTooltip.listBiomes(recipe.biomeWhitelist, tooltip);
			} else if(mouseY > 25 && mouseY < 43) {
				tooltip.add(Component.translatable("tooltip.coe.biome.blacklist"));
				BiomeTooltip.listBiomes(recipe.biomeBlacklist, tooltip);
			} else {
				BiomeTooltip.resetPage();
			}
		}
		return tooltip;
	}

	@Override
	public RecipeType<VeinRecipe> getRecipeType() {
		return JEIHandler.VEINS;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.coe.recipe.veins");
	}
}
