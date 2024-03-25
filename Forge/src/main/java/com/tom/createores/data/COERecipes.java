package com.tom.createores.data;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluids;

import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;

import com.google.gson.JsonObject;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.recipe.IRecipe.ThreeState;

public class COERecipes extends RecipeProvider {

	public COERecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(Registration.NORMAL_DRILL_ITEM.get())
		.pattern("bi ")
		.pattern("ibi")
		.pattern(" ii")
		.define('b', Tags.Items.STORAGE_BLOCKS_IRON)
		.define('i', Tags.Items.INGOTS_IRON)
		.group("create")
		.unlockedBy("iron", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.INGOTS_IRON).build()))
		.save(consumer);

		ShapedRecipeBuilder.shaped(Registration.DIAMOND_DRILL_ITEM.get())
		.pattern("bi ")
		.pattern("idi")
		.pattern(" ii")
		.define('b', Tags.Items.STORAGE_BLOCKS_DIAMOND)
		.define('i', Tags.Items.GEMS_DIAMOND)
		.define('d', Registration.NORMAL_DRILL_ITEM.get())
		.group("create")
		.unlockedBy("diamond", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.GEMS_DIAMOND).build()))
		.save(consumer);

		netheriteSmithingM(consumer, Registration.DIAMOND_DRILL_ITEM.get(), Registration.NETHERITE_DRILL_ITEM.get());

		ShapedRecipeBuilder.shaped(Registration.VEIN_FINDER_ITEM.get())
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
		.key('B', TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:storage_blocks/brass")))
		.key('b', TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:plates/brass")))
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
		.key('B', TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:storage_blocks/brass")))
		.key('b', TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge:plates/brass")))
		.key('e', AllItems.ELECTRON_TUBE.get())
		.key('H', AllBlocks.HOSE_PULLEY.get())
		.key('C', AllBlocks.BRASS_CASING.get())
		.key('m', AllItems.PRECISION_MECHANISM.get())
		.key('D', AllBlocks.MECHANICAL_DRILL.get())
		.key('s', AllItems.STURDY_SHEET.get())
		.key('P', AllBlocks.MECHANICAL_PUMP.get())
		.build(consumer);

		new DrillingBuilder(Items.RAW_IRON, 10, 30*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(10, 30).save("iron", consumer);
		new DrillingBuilder(Items.RAW_GOLD, 4, 30*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(2, 4).setStress(192).save("gold", consumer);
		new DrillingBuilder(Items.RAW_COPPER, 10, 30*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(10, 30).save("copper", consumer);
		new DrillingBuilder(Items.COAL, 20, 10*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(15, 40).save("coal", consumer);
		new DrillingBuilder(Registration.RAW_DIAMOND.get(), 2, 60*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).setStress(512).veinSize(0.5f, 2).save("diamond", consumer);
		new DrillingBuilder(Registration.RAW_REDSTONE.get(), 10, 30*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(10, 30).save("redstone", consumer);
		new DrillingBuilder(Registration.RAW_EMERALD.get(), 2, 60*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(0.2f, 1).setStress(512).save("emerald", consumer);
		new DrillingBuilder(new ProcessingOutput(new ItemStack(Registration.RAW_DIAMOND.get()), 1F), 2, 20*20, Component.translatable("ore.coe.hardenedDiamond")).addOutput(Items.DIAMOND, 0.1f).setDrill(Ingredient.of(Registration.NETHERITE_DRILL_ITEM.get())).setDrillingFluid(FluidIngredient.fromFluid(Fluids.LAVA, 500)).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).setStress(1024).veinSize(1f, 3f).save("hardened_diamond", consumer);
		new DrillingBuilder(AllItems.RAW_ZINC.get(), 10, 30*20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).veinSize(8, 24).save("zinc", consumer);

		new DrillingBuilder(Items.GLOWSTONE_DUST, 10, 60*20).setBiomeWhitelist(BiomeTags.IS_NETHER).veinSize(5, 12).save("glowstone", consumer);
		new DrillingBuilder(Items.QUARTZ, 10, 60*20).setBiomeWhitelist(BiomeTags.IS_NETHER).setStress(512).veinSize(8, 24).save("quartz", consumer);

		new ExtractorBuilder(new FluidStack(Fluids.WATER, 500), 10, 20).setBiomeWhitelist(BiomeTags.IS_OVERWORLD).setFinite(ThreeState.NEVER).save("water", consumer);

		processing("redstone_milling", AllRecipeTypes.MILLING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_REDSTONE.get())).output(new ItemStack(Items.REDSTONE, 3)).duration(250));
		processing("redstone_crushing", AllRecipeTypes.CRUSHING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_REDSTONE.get())).output(new ItemStack(Items.REDSTONE, 4)).duration(250));

		processing("diamond_cutting", AllRecipeTypes.CUTTING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_DIAMOND.get())).output(Items.DIAMOND).duration(250));
		processing("emerald_cutting", AllRecipeTypes.CUTTING, consumer, b -> b.withItemIngredients(Ingredient.of(Registration.RAW_EMERALD.get())).output(Items.EMERALD).duration(250));
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

	protected static void netheriteSmithingM(Consumer<FinishedRecipe> pFinishedRecipeConsumer, Item pIngredientItem, Item pResultItem) {
		UpgradeRecipeBuilder.smithing(Ingredient.of(pIngredientItem), Ingredient.of(Items.NETHERITE_INGOT), pResultItem).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(pFinishedRecipeConsumer, i(getItemName(pResultItem) + "_smithing"));
	}

	@SuppressWarnings("unchecked")
	public static interface AbstractExcavatingBuilder<T extends AbstractExcavatingBuilder<T>> {

		public default void init(int weight, int ticks, Component name) {
			self().weight = weight;
			self().ticks = ticks;
			self().veinName = name;
			self().drill = Ingredient.of(CreateOreExcavation.DRILL_TAG);
			self().stressMul = 256;
			self().finite = ThreeState.DEFAULT;
			self().amountMultiplierMin = 1;
			self().amountMultiplierMax = 2;
		}

		public default ExcavatingRecipe self() {
			return (ExcavatingRecipe) this;
		}

		public default T setDrill(Ingredient drill) {
			self().drill = drill;
			return (T) this;
		}

		public default T setStress(int stress) {
			self().stressMul = stress;
			return (T) this;
		}

		public default T setBiomeWhitelist(TagKey<Biome> tag) {
			self().biomeWhitelist = tag;
			return (T) this;
		}

		public default T setBiomeBlacklist(TagKey<Biome> tag) {
			self().biomeBlacklist = tag;
			return (T) this;
		}

		public default T veinSize(float min, float max) {
			self().amountMultiplierMin = min;
			self().amountMultiplierMax = max;
			return (T) this;
		}

		public default T setFinite(ThreeState finite) {
			self().finite = finite;
			return (T) this;
		}

		public default void save(String name, Consumer<FinishedRecipe> consumer) {
			consumer.accept(new Result(i(self().getGroup() + "/" + name), self().serializer, json -> {
				new ExcavatingRecipe.Serializer<>(null, null).toJson(self(), json);
			}));
		}
	}

	public static class DrillingBuilder extends DrillingRecipe implements AbstractExcavatingBuilder<DrillingBuilder> {

		public DrillingBuilder(ProcessingOutput output, int weight, int ticks, Component name) {
			super(null, null, CreateOreExcavation.DRILLING_RECIPES.getSerializer());
			init(weight, ticks, name);
			this.output = NonNullList.create();
			this.output.add(output);
			this.drillingFluid = FluidIngredient.EMPTY;
		}

		public DrillingBuilder(ItemStack output, int weight, int ticks) {
			this(new ProcessingOutput(output, 1F), weight, ticks, output.getHoverName());
		}

		public DrillingBuilder(ItemLike output, int weight, int ticks) {
			this(new ItemStack(output), weight, ticks);
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
	}

	public static class ExtractorBuilder extends ExtractorRecipe implements AbstractExcavatingBuilder<ExtractorBuilder> {

		public ExtractorBuilder(FluidStack output, int weight, int ticks) {
			this(output, weight, ticks, output.getDisplayName());
		}

		public ExtractorBuilder(FluidStack output, int weight, int ticks, Component name) {
			super(null, null, CreateOreExcavation.EXTRACTING_RECIPES.getSerializer());
			init(weight, ticks, name);
			this.output = output;
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
		@Nullable
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		@Nullable
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}