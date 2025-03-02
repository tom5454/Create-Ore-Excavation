package com.tom.createores.util;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

import joptsimple.internal.Strings;

public class TooltipUtil {
	public static void forGoggles(List<? super MutableComponent> tooltip, MutableComponent component) {
		forGoggles(tooltip, 0, component);
	}

	public static void forGoggles(List<? super MutableComponent> tooltip, int indents, MutableComponent component) {
		tooltip.add(Component.literal(Strings.repeat(' ', getIndents(Minecraft.getInstance().font, 4 + indents))).append(component));
	}

	public static final float DEFAULT_SPACE_WIDTH = 4.0F; // space width in vanilla's default font
	static int getIndents(Font font, int defaultIndents) {
		int spaceWidth = font.width(" ");
		if (DEFAULT_SPACE_WIDTH == spaceWidth) {
			return defaultIndents;
		}
		return Mth.ceil(DEFAULT_SPACE_WIDTH * defaultIndents / spaceWidth);
	}
}
