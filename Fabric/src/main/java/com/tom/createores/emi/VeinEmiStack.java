package com.tom.createores.emi;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.element.GuiGameElement;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.createores.Registration;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.NumberFormatter;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;

public class VeinEmiStack extends EmiStack {
	private static ItemStack drill = new ItemStack(Registration.NORMAL_DRILL_ITEM.get());
	private VeinRecipe recipe;

	public VeinEmiStack(VeinRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public boolean isEmpty() {
		return recipe == null;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int x, int y, float delta, int flags) {
		RenderSystem.enableDepthTest();
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(x, y, 0);

		GuiGameElement.of(recipe.icon)
		.render(guiGraphics);

		guiGraphics.pose().pushPose();
		float s = 0.5f;
		guiGraphics.pose().translate(8, 8, 100);
		guiGraphics.pose().scale(s, s, s);
		GuiGameElement.of(drill)
		.render(guiGraphics);
		guiGraphics.pose().popPose();

		guiGraphics.pose().popPose();
	}

	public static class Serializer implements EmiIngredientSerializer<VeinEmiStack> {

		@Override
		public String getType() {
			return "coe:vein";
		}

		@Override
		public EmiIngredient deserialize(JsonElement element) {
			RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
			ResourceLocation r = ResourceLocation.tryParse(element.getAsJsonObject().get("value").getAsString());
			VeinRecipe rec = mngr.byKey(r).map(v -> v instanceof VeinRecipe e ? e : null).orElse(null);
			return rec != null ? new VeinEmiStack(rec) : null;
		}

		@Override
		public JsonElement serialize(VeinEmiStack stack) {
			JsonObject json = new JsonObject();
			json.addProperty("type", "coe:vein");
			json.addProperty("value", stack.recipe.getId().toString());
			return json;
		}
	}

	@Override
	public CompoundTag getNbt() {
		return null;
	}

	@Override
	public Object getKey() {
		return recipe;
	}

	@Override
	public ResourceLocation getId() {
		return recipe.getId();
	}

	@Override
	public List<ClientTooltipComponent> getTooltip() {
		List<ClientTooltipComponent> list = Lists.newArrayList();
		List<Component> tooltip = new ArrayList<>();
		tooltip.add(getName());
		tooltip.addAll(getTooltipText());
		tooltip.add(Component.literal(EmiUtil.getModName(recipe.id.getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
		tooltip.stream().map(Component::getVisualOrderText).map(ClientTooltipComponent::create).forEach(list::add);
		list.addAll(super.getTooltip());
		return list;
	}

	@Override
	public List<Component> getTooltipText() {
		List<Component> tooltip = new ArrayList<>();
		if(recipe.isInfiniteClient())tooltip.add(Component.translatable("tooltip.coe.infiniteVeins"));
		else tooltip.add(Component.translatable("tooltip.coe.finiteVeins", NumberFormatter.formatNumber(recipe.getMinAmountClient()), NumberFormatter.formatNumber(recipe.getMaxAmountClient())));
		return tooltip;
	}

	@Override
	public Component getName() {
		return recipe.veinName;
	}

	@Override
	public EmiStack copy() {
		return this;
	}
}
