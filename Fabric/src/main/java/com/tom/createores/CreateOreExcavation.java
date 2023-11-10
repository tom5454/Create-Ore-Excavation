package com.tom.createores;

import org.slf4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraftforge.fml.config.ModConfig;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;

import com.tom.createores.network.Packets;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.VeinRecipe;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;

public class CreateOreExcavation implements ModInitializer {
	public static final String MODID = "createoreexcavation";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static CreateRegistrate registrate;
	public static boolean journeyMap;

	public static final RecipeTypeGroup<DrillingRecipe> DRILLING_RECIPES = recipe("drilling", DrillingRecipe::new);
	public static final RecipeTypeGroup<ExtractorRecipe> EXTRACTING_RECIPES = recipe("extracting", ExtractorRecipe::new);
	public static final RecipeTypeGroup<VeinRecipe> VEIN_RECIPES = recipe("vein", VeinRecipe::new);

	public static final TagKey<Item> DRILL_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(MODID, "drills"));

	public CreateOreExcavation() {
		registrate = CreateRegistrate.create(MODID);

		ForgeConfigRegistry.INSTANCE.register(CreateOreExcavation.MODID, ModConfig.Type.COMMON, Config.commonSpec);
		ForgeConfigRegistry.INSTANCE.register(CreateOreExcavation.MODID, ModConfig.Type.SERVER, Config.serverSpec);

		ModConfigEvents.loading(CreateOreExcavation.MODID).register(c -> {
			LOGGER.info("Loaded Create Ore Excavation config file {}", c.getFileName());
			Config.load(c);
		});
		ModConfigEvents.reloading(CreateOreExcavation.MODID).register(c -> {
			LOGGER.info("Create Ore Excavation config just got changed on the file system!");
			Config.load(c);
		});
		Packets.init();

		Registration.register();
	}

	private static <T extends ExcavatingRecipe> RecipeTypeGroup<T> recipe(String name, ExcavatingRecipe.RecipeFactory<T> factory) {
		RecipeTypeGroup<T> rg = new RecipeTypeGroup<>(new ResourceLocation(MODID, name));
		String stringId = rg.id.toString();
		rg.recipeType = Registry.register(BuiltInRegistries.RECIPE_TYPE, rg.id, new RecipeType<T>() {

			@Override
			public String toString() {
				return stringId;
			}
		});
		rg.serializer = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, rg.id, new ExcavatingRecipe.Serializer<>(rg, factory));
		return rg;
	}

	private static <T extends VeinRecipe> RecipeTypeGroup<T> recipe(String name, VeinRecipe.RecipeFactory<T> factory) {
		RecipeTypeGroup<T> rg = new RecipeTypeGroup<>(new ResourceLocation(MODID, name));
		String stringId = rg.id.toString();
		rg.recipeType = Registry.register(BuiltInRegistries.RECIPE_TYPE, rg.id, new RecipeType<T>() {

			@Override
			public String toString() {
				return stringId;
			}
		});
		rg.serializer = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, rg.id, new VeinRecipe.Serializer<>(rg, factory));
		return rg;
	}

	public static class RecipeTypeGroup<T extends Recipe<?>> {
		private RecipeSerializer<T> serializer;
		private RecipeType<T> recipeType;
		private ResourceLocation id;

		public RecipeTypeGroup(ResourceLocation id) {
			this.id = id;
		}

		public RecipeType<T> getRecipeType() {
			return recipeType;
		}

		public RecipeSerializer<T> getSerializer() {
			return serializer;
		}

		public ResourceLocation getId() {
			return id;
		}
	}

	public static CreateRegistrate registrate() {
		return registrate;
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Create Ore Excavation starting");
		registrate.register();
		Registration.postRegister();
		OreDataCapability.init();
		//BlockStressValues.registerProvider(MODID, AllConfigs.SERVER.kinetics.stressValues);
		COECommand.init();
	}
}
