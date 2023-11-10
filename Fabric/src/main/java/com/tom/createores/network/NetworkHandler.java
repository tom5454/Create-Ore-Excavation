package com.tom.createores.network;

import java.util.function.Function;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import io.netty.buffer.Unpooled;

public class NetworkHandler {

	public static void sendDataToServer(Packet packet) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		packet.toBytes(buf);
		ClientPlayNetworking.send(packet.getId(), buf);
	}

	public static void sendTo(ServerPlayer player, Packet packet) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		packet.toBytes(buf);
		ServerPlayNetworking.send(player, packet.getId(), buf);
	}

	public static <T extends Packet> void register(ResourceLocation rl, Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
		ServerPlayNetworking.registerGlobalReceiver(rl, (s, p, h, buf, rp) -> {
			T f = factory.apply(buf);
			s.submit(() -> f.handleServer(p));
		});
	}

}
