package com.tom.createores.util;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;

public enum IOBlockType implements StringRepresentable {
	ITEM_IN   (() -> Capabilities.ItemHandler.BLOCK),
	ITEM_OUT  (() -> Capabilities.ItemHandler.BLOCK),
	FLUID_IN  (() -> Capabilities.FluidHandler.BLOCK),
	FLUID_OUT (() -> Capabilities.FluidHandler.BLOCK),
	ENERGY_IN (() -> Capabilities.EnergyStorage.BLOCK),
	ENERGY_OUT(() -> Capabilities.EnergyStorage.BLOCK),
	;
	private final String name;
	private final Supplier<BlockCapability<?, Direction>> cap;

	private IOBlockType(Supplier<BlockCapability<?, Direction>> cap) {
		name = name().toLowerCase(Locale.ROOT);
		this.cap = cap;
	}

	public BlockCapability<?, Direction> getCap() {
		return cap.get();
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}