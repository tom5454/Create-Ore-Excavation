package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;

import com.tom.createores.block.IOBlock;
import com.tom.createores.block.MultiblockPart;
import com.tom.createores.block.MultiblockPart.MultiblockGhostPart;
import com.tom.createores.util.IOBlockType;

public class IOBlockEntity extends BlockEntity implements IHaveGoggleInformation {
	private IOBlockType type;

	public IOBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		type = pBlockState.getValue(IOBlock.TYPE);
	}

	public <T> T getCapability(BlockCapability<T, Direction> cap, Direction side) {
		if (type.getCap() == cap) {
			BlockState state = level.getBlockState(worldPosition);
			BlockPos pos = worldPosition;
			for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
				Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
				pos = pos.relative(d, 1);
				state = level.getBlockState(pos);
			}
			if (state.getBlock() instanceof MultiblockPart) {
				MultiblockPart d = (MultiblockPart) state.getBlock();
				if (!(d instanceof MultiblockGhostPart)) {
					BlockEntity te = level.getBlockEntity(pos);
					if (te instanceof MultiblockCapHandler) {
						return ((MultiblockCapHandler)te).getCaps(cap, type);
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		BlockState state = level.getBlockState(worldPosition);
		BlockPos pos = worldPosition;
		for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
			Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
			pos = pos.relative(d, 1);
			state = level.getBlockState(pos);
		}
		if (state.getBlock() instanceof MultiblockPart) {
			MultiblockPart d = (MultiblockPart) state.getBlock();
			if (d instanceof MultiblockGhostPart)return false;
			else {
				BlockEntity te = level.getBlockEntity(pos);
				if (te instanceof MultiblockCapHandler) {
					return ((MultiblockCapHandler)te).addToGoggleTooltip(tooltip, isPlayerSneaking);
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
