package com.tom.createores.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import com.simibubi.create.foundation.item.SmartInventory;

import com.google.gson.JsonObject;

import com.tom.createores.CreateOreExcavation.RecipeTypeGroup;

public abstract class ExcavatingRecipe implements Recipe<SmartInventory> {
	public ResourceLocation id;
	public RecipeType<?> type;
	public RecipeSerializer<?> serializer;
	public ResourceLocation veinId;
	public Ingredient drill;
	public int priority, ticks, stressMul;
	protected boolean isNet;

	public ExcavatingRecipe(ResourceLocation id, RecipeType<?> type, RecipeSerializer<?> serializer) {
		this.id = id;
		this.type = type;
		this.serializer = serializer;
	}

	@Override
	public boolean matches(SmartInventory pContainer, Level pLevel) {
		return false;
	}

	@Override
	public ItemStack assemble(SmartInventory p_44001_, RegistryAccess p_267165_) {
		return getResultItem(p_267165_);
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return true;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess p_267052_) {
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return serializer;
	}

	@Override
	public RecipeType<?> getType() {
		return type;
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

	protected abstract void fromJson(JsonObject json);
	protected abstract void toJson(JsonObject json);
	protected abstract void fromNetwork(FriendlyByteBuf buffer);
	protected abstract void toNetwork(FriendlyByteBuf buffer);

	public static class Serializer<T extends ExcavatingRecipe> implements RecipeSerializer<T> {
		private final RecipeTypeGroup<?> type;
		private final RecipeFactory<T> create;

		public Serializer(RecipeTypeGroup<?> type, RecipeFactory<T> create) {
			this.type = type;
			this.create = create;
		}

		@Override
		public T fromJson(ResourceLocation pRecipeId, JsonObject json) {
			T r = create.create(pRecipeId, type.getRecipeType(), this);
			r.drill = Ingredient.fromJson(json.get("drill"));
			r.ticks = GsonHelper.getAsInt(json, "ticks");
			r.priority = GsonHelper.getAsInt(json, "priority", 0);
			r.stressMul = GsonHelper.getAsInt(json, "stress", 256);
			r.veinId = new ResourceLocation(GsonHelper.getAsString(json, "vein_id"));
			r.fromJson(json);
			return r;
		}

		public void toJson(T recipe, JsonObject json) {
			json.add("drill", recipe.drill.toJson());
			json.addProperty("ticks", recipe.ticks);
			json.addProperty("stress", recipe.stressMul);
			json.addProperty("priority", recipe.priority);
			json.addProperty("vein_id", recipe.veinId.toString());
			recipe.toJson(json);
		}

		@Override
		public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buffer) {
			T r = create.create(pRecipeId, type.getRecipeType(), this);
			r.drill = Ingredient.fromNetwork(buffer);
			r.ticks = buffer.readVarInt();
			r.stressMul = buffer.readVarInt();
			r.priority = buffer.readVarInt();
			r.veinId = buffer.readResourceLocation();
			r.fromNetwork(buffer);
			r.isNet = true;
			return r;
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, T recipe) {
			recipe.drill.toNetwork(buffer);
			buffer.writeVarInt(recipe.ticks);
			buffer.writeVarInt(recipe.stressMul);
			buffer.writeVarInt(recipe.priority);
			buffer.writeResourceLocation(recipe.veinId);
			recipe.toNetwork(buffer);
		}
	}

	@FunctionalInterface
	public static interface RecipeFactory<T extends ExcavatingRecipe> {
		T create(ResourceLocation id, RecipeType<?> type, RecipeSerializer<?> serializer);
	}
}
