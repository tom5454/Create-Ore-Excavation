package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.util.IOBlockType;
import com.tom.createores.util.TooltipUtil;

public abstract class ExcavatingBlockEntityImpl<R extends ExcavatingRecipe> extends ExcavatingBlockEntity<R> {
	protected FluidTank fluidTank;

	protected ExcavatingBlockEntityImpl(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTank = new FluidTank(16000) {

			@Override
			protected void onContentsChanged() {
				notifyUpdate();
			}
		};
	}

	@Override
	public <T> T getCaps(BlockCapability<T, Direction> cap, IOBlockType type) {
		if(type == IOBlockType.FLUID_IN && cap == Capabilities.FluidHandler.BLOCK) {
			return (T) fluidTank;
		}
		return null;
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		fluidTank.readFromNBT(registries, tag.getCompound(getTankInName()));
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put(getTankInName(), fluidTank.writeToNBT(registries, new CompoundTag()));
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.fluidInfo"));
		containedFluidTooltip(tooltip, isPlayerSneaking, fluidTank);
		return true;
	}

	@Override
	public void addToGoggleTooltip(List<Component> tooltip, R rec) {
		if(rec.getDrillingFluid().getRequiredAmount() != 0 && (!rec.getDrillingFluid().test(fluidTank.getFluid()) || fluidTank.getFluidAmount() < rec.getDrillingFluid().getRequiredAmount())) {
			TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.drill.noFluid"));
		}
	}

	@Override
	protected boolean validateRecipe(R recipe) {
		return super.validateRecipe(recipe) && (recipe.getDrillingFluid().getRequiredAmount() == 0 || recipe.getDrillingFluid().test(fluidTank.getFluid()));
	}

	@Override
	protected boolean canExtract() {
		return current.value().getDrillingFluid().getRequiredAmount() == 0 ||
				(current.value().getDrillingFluid().test(fluidTank.getFluid()) &&
						fluidTank.getFluidAmount() >= current.value().getDrillingFluid().getRequiredAmount());
	}

	@Override
	protected void onFinished() {
		fluidTank.drain(current.value().getDrillingFluid().getRequiredAmount(), FluidAction.EXECUTE);
	}

	protected String getTankInName() {
		return "tank";
	}
}
