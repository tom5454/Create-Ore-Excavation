package com.tom.createores.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import com.tom.createores.block.KineticInputBlock;

public class KineticInputBlockEntityRenderer extends KineticTileEntityRenderer {

	public KineticInputBlockEntityRenderer(BlockEntityRendererProvider.Context dispatcher) {
		super(dispatcher);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticTileEntity te, BlockState state) {
		return CachedBufferer.partialFacing(AllBlockPartials.SHAFT_HALF, state, state.getValue(KineticInputBlock.SHAFT_FACING));
	}
}