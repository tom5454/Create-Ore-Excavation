package com.tom.createores.block.entity;

import java.util.List;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.util.IOBlockType;
import com.tom.createores.util.QueueInventory;

import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;

public class DrillBlockEntity extends ExcavatingBlockEntity<DrillingRecipe> {
	private QueueInventory inventory;
	private FluidTank fluidTank;

	public DrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new QueueInventory();
		fluidTank = new FluidTank(16 * FluidConstants.BUCKET, v -> current != null && current.getDrillingFluid().test(v)) {

			@Override
			protected void onContentsChanged() {
				notifyUpdate();
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Storage<T> getCaps(IOBlockType type) {
		if(type == IOBlockType.ITEM_OUT)return (Storage<T>) inventory;
		if(type == IOBlockType.FLUID_IN)return (Storage<T>) fluidTank;
		return null;
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
		containedFluidTooltip(tooltip, isPlayerSneaking, fluidTank);
		return true;
	}

	@Override
	public void addToGoggleTooltip(List<Component> tooltip, DrillingRecipe rec) {
		if(rec.getDrillingFluid().getRequiredAmount() != 0 && rec.getDrillingFluid().test(fluidTank.getFluid()) && fluidTank.getFluidAmount() >= rec.getDrillingFluid().getRequiredAmount()) {
			tooltip.add(Component.literal(spacing).append(Component.translatable("info.coe.drill.noFluid")));
		}
	}

	@Override
	public void dropInv() {
		super.dropInv();
		for(int i = 0; i < inventory.getSlots(); ++i) {
			dropItemStack(inventory.getStackInSlot(i));
		}
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
		if(current.getDrillingFluid().getRequiredAmount() != 0) {
			try(Transaction t = Transaction.openOuter()) {
				fluidTank.extract(fluidTank.variant, current.getDrillingFluid().getRequiredAmount(), t);
				t.commit();
			}
		}
	}

	@Override
	protected RecipeType<DrillingRecipe> getRecipeType() {
		return CreateOreExcavation.DRILLING_RECIPES.getRecipeType();
	}
}
