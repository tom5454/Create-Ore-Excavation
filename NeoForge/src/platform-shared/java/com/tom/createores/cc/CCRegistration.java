package com.tom.createores.cc;

import java.util.stream.Stream;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.impl.TurtleUpgrades;
import dan200.computercraft.impl.UpgradeManager;
import dan200.computercraft.shared.ModRegistry.Items;
import dan200.computercraft.shared.turtle.items.TurtleItem;

public class CCRegistration {
	public static final RegistryEntry<TurtleUpgradeSerialiser<OreVeinFinderTurtle>> VEIN_FINDER = CreateOreExcavation.registrate().
			generic("vein_finder", TurtleUpgradeSerialiser.registryId(),
					() -> TurtleUpgradeSerialiser.simpleWithCustomItem(OreVeinFinderTurtle::new)
					).onRegister(e -> {
						CreateOreExcavation.registrate().modifyCreativeModeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Registration.TAB.getId()), m -> {
							try {
								addTurtle(m, Items.TURTLE_NORMAL.get());
								addTurtle(m, Items.TURTLE_ADVANCED.get());
							} catch (Throwable ex) {
								CreateOreExcavation.LOGGER.warn("Failed to add CC: Tweaked Turtles to Creative Tab", ex);
							}
						});
					}).register();

	public static void init() {

	}

	private static Stream<ITurtleUpgrade> getUpgrades() {
		return TurtleUpgrades.instance().getUpgradeWrappers().values().stream()
				.filter(x -> x.modId().equals(CreateOreExcavation.MODID))
				.map(UpgradeManager.UpgradeWrapper::upgrade);
	}

	private static void addTurtle(CreativeModeTab.Output out, TurtleItem turtle) {
		getUpgrades()
		.map(x -> turtle.create(-1, null, -1, null, UpgradeData.ofDefault(x), 0, null))
		.forEach(out::accept);
	}
}
