package com.tom.createores.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

@JeiPlugin
public class JEIHandler implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return ResourceLocation.tryBuild(CreateOreExcavation.MODID, "jei");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new DrillingCategory(), new ExtractingCategory(), new VeinCategory());
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		registration.addRecipes(JEIRecipes.DRILLING, mngr.getAllRecipesFor(CreateOreExcavation.DRILLING_RECIPES.getRecipeType()));
		registration.addRecipes(JEIRecipes.EXTRACTING, mngr.getAllRecipesFor(CreateOreExcavation.EXTRACTING_RECIPES.getRecipeType()));
		registration.addRecipes(JEIRecipes.VEINS, mngr.getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(Registration.DRILL_BLOCK.asStack(), JEIRecipes.DRILLING);
		registration.addRecipeCatalyst(Registration.EXTRACTOR_BLOCK.asStack(), JEIRecipes.EXTRACTING);
		registration.addRecipeCatalyst(Registration.VEIN_FINDER_ITEM.asStack(), JEIRecipes.VEINS);
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {
		new VeinIngredient(registration);
	}
}
