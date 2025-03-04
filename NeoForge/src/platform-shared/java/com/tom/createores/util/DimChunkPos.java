package com.tom.createores.util;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DimChunkPos {
	private final ResourceKey<Level> dimension;
	private final int x;
	private final int z;
	private int hash;

	public static final Codec<DimChunkPos> CODEC = RecordCodecBuilder.<DimChunkPos>mapCodec(b -> {
		return b.group(
				ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(DimChunkPos::dimension),
				Codec.INT.fieldOf("x").forGetter(DimChunkPos::x),
				Codec.INT.fieldOf("z").forGetter(DimChunkPos::z)
				).apply(b, DimChunkPos::new);
	}).codec();

	public DimChunkPos(ResourceKey<Level> dimension, int x, int z) {
		this.dimension = dimension;
		this.x = x;
		this.z = z;
	}

	public DimChunkPos(Level level, ChunkPos p) {
		this(level.dimension(), p.x, p.z);
	}

	public DimChunkPos(Level level, BlockPos p) {
		this(level, new ChunkPos(p));
	}

	@Override
	public int hashCode() {
		if (this.hash == 0) {
			this.hash = Objects.hash(this.dimension.location(), this.x, this.z);
			if (this.hash == 0) {
				this.hash = 1;
			}
		}

		return this.hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof DimChunkPos)) {
			return false;
		} else {
			DimChunkPos p = (DimChunkPos) obj;
			return this.dimension == p.dimension && this.x == p.x && this.z == p.z;
		}
	}

	public ResourceKey<Level> dimension() {
		return dimension;
	}

	public int x() {
		return x;
	}

	public int z() {
		return z;
	}
}
