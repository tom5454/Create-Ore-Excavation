package com.tom.createores.emi;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import com.tom.createores.recipe.ExcavatingRecipe;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;

public class RecipeTooltipWidget extends Widget {
	private ExcavatingRecipe recipe;

	public RecipeTooltipWidget(ExcavatingRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public Bounds getBounds() {
		return new Bounds(0, 0, 134, 110);
	}

	@Override
	public void render(GuiGraphics matrices, int mouseX, int mouseY, float delta) {
	}

	@Override
	public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
		List<Component> tooltip = new ArrayList<>();
		if(mouseX > 40 && mouseX < 80 && mouseY > 25 && mouseY < 60) {
			tooltip.add(Component.translatable("tooltip.coe.processTime", recipe.getTicks()));
		}

		return tooltip.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).toList();
	}
}
