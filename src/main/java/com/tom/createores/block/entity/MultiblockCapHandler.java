package com.tom.createores.block.entity;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;

import com.tom.createores.block.IOBlock.Type;

public interface MultiblockCapHandler extends IHaveGoggleInformation {
	<T> LazyOptional<T> getCaps(Capability<T> cap, Type type);
	void addKinetic(Kinetic k);

	public static interface Kinetic {
		float getRotationSpeed();
		void setStress(float stress);
	}

	void dropInv();
}
