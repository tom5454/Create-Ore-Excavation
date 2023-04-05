package com.tom.createores.emi;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.emi.DoubleItemIcon;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.util.BiomeTooltip;
import com.tom.createores.util.NumberFormatter;

import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;

public class RecipeTooltipWidget extends Widget {
	private ExcavatingRecipe recipe;
	private EmiRenderable whitelist, blacklist;

	public RecipeTooltipWidget(ExcavatingRecipe recipe) {
		this.recipe = recipe;
		whitelist = EmiStack.of(Items.OAK_SAPLING);
		blacklist = DoubleItemIcon.of(Items.OAK_SAPLING, Items.BARRIER);
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(0, 0, 134, 110);
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
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
		if(mouseX > 40 && mouseX < 80 && mouseY > 25 && mouseY < 60) {
			tooltip.add(recipe.getName());
			tooltip.add(Component.translatable("tooltip.coe.processTime", recipe.getTicks()));
			if(recipe.isInfiniteClient())tooltip.add(Component.translatable("tooltip.coe.infiniteVeins"));
			else tooltip.add(Component.translatable("tooltip.coe.finiteVeins", NumberFormatter.formatNumber(recipe.getMinAmountClient()), NumberFormatter.formatNumber(recipe.getMaxAmountClient())));
		}

		return tooltip.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList();
	}
}
