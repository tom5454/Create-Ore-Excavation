package com.tom.createores;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	public static class Server {
		public IntValue finiteAmountBase;
		public BooleanValue defaultInfinite;
		public IntValue maxExtractorsPerVein;
		public IntValue veinFinderNear, veinFinderFar, veinFinderCd;

		private Server(ForgeConfigSpec.Builder builder) {
			builder.comment("IMPORTANT NOTICE:",
					"You can add more entries using KubeJS",
					"https://github.com/tom5454/Create-Ore-Excavation#kubejs").
			define("importantInfo", true);

			finiteAmountBase = builder.comment("Finite vein base amount").translation("config.coe.finiteAmountBase").
					defineInRange("finiteAmountBase", 1000, 1, Integer.MAX_VALUE);

			defaultInfinite = builder.comment("Veins infinite by default").translation("config.coe.defaultInfinite").define("defaultInfinite", true);

			maxExtractorsPerVein = builder.comment("Max number of extractor per ore vein, Set to 0 for infinite").translation("config.coe.maxExtractorsPerVein")
					.defineInRange("maxExtractorsPerVein", 0, 0, 64);

			veinFinderNear = builder.comment("Vein Finder 'Found Nearby' range in chunks").translation("config.coe.veinFinderNear")
					.defineInRange("veinFinderNear", 1, 1, 8);

			veinFinderFar = builder.comment("Vein Finder accuracy for 'Found traces of ...'").translation("config.coe.veinFinderFar")
					.defineInRange("veinFinderFar", 25, 1, 1000);

			veinFinderCd = builder.comment("Vein Finder use cooldown in ticks").translation("config.coe.veinFinderCd")
					.defineInRange("veinFinderCd", 100, 10, 1000);
		}
	}

	public static class Common {

		public Common(ForgeConfigSpec.Builder builder) {
			builder.comment("IMPORTANT NOTICE:",
					"THIS IS ONLY THE COMMON CONFIG. It does not contain all the values adjustable for Create Ore Excavation",
					"The settings have been moved to createoreexcavation-server.toml",
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

	public static int finiteAmountBase, maxExtractorsPerVein, veinFinderNear, veinFinderFar, veinFinderCd;
	public static boolean defaultInfinite;

	public static void load(ModConfig modConfig) {
		if(modConfig.getType() == Type.SERVER) {
			finiteAmountBase = SERVER.finiteAmountBase.get();
			defaultInfinite = SERVER.defaultInfinite.get();
			maxExtractorsPerVein = SERVER.maxExtractorsPerVein.get();
			veinFinderNear = SERVER.veinFinderNear.get();
			veinFinderFar = SERVER.veinFinderFar.get();
			veinFinderCd = SERVER.veinFinderCd.get();
		}
	}
}
