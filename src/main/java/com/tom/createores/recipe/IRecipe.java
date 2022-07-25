package com.tom.createores.recipe;

import java.util.Collection;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.biome.Biome;

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
}
