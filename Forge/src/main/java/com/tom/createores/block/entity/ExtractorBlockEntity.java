package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.util.IOBlockType;

public class ExtractorBlockEntity extends ExcavatingBlockEntityImpl<ExtractorRecipe> {
	private Tank fluidTankOut;
	private LazyOptional<FluidTank> tankCapOut;

	public ExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTankOut = new Tank();
		tankCapOut = LazyOptional.of(() -> fluidTankOut);
	}

	@Override
	public <T> LazyOptional<T> getCaps(Capability<T> cap, IOBlockType type) {
		if(type == IOBlockType.FLUID_OUT && cap == ForgeCapabilities.FLUID_HANDLER) {
			return tankCapOut.cast();
		}
		return super.getCaps(cap, type);
	}

	@Override
	protected boolean canExtract() {
		return super.canExtract() && fluidTankOut.fillInternal(current.getOutput(), FluidAction.SIMULATE) == current.getOutput().getAmount();
	}

	@Override
	protected void onFinished() {
		fluidTankOut.fillInternal(current.getOutput(), FluidAction.EXECUTE);
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
		containedFluidTooltip(tooltip, isPlayerSneaking, tankCapOut.cast());
		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		tankCapOut.invalidate();
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

	@Override
	protected RecipeType<ExtractorRecipe> getRecipeType() {
		return CreateOreExcavation.EXTRACTING_RECIPES.getRecipeType();
	}

	@Override
	protected String getTankInName() {
		return "tankIn";
	}
}
