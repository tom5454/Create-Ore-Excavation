package com.tom.createores.recipe;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.tom.createores.util.FluidIngredient;

public abstract class ExcavatingRecipe implements Recipe<RecipeWrapper> {
	public ResourceLocation veinId;
	public Ingredient drill;
	public int priority, ticks, stressMul;
	public Optional<FluidIngredient> drillingFluid;
	protected boolean isNet;

	protected void setFromCommon(ExcavatingRecipeCommon c) {
		veinId = c.veinId();
		drill = c.drill();
		priority = c.priority();
		ticks = c.ticks();
		stressMul = c.stressMul();
		drillingFluid = c.drillingFluid();
	}

	protected ExcavatingRecipeCommon getCommon() {
		return new ExcavatingRecipeCommon(veinId, drill, priority, ticks, stressMul, drillingFluid);
	}

	@Override
	public boolean matches(RecipeWrapper pContainer, Level pLevel) {
		return false;
	}

	@Override
	public ItemStack assemble(RecipeWrapper p_44001_, HolderLookup.Provider p_267165_) {
		return getResultItem(p_267165_);
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return true;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
		return ItemStack.EMPTY;
	}

	public Ingredient getDrill() {
		return drill;
	}

	public int getTicks() {
		return ticks;
	}

	@Override
	public abstract String getGroup();

	public int getStress() {
		return stressMul;
	}

	public Optional<FluidIngredient> getDrillingFluid() {
		return drillingFluid;
	}

	protected abstract void fromNetwork(RegistryFriendlyByteBuf buffer);
	protected abstract void toNetwork(RegistryFriendlyByteBuf buffer);

	public static record ExcavatingRecipeCommon(ResourceLocation veinId, Ingredient drill, int priority, int ticks, int stressMul, Optional<FluidIngredient> drillingFluid) {
	}

	public static abstract class Serializer<T extends ExcavatingRecipe> implements RecipeSerializer<T> {
		private final Supplier<T> create;

		public Serializer(Supplier<T> create) {
			this.create = create;
		}

		private T fromNetwork(RegistryFriendlyByteBuf buffer) {
			T r = create.get();
			r.drill = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
			r.ticks = buffer.readVarInt();
			r.stressMul = buffer.readVarInt();
			r.priority = buffer.readVarInt();
			r.veinId = buffer.readResourceLocation();
			r.fromNetwork(buffer);
			r.drillingFluid = FluidIngredient.read(buffer);
			r.isNet = true;
			return r;
		}

		private void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.drill);
			buffer.writeVarInt(recipe.ticks);
			buffer.writeVarInt(recipe.stressMul);
			buffer.writeVarInt(recipe.priority);
			buffer.writeResourceLocation(recipe.veinId);
			recipe.toNetwork(buffer);
			FluidIngredient.write(buffer, recipe.drillingFluid);
		}

		public static final MapCodec<ExcavatingRecipeCommon> CODEC = RecordCodecBuilder.mapCodec(
				b -> b.group(
						ResourceLocation.CODEC.fieldOf("veinId").forGetter(ExcavatingRecipeCommon::veinId),
						Ingredient.CODEC.optionalFieldOf("drill", Ingredient.EMPTY).forGetter(ExcavatingRecipeCommon::drill),
						Codec.INT.fieldOf("priority").forGetter(ExcavatingRecipeCommon::priority),
						Codec.INT.fieldOf("ticks").forGetter(ExcavatingRecipeCommon::ticks),
						Codec.INT.fieldOf("stress").forGetter(ExcavatingRecipeCommon::stressMul),
						FluidIngredient.CODEC.optionalFieldOf("fluid").forGetter(ExcavatingRecipeCommon::drillingFluid)
						)
				.apply(b, ExcavatingRecipeCommon::new)
				);

		public final StreamCodec<RegistryFriendlyByteBuf, T> STREAM_CODEC = StreamCodec.of(
				this::toNetwork, this::fromNetwork
				);

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
			return STREAM_CODEC;
		}
	}

	@FunctionalInterface
	public static interface RecipeFactory<T extends ExcavatingRecipe> {
		T create(RecipeType<?> type, RecipeSerializer<?> serializer);
	}
}
