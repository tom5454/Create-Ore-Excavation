package com.tom.createores.client;

import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;
import com.tom.createores.cc.CCRegistration;

import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;

public class ClientRegistration {
	public static final PonderTag DRILLING = new PonderTag(new ResourceLocation(CreateOreExcavation.MODID, "drilling")).
			item(Registration.DIAMOND_DRILL_ITEM.get(), true, false)
			.defaultLang("Drilling", "Extract various resources from underground");

	static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateOreExcavation.MODID);

	public static void register() {
		PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES).
		add(Registration.DRILL_BLOCK);

		HELPER.forComponents(Registration.DRILL_BLOCK, Registration.EXTRACTOR_BLOCK)
		.addStoryBoard("drilling_machine", PonderScenes::drillingMachine, AllPonderTags.KINETIC_SOURCES, DRILLING);

		HELPER.forComponents(Registration.VEIN_FINDER_ITEM, Registration.SAMPLE_DRILL_BLOCK, Registration.VEIN_ATLAS_ITEM)
		.addStoryBoard("vein_finder", PonderScenes::oreFinder, AllPonderTags.KINETIC_SOURCES, DRILLING)
		.addStoryBoard("sample_drill", PonderScenes::sampleDrill, AllPonderTags.RECENTLY_UPDATED, DRILLING);

		if (CreateOreExcavation.isModLoaded("computercraft")) {
			ComputerCraftAPIClient.registerTurtleUpgradeModeller(CCRegistration.VEIN_FINDER.get(), TurtleUpgradeModeller.flatItem());
		}
	}
}
