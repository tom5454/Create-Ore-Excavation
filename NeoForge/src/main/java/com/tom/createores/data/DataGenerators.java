package com.tom.createores.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import com.tterrag.registrate.providers.ProviderType;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.client.ClientRegistration;

@EventBusSubscriber(modid = CreateOreExcavation.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(event.includeServer(), new COERecipes(packOutput, lookupProvider));
		//generator.addProvider(event.includeServer(), new TurtleUpgradeProvider(generator.getPackOutput()));
		if(event.includeClient()) {
			ClientRegistration.register();
			CreateOreExcavation.registrate().addDataGenerator(ProviderType.LANG, provider -> {
				BiConsumer<String, String> langConsumer = provider::add;
				PonderIndex.getLangAccess().provideLang(CreateOreExcavation.MODID, langConsumer);
			});
		}
	}
}
