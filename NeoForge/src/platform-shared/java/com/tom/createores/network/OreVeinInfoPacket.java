package com.tom.createores.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.jm.OreVeinsOverlay;

public class OreVeinInfoPacket implements Packet {
	public static final CustomPacketPayload.Type<OreVeinInfoPacket> ID = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "veins_info"));
	public static final StreamCodec<FriendlyByteBuf, OreVeinInfoPacket> STREAM_CODEC = CustomPacketPayload.codec(OreVeinInfoPacket::toBytes, OreVeinInfoPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public CompoundTag tag;

	public OreVeinInfoPacket(CompoundTag tag) {
		this.tag = tag;
	}

	public OreVeinInfoPacket(FriendlyByteBuf pb) {
		tag = pb.readNbt();
	}

	@Override
	public void toBytes(FriendlyByteBuf pb) {
		pb.writeNbt(tag);
	}

	@Override
	public void handleClient() {
		if (CreateOreExcavation.journeyMap) {
			OreVeinsOverlay.addOreInfoToMap(tag);
		}
	}

	@Override
	public void handleServer(ServerPlayer p) {
	}
}
