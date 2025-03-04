package com.tom.createores.recipe;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.tom.createores.CreateOreExcavation;

public class DrillingRecipe extends ExcavatingRecipe {
	public NonNullList<ProcessingOutput> output;

	@Override
	public String getGroup() {
		return "drilling";
	}

	@Override
	protected void fromNetwork(RegistryFriendlyByteBuf buffer) {
		output = NonNullList.create();
		int size = buffer.readVarInt();
		for (int i = 0; i < size; i++)
			output.add(ProcessingOutput.read(buffer));
	}

	@Override
	protected void toNetwork(RegistryFriendlyByteBuf buffer) {
		buffer.writeVarInt(output.size());
		output.forEach(o -> o.write(buffer));
	}

	public NonNullList<ProcessingOutput> getOutput() {
		return output;
	}

	public static record DrillingRecipeData(List<ProcessingOutput> output) {
	}

	public static class Serializer extends ExcavatingRecipe.Serializer<DrillingRecipe> {

		public Serializer() {
			super(DrillingRecipe::new);
		}

		private static final MapCodec<DrillingRecipeData> CODEC2 = RecordCodecBuilder.mapCodec(
				b -> b.group(
						Codec.list(ProcessingOutput.CODEC).fieldOf("output").forGetter(DrillingRecipeData::output)
						)
				.apply(b, DrillingRecipeData::new)
				);

		public static final MapCodec<DrillingRecipe> CODEC = Codec.mapPair(
				ExcavatingRecipe.Serializer.CODEC,
				CODEC2
				).xmap((Pair<ExcavatingRecipeCommon, DrillingRecipeData> p) -> {
					var r = new DrillingRecipe();
					r.setFromCommon(p.getFirst());
					r.output = NonNullList.copyOf(p.getSecond().output());
					return r;
				}, (DrillingRecipe r) -> {
					return Pair.of(r.getCommon(), new DrillingRecipeData(r.output));
				});

		@Override
		public MapCodec<DrillingRecipe> codec() {
			return CODEC;
		}
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CreateOreExcavation.DRILLING_RECIPES.getSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return CreateOreExcavation.DRILLING_RECIPES.getRecipeType();
	}
}
