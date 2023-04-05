package com.tom.createores;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;

import net.minecraftforge.registries.RegistryObject;

import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;

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

public class Registration {
	private static final CreateRegistrate REGISTRATE = CreateOreExcavation.registrate()
			.creativeModeTab(() -> CreateOreExcavation.MOD_TAB);

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
				p.withExistingParent(Registration.DRILL_BLOCK.get().getRegistryName().getPath(),
						p.modLoc("block/drill_model"));
			}).build())
			.lang("Drilling Machine")
			.register();

	public static final BlockEntityEntry<DrillBlockEntity> DRILL_TILE = REGISTRATE
			.tileEntity("drill", DrillBlockEntity::new)
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
			.tileEntity("kinetic_input", KineticInputBlockEntity::new)
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
			.tileEntity("multiblock", MultiblockBlockEntity::new)
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
			.tileEntity("io", IOBlockEntity::new)
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
				p.withExistingParent(Registration.EXTRACTOR_BLOCK.get().getRegistryName().getPath(),
						p.modLoc("block/extractor_model"));
			}).build())
			.lang("Fluid Well Extractor")
			.register();

	public static final BlockEntityEntry<ExtractorBlockEntity> EXTRACTOR_TILE = REGISTRATE
			.tileEntity("extractor", ExtractorBlockEntity::new)
			.validBlocks(EXTRACTOR_BLOCK)
			.renderer(() -> DrillRenderer::new)
			.register();

	public static void register() {
		add("itemGroup.createoreexcavation", "Create Ore Excavation");
		add(CreateOreExcavation.NORMAL_DRILL_ITEM, "Iron Drill");
		add(CreateOreExcavation.DIAMOND_DRILL_ITEM, "Diamond Drill");
		add(CreateOreExcavation.NETHERITE_DRILL_ITEM, "Netherite Drill");
		add(CreateOreExcavation.RAW_DIAMOND, "Raw Diamond");
		add(CreateOreExcavation.RAW_EMERALD, "Raw Emerald");
		add(CreateOreExcavation.RAW_REDSTONE, "Raw Redstone");
		add(CreateOreExcavation.VEIN_FINDER_ITEM, "Ore Vein Finder");
		add("config.coe.generationChance", "Weight value for empty chunk");
		add("config.coe.finiteAmountBase", "Finite vein amount base");
		add("config.coe.defaultInfinite", "Veins infinite by default");
		add("config.coe.maxExtractorsPerVein", "Max number of extractor per ore vein, Set to 0 for infinite");
		add("chat.coe.veinFinder.info", "Vein Finder Result:");
		add("chat.coe.veinFinder.pos", "At: %d, %d");
		add("chat.coe.veinFinder.found", "Found in Chunk: %s");
		add("chat.coe.veinFinder.nothing", "Nothing");
		add("chat.coe.veinFinder.nearby", "Found nearby: %s");
		add("chat.coe.veinFinder.far", "Found traces of: %s");
		add("command.coe.setvein.success", "Successfully set vein to: %s");
		add("info.coe.drill.noFluid", "The machine needs drilling fluid");
		add("info.coe.drill.noDrill", "The machine needs drill item");
		add("info.coe.drill.badDrill", "Drill not compatible");
		add("info.coe.drill.installed", "Installed drill: %s");
		add("info.coe.drill.progress", "Progress");
		add("info.coe.drill.resourceRemaining", "Resource remaining: %s");
		add("info.coe.drill.err_no_vein", "No vein to excavate");
		add("info.coe.drill.err_vein_empty", "The vein is depleted");
		add("info.coe.drill.err_too_many_excavators", "Too many vein extractors");
		add("jei.coe.recipe.drilling", "Drilling Machine");
		add("jei.coe.recipe.extracting", "Fluid Extractor");
		add("tooltip.coe.variableImpact", "Variable Impact");
		add("tooltip.coe.biome.whitelist", "Biome Whitelist:");
		add("tooltip.coe.biome.blacklist", "Biome Blacklist:");
		add("tooltip.coe.processTime", "Ticks: %s");
		add("tooltip.coe.finiteVeins", "Finite veins size: %s - %s");
		add("tooltip.coe.infiniteVeins", "Infinite veins");
		add("tooltip.coe.page", "Page: %s/%s");
		add("ore.coe.hardenedDiamond", "Hardened Diamond");
	}

	public static void add(RegistryObject<? extends Item> key, String name) {
		REGISTRATE.addDataGenerator(ProviderType.LANG, prov -> prov.add(key.get().getDescriptionId(), name));
	}

	public static void add(String key, String value) {
		REGISTRATE.addRawLang(key, value);
	}

	public static void postRegister() {
		REGISTRATE.addToSection(DRILL_BLOCK, AllSections.KINETICS);
		REGISTRATE.addToSection(EXTRACTOR_BLOCK, AllSections.KINETICS);
	}
}
