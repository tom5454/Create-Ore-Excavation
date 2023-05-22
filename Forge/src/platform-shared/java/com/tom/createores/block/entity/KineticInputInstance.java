package com.tom.createores.block.entity;

import net.minecraft.core.Direction;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.HalfShaftInstance;

import com.tom.createores.block.KineticInputBlock;

public class KineticInputInstance extends HalfShaftInstance<KineticInputBlockEntity> {

	public KineticInputInstance(MaterialManager modelManager, KineticInputBlockEntity tile) {
		super(modelManager, tile);
	}

	@Override
	protected Direction getShaftDirection() {
		return this.blockState.getValue(KineticInputBlock.SHAFT_FACING);
	}
}
