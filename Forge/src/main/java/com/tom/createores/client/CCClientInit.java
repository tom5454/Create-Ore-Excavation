package com.tom.createores.client;

import net.minecraftforge.eventbus.api.IEventBus;

import com.tom.createores.cc.CCRegistration;

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;

public class CCClientInit {

	public static void init(IEventBus bus) {
		bus.addListener(CCClientInit::registerTurtleUpgradeModels);
	}

	private static void registerTurtleUpgradeModels(RegisterTurtleModellersEvent evt) {
		evt.register(CCRegistration.VEIN_FINDER.get(), TurtleUpgradeModeller.flatItem());
	}
}
