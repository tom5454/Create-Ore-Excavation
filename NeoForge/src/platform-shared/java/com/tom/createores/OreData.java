package com.tom.createores;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.tom.createores.block.entity.IDrill;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.ThreeState;

public class OreData {
	private ResourceLocation recipe;
	private boolean loaded;
	private long extractedAmount;
	private float randomMul;
	private Set<BlockPos> extractors;

	public OreData() {
	}

	public OreData(Serialized data) {
		load(data);
	}

	public void load(Serialized data) {
		this.loaded = data.loaded();
		this.recipe = data.recipe().orElse(null);
		this.extractedAmount = data.extracted();
		this.randomMul = data.random();
	}

	public Serialized save() {
		return new Serialized(loaded, Optional.ofNullable(recipe), extractedAmount, randomMul);
	}

	public static final Codec<OreData> CODEC = Serialized.CODEC.xmap(OreData::new, OreData::save);

	public static record Serialized(boolean loaded, Optional<ResourceLocation> recipe, long extracted, float random) {
		public static final Codec<Serialized> CODEC = RecordCodecBuilder.<Serialized>mapCodec(b -> {
			return b.group(
					Codec.BOOL.fieldOf("loaded").forGetter(Serialized::loaded),
					ResourceLocation.CODEC.optionalFieldOf("recipe").forGetter(Serialized::recipe),
					Codec.LONG.fieldOf("extracted").forGetter(Serialized::extracted),
					Codec.FLOAT.fieldOf("random").forGetter(Serialized::random)
					).apply(b, Serialized::new);
		}).codec();
	}

	public void setRecipe(ResourceLocation recipe) {
		this.recipe = recipe;
	}

	public ResourceLocation getRecipeId() {
		return recipe;
	}

	public RecipeHolder<VeinRecipe> getRecipe(RecipeManager mngr) {
		return recipe != null ? mngr.byKey(recipe).filter(r -> r.value() instanceof VeinRecipe).map(r -> (RecipeHolder<VeinRecipe>) r).orElse(null) : null;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public long getResourcesRemaining(VeinRecipe r) {
		if(r.isFinite() != ThreeState.NEVER) {
			if(r.isFinite() == ThreeState.DEFAULT && Config.defaultInfinite)return 0L;
			double mul = (r.getMaxAmount() - r.getMinAmount()) * randomMul + r.getMinAmount();
			long am = Math.round(mul * Config.finiteAmountBase);
			if(extractedAmount >= am)return -1L;
			return am - extractedAmount;
		}
		return 0L;
	}

	public float getRandomMul() {
		return randomMul;
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

	public void populate(LevelChunk chunk) {
		RecipeHolder<VeinRecipe> r = OreVeinGenerator.pick(chunk);
		if(r != null) {
			RandomSource rng = OreVeinGenerator.rngFromChunk(chunk);
			recipe = r.id();
			randomMul = rng.nextFloat();
		}
		loaded = true;
	}
}