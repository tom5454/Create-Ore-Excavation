package com.tom.createores.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class Packets {
	public static Map<ResourceLocation, Function<FriendlyByteBuf, ? extends Packet>> packets = new HashMap<>();

	public static void init() {
		addPacket(OreVeinInfoPacket.VEIN_INFO_S2C, OreVeinInfoPacket.class, OreVeinInfoPacket::new);
		addPacket(OreVeinAtlasClickPacket.ATLAS_CLICK_S2C, OreVeinAtlasClickPacket.class, OreVeinAtlasClickPacket::new);
		addPacket(OreVeinAtlasClickPacket2.ATLAS_CLICK_S2C, OreVeinAtlasClickPacket2.class, OreVeinAtlasClickPacket2::new);
	}

	public static <T extends Packet> void addPacket(ResourceLocation rl, Class<T> clazz, Function<FriendlyByteBuf, T> factory) {
		NetworkHandler.register(rl, clazz, factory);
		packets.put(rl, factory);
	}
}
