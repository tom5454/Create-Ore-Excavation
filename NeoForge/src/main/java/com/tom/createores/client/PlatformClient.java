package com.tom.createores.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PlatformClient {

	public static TextureAtlasSprite getBlockTexture(BlockState pState, Level level, BlockPos pos) {
		return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(pState, level, pos);
	}
}
