package com.tom.createores.jm;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.event.fabric.FullscreenDisplayEvent;

public class JMEventListener {

	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(JMEventListener::onStartClientTick);
		FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(JMEventListener::onAddonButtonDisplayEvent);
	}

	private static void onAddonButtonDisplayEvent(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
		event.getThemeButtonDisplay().addThemeToggleButton(I18n.get("jm.coe.veinsOverlayToggle"), "coe_veins", OreVeinsOverlay.INSTANCE.isActivated(), OreVeinsOverlay.INSTANCE::toggle);
	}

	private static void onStartClientTick(Minecraft mc) {
		OreVeinsOverlay.INSTANCE.tick();
	}

}
