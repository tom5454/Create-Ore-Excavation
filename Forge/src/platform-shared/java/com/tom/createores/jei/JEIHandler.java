package com.tom.createores.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.VeinRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

@JeiPlugin
public class JEIHandler implements IModPlugin {

	public static final RecipeType<DrillingRecipe> DRILLING = RecipeType.create(CreateOreExcavation.MODID, "drilling", DrillingRecipe.class);
	public static final RecipeType<ExtractorRecipe> EXTRACTING = RecipeType.create(CreateOreExcavation.MODID, "extractor", ExtractorRecipe.class);
	public static final RecipeType<VeinRecipe> VEINS = RecipeType.create(CreateOreExcavation.MODID, "veins", VeinRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(CreateOreExcavation.MODID, "jei");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new DrillingCategory(), new ExtractingCategory(), new VeinCategory());
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		registration.addRecipes(DRILLING, mngr.getAllRecipesFor(CreateOreExcavation.DRILLING_RECIPES.getRecipeType()));
		registration.addRecipes(EXTRACTING, mngr.getAllRecipesFor(CreateOreExcavation.EXTRACTING_RECIPES.getRecipeType()));
		registration.addRecipes(VEINS, mngr.getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(Registration.DRILL_BLOCK.asStack(), DRILLING);
		registration.addRecipeCatalyst(Registration.EXTRACTOR_BLOCK.asStack(), EXTRACTING);
		registration.addRecipeCatalyst(Registration.VEIN_FINDER_ITEM.asStack(), VEINS);
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {
		new VeinIngredient(registration);
	}
}
