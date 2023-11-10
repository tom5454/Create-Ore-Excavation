package com.tom.createores;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import com.tom.createores.block.DrillBlock;
import com.tom.createores.block.ExtractorBlock;
import com.tom.createores.block.IOBlock;
import com.tom.createores.block.KineticInputBlock;
import com.tom.createores.block.MultiblockBlock;
import com.tom.createores.block.MultiblockPart;
import com.tom.createores.block.entity.DrillBlockEntity;
import com.tom.createores.block.entity.ExtractorBlockEntity;
import com.tom.createores.block.entity.IOBlockEntity;
import com.tom.createores.block.entity.KineticInputBlockEntity;
import com.tom.createores.block.entity.KineticInputInstance;
import com.tom.createores.block.entity.MultiblockBlockEntity;
import com.tom.createores.client.DrillRenderer;
import com.tom.createores.client.KineticInputBlockEntityRenderer;
import com.tom.createores.item.MultiBlockItem;
import com.tom.createores.item.OreVeinFinderItem;

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
			.transform(BlockStressDefaults.setImpact(16))
			.tag(BlockTags.NEEDS_IRON_TOOL)
			.transform(TagGen.pickaxeOnly())
			.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), prov.models().getBuilder("kinetic_in").texture("particle", new ResourceLocation("create:block/brass_casing"))))
			.lang("Multiblock Rotational Input")
			.register();

	public static final BlockEntityEntry<KineticInputBlockEntity> KINETIC_INPUT_TILE = REGISTRATE
			.blockEntity("kinetic_input", KineticInputBlockEntity::new)
			.instance(() -> KineticInputInstance::new)
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
		add("ore.coe.hardenedDiamond", "Hardened Diamond");
		add("tag.item.createoreexcavation.drills", "All Drills");
		add("jm.coe.veinsOverlayToggle", "Create: Ore Excavation veins overlay");
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
