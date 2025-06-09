package com.tom.createores.kubejs;

import net.minecraft.world.item.ItemStack;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;

public interface COEUtil {

	static ProcessingOutput processingOutput(ItemStack stack, float chance) {
		return new ProcessingOutput(stack, chance);
	}
}
