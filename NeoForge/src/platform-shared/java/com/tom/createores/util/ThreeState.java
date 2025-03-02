package com.tom.createores.util;

import net.minecraft.util.GsonHelper;

import com.google.gson.JsonObject;

public enum ThreeState {
	DEFAULT, ALWAYS, NEVER
	;
	public static final ThreeState[] VALUES = values();
	public static ThreeState get(JsonObject json, String name) {
		if(GsonHelper.getAsBoolean(json, "always" + name, false))return ALWAYS;
		if(GsonHelper.getAsBoolean(json, "never" + name, false))return NEVER;
		return DEFAULT;
	}

	public void toJson(JsonObject json, String name) {
		switch (this) {
		case ALWAYS:
			json.addProperty("always" + name, true);
			break;

		case NEVER:
			json.addProperty("never" + name, true);
			break;

		case DEFAULT:
		default:
			break;
		}
	}
}