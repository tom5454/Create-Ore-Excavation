package com.tom.createores.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import com.mojang.datafixers.util.Pair;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.OreDataCapability;
import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.recipe.VeinRecipe;

public class RandomSpreadGenerator {
	private List<VeinRecipe> recipes = new ArrayList<>();

	public void loadAll(ServerLevel level) {
		recipes.addAll(level.getRecipeManager().getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()));
		recipes.sort(Comparator.comparingInt(VeinRecipe::getNegGenerationPriority).thenComparing(VeinRecipe::getId));
	}

	public VeinRecipe pick(LevelChunk chunk) {
		int x = chunk.getPos().x;
		int z = chunk.getPos().z;
		int minY = QuartPos.fromBlock(chunk.getMinBuildHeight());
		int maxY = minY + QuartPos.fromBlock(chunk.getHeight()) - 1;
		ServerLevel level = (ServerLevel) chunk.getLevel();
		for (VeinRecipe recipe : recipes) {
			ChunkPos chunkpos = recipe.getPlacement().getPotentialStructureChunk(level.getSeed(), x, z);
			if(chunkpos.x == x && chunkpos.z == z) {
				WorldgenRandom rng = new WorldgenRandom(new LegacyRandomSource(0L));
				rng.setLargeFeatureSeed(level.getSeed(), x, z);
				Holder<Biome> biome = chunk.getNoiseBiome(rng.nextInt(4), minY + rng.nextInt(maxY), rng.nextInt(4));
				if(recipe.canGenerate(level, biome)) {
					return recipe;
				}
			}
		}
		return null;
	}

	private VeinRecipe pick(ServerLevel level, ChunkPos chunk, VeinRecipe last) {
		int minY = QuartPos.fromBlock(level.getMinBuildHeight());
		int maxY = minY + QuartPos.fromBlock(level.getHeight()) - 1;
		for (VeinRecipe recipe : recipes) {
			ChunkPos chunkpos = recipe.getPlacement().getPotentialStructureChunk(level.getSeed(), chunk.x, chunk.z);
			if(chunkpos.x == chunk.x && chunkpos.z == chunk.z) {
				WorldgenRandom rng = new WorldgenRandom(new LegacyRandomSource(0L));
				rng.setLargeFeatureSeed(level.getSeed(), chunk.x, chunk.z);
				Holder<Biome> biome = level.getNoiseBiome(QuartPos.fromSection(chunk.x) + rng.nextInt(4), minY + rng.nextInt(maxY), QuartPos.fromSection(chunk.z) + rng.nextInt(4));

				if (recipe.canGenerate(level, biome))
					return recipe;
			}
			if(recipe == last)break;
		}
		return null;
	}

	public BlockPos locate(ResourceLocation id, BlockPos pPos, ServerLevel level, int radius) {
		Recipe<?> recipe = level.getRecipeManager().byKey(id).orElse(null);
		if(recipe instanceof VeinRecipe r) {
			int i = SectionPos.blockToSectionCoord(pPos.getX());
			int j = SectionPos.blockToSectionCoord(pPos.getZ());
			for(int k = 0; k <= radius; ++k) {
				BlockPos pos = getNearestGenerated(level, i, j, k, level.getSeed(), r);
				if(pos != null) {
					if (level.isLoaded(pos)) {
						OreData data = OreDataCapability.getData(level.getChunkAt(pos));
						VeinRecipe vr = data.getRecipe(level.getRecipeManager());
						if (vr == null || !vr.id.equals(id))continue;
					}
					return pos;
				}
			}
		}
		return null;
	}

	public Pair<BlockPos, VeinRecipe> locate(BlockPos pPos, ServerLevel level, int radius, Predicate<VeinRecipe> filter) {
		int i = SectionPos.blockToSectionCoord(pPos.getX());
		int j = SectionPos.blockToSectionCoord(pPos.getZ());
		for(int k = 0; k <= radius; ++k) {
			Pair<BlockPos, VeinRecipe> found = null;
			float dist = Float.MAX_VALUE;
			for (int j2 = 0; j2 < recipes.size(); j2++) {
				VeinRecipe r = recipes.get(j2);
				if (!filter.test(r))continue;
				BlockPos pos = getNearestGenerated(level, i, j, k, level.getSeed(), r);
				if(pos != null) {
					float d = distance2d(pos, pPos);
					if(d < dist) {
						if (level.isLoaded(pos)) {
							OreData data = OreDataCapability.getData(level.getChunkAt(pos));
							r = data.getRecipe(level.getRecipeManager());
							if (r == null)continue;
							if (!filter.test(r))continue;
						}
						found = Pair.of(pos, r);
						dist = d;
					}
				}
			}
			if(found != null)
				return found;
		}
		return null;
	}

	private BlockPos getNearestGenerated(ServerLevel pLevel, int pX, int pY, int pZ, long pSeed, VeinRecipe recipe) {
		RandomSpreadStructurePlacement pSpreadPlacement = recipe.getPlacement();
		int i = pSpreadPlacement.spacing();

		for(int j = -pZ; j <= pZ; ++j) {
			boolean flag = j == -pZ || j == pZ;

			for(int k = -pZ; k <= pZ; ++k) {
				boolean flag1 = k == -pZ || k == pZ;
				if (flag || flag1) {
					int l = pX + i * j;
					int i1 = pY + i * k;
					ChunkPos chunkpos = pSpreadPlacement.getPotentialStructureChunk(pSeed, l, i1);

					VeinRecipe picked = pick(pLevel, chunkpos, recipe);
					if(picked == recipe)
						return chunkpos.getMiddleBlockPosition(0);
				}
			}
		}

		return null;
	}

	public static float distance2d(BlockPos a, BlockPos b) {
		int i = b.getX() - a.getX();
		int j = b.getZ() - a.getZ();
		return Mth.sqrt(i * i + j * j);
	}
}
