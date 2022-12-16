package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import com.tom.createores.block.IOBlock.Type;
import com.tom.createores.recipe.ExtractorRecipe;

public class ExtractorBlockEntity extends ExcavatingBlockEntity<ExtractorRecipe> {
	private Tank fluidTank;
	private LazyOptional<FluidTank> tankCap;

	public ExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTank = new Tank();
		tankCap = LazyOptional.of(() -> fluidTank);
	}

	@Override
	public <T> LazyOptional<T> getCaps(Capability<T> cap, Type type) {
		if(type == Type.FLUID_OUT && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return tankCap.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	protected boolean instanceofCheck(Object rec) {
		return rec instanceof ExtractorRecipe;
	}

	@Override
	protected boolean canExtract() {
		return fluidTank.fillInternal(current.getOutput(), FluidAction.SIMULATE) == current.getOutput().getAmount();
	}

	@Override
	protected void onFinished() {
		fluidTank.fillInternal(current.getOutput(), FluidAction.EXECUTE);
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		fluidTank.readFromNBT(tag.getCompound("tank"));
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put("tank", fluidTank.writeToNBT(new CompoundTag()));
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		containedFluidTooltip(tooltip, isPlayerSneaking, tankCap.cast());
		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		tankCap.invalidate();
	}

	private class Tank extends FluidTank {

		public Tank() {
			super(16000);
		}

		@Override
		protected void onContentsChanged() {
			notifyUpdate();
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			return 0;
		}

		public int fillInternal(FluidStack resource, FluidAction action) {
			return super.fill(resource, action);
		}
	}
}
