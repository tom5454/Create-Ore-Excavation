package com.tom.createores.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.simibubi.create.foundation.item.SmartInventory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.createores.Config;
import com.tom.createores.CreateOreExcavation;
import com.tom.createores.CreateOreExcavation.RecipeTypeGroup;
import com.tom.createores.util.ThreeState;

public class VeinRecipe implements Recipe<SmartInventory> {
	public ResourceLocation id;
	public RecipeType<?> type;
	public RecipeSerializer<?> serializer;
	public int priority;
	public Component veinName;
	public TagKey<Biome> biomeWhitelist, biomeBlacklist;
	public ThreeState finite;
	public float amountMultiplierMin, amountMultiplierMax;
	public RandomSpreadStructurePlacement placement;
	public ItemStack icon;
	protected boolean isNet;

	public VeinRecipe(ResourceLocation id, RecipeType<?> type, RecipeSerializer<?> serializer) {
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

	public Component getName() {
		return veinName;
	}

	@Override
	public String getGroup() {
		return "ore_vein_type";
	}

	public boolean canGenerate(ServerLevel lvl, Holder<Biome> b) {
		if(biomeBlacklist != null && isInTag(biomeBlacklist, lvl, b))return false;
		if(biomeWhitelist != null) {
			return isInTag(biomeWhitelist, lvl, b);
		} else
			return true;
	}

	private static boolean isInTag(TagKey<Biome> tag, ServerLevel lvl, Holder<Biome> b) {
		return lvl.getServer().registryAccess().registryOrThrow(Registries.BIOME).getTag(tag).map(t -> t.contains(b)).orElse(false);
	}

	public ThreeState isFinite() {
		return finite;
	}

	public float getMinAmount() {
		return amountMultiplierMin;
	}

	public float getMaxAmount() {
		return amountMultiplierMax;
	}

	public boolean isInfiniteClient() {
		return isNet ? finite == ThreeState.NEVER : finite == ThreeState.DEFAULT ? Config.defaultInfinite : finite == ThreeState.NEVER;
	}

	public long getMinAmountClient() {
		return Math.round(isNet ? (double) amountMultiplierMin : (double) amountMultiplierMin * Config.finiteAmountBase);
	}

	public long getMaxAmountClient() {
		return Math.round(isNet ? (double) amountMultiplierMax : (double) amountMultiplierMax * Config.finiteAmountBase);
	}

	public int getNegGenerationPriority() {
		return -priority;
	}

	public RandomSpreadStructurePlacement getPlacement() {
		return placement;
	}

	public static class Serializer<T extends VeinRecipe> implements RecipeSerializer<T> {
		private static final ResourceLocation NULL = new ResourceLocation("coe:null");
		private final RecipeTypeGroup<?> type;
		private final RecipeFactory<T> create;

		public Serializer(RecipeTypeGroup<?> type, RecipeFactory<T> create) {
			this.type = type;
			this.create = create;
		}

		@Override
		public T fromJson(ResourceLocation pRecipeId, JsonObject json) {
			T r = create.create(pRecipeId, type.getRecipeType(), this);
			r.veinName = Component.Serializer.fromJson(json.get("name").getAsString());
			r.priority = GsonHelper.getAsInt(json, "priority", 0);
			if(json.has("biomeWhitelist")) {
				ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "biomeWhitelist"));
				r.biomeWhitelist = TagKey.create(Registries.BIOME, resourcelocation);
			}
			if(json.has("biomeBlacklist")) {
				ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "biomeBlacklist"));
				r.biomeBlacklist = TagKey.create(Registries.BIOME, resourcelocation);
			}
			r.finite = ThreeState.get(json, "Finite");
			r.amountMultiplierMin = GsonHelper.getAsFloat(json, "amountMin", 1);
			r.amountMultiplierMax = GsonHelper.getAsFloat(json, "amountMax", 2);
			DataResult<RandomSpreadStructurePlacement> result = RandomSpreadStructurePlacement.CODEC.parse(JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "placement"));
			r.placement = result.getOrThrow(false, error -> CreateOreExcavation.LOGGER.error(error));
			r.icon = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "icon"));
			return r;
		}

		public void toJson(T recipe, JsonObject json) {
			json.addProperty("name", Component.Serializer.toJson(recipe.veinName));
			json.addProperty("priority", recipe.priority);
			if(recipe.biomeWhitelist != null)json.addProperty("biomeWhitelist", recipe.biomeWhitelist.location().toString());
			if(recipe.biomeBlacklist != null)json.addProperty("biomeBlacklist", recipe.biomeBlacklist.location().toString());
			recipe.finite.toJson(json, "Finite");
			json.addProperty("amountMin", recipe.amountMultiplierMin);
			json.addProperty("amountMax", recipe.amountMultiplierMax);
			DataResult<JsonElement> result = RandomSpreadStructurePlacement.CODEC.encodeStart(JsonOps.INSTANCE, recipe.placement);
			json.add("placement", result.getOrThrow(false, error -> CreateOreExcavation.LOGGER.error(error)));
			json.add("icon", serializeItem(recipe.icon));
		}

		private JsonObject serializeItem(ItemStack stack) {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
			return jsonobject;
		}

		@Override
		public T fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buffer) {
			T r = create.create(pRecipeId, type.getRecipeType(), this);
			r.priority = buffer.readVarInt();
			r.veinName = buffer.readComponent();
			r.biomeWhitelist = create(buffer);
			r.biomeBlacklist = create(buffer);
			r.finite = buffer.readBoolean() ? ThreeState.ALWAYS : ThreeState.NEVER;
			r.amountMultiplierMin = buffer.readFloat();
			r.amountMultiplierMax = buffer.readFloat();
			r.icon = buffer.readItem();
			r.isNet = true;
			return r;
		}

		private static TagKey<Biome> create(FriendlyByteBuf buffer) {
			ResourceLocation rl = buffer.readResourceLocation();
			if(NULL.equals(rl))return null;
			else return TagKey.create(Registries.BIOME, rl);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, T recipe) {
			buffer.writeVarInt(recipe.priority);
			buffer.writeComponent(recipe.veinName);
			write(recipe.biomeWhitelist, buffer);
			write(recipe.biomeBlacklist, buffer);
			buffer.writeBoolean(recipe.finite == ThreeState.DEFAULT ? !Config.defaultInfinite : recipe.finite == ThreeState.ALWAYS);
			buffer.writeFloat(recipe.amountMultiplierMin * Config.finiteAmountBase);
			buffer.writeFloat(recipe.amountMultiplierMax * Config.finiteAmountBase);
			buffer.writeItem(recipe.icon);
		}

		private static void write(TagKey<Biome> tag, FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(tag != null ? tag.location() : NULL);
		}
	}

	@FunctionalInterface
	public static interface RecipeFactory<T extends VeinRecipe> {
		T create(ResourceLocation id, RecipeType<?> type, RecipeSerializer<?> serializer);
	}
}
