package com.tom.createores.jm;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public class JMEventListener {

	public static void register() {
		NeoForge.EVENT_BUS.addListener(JMEventListener::onClientTickEvent);
	}

	private static void onClientTickEvent(ClientTickEvent.Post event) {
		OreVeinsOverlay.INSTANCE.tick();
	}
}
