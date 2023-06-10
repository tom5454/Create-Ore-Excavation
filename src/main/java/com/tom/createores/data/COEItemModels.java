package com.tom.createores.data;

import net.minecraft.data.DataGenerator;

import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import com.tom.createores.CreateOreExcavation;

public class COEItemModels extends ItemModelProvider {

	public COEItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
		super(generator, CreateOreExcavation.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		singleTexture(CreateOreExcavation.NORMAL_DRILL_ITEM.get().getRegistryName().getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/normal_drill"));
		singleTexture(CreateOreExcavation.DIAMOND_DRILL_ITEM.get().getRegistryName().getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/diamond_drill"));
		singleTexture(CreateOreExcavation.NETHERITE_DRILL_ITEM.get().getRegistryName().getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/netherite_drill"));
		singleTexture(CreateOreExcavation.RAW_DIAMOND.get().getRegistryName().getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/raw_diamond"));
		singleTexture(CreateOreExcavation.RAW_EMERALD.get().getRegistryName().getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/raw_emerald"));
		singleTexture(CreateOreExcavation.RAW_REDSTONE.get().getRegistryName().getPath(),
				mcLoc("item/generated"),
				"layer0", modLoc("item/raw_redstone"));
		singleTexture(CreateOreExcavation.VEIN_FINDER_ITEM.get().getRegistryName().getPath(),
				mcLoc("item/handheld"),
				"layer0", modLoc("item/vein_finder"));
	}
}