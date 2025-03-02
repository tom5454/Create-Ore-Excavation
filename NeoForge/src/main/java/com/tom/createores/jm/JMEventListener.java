package com.tom.createores.jm;

import net.minecraft.client.resources.language.I18n;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import journeymap.client.api.event.forge.FullscreenDisplayEvent;

public class JMEventListener {

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(JMEventListener::onAddonButtonDisplayEvent);
		MinecraftForge.EVENT_BUS.addListener(JMEventListener::onClientTickEvent);
	}

	private static void onAddonButtonDisplayEvent(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
		event.getThemeButtonDisplay().addThemeToggleButton(I18n.get("jm.coe.veinsOverlayToggle"), "coe_veins", OreVeinsOverlay.INSTANCE.isActivated(), OreVeinsOverlay.INSTANCE::toggle);
	}

	private static void onClientTickEvent(TickEvent.ClientTickEvent event) {
		OreVeinsOverlay.INSTANCE.tick();
	}
}
