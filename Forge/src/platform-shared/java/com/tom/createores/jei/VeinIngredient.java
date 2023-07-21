package com.tom.createores.jei;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeManager;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.element.GuiGameElement;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.NumberFormatter;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IModIngredientRegistration;

public class VeinIngredient implements IIngredientHelper<VeinRecipe>, IIngredientRenderer<VeinRecipe> {
	public static final IIngredientType<VeinRecipe> VEIN = () -> VeinRecipe.class;
	private ItemStack drill;

	public VeinIngredient(IModIngredientRegistration registration) {
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		registration.register(VEIN, mngr.getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()), this, this);
		drill = new ItemStack(Registration.NORMAL_DRILL_ITEM.get());
	}

	@Override
	public void render(GuiGraphics guiGraphics, VeinRecipe ingredient) {
		RenderSystem.enableDepthTest();
		guiGraphics.pose().pushPose();

		GuiGameElement.of(ingredient.icon)
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

	@Override
	public List<Component> getTooltip(VeinRecipe ingredient, TooltipFlag tooltipFlag) {
		List<Component> tooltip = new ArrayList<>();
		tooltip.add(ingredient.veinName);
		if(ingredient.isInfiniteClient())tooltip.add(Component.translatable("tooltip.coe.infiniteVeins"));
		else tooltip.add(Component.translatable("tooltip.coe.finiteVeins", NumberFormatter.formatNumber(ingredient.getMinAmountClient()), NumberFormatter.formatNumber(ingredient.getMaxAmountClient())));
		return tooltip;
	}

	@Override
	public IIngredientType<VeinRecipe> getIngredientType() {
		return VEIN;
	}

	@Override
	public String getDisplayName(VeinRecipe ingredient) {
		return ingredient.veinName.getString();
	}

	@Override
	public String getUniqueId(VeinRecipe ingredient, UidContext context) {
		return ingredient.getId().toString();
	}

	@Override
	public ResourceLocation getResourceLocation(VeinRecipe ingredient) {
		return ingredient.getId();
	}

	@Override
	public VeinRecipe copyIngredient(VeinRecipe ingredient) {
		return ingredient;
	}

	@Override
	public String getErrorInfo(@Nullable VeinRecipe ingredient) {
		return ingredient != null && ingredient.id != null ? ingredient.id.toString() : "null";
	}
}
