package com.tom.createores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.menu.OreVeinAtlasMenu;

public class OreVeinAtlasClickPacket implements Packet {
	public static final CustomPacketPayload.Type<OreVeinAtlasClickPacket> ID = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_click"));
	public static final StreamCodec<FriendlyByteBuf, OreVeinAtlasClickPacket> STREAM_CODEC = CustomPacketPayload.codec(OreVeinAtlasClickPacket::toBytes, OreVeinAtlasClickPacket::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	private Option opt;
	private ResourceLocation id;
	private int id2;

	public OreVeinAtlasClickPacket(Option opt, ResourceLocation id) {
		this.opt = opt;
		this.id = id;
	}

	public OreVeinAtlasClickPacket(Option opt, int id) {
		this.opt = opt;
		this.id2 = id;
	}

	public OreVeinAtlasClickPacket(FriendlyByteBuf pb) {
		opt = pb.readEnum(Option.class);
		if (pb.readBoolean())
			id = pb.readResourceLocation();
		else
			id2 = pb.readVarInt();
	}

	@Override
	public void toBytes(FriendlyByteBuf pb) {
		pb.writeEnum(opt);
		pb.writeBoolean(id != null);
		if (id != null)
			pb.writeResourceLocation(id);
		else
			pb.writeVarInt(id2);
	}

	@Override
	public void handleClient() {
	}

	@Override
	public void handleServer(ServerPlayer p) {
		if (p.containerMenu instanceof OreVeinAtlasMenu m) {
			if (id != null)m.click(opt, id);
			else m.click2(opt, id2);
		}
	}

	public static enum Option {
		ADD_EXCLUDE,
		REMOVE_EXCLUDE,
		SET_TARGET,
		REMOVE_TARGET,
		TOGGLE_HIDE,
	}
}
