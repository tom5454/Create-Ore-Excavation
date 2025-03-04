package com.tom.createores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public interface Packet extends CustomPacketPayload {
	void toBytes(FriendlyByteBuf buf);
	void handleClient();
	void handleServer(ServerPlayer p);
}
