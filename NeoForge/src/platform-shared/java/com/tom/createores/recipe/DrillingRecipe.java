package com.tom.createores.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DrillingRecipe extends ExcavatingRecipe {
	public NonNullList<ProcessingOutput> output;

	public DrillingRecipe(ResourceLocation id, RecipeType<?> type, RecipeSerializer<?> serializer) {
		super(id, type, serializer);
	}

	@Override
	public String getGroup() {
		return "drilling";
	}

	@Override
	protected void fromJson(JsonObject json) {
		output = NonNullList.create();
		for (JsonElement je : GsonHelper.getAsJsonArray(json, "output")) {
			output.add(ProcessingOutput.deserialize(je));
		}
	}

	@Override
	protected void toJson(JsonObject json) {
		JsonArray ar = new JsonArray();
		output.stream().map(ProcessingOutput::serialize).forEach(ar::add);
		json.add("output", ar);
	}

	@Override
	protected void fromNetwork(FriendlyByteBuf buffer) {
		output = NonNullList.create();
		int size = buffer.readVarInt();
		for (int i = 0; i < size; i++)
			output.add(ProcessingOutput.read(buffer));
	}

	@Override
	protected void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeVarInt(output.size());
		output.forEach(o -> o.write(buffer));
	}

	public NonNullList<ProcessingOutput> getOutput() {
		return output;
	}
}
