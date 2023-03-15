package com.tom.createores;

import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;

import net.minecraftforge.client.ClientCommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import com.tom.createores.OreDataCapability.OreData;
import com.tom.createores.recipe.ExcavatingRecipe;

public class COECommand {

	public static void init() {}

	public static void register(RegisterCommandsEvent evt) {
		LiteralArgumentBuilder<CommandSourceStack> l = Commands.literal("coe");
		l.then(Commands.literal("setvein").requires(s -> s.hasPermission(2)).
				then(Commands.argument("pos", BlockPosArgument.blockPos()).
						then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(ALL_RECIPES).
								executes(c -> {
									BlockPos p = BlockPosArgument.getLoadedBlockPos(c, "pos");
									Recipe<?> rl = ResourceLocationArgument.getRecipe(c, "recipe");
									if(rl instanceof ExcavatingRecipe) {
										run(c.getSource(), p, rl.getId(), 0.8F);
										c.getSource().sendSuccess(new TranslatableComponent("command.coe.setvein.success", rl.getId()), true);
										return 1;
									}
									return 0;
								}).
								then(Commands.argument("multiplier", FloatArgumentType.floatArg(0, 1000)).
										executes(c -> {
											float mul = FloatArgumentType.getFloat(c, "multiplier");
											BlockPos p = BlockPosArgument.getLoadedBlockPos(c, "pos");
											Recipe<?> rl = ResourceLocationArgument.getRecipe(c, "recipe");
											if(rl.getType() instanceof ExcavatingRecipe) {
												run(c.getSource(), p, rl.getId(), mul);
												c.getSource().sendSuccess(new TranslatableComponent("command.coe.setvein.success", rl.getId()), true);
												return 1;
											}
											return 0;
										})
										)
								)
						));
		evt.getDispatcher().register(l);
	}

	private static void run(CommandSourceStack css, BlockPos pos, ResourceLocation rl, float mul) {
		ChunkPos p = new ChunkPos(pos);
		OreData data = OreDataCapability.getData(css.getLevel().getChunk(p.x, p.z));
		data.setRecipe(rl);
		data.setLoaded(true);
		data.setRandomMul(mul);
		data.setExtractedAmount(0);
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
			rl = Stream.concat(
					rm.getAllRecipesFor(CreateOreExcavation.DRILLING_RECIPES.getRecipeType()).stream().map(ExcavatingRecipe::getId),
					rm.getAllRecipesFor(CreateOreExcavation.EXTRACTING_RECIPES.getRecipeType()).stream().map(ExcavatingRecipe::getId)
					);
		} else rl = Stream.empty();

		return SharedSuggestionProvider.suggestResource(rl, builder);
	});
}
