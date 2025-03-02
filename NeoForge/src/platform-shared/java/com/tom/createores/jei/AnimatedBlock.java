package com.tom.createores.jei;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;

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
	public void draw(GuiGraphics matrixStack, int xOffset, int yOffset) {
		matrixStack.pose().pushPose();
		matrixStack.pose().translate(xOffset, yOffset, 0);
		AllGuiTextures.JEI_SHADOW.render(matrixStack, -16, 13);
		matrixStack.pose().translate(5, 10, 24);

		blockElement(block)
		.rotateBlock(22.5, 22.5, 0)
		.scale(scale)
		.render(matrixStack);

		matrixStack.pose().popPose();
	}

}
