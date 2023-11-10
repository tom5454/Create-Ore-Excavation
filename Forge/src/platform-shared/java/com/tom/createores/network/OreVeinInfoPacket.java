package com.tom.createores.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.jm.OreVeinsOverlay;

public class OreVeinInfoPacket implements Packet {
	public static final ResourceLocation VEIN_INFO_S2C = new ResourceLocation(CreateOreExcavation.MODID, "veins_info");
	public CompoundTag tag;

	public OreVeinInfoPacket(CompoundTag tag) {
		this.tag = tag;
	}

	public OreVeinInfoPacket(FriendlyByteBuf pb) {
		tag = pb.readAnySizeNbt();
	}

	@Override
	public void toBytes(FriendlyByteBuf pb) {
		pb.writeNbt(tag);
	}

	@Override
	public ResourceLocation getId() {
		return VEIN_INFO_S2C;
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
