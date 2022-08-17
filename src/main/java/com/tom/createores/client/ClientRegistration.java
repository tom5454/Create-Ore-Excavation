package com.tom.createores.client;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import com.tterrag.registrate.providers.ProviderType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

public class ClientRegistration {
	public static final PonderTag DRILLING = new PonderTag(new ResourceLocation(CreateOreExcavation.MODID, "drilling")).
			item(CreateOreExcavation.DIAMOND_DRILL_ITEM.get(), true, false)
			.defaultLang("Drilling", "Extract various resources from underground");

	static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreateOreExcavation.MODID);

	public static void register() {
		PonderRegistry.TAGS.forTag(PonderTag.KINETIC_APPLIANCES).
		add(Registration.DRILL_BLOCK);

		HELPER.addStoryBoard(Registration.DRILL_BLOCK, "drilling_machine", PonderScenes::oreFinder, PonderTag.KINETIC_SOURCES, DRILLING);

		CreateOreExcavation.registrate().addDataGenerator(ProviderType.LANG, prov -> {
			PonderLocalization.generateSceneLang();

			JsonObject object = new JsonObject();
			PonderLocalization.record(CreateOreExcavation.MODID, object);

			for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
				prov.add(entry.getKey(), entry.getValue().getAsString());
			}
		});
	}
}
