package com.tom.createores.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import com.google.common.base.Strings;

public class ClientUtil {

	public static Component makeProgressBar(float progress) {
		Font fontRenderer = Minecraft.getInstance().font;
		float charWidth = fontRenderer.width("|");
		float tipWidth = 60;

		int total = (int) (tipWidth / charWidth);
		int current = (int) (progress * total);
		if(progress < 0)progress = 0;

		String bars = "";
		bars += ChatFormatting.GRAY + Strings.repeat("|", current);
		if (progress < 1)
			bars += ChatFormatting.DARK_GRAY + Strings.repeat("|", total - current);
		return Component.literal(bars);
	}
}
