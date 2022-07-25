package com.tom.createores.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class QueueInventory implements IItemHandler {
	private NonNullList<ItemStack> content = NonNullList.create();
	private LazyOptional<IItemHandler> opt = LazyOptional.of(() -> this);

	@Override
	public int getSlots() {
		return content.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return content.get(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack c = content.get(slot);
		if(c.isEmpty())return ItemStack.EMPTY;
		int a = Math.min(c.getCount(), amount);
		if(simulate) {
			c = c.copy();
			c.setCount(a);
			return c;
		} else {
			return c.split(a);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return false;
	}

	public void add(ItemStack is) {
		content.add(is);
	}

	public ListTag toTag() {
		ListTag tag = new ListTag();
		content.stream().filter(s -> !s.isEmpty()).map(ItemStack::serializeNBT).forEach(tag::add);
		return tag;
	}

	public void load(ListTag tag) {
		content.clear();
		for (int i = 0; i < tag.size(); i++) {
			content.add(ItemStack.of(tag.getCompound(i)));
		}
	}

	public <T> LazyOptional<T> asCap() {
		return opt.cast();
	}

	public void invalidate() {
		opt.invalidate();
	}

	public boolean hasSpace() {
		content.removeIf(ItemStack::isEmpty);
		return content.size() < 16;
	}
}
