package com.tom.createores.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;

import com.tom.createores.CreateOreExcavation;

@EventBusSubscriber(value = Dist.CLIENT, modid = CreateOreExcavation.MODID, bus = Bus.MOD)
public class COEClient {

	@SubscribeEvent
	public static void registerLayer(RegisterLayerDefinitions event) {
		event.registerLayerDefinition(DrillRenderer.LAYER_LOCATION, DrillRenderer::createModel);
	}
}
