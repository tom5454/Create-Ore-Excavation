package com.tom.createores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface Packet {
	void toBytes(FriendlyByteBuf buf);
	ResourceLocation getId();
	void handleClient();
	void handleServer(ServerPlayer p);
}
