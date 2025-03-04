package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.ExtractorRecipe;
import com.tom.createores.util.IOBlockType;
import com.tom.createores.util.TooltipUtil;

public class ExtractorBlockEntity extends ExcavatingBlockEntityImpl<ExtractorRecipe> {
	private Tank fluidTankOut;

	public ExtractorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		fluidTankOut = new Tank();
	}


	@Override
	public <T> T getCaps(BlockCapability<T, Direction> cap, IOBlockType type) {
		if(type == IOBlockType.FLUID_OUT && cap == Capabilities.FluidHandler.BLOCK) {
			return (T) fluidTankOut;
		}
		return super.getCaps(cap, type);
	}

	@Override
	protected boolean canExtract() {
		return super.canExtract() && fluidTankOut.fillInternal(current.value().getOutput(), FluidAction.SIMULATE) == current.value().getOutput().getAmount();
	}

	@Override
	protected void onFinished() {
		fluidTankOut.fillInternal(current.value().getOutput(), FluidAction.EXECUTE);
		super.onFinished();
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		fluidTankOut.readFromNBT(registries, tag.getCompound("tank"));
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.put("tank", fluidTankOut.writeToNBT(registries, new CompoundTag()));
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		TooltipUtil.forGoggles(tooltip, Component.translatable("info.coe.extractor.output"));
		containedFluidTooltip(tooltip, isPlayerSneaking, fluidTankOut);
		return true;
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
