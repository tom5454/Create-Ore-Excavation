package com.tom.createores.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;

import net.minecraftforge.common.data.ExistingFileHelper;

import com.tom.createores.CreateOreExcavation;

public class COEItemTags extends ItemTagsProvider {

	public COEItemTags(DataGenerator generator, BlockTagsProvider blockTags, ExistingFileHelper helper) {
		super(generator, blockTags, CreateOreExcavation.MODID, helper);
	}

	@Override
	protected void addTags() {
		tag(CreateOreExcavation.DRILL_TAG)
		.add(CreateOreExcavation.NORMAL_DRILL_ITEM.get())
		.add(CreateOreExcavation.DIAMOND_DRILL_ITEM.get())
		.add(CreateOreExcavation.NETHERITE_DRILL_ITEM.get());
	}

	@Override
	public String getName() {
		return "COE Tags";
	}
}