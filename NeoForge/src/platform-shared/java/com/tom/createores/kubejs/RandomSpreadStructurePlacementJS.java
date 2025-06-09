package com.tom.createores.kubejs;

import java.util.Map;
import java.util.Optional;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement.FrequencyReductionMethod;

import com.mojang.serialization.JavaOps;

public class RandomSpreadStructurePlacementJS {
	public int spacing;
	public int separation;
	public RandomSpreadType spreadType;
	public FrequencyReductionMethod frequencyReductionMethod;
	public float frequency;
	public int salt;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public RandomSpreadStructurePlacementJS(RandomSpreadStructurePlacement inst) {
		spacing = inst.spacing();
		separation = inst.separation();
		spreadType = inst.spreadType();
		RandomSpreadStructurePlacement.CODEC.codec().encodeStart(JavaOps.INSTANCE, inst).ifSuccess(obj -> {
			try {
				Map map = (Map) obj;
				this.frequency = (float) map.getOrDefault("frequency", 1f);
				this.salt = (int) map.getOrDefault("salt", 0);
				this.frequencyReductionMethod = (FrequencyReductionMethod) map.getOrDefault("frequency_reduction_method", FrequencyReductionMethod.DEFAULT);
			} catch (ClassCastException e) {
			}
		});
	}

	public RandomSpreadStructurePlacementJS(int spacing, int separation, int salt) {
		this.spacing = spacing;
		this.separation = separation;
		this.spreadType = RandomSpreadType.LINEAR;
		this.salt = salt;
		this.frequencyReductionMethod = FrequencyReductionMethod.DEFAULT;
		this.frequency = 1f;
	}

	public RandomSpreadStructurePlacement map() {
		return new RandomSpreadStructurePlacement(Vec3i.ZERO, frequencyReductionMethod, frequency, salt, Optional.empty(), spacing, separation, spreadType);
	}
}
