package com.tom.createores.block.entity;

import net.minecraft.core.Direction;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;

import com.tom.createores.block.KineticInputBlock;

public class KineticInputInstance extends HalfShaftInstance {

	public KineticInputInstance(MaterialManager modelManager, KineticTileEntity tile) {
		super(modelManager, tile);
	}

	@Override
	protected Direction getShaftDirection() {
		return this.blockState.getValue(KineticInputBlock.SHAFT_FACING);
	}
}
