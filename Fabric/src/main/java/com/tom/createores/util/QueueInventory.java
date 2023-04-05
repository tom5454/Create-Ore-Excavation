package com.tom.createores.util;

import java.util.Iterator;

import org.spongepowered.include.com.google.common.collect.Iterators;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class QueueInventory extends SnapshotParticipant<NonNullList<ItemStack>> implements Storage<ItemVariant> {
	private NonNullList<ItemStack> content = NonNullList.create();

	public int getSlots() {
		return content.size();
	}

	public ItemStack getStackInSlot(int slot) {
		return content.get(slot);
	}

	public void add(ItemStack is) {
		content.add(is);
	}

	public ListTag toTag() {
		ListTag tag = new ListTag();
		content.stream().filter(s -> !s.isEmpty()).map(s -> s.save(new CompoundTag())).forEach(tag::add);
		return tag;
	}

	public void load(ListTag tag) {
		content.clear();
		for (int i = 0; i < tag.size(); i++) {
			content.add(ItemStack.of(tag.getCompound(i)));
		}
	}

	public boolean hasSpace() {
		content.removeIf(ItemStack::isEmpty);
		return content.size() < 16;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (maxAmount < 1) return 0;
		updateSnapshots(transaction);
		long ext = 0;
		for (int i = 0; i < content.size(); i++) {
			ItemStack is = content.get(i);
			if(!is.isEmpty() && resource.matches(is)) {
				int e = Math.min((int) (maxAmount - ext), is.getCount());
				is.shrink(e);
				ext += e;
				if(ext == maxAmount)break;
			}
		}
		return ext;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return Iterators.transform(content.iterator(), SV::new);
	}

	private class SV implements StorageView<ItemVariant> {
		private final ItemStack s;

		public SV(ItemStack s) {
			this.s = s;
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			return QueueInventory.this.extract(resource, maxAmount, transaction);
		}

		@Override
		public boolean isResourceBlank() {
			return s.isEmpty();
		}

		@Override
		public ItemVariant getResource() {
			return ItemVariant.of(s);
		}

		@Override
		public long getAmount() {
			return s.getCount();
		}

		@Override
		public long getCapacity() {
			return 0;
		}
	}

	@Override
	protected NonNullList<ItemStack> createSnapshot() {
		NonNullList<ItemStack> l = NonNullList.create();
		content.forEach(s -> {
			if(!s.isEmpty())l.add(s.copy());
		});
		return l;
	}

	@Override
	protected void readSnapshot(NonNullList<ItemStack> snapshot) {
		NonNullList<ItemStack> l = NonNullList.create();
		snapshot.forEach(s -> {
			if(!s.isEmpty())l.add(s.copy());
		});
		this.content = l;
	}
}
