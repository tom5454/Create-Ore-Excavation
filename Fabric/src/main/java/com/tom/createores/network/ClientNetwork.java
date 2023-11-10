package com.tom.createores.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetwork {

	public static void init() {
		Packets.packets.forEach((rl, f) -> {
			ClientPlayNetworking.registerGlobalReceiver(rl, (mc, h, buf, rp) -> {
				Packet p = f.apply(buf);
				mc.submit(p::handleClient);
			});
		});
	}
}
