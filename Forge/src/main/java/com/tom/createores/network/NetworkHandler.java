package com.tom.createores.network;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import com.tom.createores.CreateOreExcavation;

public class NetworkHandler {
	private static final String PROTOCOL_VERSION = "2";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(CreateOreExcavation.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
			);

	public static void init() {
		Packets.init();
		CreateOreExcavation.LOGGER.info("Initilaized Network Handler");
	}

	public static void handleData(Packet packet, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
			ctx.get().enqueueWork(() -> packet.handleClient());
		} else {
			ctx.get().enqueueWork(() -> packet.handleServer(ctx.get().getSender()));
		}
		ctx.get().setPacketHandled(true);
	}

	public static void sendDataToServer(Packet packet) {
		INSTANCE.sendToServer(packet);
	}

	public static void sendTo(ServerPlayer player, Packet packet) {
		NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}

	private static int id;
	public static <T extends Packet> void register(ResourceLocation rl, Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
		INSTANCE.registerMessage(id++, clazz, Packet::toBytes, factory, NetworkHandler::handleData);
	}
}
