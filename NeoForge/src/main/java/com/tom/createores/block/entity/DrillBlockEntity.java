package com.tom.createores.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.DrillingRecipe;
import com.tom.createores.util.IOBlockType;
import com.tom.createores.util.QueueInventory;

public class DrillBlockEntity extends ExcavatingBlockEntityImpl<DrillingRecipe> {
	private QueueInventory inventory;

	public DrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventory = new QueueInventory();
	}

	@Override
	public <T> T getCaps(BlockCapability<T, Direction> cap, IOBlockType type) {
		if(type == IOBlockType.ITEM_OUT && cap == Capabilities.ItemHandler.BLOCK) {
			return inventory.asCap();
		}
		return super.getCaps(cap, type);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		if(!clientPacket) {
			inventory.load(tag.getList("inv", Tag.TAG_COMPOUND), registries);
		}
	}

	@Override
	public void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		if(!clientPacket) {
			tag.put("inv", inventory.toTag(registries));
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
		return inventory.hasSpace() && super.canExtract();
	}

	@Override
	protected void onFinished() {
		current.value().getOutput().stream().map(ProcessingOutput::rollOutput).filter(i -> !i.isEmpty()).forEach(inventory::add);
		super.onFinished();
	}

	@Override
	protected RecipeType<DrillingRecipe> getRecipeType() {
		return CreateOreExcavation.DRILLING_RECIPES.getRecipeType();
	}
}
