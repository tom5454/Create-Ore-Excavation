package com.tom.createores;

import java.util.Optional;

import org.slf4j.Logger;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.block.BlockStressValues;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;

import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.client.ClientRegistration;
import com.tom.createores.item.DrillItem;
import com.tom.createores.item.OreVeinFinderItem;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.ExcavatingRecipe.Serializer.RecipeFactory;
import com.tom.createores.recipe.ExtractorRecipe;

@Mod(CreateOreExcavation.MODID)
public class CreateOreExcavation {
	public static final String MODID = "createoreexcavation";
	public static final Logger LOGGER = LogUtils.getLogger();

	private static NonNullSupplier<CreateRegistrate> registrate;

	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPE = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

	public static final RecipeTypeGroup<DrillingRecipe> DRILLING_RECIPES = recipe("drilling", DrillingRecipe::new);
	public static final RecipeTypeGroup<ExtractorRecipe> EXTRACTING_RECIPES = recipe("extracting", ExtractorRecipe::new);

	public static final RegistryObject<DrillItem> NORMAL_DRILL_ITEM = ITEMS.register("drill", DrillItem::new);
	public static final RegistryObject<DrillItem> DIAMOND_DRILL_ITEM = ITEMS.register("diamond_drill", DrillItem::new);
	public static final RegistryObject<DrillItem> NETHERITE_DRILL_ITEM = ITEMS.register("netherite_drill", DrillItem::new);

	public static final RegistryObject<OreVeinFinderItem> VEIN_FINDER_ITEM = ITEMS.register("vein_finder", OreVeinFinderItem::new);

	public static final CreativeModeTab MOD_TAB = new CreativeModeTab("createoreexcavation") {

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(DIAMOND_DRILL_ITEM.get());
		}
	};

	public static final RegistryObject<Item> RAW_DIAMOND = ITEMS.register("raw_diamond", () -> new Item(new Item.Properties().tab(MOD_TAB)));
	public static final RegistryObject<Item> RAW_EMERALD = ITEMS.register("raw_emerald", () -> new Item(new Item.Properties().tab(MOD_TAB)));
	public static final RegistryObject<Item> RAW_REDSTONE = ITEMS.register("raw_redstone", () -> new Item(new Item.Properties().tab(MOD_TAB)));

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

		registrate = CreateRegistrate.lazy(MODID);
		registrate.get();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
		FMLJavaModLoadingContext.get().getModEventBus().register(Config.class);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		Registration.register();

		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		MENU_TYPE.register(FMLJavaModLoadingContext.get().getModEventBus());
		RECIPE_SERIALIZER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	private static <T extends ExcavatingRecipe> RecipeTypeGroup<T> recipe(String name, RecipeFactory<T> factory) {
		RecipeTypeGroup<T> rg = new RecipeTypeGroup<>();
		rg.recipeType = Optional.empty();
		rg.serializer = RECIPE_SERIALIZER.register(name, () -> {
			rg.recipeType = Optional.of(AllRecipeTypes.simpleType(new ResourceLocation(MODID, name)));
			return new ExcavatingRecipe.Serializer<>(rg.getRecipeType(), factory);
		});
		return rg;
	}

	public static class RecipeTypeGroup<T extends Recipe<?>> {
		private RegistryObject<RecipeSerializer<T>> serializer;
		private Optional<RecipeType<T>> recipeType;

		public RecipeType<T> getRecipeType() {
			return recipeType.get();
		}

		public RecipeSerializer<T> getSerializer() {
			return serializer.get();
		}
	}

	private static <M extends AbstractContainerMenu> RegistryObject<MenuType<M>> menu(String name, MenuSupplier<M> create) {
		return MENU_TYPE.register(name, () -> new MenuType<>(create));
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("Create Ore Excavation starting");
		Registration.postRegister();
		BlockStressValues.registerProvider(MODID, AllConfigs.SERVER.kinetics.stressValues);
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
		LiteralArgumentBuilder<CommandSourceStack> l = Commands.literal("coe");
		/*l.then(Commands.literal("chunkInfo").executes(ctx -> {
			OreData d = OreDataCapability.getData(css.getLevel().getChunkAt(css.getPlayerOrException().blockPosition()));
			ctx.getSource().sendSuccess(null, false);
			return 1;
		}));*/

		l.then(Commands.argument("arg", StringArgumentType.greedyString()).executes(cc -> {
			test(cc.getSource(), StringArgumentType.getString(cc, "arg"));
			return 1;
		}));
		evt.getDispatcher().register(l);
	}

	private static void test(CommandSourceStack css, String arg) throws CommandSyntaxException {
		OreData d = OreDataCapability.getData(css.getLevel().getChunkAt(css.getPlayerOrException().blockPosition()));
		css.sendSuccess(new TextComponent("Ore: " + d.getRecipeId()), true);
	}

	@SubscribeEvent
	public void onAttachCapabilitiesChunk(AttachCapabilitiesEvent<LevelChunk> event){
		if (!event.getObject().getCapability(OreDataCapability.ORE_CAP).isPresent()) {
			event.addCapability(new ResourceLocation(MODID, "ore_data"), new OreDataCapability());
		}
	}

	@SubscribeEvent
	public void registerCommands(TagsUpdatedEvent evt) {
		OreVeinGenerator.invalidate();
	}

	public static CreateRegistrate registrate() {
		return registrate.get();
	}
}
