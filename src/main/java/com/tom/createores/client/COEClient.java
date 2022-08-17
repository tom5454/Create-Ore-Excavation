package com.tom.createores.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import com.tom.createores.CreateOreExcavation;

@EventBusSubscriber(value = Dist.CLIENT, modid = CreateOreExcavation.MODID, bus = Bus.MOD)
public class COEClient {

	@SubscribeEvent
	public static void registerLayer(RegisterLayerDefinitions event) {
		event.registerLayerDefinition(DrillRenderer.LAYER_LOCATION, DrillRenderer::createModel);
	}
}
