package com.tom.createores.block.entity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.tom.createores.block.IOBlock.Type;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.util.QueueInventory;

public class DrillBlockEntity extends ExcavatingBlockEntity<DrillingRecipe> {
	private QueueInventory inventory;
	private FluidTank fluidTank;
	private LazyOptional<FluidTank> tankCap;

	public DrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new QueueInventory();
		fluidTank = new FluidTank(16000, v -> current != null && current.getDrillingFluid().test(v)) {

			@Override
			protected void onContentsChanged() {
				notifyUpdate();
			}
		};
		tankCap = LazyOptional.of(() -> fluidTank);
	}

	@Override
	public <T> LazyOptional<T> getCaps(Capability<T> cap, Type type) {
		if(type == Type.ITEM_OUT && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventory.asCap();
		}

		if(type == Type.FLUID_IN && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return tankCap.cast();
		}
		return LazyOptional.empty();
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		fluidTank.readFromNBT(tag.getCompound("tank"));
		if(!clientPacket) {
			inventory.load(tag.getList("inv", Tag.TAG_COMPOUND));
		}
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.put("tank", fluidTank.writeToNBT(new CompoundTag()));
		if(!clientPacket) {
			tag.put("inv", inventory.toTag());
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);
		containedFluidTooltip(tooltip, isPlayerSneaking, tankCap.cast());
		return true;
	}

	@Override
	public void addToGoggleTooltip(List<Component> tooltip, DrillingRecipe rec) {
		if(rec.getDrillingFluid().getRequiredAmount() != 0 && rec.getDrillingFluid().test(fluidTank.getFluid()) && fluidTank.getFluidAmount() >= rec.getDrillingFluid().getRequiredAmount()) {
			tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("info.coe.drill.noFluid")));
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		inventory.invalidate();
		tankCap.invalidate();
	}

	@Override
	public void dropInv() {
		super.dropInv();
		for(int i = 0; i < inventory.getSlots(); ++i) {
			dropItemStack(inventory.getStackInSlot(i));
		}
	}

	@Override
	protected boolean instanceofCheck(Object rec) {
		return rec instanceof DrillingRecipe;
	}

	@Override
	protected boolean canExtract() {
		return inventory.hasSpace() && current.getDrillingFluid().getRequiredAmount() == 0 ||
				(current.getDrillingFluid().test(fluidTank.getFluid()) &&
						fluidTank.getFluidAmount() >= current.getDrillingFluid().getRequiredAmount());
	}

	@Override
	protected void onFinished() {
		current.getOutput().stream().map(ProcessingOutput::rollOutput).filter(i -> !i.isEmpty()).forEach(inventory::add);
		fluidTank.drain(current.getDrillingFluid().getRequiredAmount(), FluidAction.EXECUTE);
	}
}
