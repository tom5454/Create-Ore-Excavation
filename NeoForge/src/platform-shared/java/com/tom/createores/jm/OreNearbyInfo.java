package com.tom.createores.jm;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.VeinRecipe;

import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.model.MapPolygon;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.client.model.TextProperties;

public class OreNearbyInfo {
	private long time;
	private PolygonOverlay overlay;

	public OreNearbyInfo(ResourceKey<Level> dim, ChunkPos center, VeinRecipe vein, long time) {
		this.time = time;

		int x = center.x << 4;
		int z = center.z << 4;
		BlockPos sw = new BlockPos(x - 16, 1, z + 32);
		BlockPos se = new BlockPos(x + 32, 1, z + 32);
		BlockPos ne = new BlockPos(x + 32, 1, z - 16);
		BlockPos nw = new BlockPos(x - 16, 1, z - 16);
		var polygon = new MapPolygon(new BlockPos[]{sw, se, ne, nw});

		int color = 0xFF0000FF;
		var shapeProps = new ShapeProperties()
				.setStrokeWidth(2)
				.setStrokeColor(color)
				.setFillColor(color)
				.setFillOpacity(0.1f);

		var textProps = new TextProperties()
				.setColor(color)
				.setOpacity(1f)
				.setFontShadow(true);

		overlay = new PolygonOverlay(CreateOreExcavation.MODID, dim, shapeProps, polygon);

		overlay.setOverlayGroupName("COE Veins")
		.setLabel(Component.translatable("chat.coe.veinFinder.nearby", vein.getName()).getString())
		.setTextProperties(textProps);
	}

	public PolygonOverlay getOverlay() {
		return overlay;
	}

	public boolean timedOut(long time) {
		return this.time < time;
	}

}
