package com.tom.createores.recipe;

import java.util.Collection;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;

import com.google.gson.JsonObject;

import com.tom.createores.Config;

public interface IRecipe {
	public static final IRecipe EMPTY = new IRecipe() {

		@Override
		public int getWeight() {
			return Config.generationChance;
		}

		@Override
		public ResourceLocation getRecipeId() {
			return null;
		}

		@Override
		public Component getName() {
			return null;
		}

		@Override
		public boolean canGenerate(ServerLevel lvl, Holder<Biome> b) {
			return true;
		}
	};

	ResourceLocation getRecipeId();
	int getWeight();
	Component getName();
	boolean canGenerate(ServerLevel lvl, Holder<Biome> holder);

	public static interface IResourceRecipe {
		ThreeState isFinite();
		float getMinAmount();
		float getMaxAmount();
	}

	public static class RandomizerBuilder extends SimpleWeightedRandomList.Builder<IRecipe> {

		public RandomizerBuilder() {
			add(EMPTY);
		}

		public void add(IRecipe add) {
			add(add, add.getWeight());
		}

		public void addAll(Collection<? extends IRecipe> add) {
			add.forEach(this::add);
		}
	}

	public static enum ThreeState {
		DEFAULT, ALWAYS, NEVER
		;
		public static final ThreeState[] VALUES = values();
		public static ThreeState get(JsonObject json, String name) {
			if(GsonHelper.getAsBoolean(json, "always" + name, false))return ALWAYS;
			if(GsonHelper.getAsBoolean(json, "never" + name, false))return NEVER;
			return DEFAULT;
		}

		public void toJson(JsonObject json, String name) {
			switch (this) {
			case ALWAYS:
				json.addProperty("always" + name, true);
				break;

			case NEVER:
				json.addProperty("never" + name, true);
				break;

			case DEFAULT:
			default:
				break;
			}
		}
	}
}
