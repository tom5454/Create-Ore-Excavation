package com.tom.createores;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.MenuBuilder.MenuFactory;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import com.tom.createores.block.DrillBlock;
import com.tom.createores.block.ExtractorBlock;
import com.tom.createores.block.IOBlock;
import com.tom.createores.block.KineticInputBlock;
import com.tom.createores.block.MultiblockBlock;
import com.tom.createores.block.MultiblockPart;
import com.tom.createores.block.SampleDrillBlock;
import com.tom.createores.block.entity.DrillBlockEntity;
import com.tom.createores.block.entity.ExtractorBlockEntity;
import com.tom.createores.block.entity.IOBlockEntity;
import com.tom.createores.block.entity.KineticInputBlockEntity;
import com.tom.createores.block.entity.KineticInputVisual;
import com.tom.createores.block.entity.MultiblockBlockEntity;
import com.tom.createores.block.entity.SampleDrillBlockEntity;
import com.tom.createores.client.DrillRenderer;
import com.tom.createores.client.KineticInputBlockEntityRenderer;
import com.tom.createores.item.MultiBlockItem;
import com.tom.createores.item.OreVeinAtlasItem;
import com.tom.createores.item.OreVeinFinderItem;
import com.tom.createores.menu.OreVeinAtlasMenu;
import com.tom.createores.menu.OreVeinAtlasScreen;

public class Registration {
	private static final CreateRegistrate REGISTRATE = CreateOreExcavation.registrate();

	public static final RegistryEntry<CreativeModeTab> TAB = REGISTRATE.defaultCreativeTab("create_ore_excavation",
			c -> c.icon(() -> new ItemStack(Registration.DIAMOND_DRILL_ITEM.get()))
			).register();

	public static final BlockEntry<DrillBlock> DRILL_BLOCK = REGISTRATE.block("drilling_machine", DrillBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(MultiblockPart.props())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			.blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
					.getExistingFile(prov.modLoc("drill_model"))))
			.item(MultiBlockItem::new)
			.properties(p -> p.stacksTo(1))
			.transform(b -> b.model((c, p) -> {
				p.withExistingParent("drilling_machine",
						p.modLoc("block/drill_model"));
			}).build())
			.lang("Drilling Machine")
			.register();

	public static final BlockEntityEntry<DrillBlockEntity> DRILL_TILE = REGISTRATE
			.blockEntity("drill", DrillBlockEntity::new)
			.validBlocks(DRILL_BLOCK)
			.renderer(() -> DrillRenderer::new)
			.register();

	public static final BlockEntry<KineticInputBlock> KINETIC_INPUT = REGISTRATE.block("kinetic_input", KineticInputBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(MultiblockPart.propsGhost())
			//.transform(BlockStressDefaults.setImpact(16))
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getBuilder("kinetic_in").texture("particle", new ResourceLocation("create:block/brass_casing"))))
			.lang("Multiblock Rotational Input")
			.register();

	public static final BlockEntityEntry<KineticInputBlockEntity> KINETIC_INPUT_TILE = REGISTRATE
			.blockEntity("kinetic_input", KineticInputBlockEntity::new)
			.visual(() -> KineticInputVisual::new)
			.validBlocks(KINETIC_INPUT)
			.renderer(() -> KineticInputBlockEntityRenderer::new)
			.register();

	public static final BlockEntry<MultiblockBlock> GHOST_BLOCK = REGISTRATE.block("multiblock", MultiblockBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(MultiblockPart.propsGhost())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getBuilder("multiblock_ghost").texture("particle", new ResourceLocation("create:block/brass_casing"))))
			.lang("Multiblock")
			.register();

	public static final BlockEntityEntry<MultiblockBlockEntity> GHOST_TILE = REGISTRATE
			.blockEntity("multiblock", MultiblockBlockEntity::new)
			.validBlocks(GHOST_BLOCK)
			.register();

	public static final BlockEntry<IOBlock> IO_BLOCK = REGISTRATE.block("io_block", IOBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(MultiblockPart.propsGhost())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getBuilder("io_block").texture("particle", new ResourceLocation("create:block/brass_casing"))))
			.lang("Multiblock IO")
			.register();

	public static final BlockEntityEntry<IOBlockEntity> IO_TILE = REGISTRATE
			.blockEntity("io", IOBlockEntity::new)
			.validBlocks(IO_BLOCK)
			.register();

	public static final BlockEntry<ExtractorBlock> EXTRACTOR_BLOCK = REGISTRATE.block("extractor", ExtractorBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(MultiblockPart.props())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			.blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
					.getExistingFile(prov.modLoc("extractor_model"))))
			.item(MultiBlockItem::new)
			.properties(p -> p.stacksTo(1))
			.transform(b -> b.model((c, p) -> {
				p.withExistingParent("extractor",
						p.modLoc("block/extractor_model"));
			}).build())
			.lang("Fluid Well Extractor")
			.register();

	public static final BlockEntityEntry<ExtractorBlockEntity> EXTRACTOR_TILE = REGISTRATE
			.blockEntity("extractor", ExtractorBlockEntity::new)
			.validBlocks(EXTRACTOR_BLOCK)
			.renderer(() -> DrillRenderer::new)
			.register();

	public static final BlockEntry<SampleDrillBlock> SAMPLE_DRILL_BLOCK = REGISTRATE.block("sample_drill", SampleDrillBlock::new)
			.initialProperties(SharedProperties::copperMetal)
			.properties(MultiblockPart.props())
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models()
					.getExistingFile(prov.modLoc("sample_drill_model"))))
			.item()
			.properties(p -> p.stacksTo(1))
			.transform(b -> b.model((c, p) -> {
				p.withExistingParent("sample_drill",
						p.modLoc("block/sample_drill_item"));
			}).build())
			.lang("Sample Drill")
			.register();

	public static final BlockEntityEntry<SampleDrillBlockEntity> SAMPLE_DRILL_TILE = REGISTRATE
			.blockEntity("sample_drill", SampleDrillBlockEntity::new)
			.validBlocks(SAMPLE_DRILL_BLOCK)
			.renderer(() -> DrillRenderer::new)
			.register();

	public static final ItemEntry<Item> NORMAL_DRILL_ITEM = REGISTRATE
			.item("drill", Item::new)
			.tag(CreateOreExcavation.DRILL_TAG)
			.properties(Registration::tool)
			.lang("Iron Drill")
			.model(item2d("item/normal_drill"))
			.register();

	public static final ItemEntry<Item> DIAMOND_DRILL_ITEM = REGISTRATE
			.item("diamond_drill", Item::new)
			.tag(CreateOreExcavation.DRILL_TAG)
			.properties(Registration::tool)
			.lang("Diamond Drill")
			.model(item2d("item/diamond_drill"))
			.register();

	public static final ItemEntry<Item> NETHERITE_DRILL_ITEM = REGISTRATE
			.item("netherite_drill", Item::new)
			.tag(CreateOreExcavation.DRILL_TAG)
			.properties(Registration::tool)
			.lang("Netherite Drill")
			.model(item2d("item/netherite_drill"))
			.register();

	public static final ItemEntry<Item> RAW_DIAMOND = REGISTRATE
			.item("raw_diamond", Item::new)
			.properties(Registration::item)
			.lang("Raw Diamond")
			.model(item2d("item/raw_diamond"))
			.register();

	public static final ItemEntry<Item> RAW_EMERALD = REGISTRATE
			.item("raw_emerald", Item::new)
			.properties(Registration::item)
			.lang("Raw Emerald")
			.model(item2d("item/raw_emerald"))
			.register();

	public static final ItemEntry<Item> RAW_REDSTONE = REGISTRATE
			.item("raw_redstone", Item::new)
			.properties(Registration::item)
			.lang("Raw Redstone")
			.model(item2d("item/raw_redstone"))
			.register();

	public static final ItemEntry<OreVeinFinderItem> VEIN_FINDER_ITEM = REGISTRATE
			.item("vein_finder", OreVeinFinderItem::new)
			.properties(Registration::tool)
			.lang("Ore Vein Finder")
			.model(itemTool("item/vein_finder"))
			.register();

	public static final ItemEntry<OreVeinAtlasItem> VEIN_ATLAS_ITEM = REGISTRATE
			.item("vein_atlas", OreVeinAtlasItem::new)
			.properties(Registration::tool)
			.lang("Ore Vein Atlas")
			.model(item2d("item/vein_atlas"))
			.register();

	public static final MenuEntry<OreVeinAtlasMenu> VEIN_ATLAS_MENU = REGISTRATE
			.menu("vein_atlas", (MenuFactory<OreVeinAtlasMenu>) OreVeinAtlasMenu::new, () -> OreVeinAtlasScreen::new)
			.register();

	public static void register() {
		add("config.coe.finiteAmountBase", "Finite vein amount base");
		add("config.coe.defaultInfinite", "Veins infinite by default");
		add("config.coe.maxExtractorsPerVein", "Max number of extractor per ore vein, Set to 0 for infinite");
		add("config.coe.veinFinderNear", "Vein Finder 'Found Nearby' range in chunks");
		add("config.coe.veinFinderFar", "Vein Finder accuracy for 'Found traces of ...'");
		add("config.coe.veinFinderCd", "Vein Finder use cooldown in ticks");
		add("chat.coe.veinFinder.info", "Vein Finder Result:");
		add("chat.coe.veinFinder.pos", "At: %d, %d");
		add("chat.coe.veinFinder.found", "Found in Chunk: %s");
		add("chat.coe.veinFinder.nothing", "Nothing");
		add("chat.coe.veinFinder.nearby", "Found nearby: %s");
		add("chat.coe.veinFinder.far", "Found traces of: %s");
		add("chat.coe.veinFinder.distance", "%s (~%s blocks away)");
		add("chat.coe.sampleDrill.addedToAtlas", "Vein Details added to Ore Vein Atlas");
		add("chat.coe.sampleDrill.noAtlas", "Ore Vein Atlas not found");
		add("chat.coe.sampleDrill.start", "Sample Drill started");
		add("chat.coe.sampleDrill.running", "Sample Drill already running");
		add("chat.coe.sampleDrill.noAir", "Missing Copper Backtank on top");
		add("chat.coe.sampleDrill.lowAir", "Copper Backtank low on air");
		add("chat.coe.sampleDrill.notDone", "The drill must finish a single mining cycle before collecting a sample.");
		add("command.coe.setvein.success", "Successfully set vein to: %s");
		add("command.coe.locate.success", "The nearest %s is at %s (%s blocks away)");
		add("command.coe.locate.failed", "Could not find \"%s\" within reasonable distance");
		add("info.coe.drill.noFluid", "The machine needs drilling fluid");
		add("info.coe.drill.noDrill", "The machine needs drill item");
		add("info.coe.drill.badDrill", "Drill not compatible");
		add("info.coe.drill.installed", "Installed drill: %s");
		add("info.coe.drill.progress", "Progress");
		add("info.coe.drill.resourceRemaining", "Resource remaining: %s");
		add("info.coe.drill.err_no_vein", "No vein to excavate");
		add("info.coe.drill.err_vein_empty", "The vein is depleted");
		add("info.coe.drill.err_too_many_excavators", "Too many vein extractors");
		add("info.coe.drill.err_no_recipe", "Couldn't find a valid recipe for this vein");
		add("info.coe.drill.noGround", "The machine must be on solid ground");
		add("info.coe.drill.fluidInfo", "Drilling Fluid:");
		add("info.coe.extractor.output", "Extractor Output:");
		add("info.coe.sample_drill.air", "Air");
		add("info.coe.sample_drill.no_air", "Missing Copper Backtank on top");
		add("info.coe.sample_drill.low_air", "Copper Backtank low on air");
		add("info.coe.sample_drill.done", "Done, Click to add result to Atlas");
		add("info.coe.sample_drill.click_to_start", "Click to start drilling");
		add("info.coe.atlas.vein_types", "Vein Types");
		add("info.coe.atlas.vein_size", "Vein Size: %s");
		add("info.coe.atlas.vein_size.infinite", "Renewable (\u221E)");
		add("info.coe.atlas.location", "Location:");
		add("info.coe.atlas.location2", "~%s, *, ~%s");
		add("info.coe.atlas.dimension", "In: %s");
		add("jei.coe.recipe.drilling", "Drilling Machine");
		add("jei.coe.recipe.extracting", "Fluid Extractor");
		add("jei.coe.recipe.veins", "Ore Veins");
		add("tooltip.coe.variableImpact", "Variable Impact");
		add("tooltip.coe.biome.whitelist", "Biome Whitelist:");
		add("tooltip.coe.biome.blacklist", "Biome Blacklist:");
		add("tooltip.coe.processTime", "Ticks: %s");
		add("tooltip.coe.finiteVeins", "Finite veins size: %s - %s");
		add("tooltip.coe.infiniteVeins", "Infinite veins");
		add("tooltip.coe.page", "Page: %s/%s");
		add("tooltip.coe.atlas.exclude", "Vein Excluded");
		add("tooltip.coe.atlas.include", "Click to exclude");
		add("tooltip.coe.atlas.target", "Vein Targeted");
		add("tooltip.coe.atlas.switch_target", "Switch Target");
		add("tooltip.coe.atlas.set_target", "Set Target");
		add("tooltip.coe.atlas.show_hide", "Show Hidden Veins");
		add("tooltip.coe.atlas.hide_vein", "Hide Vein");
		add("tooltip.coe.vein_finder.filtered", "Ore Vein Finder filtered using the Atlas");
		add("ore.coe.hardenedDiamond", "Hardened Diamond");
		add("tag.item.createoreexcavation.drills", "All Drills");
		add("jm.coe.veinsOverlayToggle", "Create: Ore Excavation veins overlay");
		add("upgrade.createoreexcavation.vein_finder.adjective", "Vein Surveyor");

		if (CreateOreExcavation.isModLoaded("computercraft")) {
			//CCRegistration.init();
		}
	}

	public static void add(String key, String value) {
		REGISTRATE.addRawLang(key, value);
	}

	public static void postRegister() {
		//REGISTRATE.addToSection(DRILL_BLOCK, AllSections.KINETICS);
		//REGISTRATE.addToSection(EXTRACTOR_BLOCK, AllSections.KINETICS);
	}

	private static Item.Properties tool(Item.Properties p) {
		return p.stacksTo(1);
	}

	private static Item.Properties item(Item.Properties p) {
		return p;
	}

	private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> item2d(String texture) {
		return (c, p) -> p.singleTexture(c.getName(), p.mcLoc("item/generated"), "layer0", p.modLoc(texture));
	}

	private static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> itemTool(String texture) {
		return (c, p) -> p.singleTexture(c.getName(), p.mcLoc("item/handheld"), "layer0", p.modLoc(texture));
	}
}
