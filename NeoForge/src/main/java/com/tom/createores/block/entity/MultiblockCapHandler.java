package com.tom.createores.block.entity;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;

import com.tom.createores.util.IOBlockType;

public interface MultiblockCapHandler extends IHaveGoggleInformation {
	<T> T getCaps(BlockCapability<T, Direction> cap, IOBlockType type);
	void addKinetic(Kinetic k);

	public static interface Kinetic {
		float getRotationSpeed();
		void setStress(float stress);
	}

	void dropInv();
}
