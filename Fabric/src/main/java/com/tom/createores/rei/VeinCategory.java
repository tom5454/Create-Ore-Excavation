package com.tom.createores.rei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.simibubi.create.compat.rei.DoubleItemIcon;
import com.simibubi.create.compat.rei.ItemIcon;
import com.simibubi.create.compat.rei.display.CreateDisplay;

import com.tom.createores.Registration;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.BiomeTooltip;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;

public class VeinCategory implements DisplayCategory<CreateDisplay<VeinRecipe>> {
	protected Renderer icon;
	protected Renderer biomeWIcon, biomeBIcon;

	public VeinCategory() {
		icon = new ItemIcon(() -> new ItemStack(Registration.NORMAL_DRILL_ITEM.get()));
		biomeWIcon = new ItemIcon(() -> new ItemStack(Items.OAK_SAPLING));
		biomeBIcon = new DoubleItemIcon(() -> new ItemStack(Items.OAK_SAPLING), () -> new ItemStack(Items.BARRIER));
	}

	@Override
	public Renderer getIcon() {
		return icon;
	}

	@Override
	public List<Widget> setupDisplay(CreateDisplay<VeinRecipe> display, Rectangle bounds) {
		VeinRecipe recipe = display.getRecipe();
		Point origin = new Point(bounds.getX(), bounds.getY() + 4);
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.wrapRenderer(new Rectangle(origin.x + 100, origin.y + 5, 16, 16), biomeWIcon));
		widgets.add(Widgets.wrapRenderer(new Rectangle(origin.x + 100, origin.y + 25, 16, 16), biomeBIcon));
		widgets.add(new WidgetWithBounds() {

			@Override
			public List<? extends GuiEventListener> children() {
				return Collections.emptyList();
			}

			@Override
			public void render(GuiGraphics poseStack, int mouseX, int mouseY, float f) {
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
				if(!tooltip.isEmpty())Tooltip.create(tooltip).queue();
			}

			@Override
			public Rectangle getBounds() {
				return bounds;
			}
		});
		widgets.add(Widgets.createSlot(new Point(origin.x + 50, origin.y + 25)).disableBackground().markInput().entries(Collections.singletonList(EntryStack.of(REIPlugin.VEIN_TYPE, recipe))));
		return widgets;
	}

	@Override
	public int getDisplayHeight() {
		return 120;
	}

	@Override
	public CategoryIdentifier<? extends CreateDisplay<VeinRecipe>> getCategoryIdentifier() {
		return REIPlugin.VEINS;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.coe.recipe.veins");
	}
}
