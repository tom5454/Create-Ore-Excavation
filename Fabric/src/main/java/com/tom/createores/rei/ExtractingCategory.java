package com.tom.createores.rei;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.simibubi.create.compat.rei.DoubleItemIcon;
import com.simibubi.create.compat.rei.category.WidgetUtil;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import com.tom.createores.Registration;
import com.tom.createores.recipe.ExtractorRecipe;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class ExtractingCategory extends ExcavatingCategory<ExtractorRecipe> {

	public ExtractingCategory() {
		icon = new DoubleItemIcon(Registration.EXTRACTOR_BLOCK::asStack, () -> new ItemStack(Items.BUCKET));
	}

	@Override
	public CategoryIdentifier<? extends CreateDisplay<ExtractorRecipe>> getCategoryIdentifier() {
		return REIPlugin.EXTRACTING;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.coe.recipe.extracting");
	}

	@Override
	public void setupDisplay0(CreateDisplay<ExtractorRecipe> display, Rectangle bounds, List<Widget> widgets) {
		ExtractorRecipe recipe = display.getRecipe();
		Point origin = new Point(bounds.getX(), bounds.getY() + 4);
		int xOffset = bounds.getWidth() / 2;
		int yOffset = 86;
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SLOT, origin.x + xOffset, origin.y + yOffset));
		widgets.add(Widgets.createSlot(new Point(origin.x + xOffset + 1, origin.y + yOffset + 1)).disableBackground().markOutput().entries(EntryIngredients.of(ReiPlatform.wrapFluid(recipe.output))));

		widgets.add(new AnimatedBlock(Registration.EXTRACTOR_BLOCK.getDefaultState(), 11).pos(new Point(origin.x + 48, origin.y + 35)));
	}

}
