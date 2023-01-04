package com.tom.createores;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import com.tom.createores.block.entity.IDrill;
import com.tom.createores.recipe.IRecipe;
import com.tom.createores.recipe.IRecipe.IResourceRecipe;
import com.tom.createores.recipe.IRecipe.ThreeState;

public class OreDataCapability implements ICapabilityProvider, INBTSerializable<CompoundTag> {
	public static Capability<OreData> ORE_CAP = CapabilityManager.get(new CapabilityToken<>(){});

	private OreData oreData = null;
	private final LazyOptional<OreData> opt = LazyOptional.of(this::create);

	private OreData create() {
		if (oreData == null) {
			oreData = new OreData();
		}
		return oreData;
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap) {
		if (cap == ORE_CAP) {
			return opt.cast();
		}
		return LazyOptional.empty();
	}


	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return getCapability(cap);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		create().saveNBTData(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		create().loadNBTData(nbt);
	}

	public static class OreData {
		private ResourceLocation recipe;
		private boolean loaded;
		private long extractedAmount;
		private float randomMul;
		private Set<BlockPos> extractors;

		public void loadNBTData(CompoundTag nbt) {
			loaded = nbt.getBoolean("gen");
			if(nbt.contains("ore")) {
				recipe = new ResourceLocation(nbt.getString("ore"));
				extractedAmount = nbt.getLong("ext");
				randomMul = nbt.contains("mul") ? nbt.getFloat("mul") : 0.8F;
			}
		}

		public void saveNBTData(CompoundTag nbt) {
			nbt.putBoolean("gen", loaded);
			if(recipe != null) {
				nbt.putString("ore", recipe.toString());
				nbt.putLong("ext", extractedAmount);
				nbt.putFloat("mul", randomMul);
			}
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
	}

	public static OreData getData(LevelChunk chunk) {
		if(chunk.getLevel().isClientSide)throw new RuntimeException("Ore Data accessed from client");
		OreData data = chunk.getCapability(ORE_CAP).orElse(null);
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
