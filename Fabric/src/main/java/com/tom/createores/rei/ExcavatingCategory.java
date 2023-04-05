package com.tom.createores.rei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.rei.DoubleItemIcon;
import com.simibubi.create.compat.rei.ItemIcon;
import com.simibubi.create.compat.rei.category.WidgetUtil;
import com.simibubi.create.compat.rei.display.CreateDisplay;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.util.BiomeTooltip;
import com.tom.createores.util.NumberFormatter;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.util.EntryIngredients;

public abstract class ExcavatingCategory<R extends ExcavatingRecipe> implements DisplayCategory<CreateDisplay<R>> {
	protected Renderer icon;
	protected Renderer biomeWIcon, biomeBIcon;

	public ExcavatingCategory() {
		biomeWIcon = new ItemIcon(() -> new ItemStack(Items.OAK_SAPLING));
		biomeBIcon = new DoubleItemIcon(() -> new ItemStack(Items.OAK_SAPLING), () -> new ItemStack(Items.BARRIER));
	}

	@Override
	public Renderer getIcon() {
		return icon;
	}

	@Override
	public List<Widget> setupDisplay(CreateDisplay<R> display, Rectangle bounds) {
		ExcavatingRecipe recipe = display.getRecipe();
		Point origin = new Point(bounds.getX(), bounds.getY() + 4);
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.createSlot(new Point(origin.x + 51, origin.y + 3)).disableBackground().markInput().entries(EntryIngredients.ofIngredient(recipe.drill)));
		widgets.add(WidgetUtil.textured(AllGuiTextures.JEI_SLOT, origin.x + 50, origin.y + 2));
		setupDisplay0(display, bounds, widgets);
		widgets.add(Widgets.wrapRenderer(new Rectangle(origin.x + 100, origin.y + 5, 16, 16), biomeWIcon));
		widgets.add(Widgets.wrapRenderer(new Rectangle(origin.x + 100, origin.y + 25, 16, 16), biomeBIcon));
		widgets.add(new WidgetWithBounds() {

			@Override
			public List<? extends GuiEventListener> children() {
				return Collections.emptyList();
			}

			@Override
			public void render(PoseStack poseStack, int mouseX, int mouseY, float f) {
				mouseX -= bounds.x;
				mouseY -= bounds.y;
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
				if(!tooltip.isEmpty())Tooltip.create(tooltip).queue();
			}

			@Override
			public Rectangle getBounds() {
				return bounds;
			}
		});
		return widgets;
	}

	public abstract void setupDisplay0(CreateDisplay<R> display, Rectangle bounds, List<Widget> widgets);

	@Override
	public int getDisplayHeight() {
		return 120;
	}
}
