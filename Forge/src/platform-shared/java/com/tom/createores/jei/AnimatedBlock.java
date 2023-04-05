package com.tom.createores.jei;

import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.AllGuiTextures;

public class AnimatedBlock extends AnimatedKinetics {
	private BlockState block;
	private float scale;

	public AnimatedBlock(BlockState block, float scale) {
		this.block = block;
		this.scale = scale;
	}

	@Override
	public void draw(PoseStack matrixStack, int xOffset, int yOffset) {
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 0);
		AllGuiTextures.JEI_SHADOW.render(matrixStack, -16, 13);
		matrixStack.translate(5, 10, 24);

		blockElement(block)
		.rotateBlock(22.5, 22.5, 0)
		.scale(scale)
		.render(matrixStack);

		matrixStack.popPose();
	}

}
