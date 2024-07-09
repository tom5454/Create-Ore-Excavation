package com.tom.createores.util;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public interface PlatformMenuProvider extends MenuProvider, ExtendedScreenHandlerFactory {
	@Override
	default void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
	}
}
