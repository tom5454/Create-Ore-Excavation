package com.tom.createores.block.entity;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;

import com.tom.createores.block.KineticInputBlock;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

public class KineticInputVisual extends KineticBlockEntityVisual<KineticInputBlockEntity> {
	private RotatingInstance shaft;

	public KineticInputVisual(VisualizationContext context, KineticInputBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);

		float speed = blockEntity.getSpeed();

		shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
				.createInstance();

		shaft.setup(blockEntity, speed)
		.setPosition(getVisualPosition())
		.rotateToFace(Direction.SOUTH, getDir())
		.setChanged();
	}

	private Direction getDir() {
		return blockState.getValue(KineticInputBlock.SHAFT_FACING);
	}

	@Override
	public void update(float partialTick) {
		shaft.setup(blockEntity, blockEntity.getSpeed()).setChanged();
	}

	@Override
	public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
		consumer.accept(shaft);
	}

	@Override
	public void updateLight(float partialTick) {
		relight(shaft);
	}

	@Override
	protected void _delete() {
		shaft.delete();
	}
}
