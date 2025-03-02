package com.tom.createores.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.menu.OreVeinAtlasMenu;

public class OreVeinAtlasClickPacket implements Packet {
	public static final ResourceLocation ATLAS_CLICK_S2C = new ResourceLocation(CreateOreExcavation.MODID, "atlas_click");
	private Option opt;
	private ResourceLocation id;

	public OreVeinAtlasClickPacket(Option opt, ResourceLocation id) {
		this.opt = opt;
		this.id = id;
	}

	public OreVeinAtlasClickPacket(FriendlyByteBuf pb) {
		opt = pb.readEnum(Option.class);
		if (pb.readBoolean())
			id = pb.readResourceLocation();
	}

	@Override
	public void toBytes(FriendlyByteBuf pb) {
		pb.writeEnum(opt);
		pb.writeBoolean(id != null);
		if (id != null)
			pb.writeResourceLocation(id);
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
			m.click(opt, id);
		}
	}

	public static enum Option {
		ADD_EXCLUDE,
		REMOVE_EXCLUDE,
		SET_TARGET,
		REMOVE_TARGET,
	}
}
