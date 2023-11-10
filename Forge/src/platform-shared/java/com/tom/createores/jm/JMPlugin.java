package com.tom.createores.jm;

import java.util.EnumSet;

import com.tom.createores.CreateOreExcavation;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;

@ClientPlugin
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
		api.subscribe(getModId(), EnumSet.of(ClientEvent.Type.MAPPING_STARTED, ClientEvent.Type.MAPPING_STOPPED));
	}

	@Override
	public void onEvent(ClientEvent event) {
		switch (event.type) {
		case MAPPING_STARTED:
			OreVeinsOverlay.INSTANCE.onMappingStarted();
			break;

		case MAPPING_STOPPED:
			OreVeinsOverlay.INSTANCE.onMappingStopped();
			break;

		default:
			break;
		}
	}
}
