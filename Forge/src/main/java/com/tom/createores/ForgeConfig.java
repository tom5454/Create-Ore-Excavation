package com.tom.createores;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class ForgeConfig {
	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		CreateOreExcavation.LOGGER.info("Loaded Create Ore Excavation config file {}", configEvent.getConfig().getFileName());
		Config.load(configEvent.getConfig());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		CreateOreExcavation.LOGGER.info("Create Ore Excavation config just got changed on the file system!");
		Config.load(configEvent.getConfig());
	}
}
