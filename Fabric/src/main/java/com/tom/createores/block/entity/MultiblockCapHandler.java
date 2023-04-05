package com.tom.createores.block.entity;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import com.tom.createores.util.IOBlockType;

public interface MultiblockCapHandler extends IHaveGoggleInformation {
	<T> Storage<T> getCaps(IOBlockType type);
	void addKinetic(Kinetic k);

	public static interface Kinetic {
		float getRotationSpeed();
		void setStress(float stress);
	}

	void dropInv();
}
