package com.tom.createores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.menu.OreVeinAtlasMenu;

public class OreVeinAtlasClickPacket2 implements Packet {
	public static final ResourceLocation ATLAS_CLICK_S2C = new ResourceLocation(CreateOreExcavation.MODID, "atlas_click2");
	private Option opt;
	private int id;

	public OreVeinAtlasClickPacket2(Option opt, int id) {
		this.opt = opt;
		this.id = id;
	}

	public OreVeinAtlasClickPacket2(FriendlyByteBuf pb) {
		opt = pb.readEnum(Option.class);
		id = pb.readVarInt();
	}

	@Override
	public void toBytes(FriendlyByteBuf pb) {
		pb.writeEnum(opt);
		pb.writeVarInt(id);
	}

	@Override
	public ResourceLocation getId() {
		return ATLAS_CLICK_S2C;
	}

	@Override
	public void handleClient() {
	}

	@Override
	public void handleServer(ServerPlayer p) {
		if (p.containerMenu instanceof OreVeinAtlasMenu m) {
			m.click2(opt, id);
		}
	}

	public static enum Option {
		TOGGLE_HIDE,
	}
}
