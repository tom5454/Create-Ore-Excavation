package com.tom.createores.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Kind;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeTooltip {
	private static long lastBiomeChangeTime;
	private static int biomePage;

	public static void listBiomes(TagKey<Biome> tag, List<Component> tooltip) {
		boolean isShift = Screen.hasShiftDown();
		Registry<Biome> biomeReg = Minecraft.getInstance().getConnection().registryAccess().registryOrThrow(Registries.BIOME);
		biomeReg.getTag(tag).
		map(t -> {
			Stream<Holder<Biome>> s = t.stream();
			int size = t.size();
			Component pg = null;
			if(size > 16) {
				if(!isShift) {
					if(System.currentTimeMillis() - lastBiomeChangeTime > 2000) {
						biomePage++;
						if(biomePage * 16 >= size)biomePage = 0;
						lastBiomeChangeTime = System.currentTimeMillis();
					}
				}
				pg = Component.translatable("tooltip.coe.page", biomePage + 1, (size / 16) + 1);
				s = s.skip(biomePage * 16).limit(16);
			}
			List<Component> comps = s.map(b -> getBiomeId(biomeReg, b)).
					map(b -> Component.translatable("biome." + b.getNamespace() + "." + b.getPath())).
					collect(Collectors.toList());
			if(pg != null)comps.add(pg);
			return comps;
		}).ifPresent(tooltip::addAll);
	}

	private static ResourceLocation getBiomeId(Registry<Biome> biomeReg, Holder<Biome> h) {
		try {
			if(h.kind() == Kind.DIRECT) {
				return biomeReg.getKey(h.value());
			} else {
				return ((Holder.Reference<Biome>)h).key().location();
			}
		} catch (Exception e) {
			return new ResourceLocation("null");
		}
	}

	public static void resetPage() {
		biomePage = 0;
		lastBiomeChangeTime = 0;
	}
}
