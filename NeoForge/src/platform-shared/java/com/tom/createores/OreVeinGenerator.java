package com.tom.createores;

import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.chunk.LevelChunk;

import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.RandomSpreadGenerator;

public class OreVeinGenerator {
	private static AtomicReference<RandomSpreadGenerator> picker = new AtomicReference<>();

	public static void invalidate() {
		picker.set(null);
	}

	public static RandomSpreadGenerator getPicker(ServerLevel chunk) {
		RandomSpreadGenerator v = picker.get();
		if(v != null)return v;
		synchronized (picker) {
			v = picker.get();
			if(v != null)return v;
			v = new RandomSpreadGenerator();
			v.loadAll(chunk);
			picker.set(v);
			return v;
		}
	}

	public static RecipeHolder<VeinRecipe> pick(LevelChunk chunk) {
		return getPicker((ServerLevel) chunk.getLevel()).pick(chunk);
	}

	public static RandomSource rngFromChunk(LevelChunk chunk) {
		ServerLevel lvl = (ServerLevel) chunk.getLevel();
		long seed = lvl.getSeed();
		return RandomSource.create(seed ^ chunk.getPos().toLong());
	}
}
