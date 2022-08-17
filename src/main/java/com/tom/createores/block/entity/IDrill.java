package com.tom.createores.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface IDrill {
	boolean isActive();
	ItemStack getDrill();
	BlockPos getBelow();
}
