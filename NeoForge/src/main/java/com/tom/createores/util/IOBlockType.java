package com.tom.createores.util;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.util.StringRepresentable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public enum IOBlockType implements StringRepresentable {
	ITEM_IN   (() -> ForgeCapabilities.ITEM_HANDLER),
	ITEM_OUT  (() -> ForgeCapabilities.ITEM_HANDLER),
	FLUID_IN  (() -> ForgeCapabilities.FLUID_HANDLER),
	FLUID_OUT (() -> ForgeCapabilities.FLUID_HANDLER),
	ENERGY_IN (() -> ForgeCapabilities.ENERGY),
	ENERGY_OUT(() -> ForgeCapabilities.ENERGY),
	;
	private final String name;
	private final Supplier<Capability<?>> cap;

	private IOBlockType(Supplier<Capability<?>> cap) {
		name = name().toLowerCase(Locale.ROOT);
		this.cap = cap;
	}

	public Capability<?> getCap() {
		return cap.get();
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}