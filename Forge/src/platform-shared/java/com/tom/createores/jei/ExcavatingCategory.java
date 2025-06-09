package com.tom.createores.jei;

import static net.minecraft.ChatFormatting.GRAY;

import java.util.ArrayList;
import java.util.List;

import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeManager;

import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.VeinRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;

public abstract class ExcavatingCategory<T extends ExcavatingRecipe> implements IRecipeCategory<T> {
	protected AnimatedBlock block;
	protected IDrawable background;
	protected IDrawable icon;

	public ExcavatingCategory() {
		background = new EmptyBackground(177, 100);
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();

		builder
		.addSlot(RecipeIngredientRole.INPUT, 51, 3)
		.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
		.addIngredients(recipe.getDrill());

		mngr.byKey(recipe.veinId).ifPresent(rec -> {
			if(rec instanceof VeinRecipe r)
				builder
				.addSlot(RecipeIngredientRole.CATALYST, 100, 3)
				.addIngredient(VeinIngredient.VEIN, r);
		});

		if(recipe.getDrillingFluid() != FluidIngredient.EMPTY) {
			builder
			.addSlot(RecipeIngredientRole.INPUT, 51 + 18, 3)
			.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
			.addIngredients(JeiPlatform.FLUID_STACK, JeiPlatform.wrapFluid(recipe.getDrillingFluid().getMatchingFluidStacks()))
			.setFluidRenderer(recipe.getDrillingFluid().getRequiredAmount(), false, 16, 16);
			//.addRichTooltipCallback(CreateRecipeCategory.addFluidTooltip(recipe.getDrillingFluid().getRequiredAmount()));
		}
	}

	@Override
	public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX,
			double mouseY) {
		block.draw(stack, 48, 35);
	}

	@Override
	public List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> tooltip = new ArrayList<>();
		if(mouseX > 40 && mouseX < 80 && mouseY > 25 && mouseY < 60) {
			tooltip.add(Component.translatable("tooltip.coe.processTime", recipe.getTicks()));
			boolean hasGoggles = GogglesItem.isWearingGoggles(Minecraft.getInstance().player);
			if (hasGoggles) {
				LangBuilder rpmUnit = CreateLang.translate("generic.unit.rpm");
				tooltip.add(CreateLang.translate("tooltip.stressImpact")
						.style(GRAY)
						.component());

				int impact = recipe.getStress();
				StressImpact impactId = StressImpact.HIGH;
				LangBuilder builder = CreateLang.builder()
						.add(CreateLang.text(TooltipHelper.makeProgressBar(3, impactId.ordinal() + 1))
								.style(impactId.getAbsoluteColor()));
				tooltip.add(builder.add(CreateLang.number(impact))
						.text("x ")
						.add(rpmUnit)
						.component());
			}
		}
		return tooltip;
	}
}
