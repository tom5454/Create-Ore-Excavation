package com.tom.createores.block.entity;

import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.util.IOBlockType;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;

public class ExtractorBlockEntity extends ExcavatingBlockEntityImpl<ExtractorRecipe> {
	private Tank fluidTankOut;

	public ExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTankOut = new Tank();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Storage<T> getCaps(IOBlockType type) {
		if(type == IOBlockType.FLUID_IN)return (Storage<T>) fluidTank;
		if(type == IOBlockType.FLUID_OUT)return (Storage<T>) fluidTankOut;
		return null;
	}

	@Override
	protected boolean canExtract() {
		return super.canExtract() && fluidTankOut.fillInternal(current.getOutput(), true) == current.getOutput().getAmount();
	}

	@Override
	protected void onFinished() {
		fluidTankOut.fillInternal(current.getOutput(), false);
		super.onFinished();
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		fluidTankOut.readFromNBT(tag.getCompound("tank"));
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put("tank", fluidTankOut.writeToNBT(new CompoundTag()));
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.extractor.output")));
		containedFluidTooltip(tooltip, isPlayerSneaking, fluidTankOut);
		return true;
	}

	private class Tank extends FluidTank {

		public Tank() {
			super(16 * FluidConstants.BUCKET);
		}

		@Override
		protected void onContentsChanged() {
			notifyUpdate();
		}

		@Override
		public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
			return 0L;
		}

		public long fillInternal(FluidStack resource, boolean simulate) {
			long f;
			try(Transaction t = Transaction.openOuter()) {
				f = super.insert(resource.getType(), resource.getAmount(), t);
				if(simulate)t.abort();
				else t.commit();
			}
			return f;
		}
	}

	@Override
	protected RecipeType<ExtractorRecipe> getRecipeType() {
		return CreateOreExcavation.EXTRACTING_RECIPES.getRecipeType();
	}

	@Override
	protected String getTankInName() {
		return "tankIn";
	}
}
