package com.tom.createores.data;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.client.ClientRegistration;

@Mod.EventBusSubscriber(modid = CreateOreExcavation.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeServer(), new COERecipes(generator));
		COEBlockTags blockTags = new COEBlockTags(generator, event.getExistingFileHelper());
		generator.addProvider(event.includeServer(), blockTags);
		generator.addProvider(event.includeServer(), new COEItemTags(generator, blockTags, event.getExistingFileHelper()));
		generator.addProvider(event.includeClient(), new COEItemModels(generator, event.getExistingFileHelper()));
		if(event.includeClient())ClientRegistration.register();
	}
}
