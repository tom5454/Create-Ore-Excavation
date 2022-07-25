package com.tom.createores;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class Config {
	public static class Server {
		public IntValue generationChance;
		public IntValue generationAttempts;
		public IntValue generationHeight;

		private Server(ForgeConfigSpec.Builder builder) {
			builder.comment("IMPORTANT NOTICE:",
					"You can add more entries using KubeJS",
					"Put this script into server_scripts folder",
					"onEvent('recipes', event => {",
					"    event.recipes.createOreExcavationItem([<output>, ...], <vein name>, <spawn weight>, <ticks>).id('<id>')//mode: 'FIRST', or 'RANDOM', 'ROUND_ROBIN'",
					"    event.recipes.createOreExcavationItem([<output>, ...], <vein name>, <spawn weight>, <ticks>).fluid(<drilling fluid stack>).id('<id>')//Or with a drilling fluid per operation",
					"    event.recipes.createOreExcavationFluid(<fluid>, <vein name>, <spawn weight>, <ticks>).id('<id>')//mode: 'FIRST', or 'RANDOM', 'ROUND_ROBIN'",
					"})").
			define("importantInfo", true);

			generationChance = builder.comment("Weight value for empty chunk").translation("config.coe.generationChance").
					defineInRange("generationChance", 5000, 1, Integer.MAX_VALUE);

			generationAttempts = builder.comment("Amount of times to try generating an ore vein").translation("config.coe.generationAttempts").
					defineInRange("generationAttempts", 5, 1, 1000);
		}
	}

	public static class Common {
		public ConfigValue<List<? extends String>> multiblockInvs;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.comment("IMPORTANT NOTICE:",
					"THIS IS ONLY THE COMMON CONFIG. It does not contain all the values adjustable for Create Ore Excavation",
					"The settings have been moved to toms_storage-server.toml",
					"That file is PER WORLD, meaning you have to go into 'saves/<world name>/serverconfig' to adjust it. Those changes will then only apply for THAT WORLD.",
					"You can then take that config file and put it in the 'defaultconfigs' folder to make it apply automatically to all NEW worlds you generate FROM THERE ON.",
					"This may appear confusing to many of you, but it is a new sensible way to handle configuration, because the server configuration is synced when playing multiplayer.").
			define("importantInfo", true);
		}
	}

	static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	static final ForgeConfigSpec serverSpec;
	public static final Server SERVER;
	static {
		final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
		serverSpec = specPair.getRight();
		SERVER = specPair.getLeft();
	}

	public static int generationChance, generationAttempts;

	private static void load(ModConfig modConfig) {
		if(modConfig.getType() == Type.SERVER) {
			generationChance = SERVER.generationChance.get();
			generationAttempts = SERVER.generationAttempts.get();
			OreVeinGenerator.invalidate();
		}
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		CreateOreExcavation.LOGGER.info("Loaded Create Ore Excavation config file {}", configEvent.getConfig().getFileName());
		load(configEvent.getConfig());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		CreateOreExcavation.LOGGER.info("Create Ore Excavation config just got changed on the file system!");
		load(configEvent.getConfig());
	}
}
