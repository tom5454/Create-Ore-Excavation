package com.tom.createores.client;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.Registration;

public class COEPonderPlugin implements PonderPlugin {
	public static final ResourceLocation DRILLING = ResourceLocation.tryBuild(CreateOreExcavation.MODID, "drilling");

	@Override
	public String getModId() {
		return CreateOreExcavation.MODID;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		HELPER.forComponents(Registration.DRILL_BLOCK, Registration.EXTRACTOR_BLOCK)
		.addStoryBoard("drilling_machine", PonderScenes::drillingMachine, AllCreatePonderTags.KINETIC_SOURCES, DRILLING);

		HELPER.forComponents(Registration.VEIN_FINDER_ITEM, Registration.SAMPLE_DRILL_BLOCK, Registration.VEIN_ATLAS_ITEM)
		.addStoryBoard("vein_finder", PonderScenes::oreFinder, AllCreatePonderTags.KINETIC_SOURCES, DRILLING)
		.addStoryBoard("sample_drill", PonderScenes::sampleDrill, DRILLING);
	}

	@Override
	public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
		helper.registerTag(DRILLING)
		.addToIndex()
		.item(Registration.DIAMOND_DRILL_ITEM.get(), true, false)
		.title("Drilling")
		.description("Extract various resources from underground")
		.register();

		PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

		HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES).
		add(Registration.DRILL_BLOCK).
		add(Registration.EXTRACTOR_BLOCK);
	}
}
