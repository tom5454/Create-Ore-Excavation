package com.tom.createores.util;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class DimChunkPos {
	public final ResourceKey<Level> dimension;
	public final int x;
	public final int z;
	private int hash;

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
}
