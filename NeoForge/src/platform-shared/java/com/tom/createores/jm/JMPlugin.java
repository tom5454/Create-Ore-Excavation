package com.tom.createores.jm;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import com.tom.createores.CreateOreExcavation;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.common.event.ClientEventRegistry;
import journeymap.api.v2.common.event.FullscreenEventRegistry;

@JourneyMapPlugin(apiVersion = IClientAPI.API_VERSION)
public class JMPlugin implements IClientPlugin {
	private IClientAPI api;

	@Override
	public String getModId() {
		return CreateOreExcavation.MODID;
	}

	@Override
	public void initialize(IClientAPI api) {
		this.api = api;
		OreVeinsOverlay.INSTANCE.setApi(api);
		ClientEventRegistry.MAPPING_EVENT.subscribe(getModId(), me -> {
			if (me.getStage() == MappingEvent.Stage.MAPPING_STARTED)
				OreVeinsOverlay.INSTANCE.onMappingStarted();
			else if (me.getStage() == MappingEvent.Stage.MAPPING_STOPPED)
				OreVeinsOverlay.INSTANCE.onMappingStopped();
		});
		FullscreenEventRegistry.ADDON_BUTTON_DISPLAY_EVENT.subscribe(getModId(), event -> {
			event.getThemeButtonDisplay().addThemeToggleButton(I18n.get("jm.coe.veinsOverlayToggle"), ResourceLocation.tryBuild(CreateOreExcavation.MODID, "textures/gui/jm_coe_veins.png"), OreVeinsOverlay.INSTANCE.isActivated(), OreVeinsOverlay.INSTANCE::toggle);
		});
	}
}
