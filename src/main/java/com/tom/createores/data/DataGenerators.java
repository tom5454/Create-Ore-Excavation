package com.tom.createores.data;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.client.ClientRegistration;

@Mod.EventBusSubscriber(modid = CreateOreExcavation.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {
			generator.addProvider(new COERecipes(generator));
			COEBlockTags blockTags = new COEBlockTags(generator, event.getExistingFileHelper());
			generator.addProvider(blockTags);
			generator.addProvider(new COEItemTags(generator, blockTags, event.getExistingFileHelper()));
		}
		if (event.includeClient()) {
			generator.addProvider(new COEItemModels(generator, event.getExistingFileHelper()));
			ClientRegistration.register();
		}
	}
}
