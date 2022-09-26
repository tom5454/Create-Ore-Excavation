package com.tom.createores;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.chunk.LevelChunk;

import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.IRecipe;
import com.tom.createores.recipe.IRecipe.RandomizerBuilder;

public class OreVeinGenerator {
	private static AtomicReference<SimpleWeightedRandomList<IRecipe>> picker = new AtomicReference<>();

	public static void invalidate() {
		picker.set(null);
	}

	public static SimpleWeightedRandomList<IRecipe> getPicker(LevelChunk chunk) {
		SimpleWeightedRandomList<IRecipe> v = picker.get();
		if(v != null)return v;
		synchronized (picker) {
			v = picker.get();
			if(v != null)return v;
			List<DrillingRecipe> rs = chunk.getLevel().getRecipeManager().getAllRecipesFor(CreateOreExcavation.DRILLING_RECIPES.getRecipeType());
			List<ExtractorRecipe> ps = chunk.getLevel().getRecipeManager().getAllRecipesFor(CreateOreExcavation.EXTRACTING_RECIPES.getRecipeType());
			RandomizerBuilder b = new RandomizerBuilder();
			b.addAll(ps);
			b.addAll(rs);
			v = b.build();
			picker.set(v);
			return v;
		}
	}

	public static IRecipe pick(LevelChunk chunk, Random rng) {
		ServerLevel lvl = (ServerLevel) chunk.getLevel();
		int i = QuartPos.fromBlock(chunk.getMinBuildHeight());
		int k = i + QuartPos.fromBlock(chunk.getHeight()) - 1;
		return getPicker(chunk).getRandomValue(rng).filter(r -> r.canGenerate(lvl, chunk.getNoiseBiome(rng.nextInt(4), i + rng.nextInt(k), rng.nextInt(4)))).orElse(null);
	}

	public static Random rngFromChunk(LevelChunk chunk) {
		ServerLevel lvl = (ServerLevel) chunk.getLevel();
		long seed = lvl.getSeed();
		return new Random(seed ^ chunk.getPos().toLong());
	}
}
