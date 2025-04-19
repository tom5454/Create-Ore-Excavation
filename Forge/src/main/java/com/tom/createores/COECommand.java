package com.tom.createores;

import java.util.stream.Stream;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;

import net.minecraftforge.client.ClientCommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import com.google.common.base.Stopwatch;

import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.RandomSpreadGenerator;

public class COECommand {
	private static final DynamicCommandExceptionType ERROR_VEIN_NOT_FOUND = new DynamicCommandExceptionType((p_214514_) -> {
		return Component.translatable("command.coe.locate.failed", p_214514_);
	});

	public static void init() {}

	public static void register(RegisterCommandsEvent evt) {
		LiteralArgumentBuilder<CommandSourceStack> l = Commands.literal("coe");
		l.then(Commands.literal("setvein").requires(s -> s.hasPermission(2)).
				then(Commands.argument("pos", BlockPosArgument.blockPos()).
						then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(ALL_RECIPES).
								executes(c -> {
									BlockPos p = BlockPosArgument.getLoadedBlockPos(c, "pos");
									Recipe<?> rl = ResourceLocationArgument.getRecipe(c, "recipe");
									if(rl instanceof VeinRecipe) {
										setVein(c.getSource(), p, rl.getId(), 0.8F);
										c.getSource().sendSuccess(() -> Component.translatable("command.coe.setvein.success", rl.getId().toString()), true);
										return 1;
									}
									return 0;
								}).
								then(Commands.argument("multiplier", FloatArgumentType.floatArg(0, 1000)).
										executes(c -> {
											float mul = FloatArgumentType.getFloat(c, "multiplier");
											BlockPos p = BlockPosArgument.getLoadedBlockPos(c, "pos");
											Recipe<?> rl = ResourceLocationArgument.getRecipe(c, "recipe");
											if(rl instanceof VeinRecipe) {
												setVein(c.getSource(), p, rl.getId(), mul);
												c.getSource().sendSuccess(() -> Component.translatable("command.coe.setvein.success", rl.getId().toString()), true);
												return 1;
											}
											return 0;
										})
										)
								)
						)
				);
		l.then(Commands.literal("removevein").requires(s -> s.hasPermission(2)).
				then(Commands.argument("pos", BlockPosArgument.blockPos()).
						executes(c -> {
							BlockPos p = BlockPosArgument.getLoadedBlockPos(c, "pos");
							setVein(c.getSource(), p, null, 0F);
							c.getSource().sendSuccess(() -> Component.translatable("command.coe.setvein.success", Component.translatable("chat.coe.veinFinder.nothing")), true);
							return 1;
						})
						)
				);
		l.then(Commands.literal("locate").requires(s -> s.hasPermission(2)).
				then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(ALL_RECIPES).
						executes(c -> {
							Recipe<?> rl = ResourceLocationArgument.getRecipe(c, "recipe");
							if(rl instanceof VeinRecipe) {
								BlockPos blockpos = BlockPos.containing(c.getSource().getPosition());
								Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
								BlockPos at = OreVeinGenerator.getPicker(c.getSource().getLevel()).locate(rl.getId(), blockpos, c.getSource().getLevel(), 100);
								stopwatch.stop();
								if(at != null) {
									int i = Mth.floor(RandomSpreadGenerator.distance2d(at, blockpos));
									Component component = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", at.getX(), "~", at.getZ())).withStyle(tc -> {
										return tc.withColor(ChatFormatting.GREEN).
												withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + at.getX() + " ~ " + at.getZ())).
												withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")));
									});
									c.getSource().sendSuccess(() -> {
										return Component.translatable("command.coe.locate.success", rl.getId().toString(), component, i);
									}, false);
									CreateOreExcavation.LOGGER.info("Locating element " + rl.getId() + " took " + stopwatch.elapsed().toMillis() + " ms");
									return i;
								} else {
									throw ERROR_VEIN_NOT_FOUND.create(rl.getId().toString());
								}
							} else {
								throw ERROR_VEIN_NOT_FOUND.create(rl.getId().toString());
							}
						})
						)
				);
		evt.getDispatcher().register(l);
	}

	private static void setVein(CommandSourceStack css, BlockPos pos, ResourceLocation rl, float mul) {
		ChunkPos p = new ChunkPos(pos);
		var chunk = css.getLevel().getChunk(p.x, p.z);
		OreData data = OreDataCapability.getData(chunk);
		data.setRecipe(rl);
		data.setLoaded(true);
		data.setRandomMul(mul);
		data.setExtractedAmount(0);
		chunk.setUnsaved(true);
	}

	public static final SuggestionProvider<CommandSourceStack> ALL_RECIPES = SuggestionProviders.register(new ResourceLocation(CreateOreExcavation.MODID, "all_recipes"), (ctx, builder) -> {
		Stream<ResourceLocation> rl;
		RecipeManager rm;
		if(ctx.getSource() instanceof ClientCommandSourceStack || ctx.getSource() instanceof ClientSuggestionProvider) {
			rm = Minecraft.getInstance().getConnection().getRecipeManager();
		} else if(ctx.getSource() instanceof CommandSourceStack css) {
			rm = css.getServer().getRecipeManager();
		} else {
			rm = null;
		}
		if(rm != null) {
			rl = rm.getAllRecipesFor(CreateOreExcavation.VEIN_RECIPES.getRecipeType()).stream().map(VeinRecipe::getId);
		} else rl = Stream.empty();

		return SharedSuggestionProvider.suggestResource(rl, builder);
	});
}
