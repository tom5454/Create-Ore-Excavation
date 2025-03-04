package com.tom.createores.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class QueueInventory implements IItemHandler {
	private NonNullList<ItemStack> content = NonNullList.create();

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

	public ListTag toTag(HolderLookup.Provider registries) {
		ListTag tag = new ListTag();
		content.stream().filter(s -> !s.isEmpty()).map(s -> s.save(registries)).forEach(tag::add);
		return tag;
	}

	public void load(ListTag tag, HolderLookup.Provider registries) {
		content.clear();
		for (int i = 0; i < tag.size(); i++) {
			content.add(ItemStack.parseOptional(registries, tag.getCompound(i)));
		}
	}

	public <T> T asCap() {
		return (T) this;
	}

	public boolean hasSpace() {
		content.removeIf(ItemStack::isEmpty);
		return content.size() < 16;
	}
}
