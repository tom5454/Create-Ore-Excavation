package com.tom.createores.util;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum IOBlockType implements StringRepresentable {
	ITEM_IN   (true, true),
	ITEM_OUT  (false, true),
	FLUID_IN  (true, false),
	FLUID_OUT (false, false),
	;
	private final String name;
	public final boolean in, item;

	private IOBlockType(boolean in, boolean item) {
		name = name().toLowerCase(Locale.ROOT);
		this.in = in;
		this.item = item;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}