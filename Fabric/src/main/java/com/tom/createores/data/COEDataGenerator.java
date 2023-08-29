package com.tom.createores.data;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataProvider;

import com.google.common.collect.BiMap;

import com.tom.createores.CreateOreExcavation;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;

public class COEDataGenerator implements DataGeneratorEntrypoint {

	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		try {
			Field f = RegistrateDataProvider.class.getDeclaredField("TYPES");
			f.setAccessible(true);
			BiMap<String, ProviderType<?>> map = (BiMap<String, ProviderType<?>>) f.get(null);
			map.remove("loot");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		Path[] paths = Arrays.stream(System.getProperty("porting_lib.datagen.existing_resources").split(";")).map(Paths::get).toArray(Path[]::new);
		ExistingFileHelper helper = ExistingFileHelper.withResources(paths);
		Pack pack = generator.createPack();
		CreateOreExcavation.registrate().setupDatagen(pack, helper);
		pack.addProvider(COERecipes::new);
	}
}
