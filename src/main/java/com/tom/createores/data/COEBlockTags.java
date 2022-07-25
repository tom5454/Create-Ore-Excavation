package com.tom.createores.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;

import net.minecraftforge.common.data.ExistingFileHelper;

import com.tom.createores.CreateOreExcavation;

public class COEBlockTags extends BlockTagsProvider {

	public COEBlockTags(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, CreateOreExcavation.MODID, helper);
	}

	@Override
	protected void addTags() {
	}

	@Override
	public String getName() {
		return "COE Tags";
	}
}