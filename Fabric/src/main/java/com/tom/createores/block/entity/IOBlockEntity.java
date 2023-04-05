package com.tom.createores.block.entity;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import com.tom.createores.block.IOBlock;
import com.tom.createores.block.MultiblockPart;
import com.tom.createores.block.MultiblockPart.MultiblockGhostPart;
import com.tom.createores.util.IOBlockType;

public class IOBlockEntity extends BlockEntity implements IHaveGoggleInformation, SidedStorageBlockEntity {
	private IOBlockType type;
	private Storage<?> cap;

	public IOBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
		super(pType, pWorldPosition, pBlockState);
		type = pBlockState.getValue(IOBlock.TYPE);
	}

	private <T> Storage<T> getCapability() {
		if(this.cap == null) {
			BlockState state = level.getBlockState(worldPosition);
			BlockPos pos = worldPosition;
			for (int i = 0;i<5 && state.getBlock() instanceof MultiblockGhostPart;i++) {
				Direction d = ((MultiblockGhostPart)state.getBlock()).getParentDir(state);
				pos = pos.relative(d, 1);
				state = level.getBlockState(pos);
			}
			if (state.getBlock() instanceof MultiblockPart) {
				MultiblockPart d = (MultiblockPart) state.getBlock();
				if (d instanceof MultiblockGhostPart)this.cap = null;
				else {
					BlockEntity te = level.getBlockEntity(pos);
					if (te instanceof MultiblockCapHandler) {
						this.cap = ((MultiblockCapHandler)te).getCaps(type);
					} else {
						this.cap = null;
					}
				}
			}
		}
		return (Storage<T>) cap;
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

	@Override
	public @Nullable Storage<ItemVariant> getItemStorage(Direction side) {
		if(!type.item)return null;
		Storage<ItemVariant> cap = getCapability();
		return type.in ? FilteringStorage.insertOnlyOf(cap) : FilteringStorage.extractOnlyOf(cap);
	}

	@Override
	public @Nullable Storage<FluidVariant> getFluidStorage(Direction side) {
		if(type.item)return null;
		Storage<FluidVariant> cap = getCapability();
		return type.in ? FilteringStorage.insertOnlyOf(cap) : FilteringStorage.extractOnlyOf(cap);
	}
}
