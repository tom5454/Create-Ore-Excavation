package com.tom.createores.jm;

import net.minecraft.resources.ResourceLocation;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.DimChunkPos;

import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;

public class OreVeinInfo {
	private PolygonOverlay overlay;
	public final ResourceLocation id;

	public OreVeinInfo(DimChunkPos pos, VeinRecipe vein, boolean legacy) {
		int color = legacy ? 0xFF777777 : 0xFFFFFF00;
		var displayId = "coe_vein_" + pos.x + ',' + pos.z;
		var shapeProps = new ShapeProperties()
				.setStrokeWidth(1)
				.setStrokeColor(color)
				.setFillColor(color)
				.setFillOpacity(0.2f);

		var textProps = new TextProperties()
				.setColor(color)
				.setOpacity(1f)
				.setFontShadow(true);

		var polygon = PolygonHelper.createChunkPolygon(pos.x, 1, pos.z);

		overlay = new PolygonOverlay(CreateOreExcavation.MODID, displayId, pos.dimension, shapeProps, polygon);

		overlay.setOverlayGroupName("COE Veins")
		.setLabel(vein.getName().getString())
		.setTextProperties(textProps);

		this.id = vein.id;
	}

	public PolygonOverlay getOverlay() {
		return overlay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((overlay == null) ? 0 : overlay.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		OreVeinInfo other = (OreVeinInfo) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (overlay == null) {
			if (other.overlay != null) return false;
		} else if (!overlay.equals(other.overlay)) return false;
		return true;
	}
}
