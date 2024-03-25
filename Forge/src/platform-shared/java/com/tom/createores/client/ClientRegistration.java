package com.tom.createores.client;

import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

public class ClientRegistration {
	public static final PonderTag DRILLING = new PonderTag(new ResourceLocation(CreateOreExcavation.MODID, "drilling")).
			item(Registration.DIAMOND_DRILL_ITEM.get(), true, false)
			.defaultLang("Drilling", "Extract various resources from underground");

	static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateOreExcavation.MODID);

	public static void register() {
		PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_APPLIANCES).
		add(Registration.DRILL_BLOCK);

		HELPER.addStoryBoard(Registration.DRILL_BLOCK, "drilling_machine", PonderScenes::oreFinder, AllPonderTags.KINETIC_SOURCES, DRILLING);
	}
}
