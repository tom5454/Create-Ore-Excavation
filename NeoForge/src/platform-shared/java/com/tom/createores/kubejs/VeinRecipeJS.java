package com.tom.createores.kubejs;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.FrequencyReductionMethod;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.util.ThreeState;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.KubeRecipeFactory;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.function.RecipeFunctionInstance;

public class VeinRecipeJS extends KubeRecipe {
	public static final KubeRecipeFactory RECIPE_FACTORY = new KubeRecipeFactory(CreateOreExcavation.VEIN_RECIPES.getId(), VeinRecipeJS.class, VeinRecipeJS::new);

	public static final RecipeKey<ItemStack> ICON = ItemStackComponent.ITEM_STACK.otherKey("icon");
	public static final RecipeKey<Component> NAME = ComponentComponent.INSTANCE.otherKey("name");
	public static final RecipeKey<RandomSpreadStructurePlacementJS> PLACEMENT = PlacementJS.INSTANCE.otherKey("placement");
	public static final RecipeKey<Integer> PRIORITY = NumberComponent.INT.range(0, Integer.MAX_VALUE).otherKey("priority").defaultOptional();
	public static final RecipeKey<ThreeState> FINITE = EnumComponent.of(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "three_state"), ThreeState.class, ThreeState.CODEC).otherKey("finite").defaultOptional();
	public static final RecipeKey<Float> AMOUNT_MIN = NumberComponent.floatRange(0, Float.MAX_VALUE).otherKey("amountMultiplierMin").defaultOptional();
	public static final RecipeKey<Float> AMOUNT_MAX = NumberComponent.floatRange(0, Float.MAX_VALUE).otherKey("amountMultiplierMax").defaultOptional();
	public static final RecipeKey<String> BIOME_WHITELIST = StringComponent.ID.otherKey("biomeWhitelist").defaultOptional();
	public static final RecipeKey<String> BIOME_BLACKLIST = StringComponent.ID.otherKey("biomeBlacklist").defaultOptional();
	public static final RecipeComponent<RandomSpreadType> RANDOM_SPREAD = EnumComponent.of(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "random_spread"), RandomSpreadType.class, RandomSpreadType.CODEC).instance();
	public static final RecipeComponent<FrequencyReductionMethod> FREQ_REDUCTION = EnumComponent.of(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "freq_reduction"), FrequencyReductionMethod.class, FrequencyReductionMethod.CODEC).instance();

	public static final RecipeSchema SCHEMA = new RecipeSchema(NAME, ICON, PLACEMENT, PRIORITY,
			FINITE, AMOUNT_MIN, AMOUNT_MAX, BIOME_WHITELIST, BIOME_BLACKLIST).
			uniqueIds(List.of(PLACEMENT, PRIORITY)).constructor(NAME, ICON).
			function(new RecipeFunctionInstance("alwaysInfinite", KubeJSUtil.wrapFunc(VeinRecipeJS::alwaysInfinite))).
			function(new RecipeFunctionInstance("alwaysFinite", KubeJSUtil.wrapFunc(VeinRecipeJS::alwaysFinite))).
			function(new RecipeFunctionInstance("defaultFinite", KubeJSUtil.wrapFunc(VeinRecipeJS::defaultFinite))).
			function(new RecipeFunctionInstance("veinSize", KubeJSUtil.wrapFunc(NumberComponent.FLOAT, NumberComponent.FLOAT, VeinRecipeJS::veinSize))).
			function(new RecipeFunctionInstance("biomeWhitelist", KubeJSUtil.wrapFunc(StringComponent.ID.instance(), VeinRecipeJS::biomeWhitelist))).
			function(new RecipeFunctionInstance("biomeBlacklist", KubeJSUtil.wrapFunc(StringComponent.ID.instance(), VeinRecipeJS::biomeBlacklist))).
			function(new RecipeFunctionInstance("placement", KubeJSUtil.wrapFunc(NumberComponent.INT, NumberComponent.INT, NumberComponent.INT, VeinRecipeJS::placement))).
			function(new RecipeFunctionInstance("spread", KubeJSUtil.wrapFunc(RANDOM_SPREAD, VeinRecipeJS::spread))).
			function(new RecipeFunctionInstance("reduction", KubeJSUtil.wrapFunc(FREQ_REDUCTION, VeinRecipeJS::reduction))).
			function(new RecipeFunctionInstance("priority", KubeJSUtil.wrapFunc(NumberComponent.INT, VeinRecipeJS::priority))).
			factory(RECIPE_FACTORY);

	@Override
	public void initValues(boolean created) {
		super.initValues(created);
		if(created) {
			setValue(PLACEMENT, new RandomSpreadStructurePlacementJS(64, 8, 0));
			setValue(PRIORITY, 0);
			setValue(AMOUNT_MIN, 1f);
			setValue(AMOUNT_MAX, 2f);
			setValue(FINITE, ThreeState.DEFAULT);
		}
	}

	public VeinRecipeJS alwaysInfinite() {
		setValue(FINITE, ThreeState.NEVER);
		return this;
	}

	public VeinRecipeJS alwaysFinite() {
		setValue(FINITE, ThreeState.ALWAYS);
		return this;
	}

	public VeinRecipeJS defaultFinite() {
		setValue(FINITE, ThreeState.DEFAULT);
		return this;
	}

	public VeinRecipeJS veinSize(float min, float max) {
		setValue(AMOUNT_MIN, min);
		setValue(AMOUNT_MAX, max);
		return this;
	}

	public VeinRecipeJS biomeWhitelist(String tag) {
		setValue(BIOME_WHITELIST, tag);
		return this;
	}

	public VeinRecipeJS biomeBlacklist(String tag) {
		setValue(BIOME_BLACKLIST, tag);
		return this;
	}

	public VeinRecipeJS placement(int spacing, int separation, int salt) {
		var p = getValue(PLACEMENT);
		p.spacing = spacing;
		p.separation = separation;
		p.salt = salt;
		save();
		return this;
	}

	public VeinRecipeJS spread(RandomSpreadType spread) {
		getValue(PLACEMENT).spreadType = spread;
		save();
		return this;
	}

	public VeinRecipeJS reduction(FrequencyReductionMethod freqReduction) {
		getValue(PLACEMENT).frequencyReductionMethod = freqReduction;
		save();
		return this;
	}

	public VeinRecipeJS priority(int priority) {
		setValue(PRIORITY, priority);
		return this;
	}
}
