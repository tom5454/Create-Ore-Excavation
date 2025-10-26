package com.tom.createores.data;

import java.util.Random;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.material.Fluids;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import com.google.gson.JsonObject;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.ThreeState;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.tags.Tags;

public class COERecipes extends FabricRecipeProvider {
	private static Random seedRandom;

	public COERecipes(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.NORMAL_DRILL_ITEM.get())
		.pattern("bi ")
		.pattern("ibi")
		.pattern(" ii")
		.define('b', Tags.Items.STORAGE_BLOCKS_IRON)
		.define('i', Tags.Items.INGOTS_IRON)
		.group("create")
		.unlockedBy("iron", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
		.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.DIAMOND_DRILL_ITEM.get())
		.pattern("bi ")
		.pattern("idi")
		.pattern(" ii")
		.define('b', Tags.Items.STORAGE_BLOCKS_DIAMOND)
		.define('i', Tags.Items.GEMS_DIAMOND)
		.define('d', Registration.NORMAL_DRILL_ITEM.get())
		.group("create")
		.unlockedBy("diamond", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.GEMS_DIAMOND).build()))
		.save(consumer);

		netheriteSmithing(consumer, Registration.DIAMOND_DRILL_ITEM.get(), RecipeCategory.MISC, Registration.NETHERITE_DRILL_ITEM.get());

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.VEIN_FINDER_ITEM.get())
		.pattern("ea ")
		.pattern("rs ")
		.pattern("  s")
		.define('e', Items.ENDER_EYE)
		.define('a', Tags.Items.GEMS_AMETHYST)
		.define('s', Tags.Items.RODS_WOODEN)
		.define('r', Tags.Items.ORES_REDSTONE)
		.group("create")
		.unlockedBy("diamond", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENDER_EYE))
		.save(consumer);

		MechanicalCraftingRecipeBuilder.shapedRecipe(Registration.DRILL_BLOCK.get())
		.patternLine("BbtbB")
		.patternLine("beSeb")
		.patternLine("CmDmF")
		.patternLine("bsssb")
		.patternLine("BbbbB")
		.key('B', TagKey.create(Registries.ITEM, new ResourceLocation("c:brass_blocks")))
		.key('b', TagKey.create(Registries.ITEM, new ResourceLocation("c:brass_plates")))
		.key('e', AllItems.ELECTRON_TUBE.get())
		.key('S', AllBlocks.SPOUT.get())
		.key('C', AllBlocks.BRASS_CASING.get())
		.key('m', AllItems.PRECISION_MECHANISM.get())
		.key('D', AllBlocks.MECHANICAL_DRILL.get())
		.key('s', AllItems.STURDY_SHEET.get())
		.key('F', AllBlocks.BRASS_TUNNEL.get())
		.key('t', AllBlocks.COPPER_CASING.get())
		.build(consumer);

		MechanicalCraftingRecipeBuilder.shapedRecipe(Registration.EXTRACTOR_BLOCK.get())
		.patternLine("BbPbB")
		.patternLine("beHeb")
		.patternLine("CmDmb")
		.patternLine("bsssb")
		.patternLine("BbbbB")
		.key('B', TagKey.create(Registries.ITEM, new ResourceLocation("c:brass_blocks")))
		.key('b', TagKey.create(Registries.ITEM, new ResourceLocation("c:brass_plates")))
		.key('e', AllItems.ELECTRON_TUBE.get())
		.key('H', AllBlocks.HOSE_PULLEY.get())
		.key('C', AllBlocks.BRASS_CASING.get())
		.key('m', AllItems.PRECISION_MECHANISM.get())
		.key('D', AllBlocks.MECHANICAL_DRILL.get())
		.key('s', AllItems.STURDY_SHEET.get())
		.key('P', AllBlocks.MECHANICAL_PUMP.get())
		.build(consumer);

		seedRandom = new Random(10387320);

		new DrillingBuilder(Items.RAW_IRON, 30*20, 1024, 128).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(10, 30).save("iron", consumer);
		new DrillingBuilder(Items.RAW_GOLD, 30*20, 1024, 512).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(2, 4).setStress(192).save("gold", consumer);
		new DrillingBuilder(Items.RAW_COPPER, 30*20, 1024, 128).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(10, 30).save("copper", consumer);
		new DrillingBuilder(Items.COAL, 10*20, 1024, 128).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(15, 40).save("coal", consumer);
		new DrillingBuilder(Registration.RAW_DIAMOND.get(), 60*20, 2048, 1024).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).setStress(512).veinSize(0.5f, 2).save("diamond", consumer);
		new DrillingBuilder(Registration.RAW_REDSTONE.get(), 30*20, 1024, 256).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(10, 30).save("redstone", consumer);
		new DrillingBuilder(Registration.RAW_EMERALD.get(), 60*20, 2048, 1024).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(0.2f, 1).setStress(512).save("emerald", consumer);
		new DrillingBuilder(new ProcessingOutput(new ItemStack(Registration.RAW_DIAMOND.get()), 1F), 20*20, Component.translatable("ore.coe.hardenedDiamond"), 4096, 2048).addOutput(Items.DIAMOND, 0.1f).setDrill(Ingredient.of(Registration.NETHERITE_DRILL_ITEM.get())).setDrillingFluid(FluidIngredient.fromFluid(Fluids.LAVA, 500)).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).setStress(1024).veinSize(1f, 3f).save("hardened_diamond", consumer);
		new DrillingBuilder(AllItems.RAW_ZINC.get(), 30*20, 1024, 128).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(8, 24).save("zinc", consumer);
		new DrillingBuilder(Items.LAPIS_LAZULI, 20*20, 1024, 128).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(8, 24).save("lapis", consumer);

		new DrillingBuilder(Items.GLOWSTONE_DUST, 60*20, 1024, 128).setBiomeWhitelist(BiomeTags.IS_NETHER).veinSize(5, 12).save("glowstone", consumer);
		new DrillingBuilder(Items.QUARTZ, 60*20, 1024, 128).setBiomeWhitelist(BiomeTags.IS_NETHER).setStress(512).veinSize(8, 24).save("quartz", consumer);
		new DrillingBuilder(new ItemStack(Blocks.ANCIENT_DEBRIS), 0.2f, 200*20, 4096, 2048).addOutput(Items.GOLD_NUGGET, 0.8f).addOutput(Blocks.NETHERRACK, 0.8f).addOutput(Blocks.MAGMA_BLOCK, 0.5f).setBiomeWhitelist(BiomeTags.IS_NETHER).setDrill(Ingredient.of(Registration.NETHERITE_DRILL_ITEM.get())).setDrillingFluid(FluidIngredient.fromFluid(Fluids.LAVA, 1000)).setStress(2048).veinSize(0.5f, 0.8f).save("netherite", consumer);
		new DrillingBuilder(new ItemStack(Items.GOLD_NUGGET, 3), 20*20, 2048, 1024).setBiomeWhitelist(BiomeTags.IS_NETHER).addOutput(Items.GOLD_NUGGET, 0.5f).veinSize(3, 8).setStress(192).save("nether_gold", consumer);

		new ExtractorBuilder(new FluidStack(Fluids.WATER, 500), 20, 512, 128).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).setFinite(ThreeState.NEVER).save("water", consumer);

		processing("redstone_milling", AllRecipeTypes.MILLING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_REDSTONE.get())).output(new ItemStack(Items.REDSTONE, 3)).duration(250));
		processing("redstone_crushing", AllRecipeTypes.CRUSHING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_REDSTONE.get())).output(new ItemStack(Items.REDSTONE, 4)).duration(250));

		processing("diamond_cutting", AllRecipeTypes.CUTTING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_DIAMOND.get())).output(Items.DIAMOND).duration(250));
		processing("emerald_cutting", AllRecipeTypes.CUTTING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_EMERALD.get())).output(Items.EMERALD).duration(250));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.VEIN_ATLAS_ITEM.get())
		.pattern("ca")
		.pattern("mb")
		.define('c', Tags.Items.CHESTS)
		.define('a', Tags.Items.GEMS_AMETHYST)
		.define('m', Items.MAP)
		.define('b', Items.WRITABLE_BOOK)
		.group("create")
		.unlockedBy("map", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MAP))
		.save(consumer);

		MechanicalCraftingRecipeBuilder.shapedRecipe(Registration.SAMPLE_DRILL_BLOCK.get())
		.patternLine("beb")
		.patternLine("mCb")
		.patternLine("sDs")
		.key('b', TagKey.create(Registries.ITEM, new ResourceLocation("c:brass_plates")))
		.key('e', AllItems.ELECTRON_TUBE.get())
		.key('C', AllBlocks.BRASS_CASING.get())
		.key('m', AllItems.PRECISION_MECHANISM.get())
		.key('D', AllBlocks.MECHANICAL_DRILL.get())
		.key('s', AllItems.STURDY_SHEET.get())
		.build(consumer);
	}

	@SuppressWarnings("unchecked")
	private static <T extends ProcessingRecipe<?>> void processing(String name, AllRecipeTypes type, Consumer<FinishedRecipe> consumer, Consumer<ProcessingRecipeBuilder<?>> f) {
		ResourceLocation id = i(name);
		ProcessingRecipeBuilder<T> b = new ProcessingRecipeBuilder<>(((ProcessingRecipeSerializer<T>) type.getSerializer()).getFactory(), id);
		f.accept(b);
		b.build(consumer);
	}

	/*private static void genRarities(Item item, String name, int w, int a, boolean water, UnaryOperator<DrillingBuilder> b, Consumer<FinishedRecipe> consumer) {
		b.apply(new DrillingBuilder(item, w, 2*a*20)).save(name + "_impure", consumer);
		b.apply(new DrillingBuilder(item, w, a*20)).save(name + "_normal", consumer);
		b.apply(new DrillingBuilder(item, w / 2, a*10)).save(name + "_pure", consumer);
	}*/

	private static ResourceLocation i(String name) {
		return new ResourceLocation(CreateOreExcavation.MODID, name);
	}

	@SuppressWarnings("unchecked")
	public static interface AbstractExcavatingBuilder<T extends AbstractExcavatingBuilder<T>> {

		public default void init(int ticks, Component name, int spacing, int separation) {
			self().ticks = ticks;
			self().drill = Ingredient.of(CreateOreExcavation.DRILL_TAG);
			self().stressMul = 256;
			self().drillingFluid = FluidIngredient.EMPTY;
			initVein(ticks, name, spacing, separation);
		}

		public default void initVein(int ticks, Component name, int spacing, int separation) {
			vein().veinName = name;
			vein().finite = ThreeState.DEFAULT;
			vein().amountMultiplierMin = 1;
			vein().amountMultiplierMax = 2;
			vein().placement = new RandomSpreadStructurePlacement(spacing / 8, separation / 16, RandomSpreadType.LINEAR, seedRandom.nextInt(Integer.MAX_VALUE));
		}

		public default ExcavatingRecipe self() {
			return (ExcavatingRecipe) this;
		}

		public VeinRecipe vein();

		public default T setDrill(Ingredient drill) {
			self().drill = drill;
			return (T) this;
		}

		public default T setStress(int stress) {
			self().stressMul = stress;
			return (T) this;
		}

		public default T setBiomeWhitelist(TagKey<Biome> tag) {
			vein().biomeWhitelist = tag;
			return (T) this;
		}

		public default T setBiomeBlacklist(TagKey<Biome> tag) {
			vein().biomeBlacklist = tag;
			return (T) this;
		}

		public default T veinSize(float min, float max) {
			vein().amountMultiplierMin = min;
			vein().amountMultiplierMax = max;
			return (T) this;
		}

		public default T setFinite(ThreeState finite) {
			vein().finite = finite;
			return (T) this;
		}

		public default T setPriority(int priority) {
			vein().priority = priority;
			return (T) this;
		}

		public default void save(String name, Consumer<FinishedRecipe> consumer) {
			ResourceLocation vein = i(vein().getGroup() + "/" + name);
			self().veinId = vein;
			consumer.accept(new Result(i(self().getGroup() + "/" + name), self().serializer, json -> {
				new ExcavatingRecipe.Serializer<>(null, null).toJson(self(), json);
			}));
			consumer.accept(new Result(vein, vein().serializer, json -> {
				new VeinRecipe.Serializer<>(null, null).toJson(vein(), json);
			}));
		}
	}

	public static class DrillingBuilder extends DrillingRecipe implements AbstractExcavatingBuilder<DrillingBuilder> {
		private VeinRecipe vein;

		public DrillingBuilder(ProcessingOutput output, int ticks, Component name, int spacing, int separation) {
			super(null, null, CreateOreExcavation.DRILLING_RECIPES.getSerializer());
			init(ticks, name, spacing, separation);
			this.output = NonNullList.create();
			this.output.add(output);
			vein.icon = output.getStack();
		}

		public DrillingBuilder(ItemStack output, float chance, int ticks, int spacing, int separation) {
			this(new ProcessingOutput(output, chance), ticks, output.getHoverName(), spacing, separation);
		}

		public DrillingBuilder(ItemStack output, int ticks, int spacing, int separation) {
			this(new ProcessingOutput(output, 1F), ticks, output.getHoverName(), spacing, separation);
		}

		public DrillingBuilder(ItemLike output, int ticks, int spacing, int separation) {
			this(new ItemStack(output), ticks, spacing, separation);
		}

		public DrillingBuilder setDrillingFluid(FluidIngredient drillingFluid) {
			this.drillingFluid = drillingFluid;
			return this;
		}

		public DrillingBuilder addOutput(ProcessingOutput output) {
			this.output.add(output);
			return this;
		}

		public DrillingBuilder addOutput(ItemStack output) {
			addOutput(new ProcessingOutput(output, 1F));
			return this;
		}

		public DrillingBuilder addOutput(ItemLike output) {
			addOutput(new ItemStack(output));
			return this;
		}

		public DrillingBuilder addOutput(ItemStack output, float chance) {
			addOutput(new ProcessingOutput(output, chance));
			return this;
		}

		public DrillingBuilder addOutput(ItemLike output, float chance) {
			addOutput(new ItemStack(output), chance);
			return this;
		}

		@Override
		public VeinRecipe vein() {
			return vein;
		}

		@Override
		public void initVein(int ticks, Component name, int spacing, int separation) {
			vein = new VeinRecipe(null, null, CreateOreExcavation.VEIN_RECIPES.getSerializer());
			AbstractExcavatingBuilder.super.initVein(ticks, name, spacing, separation);
		}
	}

	public static class ExtractorBuilder extends ExtractorRecipe implements AbstractExcavatingBuilder<ExtractorBuilder> {
		private VeinRecipe vein;

		public ExtractorBuilder(FluidStack output, int ticks, int spacing, int separation) {
			this(output, ticks, output.getDisplayName(), spacing, separation);
		}

		public ExtractorBuilder(FluidStack output, int ticks, Component name, int spacing, int separation) {
			super(null, null, CreateOreExcavation.EXTRACTING_RECIPES.getSerializer());
			init(ticks, name, spacing, separation);
			this.output = output;
			vein.icon = new ItemStack(output.getFluid().getBucket());
		}

		@Override
		public void initVein(int ticks, Component name, int spacing, int separation) {
			vein = new VeinRecipe(null, null, CreateOreExcavation.VEIN_RECIPES.getSerializer());
			AbstractExcavatingBuilder.super.initVein(ticks, name, spacing, separation);
		}

		@Override
		public VeinRecipe vein() {
			return vein;
		}
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final Consumer<JsonObject> writer;
		private final RecipeSerializer<?> type;

		public Result(ResourceLocation pId, RecipeSerializer<?> pType, Consumer<JsonObject> writer) {
			this.id = pId;
			this.type = pType;
			this.writer = writer;
		}

		@Override
		public void serializeRecipeData(JsonObject pJson) {
			writer.accept(pJson);
		}

		@Override
		public ResourceLocation getId() {
			return this.id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return this.type;
		}

		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}