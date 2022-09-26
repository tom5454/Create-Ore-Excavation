package com.tom.createores.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import net.minecraftforge.registries.ForgeRegistryEntry;

import com.simibubi.create.foundation.item.SmartInventory;

import com.google.gson.JsonObject;

import com.tom.createores.Config;
import com.tom.createores.recipe.IRecipe.IResourceRecipe;

public abstract class ExcavatingRecipe implements Recipe<SmartInventory>, IRecipe, IResourceRecipe {
	public ResourceLocation id;
	public RecipeType<?> type;
	public RecipeSerializer<?> serializer;
	public Ingredient drill;
	public int weight, ticks, stressMul;
	public Component veinName;
	public TagKey<Biome> biomeWhitelist, biomeBlacklist;
	public ThreeState finite;
	public float amountMultiplierMin, amountMultiplierMax;

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
	public ItemStack assemble(SmartInventory pContainer) {
		return getResultItem();
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return true;
	}

	@Override
	public ItemStack getResultItem() {
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

	@Override
	public ResourceLocation getRecipeId() {
		return id;
	}

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public Component getName() {
		return veinName;
	}

	public int getStress() {
		return stressMul;
	}

	@Override
	public boolean canGenerate(ServerLevel lvl, Holder<Biome> b) {
		if(biomeBlacklist != null && isInTag(biomeBlacklist, lvl, b))return false;
		if(biomeWhitelist != null) {
			return isInTag(biomeWhitelist, lvl, b);
		} else
			return true;
	}

	private static boolean isInTag(TagKey<Biome> tag, ServerLevel lvl, Holder<Biome> b) {
		return lvl.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getTag(tag).map(t -> t.contains(b)).orElse(false);
	}

	@Override
	public ThreeState isFinite() {
		return finite;
	}

	@Override
	public float getMinAmount() {
		return amountMultiplierMin;
	}

	@Override
	public float getMaxAmount() {
		return amountMultiplierMax;
	}

	protected abstract void fromJson(JsonObject json);
	protected abstract void toJson(JsonObject json);
	protected abstract void fromNetwork(FriendlyByteBuf buffer);
	protected abstract void toNetwork(FriendlyByteBuf buffer);

	public static class Serializer<T extends ExcavatingRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
		private static final ResourceLocation NULL = new ResourceLocation("coe:null");
		private final RecipeType<?> type;
		private final RecipeFactory<T> create;

		public Serializer(RecipeType<?> type, RecipeFactory<T> create) {
			this.type = type;
			this.create = create;
		}

		@Override
		public T fromJson(ResourceLocation pRecipeId, JsonObject json) {
			T r = create.create(pRecipeId, type, this);
			r.drill = Ingredient.fromJson(json.get("drill"));
			r.veinName = Component.Serializer.fromJson(json.get("name").getAsString());
			r.weight = GsonHelper.getAsInt(json, "weight");
			r.ticks = GsonHelper.getAsInt(json, "ticks");
			if(json.has("biomeWhitelist")) {
				ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "biomeWhitelist"));
				r.biomeWhitelist = TagKey.create(Registry.BIOME_REGISTRY, resourcelocation);
			}
			if(json.has("biomeBlacklist")) {
				ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "biomeBlacklist"));
				r.biomeBlacklist = TagKey.create(Registry.BIOME_REGISTRY, resourcelocation);
			}
			r.stressMul = GsonHelper.getAsInt(json, "stress", 256);
			r.finite = ThreeState.get(json, "Finite");
			r.amountMultiplierMin = GsonHelper.getAsFloat(json, "amountMin", 1);
			r.amountMultiplierMax = GsonHelper.getAsFloat(json, "amountMax", 2);
			r.fromJson(json);
			return r;
		}

		public void toJson(T recipe, JsonObject json) {
			json.add("drill", recipe.drill.toJson());
			json.addProperty("name", Component.Serializer.toJson(recipe.veinName));
			json.addProperty("weight", recipe.weight);
			json.addProperty("ticks", recipe.ticks);
			if(recipe.biomeWhitelist != null)json.addProperty("biomeWhitelist", recipe.biomeWhitelist.location().toString());
			if(recipe.biomeBlacklist != null)json.addProperty("biomeBlacklist", recipe.biomeBlacklist.location().toString());
			json.addProperty("stress", recipe.stressMul);
			recipe.finite.toJson(json, "Finite");
			json.addProperty("amountMin", recipe.amountMultiplierMin);
			json.addProperty("amountMax", recipe.amountMultiplierMax);
			recipe.toJson(json);
		}

		@Override
		public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buffer) {
			T r = create.create(pRecipeId, type, this);
			r.drill = Ingredient.fromNetwork(buffer);
			r.ticks = buffer.readVarInt();
			r.weight = buffer.readVarInt();
			r.veinName = buffer.readComponent();
			r.biomeWhitelist = create(buffer);
			r.biomeBlacklist = create(buffer);
			r.stressMul = buffer.readVarInt();
			r.fromNetwork(buffer);
			r.finite = buffer.readBoolean() ? ThreeState.ALWAYS : ThreeState.NEVER;
			r.amountMultiplierMin = buffer.readFloat();
			r.amountMultiplierMax = buffer.readFloat();
			return r;
		}

		private static TagKey<Biome> create(FriendlyByteBuf buffer) {
			ResourceLocation rl = buffer.readResourceLocation();
			if(NULL.equals(rl))return null;
			else return TagKey.create(Registry.BIOME_REGISTRY, rl);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, T recipe) {
			recipe.drill.toNetwork(buffer);
			buffer.writeVarInt(recipe.ticks);
			buffer.writeVarInt(recipe.weight);
			buffer.writeComponent(recipe.veinName);
			write(recipe.biomeWhitelist, buffer);
			write(recipe.biomeBlacklist, buffer);
			buffer.writeVarInt(recipe.stressMul);
			recipe.toNetwork(buffer);
			buffer.writeBoolean(recipe.finite == ThreeState.DEFAULT ? !Config.defaultInfinite : recipe.finite == ThreeState.ALWAYS);
			buffer.writeFloat(recipe.amountMultiplierMin);
			buffer.writeFloat(recipe.amountMultiplierMax);
		}

		private static void write(TagKey<Biome> tag, FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(tag != null ? tag.location() : NULL);
		}

		@FunctionalInterface
		public static interface RecipeFactory<T extends ExcavatingRecipe> {
			T create(ResourceLocation id, RecipeType<?> type, RecipeSerializer<?> serializer);
		}
	}
}
