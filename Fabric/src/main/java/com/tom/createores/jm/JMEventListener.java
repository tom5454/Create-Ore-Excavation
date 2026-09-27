package com.tom.createores.jm;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class JMEventListener {

	public static void init() {
		ClientTickEvents.START_CLIENT_TICK.register(JMEventListener::onStartClientTick);
	}

	private static void onStartClientTick(Minecraft mc) {
		OreVeinsOverlay.INSTANCE.tick();
	}

}
