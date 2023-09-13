package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.util.IOBlockType;

public abstract class ExcavatingBlockEntityImpl<R extends ExcavatingRecipe> extends ExcavatingBlockEntity<R> {
	protected FluidTank fluidTank;
	protected LazyOptional<FluidTank> tankCap;

	protected ExcavatingBlockEntityImpl(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTank = new FluidTank(16000) {

			@Override
			protected void onContentsChanged() {
				notifyUpdate();
			}
		};
		tankCap = LazyOptional.of(() -> fluidTank);
	}

	@Override
	public <T> LazyOptional<T> getCaps(Capability<T> cap, IOBlockType type) {
		if(type == IOBlockType.FLUID_IN && cap == ForgeCapabilities.FLUID_HANDLER) {
			return tankCap.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		fluidTank.readFromNBT(tag.getCompound(getTankInName()));
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put(getTankInName(), fluidTank.writeToNBT(new CompoundTag()));
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.drill.fluidInfo")));
		containedFluidTooltip(tooltip, isPlayerSneaking, tankCap.cast());
		return true;
	}

	@Override
	public void addToGoggleTooltip(List<Component> tooltip, R rec) {
		if(rec.getDrillingFluid().getRequiredAmount() != 0 && (!rec.getDrillingFluid().test(fluidTank.getFluid()) || fluidTank.getFluidAmount() < rec.getDrillingFluid().getRequiredAmount())) {
			tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.drill.noFluid")));
		}
	}

	@Override
	protected boolean validateRecipe(R recipe) {
		return super.validateRecipe(recipe) && (recipe.getDrillingFluid().getRequiredAmount() == 0 || recipe.getDrillingFluid().test(fluidTank.getFluid()));
	}

	@Override
	public void invalidate() {
		super.invalidate();
		tankCap.invalidate();
	}


	@Override
	protected boolean canExtract() {
		return current.getDrillingFluid().getRequiredAmount() == 0 ||
				(current.getDrillingFluid().test(fluidTank.getFluid()) &&
						fluidTank.getFluidAmount() >= current.getDrillingFluid().getRequiredAmount());
	}

	@Override
	protected void onFinished() {
		fluidTank.drain(current.getDrillingFluid().getRequiredAmount(), FluidAction.EXECUTE);
	}

	protected String getTankInName() {
		return "tank";
	}
}
