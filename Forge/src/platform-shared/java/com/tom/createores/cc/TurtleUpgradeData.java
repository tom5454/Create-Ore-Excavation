package com.tom.createores.cc;

import java.util.function.Consumer;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;

public class TurtleUpgradeData extends TurtleUpgradeDataProvider {

	public TurtleUpgradeData(PackOutput output) {
		super(output);
	}

	@Override
	protected void addUpgrades(Consumer<Upgrade<TurtleUpgradeSerialiser<?>>> addUpgrade) {
		simpleWithCustomItem(id("vein_finder"), CCRegistration.VEIN_FINDER.get(), Registration.VEIN_FINDER_ITEM.get()).add(addUpgrade);
	}

	private static ResourceLocation id(String id) {
		return new ResourceLocation(CreateOreExcavation.MODID, id);
	}
}
