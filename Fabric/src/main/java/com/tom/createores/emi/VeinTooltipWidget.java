package com.tom.createores.emi;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import com.simibubi.create.compat.emi.DoubleItemIcon;

import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.BiomeTooltip;

import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;

public class VeinTooltipWidget extends Widget {
	private VeinRecipe recipe;
	private EmiRenderable whitelist, blacklist;

	public VeinTooltipWidget(VeinRecipe recipe) {
		this.recipe = recipe;
		whitelist = EmiStack.of(Items.OAK_SAPLING);
		blacklist = DoubleItemIcon.of(Items.OAK_SAPLING, Items.BARRIER);
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(0, 0, 134, 110);
	}

	@Override
	public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
		whitelist.render(matrices, 100, 5, delta);
		blacklist.render(matrices, 100, 25, delta);
	}

	@Override
	public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
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

		return tooltip.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList();
	}
}
