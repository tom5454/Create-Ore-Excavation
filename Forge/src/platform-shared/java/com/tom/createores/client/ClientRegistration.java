package com.tom.createores.client;

import net.createmod.ponder.foundation.PonderIndex;

public class ClientRegistration {

	public static void register() {
		PonderIndex.addPlugin(new COEPonderPlugin());
	}
}
