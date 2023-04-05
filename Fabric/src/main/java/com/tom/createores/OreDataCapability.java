package com.tom.createores;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import com.tom.chunkstorage.api.ChunkStorageApi;
import com.tom.chunkstorage.api.DataObject;
import com.tom.chunkstorage.api.DataObjectKey;
import com.tom.createores.block.entity.IDrill;
import com.tom.createores.recipe.IRecipe;
import com.tom.createores.recipe.IRecipe.IResourceRecipe;
import com.tom.createores.recipe.IRecipe.ThreeState;

public class OreDataCapability {
	private static final DataObjectKey<OreData> KEY = ChunkStorageApi.registerObjectFactory(new ResourceLocation(CreateOreExcavation.MODID, "ore_vein_data"), OreData::new);

	public static void init() {}

	public static class OreData implements DataObject {
		private ResourceLocation recipe;
		private boolean loaded;
		private long extractedAmount;
		private float randomMul;
		private Set<BlockPos> extractors;

		@Override
		public void load(CompoundTag nbt) {
			loaded = nbt.getBoolean("gen");
			if(nbt.contains("ore")) {
				recipe = new ResourceLocation(nbt.getString("ore"));
				extractedAmount = nbt.getLong("ext");
				randomMul = nbt.contains("mul") ? nbt.getFloat("mul") : 0.8F;
			}
		}

		@Override
		public CompoundTag save() {
			CompoundTag nbt = new CompoundTag();
			nbt.putBoolean("gen", loaded);
			if(recipe != null) {
				nbt.putString("ore", recipe.toString());
				nbt.putLong("ext", extractedAmount);
				nbt.putFloat("mul", randomMul);
			}
			return nbt;
		}

		public void setRecipe(ResourceLocation recipe) {
			this.recipe = recipe;
		}

		public ResourceLocation getRecipeId() {
			return recipe;
		}

		public IRecipe getRecipe(RecipeManager mngr) {
			return mngr.byKey(recipe).filter(r -> r instanceof IRecipe).map(r -> (IRecipe) r).orElse(null);
		}

		public boolean isLoaded() {
			return loaded;
		}

		public long getResourcesRemaining(IRecipe r) {
			if(r instanceof IResourceRecipe rr && rr.isFinite() != ThreeState.NEVER) {
				if(rr.isFinite() == ThreeState.DEFAULT && Config.defaultInfinite)return 0L;
				double mul = (rr.getMaxAmount() - rr.getMinAmount()) * randomMul + rr.getMinAmount();
				long am = Math.round(mul * Config.finiteAmountBase);
				if(extractedAmount >= am)return -1L;
				return am - extractedAmount;
			}
			return 0L;
		}

		public void extract(int a) {
			extractedAmount += a;
		}

		public boolean canExtract(Level lvl, BlockPos pos) {
			if(Config.maxExtractorsPerVein == 0)return true;
			if(extractors == null) {
				extractors = new HashSet<>();
				extractors.add(pos);
				return true;
			}
			if(extractors.contains(pos))
				return true;
			extractors.removeIf(p -> !(lvl.getBlockEntity(p) instanceof IDrill));
			if(extractors.size() < Config.maxExtractorsPerVein) {
				extractors.add(pos);
				return true;
			}
			return false;
		}

		public void setRandomMul(float randomMul) {
			this.randomMul = randomMul;
		}

		public void setLoaded(boolean loaded) {
			this.loaded = loaded;
		}

		public void setExtractedAmount(long extractedAmount) {
			this.extractedAmount = extractedAmount;
		}
	}

	public static OreData getData(LevelChunk chunk) {
		if(chunk.getLevel().isClientSide)throw new RuntimeException("Ore Data accessed from client");
		OreData data = ChunkStorageApi.getOrCreateFromChunk(chunk, KEY);
		if(data != null && !data.loaded) {
			RandomSource rng = OreVeinGenerator.rngFromChunk(chunk);
			IRecipe r = OreVeinGenerator.pick(chunk, rng);
			if(r != null) {
				data.recipe = r.getRecipeId();
				data.randomMul = rng.nextFloat();
			}
			data.loaded = true;
		}
		return data;
	}
}
