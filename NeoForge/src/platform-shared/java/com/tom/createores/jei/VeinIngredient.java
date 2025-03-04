package com.tom.createores.jei;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeManager;

import com.mojang.blaze3d.systems.RenderSystem;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.util.NumberFormatter;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IModIngredientRegistration;

public class VeinIngredient implements IIngredientHelper<Vein>, IIngredientRenderer<Vein> {
	public static final IIngredientType<Vein> VEIN = () -> Vein.class;
	private ItemStack drill;

	public VeinIngredient(IModIngredientRegistration registration) {
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		registration.register(VEIN, mngr.getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()).stream().map(Vein::new).toList(), this, this, Vein.CODEC);
		drill = new ItemStack(Registration.NORMAL_DRILL_ITEM.get());
	}

	@Override
	public void render(GuiGraphics guiGraphics, Vein ingredient) {
		RenderSystem.enableDepthTest();
		guiGraphics.pose().pushPose();

		GuiGameElement.of(ingredient.recipe0().icon)
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
	@Deprecated
	public List<Component> getTooltip(Vein ingredient, TooltipFlag tooltipFlag) {
		return Collections.emptyList();
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, Vein ingredient, TooltipFlag tooltipFlag) {
		tooltip.add(ingredient.recipe0().veinName);
		if(ingredient.recipe0().isInfiniteClient())tooltip.add(Component.translatable("tooltip.coe.infiniteVeins"));
		else tooltip.add(Component.translatable("tooltip.coe.finiteVeins", NumberFormatter.formatNumber(ingredient.recipe0().getMinAmountClient()), NumberFormatter.formatNumber(ingredient.recipe0().getMaxAmountClient())));
	}

	@Override
	public IIngredientType<Vein> getIngredientType() {
		return VEIN;
	}

	@Override
	public String getDisplayName(Vein ingredient) {
		return ingredient.recipe0().veinName.getString();
	}

	@Override
	public String getUid(Vein ingredient, UidContext context) {
		return ingredient.id().toString();
	}

	@Override
	@Deprecated
	public String getUniqueId(Vein ingredient, UidContext context) {
		return getUid(ingredient, context);
	}

	@Override
	public ResourceLocation getResourceLocation(Vein ingredient) {
		return ingredient.id();
	}

	@Override
	public Vein copyIngredient(Vein ingredient) {
		return ingredient;
	}

	@Override
	public String getErrorInfo(@Nullable Vein ingredient) {
		return ingredient != null && ingredient.id() != null ? ingredient.id().toString() : "null";
	}
}
