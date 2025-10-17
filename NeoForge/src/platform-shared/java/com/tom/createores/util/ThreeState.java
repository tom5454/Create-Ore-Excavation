package com.tom.createores.util;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

import com.mojang.serialization.Codec;

public enum ThreeState implements StringRepresentable {
	DEFAULT, ALWAYS, NEVER
	;
	public static final Codec<ThreeState> CODEC = StringRepresentable.<ThreeState>fromEnum(() -> ThreeState.values());

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}