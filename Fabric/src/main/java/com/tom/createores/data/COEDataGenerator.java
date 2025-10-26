package com.tom.createores.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.BiConsumer;

import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.minecraft.data.DataProvider;

import com.simibubi.create.Create;
import com.tterrag.registrate.providers.ProviderType;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.cc.TurtleUpgradeData;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;

public class COEDataGenerator implements DataGeneratorEntrypoint {

	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		Path[] paths = Arrays.stream(System.getProperty("porting_lib.datagen.existing_resources").split(";")).map(Paths::get).toArray(Path[]::new);
		ExistingFileHelper helper = ExistingFileHelper.withResources(paths);
		Pack pack = generator.createPack();
		CreateOreExcavation.registrate().setupDatagen(pack, helper);
		pack.addProvider(COERecipes::new);
		pack.addProvider((DataProvider.Factory) TurtleUpgradeData::new);
		CreateOreExcavation.registrate().addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;
			PonderIndex.getLangAccess().provideLang(Create.ID, langConsumer);
		});
	}
}
