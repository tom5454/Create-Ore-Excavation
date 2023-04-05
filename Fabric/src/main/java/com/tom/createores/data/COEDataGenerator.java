package com.tom.createores.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraftforge.common.data.ExistingFileHelper;

import com.tom.createores.CreateOreExcavation;

public class COEDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		Path[] paths = Arrays.stream(System.getProperty("porting_lib.datagen.existing_resources").split(";")).map(Paths::get).toArray(Path[]::new);
		ExistingFileHelper helper = ExistingFileHelper.withResources(paths);
		CreateOreExcavation.registrate().setupDatagen(fabricDataGenerator, helper);
		fabricDataGenerator.addProvider(new COERecipes(fabricDataGenerator));
	}
}
