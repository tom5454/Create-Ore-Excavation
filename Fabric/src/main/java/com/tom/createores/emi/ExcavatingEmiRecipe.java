package com.tom.createores.emi;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.compat.emi.recipes.CreateEmiRecipe;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.VeinRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;

public abstract class ExcavatingEmiRecipe<T extends ExcavatingRecipe> extends CreateEmiRecipe<T> {

	public ExcavatingEmiRecipe(EmiRecipeCategory category, T recipe) {
		super(category, recipe, 134, 110);
		input = new ArrayList<>();
		input.add(EmiIngredient.of(recipe.drill));
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		mngr.byKey(recipe.veinId).ifPresent(rec -> {
			if(rec instanceof VeinRecipe r)input.add(new VeinEmiStack(r));
		});
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addSlot(widgets, EmiIngredient.of(recipe.drill), 29, 6);

		AnimatedBlock.addBlock(getBlock(), widgets, 41, 55);

		widgets.add(new RecipeTooltipWidget(recipe));

		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		mngr.byKey(recipe.veinId).ifPresent(rec -> {
			if(rec instanceof VeinRecipe r)
				addSlot(widgets, new VeinEmiStack(r), 100, 3);
		});
	}

	protected abstract BlockState getBlock();
}
