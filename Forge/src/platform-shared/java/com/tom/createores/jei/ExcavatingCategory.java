package com.tom.createores.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.util.BiomeTooltip;
import com.tom.createores.util.NumberFormatter;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;

public abstract class ExcavatingCategory<T extends ExcavatingRecipe> implements IRecipeCategory<T> {
	protected AnimatedBlock block;
	protected IDrawable background;
	protected IDrawable icon;
	protected IDrawable biomeWIcon, biomeBIcon;

	public ExcavatingCategory() {
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
	public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
		builder
		.addSlot(RecipeIngredientRole.INPUT, 51, 3)
		.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
		.addIngredients(recipe.getDrill());
	}

	@Override
	public void draw(T recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		block.draw(stack, 48, 35);
		biomeWIcon.draw(stack, 100, 5);
		biomeBIcon.draw(stack, 100, 25);
	}

	@Override
	public List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
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
		if(mouseX > 40 && mouseX < 80 && mouseY > 25 && mouseY < 60) {
			tooltip.add(recipe.getName());
			tooltip.add(Component.translatable("tooltip.coe.processTime", recipe.getTicks()));
			if(recipe.isInfiniteClient())tooltip.add(Component.translatable("tooltip.coe.infiniteVeins"));
			else tooltip.add(Component.translatable("tooltip.coe.finiteVeins", NumberFormatter.formatNumber(recipe.getMinAmountClient()), NumberFormatter.formatNumber(recipe.getMaxAmountClient())));
		}
		return tooltip;
	}
}
