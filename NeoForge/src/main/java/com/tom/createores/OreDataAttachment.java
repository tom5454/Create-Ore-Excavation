package com.tom.createores;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class OreDataAttachment implements INBTSerializable<CompoundTag> {
	private OreData data;

	public OreDataAttachment(IAttachmentHolder holder) {
		data = new OreData();
	}

	@Override
	public CompoundTag serializeNBT(Provider provider) {
		return (CompoundTag) OreData.Serialized.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), data.save()).getOrThrow();
	}

	@Override
	public void deserializeNBT(Provider provider, CompoundTag nbt) {
		OreData.Serialized.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), nbt).ifSuccess(data::load);
	}

	public static OreData getData(LevelChunk chunk) {
		if(chunk.getLevel().isClientSide)throw new RuntimeException("Ore Data accessed from client");
		OreDataAttachment at = chunk.getData(CreateOreExcavation.ORE_DATA);
		if (at != null) {
			OreData data = at.data;
			if (!data.isLoaded()) {
				data.populate(chunk);
			}
			return at.data;
		} else {
			at = new OreDataAttachment(chunk);
			at.data.populate(chunk);
			chunk.setData(CreateOreExcavation.ORE_DATA, at);
			return at.data;
		}
	}
}
