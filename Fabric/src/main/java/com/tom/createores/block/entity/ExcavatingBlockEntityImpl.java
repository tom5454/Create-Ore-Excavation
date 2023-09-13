package com.tom.createores.block.entity;

import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.createores.recipe.ExcavatingRecipe;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;

public abstract class ExcavatingBlockEntityImpl<R extends ExcavatingRecipe> extends ExcavatingBlockEntity<R> {
	protected FluidTank fluidTank;

	protected ExcavatingBlockEntityImpl(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTank = new FluidTank(16 * FluidConstants.BUCKET) {

			@Override
			protected void onContentsChanged() {
				notifyUpdate();
			}
		};
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
		containedFluidTooltip(tooltip, isPlayerSneaking, fluidTank);
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
	protected boolean canExtract() {
		return current.getDrillingFluid().getRequiredAmount() == 0 ||
				(current.getDrillingFluid().test(fluidTank.getFluid()) &&
						fluidTank.getFluidAmount() >= current.getDrillingFluid().getRequiredAmount());
	}

	@Override
	protected void onFinished() {
		if(current.getDrillingFluid().getRequiredAmount() != 0) {
			try(Transaction t = Transaction.openOuter()) {
				fluidTank.extract(fluidTank.variant, current.getDrillingFluid().getRequiredAmount(), t);
				t.commit();
			}
		}
	}

	protected String getTankInName() {
		return "tank";
	}
}
