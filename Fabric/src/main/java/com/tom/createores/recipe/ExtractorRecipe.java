package com.tom.createores.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import com.simibubi.create.foundation.fluid.FluidHelper;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;

public class ExtractorRecipe extends ExcavatingRecipe {
	public FluidStack output;

	public ExtractorRecipe(ResourceLocation id, RecipeType<?> type, RecipeSerializer<?> serializer) {
		super(id, type, serializer);
	}

	@Override
	public String getGroup() {
		return "extractor";
	}

	@Override
	protected void fromJson(JsonObject json) {
		output = FluidHelper.deserializeFluidStack(json.getAsJsonObject("output"));
	}

	@Override
	protected void fromNetwork(FriendlyByteBuf buffer) {
		output = FluidStack.readFromPacket(buffer);
	}

	@Override
	protected void toNetwork(FriendlyByteBuf buffer) {
		output.writeToPacket(buffer);
	}

	public FluidStack getOutput() {
		return output;
	}

	@Override
	protected void toJson(JsonObject json) {
		json.add("output", FluidHelper.serializeFluidStack(output));
	}
}
