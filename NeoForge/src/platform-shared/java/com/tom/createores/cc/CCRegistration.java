package com.tom.createores.cc;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import com.tterrag.registrate.util.entry.RegistryEntry;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeBase;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.api.upgrades.UpgradeType;
import dan200.computercraft.shared.ModRegistry.DataComponents;
import dan200.computercraft.shared.ModRegistry.Items;
import dan200.computercraft.shared.turtle.items.TurtleItem;
import dan200.computercraft.shared.util.DataComponentUtil;

public class CCRegistration {

	public static final RegistryEntry<UpgradeType<? extends ITurtleUpgrade>, UpgradeType<OreVeinFinderTurtle>> VEIN_FINDER_TYPE = CreateOreExcavation.registrate().
			generic("vein_finder", ITurtleUpgrade.typeRegistry(),
					() -> UpgradeType.simple(new OreVeinFinderTurtle())
					)
			.onRegister(e -> {
				CreateOreExcavation.registrate().modifyCreativeModeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Registration.TAB.getId()), m -> {
					try {
						addTurtle(m, Items.TURTLE_NORMAL.get(), m.getParameters().holders());
						addTurtle(m, Items.TURTLE_ADVANCED.get(), m.getParameters().holders());
					} catch (Throwable ex) {
						CreateOreExcavation.LOGGER.warn("Failed to add CC: Tweaked Turtles to Creative Tab", ex);
					}
				});
			}).register();

	public static void init() {
	}

	private static void addTurtle(final CreativeModeTab.Output out, final TurtleItem turtle,
			final HolderLookup.Provider registries) {
		registries.lookupOrThrow(ITurtleUpgrade.REGISTRY).listElements()
		.filter(CCRegistration::isOurUpgrade)
		.map(x -> DataComponentUtil.createStack(turtle,
				DataComponents.RIGHT_TURTLE_UPGRADE.get(),
				UpgradeData.ofDefault(x))).forEach(out::accept);
	}

	private static boolean isOurUpgrade(final Holder.Reference<? extends UpgradeBase> upgrade) {
		final String namespace = upgrade.key().location().getNamespace();
		return namespace.equals(CreateOreExcavation.MODID);
	}
}
