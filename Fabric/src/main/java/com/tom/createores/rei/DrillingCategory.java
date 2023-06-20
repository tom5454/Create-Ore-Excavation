package com.tom.createores.rei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.simibubi.create.compat.rei.DoubleItemIcon;
import com.simibubi.create.compat.rei.category.WidgetUtil;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.ponder.ui.LayoutHelper;
import com.simibubi.create.foundation.utility.Lang;

import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public class DrillingCategory extends ExcavatingCategory<DrillingRecipe> {

	public DrillingCategory() {
		icon = new DoubleItemIcon(Registration.DRILL_BLOCK::asStack, () -> new ItemStack(Registration.NORMAL_DRILL_ITEM.get()));
	}

	@Override
	public CategoryIdentifier<? extends CreateDisplay<DrillingRecipe>> getCategoryIdentifier() {
		return REIPlugin.DRILLING;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.coe.recipe.drilling");
	}

	@Override
	public void setupDisplay0(CreateDisplay<DrillingRecipe> display, Rectangle bounds, List<Widget> widgets) {
		DrillingRecipe recipe = display.getRecipe();
		Point origin = new Point(bounds.getX(), bounds.getY() + 4);
		if(recipe.getDrillingFluid() != FluidIngredient.EMPTY) {
			widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SLOT, origin.x + 50 + 18, origin.y + 2));
			widgets.add(Widgets.createSlot(new Point(origin.x + 51 + 18, origin.y + 3)).disableBackground().markInput().entries(EntryIngredients.of(ReiPlatform.wrapFluid(recipe.getDrillingFluid().getMatchingFluidStacks().get(0)))));
		}
		int xOffset = bounds.getWidth() / 2;
		int yOffset = 86;

		layoutOutput(recipe).forEach(layoutEntry -> {
			float c = layoutEntry.output().getChance();
			widgets.add(WidgetUtil.textured(c != 1 ? AllGuiTextures.JEI_CHANCE_SLOT : AllGuiTextures.JEI_SLOT, origin.x + xOffset + layoutEntry.posX(), origin.y + yOffset + layoutEntry.posY()));
			List<Component> tooltip = new ArrayList<>();
			if(c != 1) {
				tooltip.add(Lang.translateDirect("recipe.processing.chance", c < 0.01 ? "<1" : (int) (c * 100))
						.withStyle(ChatFormatting.GOLD));
			}
			widgets.add(
					Widgets.createSlot(new Point(origin.x + xOffset + layoutEntry.posX() + 1, origin.y + yOffset + layoutEntry.posY() + 1))
					.markOutput().disableBackground().entry(EntryStack.of(VanillaEntryTypes.ITEM, layoutEntry.output().getStack()).tooltip(tooltip))
					);
		});

		widgets.add(new AnimatedBlock(Registration.DRILL_BLOCK.getDefaultState(), 11).pos(new Point(origin.x + 48, origin.y + 35)));
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
