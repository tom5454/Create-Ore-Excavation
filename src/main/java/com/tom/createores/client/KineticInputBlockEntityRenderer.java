package com.tom.createores.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import com.tom.createores.block.KineticInputBlock;
import com.tom.createores.block.entity.KineticInputBlockEntity;

public class KineticInputBlockEntityRenderer extends KineticBlockEntityRenderer<KineticInputBlockEntity> {

	public KineticInputBlockEntityRenderer(BlockEntityRendererProvider.Context dispatcher) {
		super(dispatcher);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticInputBlockEntity te, BlockState state) {
		return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, state.getValue(KineticInputBlock.SHAFT_FACING));
	}
}