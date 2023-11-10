package com.tom.createores.jm;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.VeinRecipe;

import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;

public class OreDistanceInfo {
	private long time;
	private PolygonOverlay overlay;

	public OreDistanceInfo(ResourceKey<Level> dim, BlockPos center, int radius, VeinRecipe vein, long time) {
		this.time = time;
		BlockPos[] c = new BlockPos[36];
		int x = center.getX();
		int z = center.getZ();
		for (int i = 0;i<36;i++) {
			double r = Math.toRadians(i * 10);
			c[i] = new BlockPos(x + (int) (Math.cos(r) * radius), 1, z + (int) (Math.sin(r) * radius));
		}
		var polygon = new MapPolygon(c);

		int color = 0xFFFF0000;
		var displayId = "coe_vein_dist_" + x + ',' + z + "," + radius;
		var shapeProps = new ShapeProperties()
				.setStrokeWidth(radius > 50 ? 4 : 2)
				.setStrokeColor(color)
				.setFillColor(color)
				.setFillOpacity(0.1f);

		var textProps = new TextProperties()
				.setColor(color)
				.setOpacity(1f)
				.setFontShadow(true);

		overlay = new PolygonOverlay(CreateOreExcavation.MODID, displayId, dim, shapeProps, polygon);

		overlay.setOverlayGroupName("COE Veins")
		.setLabel(Component.translatable("chat.coe.veinFinder.distance", vein.getName(), radius).getString())
		.setTextProperties(textProps);
	}

	public PolygonOverlay getOverlay() {
		return overlay;
	}

	public boolean timedOut(long time) {
		return this.time < time;
	}

}
