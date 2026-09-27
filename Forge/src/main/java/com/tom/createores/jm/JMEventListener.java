package com.tom.createores.jm;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;

public class JMEventListener {

	public static void register() {
		MinecraftForge.EVENT_BUS.addListener(JMEventListener::onClientTickEvent);
	}

	private static void onClientTickEvent(ClientTickEvent event) {
		if (event.phase == Phase.END)
			OreVeinsOverlay.INSTANCE.tick();
	}
}
