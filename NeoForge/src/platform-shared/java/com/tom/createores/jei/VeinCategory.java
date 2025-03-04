package com.tom.createores.jei;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.simibubi.create.compat.jei.DoubleItemIcon;

import com.tom.createores.Registration;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.BiomeTooltip;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class VeinCategory implements IRecipeCategory<RecipeHolder<VeinRecipe>> {
	protected IDrawable icon;
	protected IDrawable biomeWIcon, biomeBIcon;

	public VeinCategory() {
		icon = new ItemIcon(() -> new ItemStack(Registration.NORMAL_DRILL_ITEM.get()));
		biomeWIcon = new ItemIcon(() -> new ItemStack(Items.OAK_SAPLING));
		biomeBIcon = new DoubleItemIcon(() -> new ItemStack(Items.OAK_SAPLING), () -> new ItemStack(Items.BARRIER));
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
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<VeinRecipe> recipe, IFocusGroup focuses) {
		builder
		.addSlot(RecipeIngredientRole.OUTPUT, 50, 25)
		.addIngredient(VeinIngredient.VEIN, new Vein(recipe));
	}

	@Override
	public void draw(RecipeHolder<VeinRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		biomeWIcon.draw(stack, 100, 5);
		biomeBIcon.draw(stack, 100, 25);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<VeinRecipe> recipe, IRecipeSlotsView recipeSlotsView,
			double mouseX, double mouseY) {
		if(mouseX > 100 && mouseX < 118) {
			if(mouseY > 5 && mouseY < 23) {
				tooltip.add(Component.translatable("tooltip.coe.biome.whitelist"));
				BiomeTooltip.listBiomes(recipe.value().biomeWhitelist, tooltip);
			} else if(mouseY > 25 && mouseY < 43) {
				tooltip.add(Component.translatable("tooltip.coe.biome.blacklist"));
				BiomeTooltip.listBiomes(recipe.value().biomeBlacklist, tooltip);
			} else {
				BiomeTooltip.resetPage();
			}
		}
	}

	@Override
	public RecipeType<RecipeHolder<VeinRecipe>> getRecipeType() {
		return JEIRecipes.VEINS;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.coe.recipe.veins");
	}
}
