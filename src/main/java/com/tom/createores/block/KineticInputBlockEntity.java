package com.tom.createores.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;

import com.tom.createores.block.MultiblockCapHandler.Kinetic;
import com.tom.createores.block.MultiblockPart.MultiblockGhostPart;

public class KineticInputBlockEntity extends KineticTileEntity implements Kinetic {
	private float reqStress = 1;

	public KineticInputBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	@Override
	public float calculateStressApplied() {
		this.lastStressApplied = reqStress;
		return reqStress;
	}

	@Override
	public float calculateAddedStressCapacity() {
		return 0;
	}

	@Override
	public void initialize() {
		super.initialize();

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
					((MultiblockCapHandler)te).addKinetic(this);
				}
			}
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		BlockState state = level.getBlockState(worldPosition);
		BlockPos pos = worldPosition;
		for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
			Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
			pos = pos.relative(d, 1);
			state = level.getBlockState(pos);
		}
		if (state.getBlock() instanceof MultiblockPart) {
			MultiblockPart d = (MultiblockPart) state.getBlock();
			if (d instanceof MultiblockGhostPart)return added;
			else {
				BlockEntity te = level.getBlockEntity(pos);
				if (te instanceof MultiblockCapHandler) {
					return ((MultiblockCapHandler)te).addToGoggleTooltip(tooltip, isPlayerSneaking) || added;
				} else {
					return added;
				}
			}
		}
		return added;
	}

	@Override
	public void setStress(float stress) {
		this.reqStress = stress;
		if(lastStressApplied != stress && hasNetwork()) {
			getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
			networkDirty = true;
			notifyUpdate();
		}
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putFloat("reqStress", reqStress);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		reqStress = compound.getFloat("reqStress");
	}

	@Override
	public float getRotationSpeed() {
		if(lastStressApplied != reqStress)return 0;
		return Math.abs(speed);
	}
}
