package com.tom.createores;

import org.slf4j.Logger;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.chunk.LevelChunk;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.block.BlockStressValues;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.data.CreateRegistrate;

import com.tom.createores.client.ClientRegistration;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.ExcavatingRecipe.Serializer.RecipeFactory;
import com.tom.createores.recipe.ExtractorRecipe;

@Mod(CreateOreExcavation.MODID)
public class CreateOreExcavation {
	public static final String MODID = "createoreexcavation";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static CreateRegistrate registrate;

	private static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
	private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MODID);

	public static final RecipeTypeGroup<DrillingRecipe> DRILLING_RECIPES = recipe("drilling", DrillingRecipe::new);
	public static final RecipeTypeGroup<ExtractorRecipe> EXTRACTING_RECIPES = recipe("extracting", ExtractorRecipe::new);

	public static final CreativeModeTab MOD_TAB = new CreativeModeTab("createoreexcavation.tab") {

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(Registration.DIAMOND_DRILL_ITEM.get());
		}
	};

	public static final TagKey<Item> DRILL_TAG = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(MODID, "drills"));

	public CreateOreExcavation() {
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		// Register the enqueueIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

		registrate = CreateRegistrate.create(MODID).registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus());

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
		FMLJavaModLoadingContext.get().getModEventBus().register(ForgeConfig.class);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		Registration.register();

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MENU_TYPE.register(bus);
		RECIPE_SERIALIZER.register(bus);
		TYPE_REGISTER.register(bus);
	}

	private static <T extends ExcavatingRecipe> RecipeTypeGroup<T> recipe(String name, RecipeFactory<T> factory) {
		RecipeTypeGroup<T> rg = new RecipeTypeGroup<>(new ResourceLocation(MODID, name));
		rg.recipeType = TYPE_REGISTER.register(name, () -> RecipeType.simple(new ResourceLocation(MODID, name)));
		rg.serializer = RECIPE_SERIALIZER.register(name, () -> new ExcavatingRecipe.Serializer<>(rg, factory));
		return rg;
	}

	public static class RecipeTypeGroup<T extends Recipe<?>> {
		private RegistryObject<RecipeSerializer<T>> serializer;
		private RegistryObject<RecipeType<T>> recipeType;
		private ResourceLocation id;

		public RecipeTypeGroup(ResourceLocation id) {
			this.id = id;
		}

		public RecipeType<T> getRecipeType() {
			return recipeType.get();
		}

		public RecipeSerializer<T> getSerializer() {
			return serializer.get();
		}

		public ResourceLocation getId() {
			return id;
		}
	}

	private static <M extends AbstractContainerMenu> RegistryObject<MenuType<M>> menu(String name, MenuSupplier<M> create) {
		return MENU_TYPE.register(name, () -> new MenuType<>(create));
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Create Ore Excavation starting");
		Registration.postRegister();
		BlockStressValues.registerProvider(MODID, AllConfigs.SERVER.kinetics.stressValues);
		COECommand.init();
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		event.enqueueWork(ClientRegistration::register);
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
	}

	private void processIMC(final InterModProcessEvent event) {
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
	}

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent evt) {
		COECommand.register(evt);
	}

	@SubscribeEvent
	public void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<LevelChunk> event){
		if (!event.getObject().getCapability(OreDataCapability.ORE_CAP).isPresent()) {
			event.addCapability(new ResourceLocation(MODID, "ore_data"), new OreDataCapability());
		}
	}

	@SubscribeEvent
	public void reloadEvent(TagsUpdatedEvent evt) {
		OreVeinGenerator.invalidate();
	}

	public static CreateRegistrate registrate() {
		return registrate;
	}
}
