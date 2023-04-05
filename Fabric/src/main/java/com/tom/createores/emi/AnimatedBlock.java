package com.tom.createores.emi;

import net.minecraft.world.level.block.state.BlockState;

import com.mojang.math.Vector3f;
import com.simibubi.create.compat.emi.CreateEmiAnimations;

import dev.emi.emi.api.widget.WidgetHolder;

public class AnimatedBlock  {

	public static void addBlock(BlockState state, WidgetHolder widgets, int x, int y) {
		widgets.addDrawable(x, y, 0, 0, (matrices, mouseX, mouseY, delta) -> {
			matrices.translate(0, 0, 200);
			matrices.mulPose(Vector3f.XP.rotationDegrees(-12.5f));
			matrices.mulPose(Vector3f.YP.rotationDegrees(22.5f));
			int scale = 16;

			CreateEmiAnimations.blockElement(state)
			.rotateBlock(0, 0, 0)
			.scale(scale)
			.render(matrices);
		});
	}
}
