package com.tom.createores.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IDrill {
	ItemStack getDrill();
	BlockPos getBelow();
	Direction getFacing();
	boolean shouldRenderRubble();
	float getYOffset();
	float getDrillOffset();
	float getRotation();
	float getPrevRotation();
	boolean shouldRenderShaft();
}
