package com.tom.createores.jm;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.DimChunkPos;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.Displayable;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.display.PolygonOverlay;

public enum OreVeinsOverlay {
	INSTANCE;
	private static final Gson gson = new GsonBuilder().create();
	private IClientAPI api;
	private Map<DimChunkPos, OreVeinInfo> chunkData = new HashMap<>();
	private Map<DimChunkPos, PolygonOverlay> chunkOverlays = new HashMap<>();
	private List<OreDistanceInfo> oreDistanceInfos = new ArrayList<>();
	private List<OreNearbyInfo> oreNearbyInfos = new ArrayList<>();
	private final Minecraft mc = Minecraft.getInstance();
	private boolean activated = true;

	public void setApi(IClientAPI api) {
		this.api = api;
	}

	@SuppressWarnings("unchecked")
	public void onMappingStarted() {
		final var level = mc.level;

		if (level == null) return;

		File modFolder = api.getDataPath(CreateOreExcavation.MODID);
		File veins = new File(modFolder, "veins.json");
		if (veins.exists()) {
			RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
			try (FileReader rd = new FileReader(veins)) {
				Map<String, Object> root = (Map<String, Object>) gson.fromJson(rd, Object.class);
				((List<Map<String, Object>>) root.getOrDefault("veins", Collections.emptyList())).forEach(dimMap -> {
					ResourceLocation dimKey = new ResourceLocation((String) dimMap.get("dim"));
					ResourceKey<Level> lvl = ResourceKey.create(Registries.DIMENSION, dimKey);
					((List<Map<String, Object>>) dimMap.getOrDefault("veins", Collections.emptyList())).forEach(vein -> {
						int x = ((Number) vein.get("x")).intValue();
						int z = ((Number) vein.get("z")).intValue();
						ResourceLocation key = new ResourceLocation((String) vein.get("id"));
						VeinRecipe v = mngr.byKey(key).filter(r -> r instanceof VeinRecipe).map(r -> (VeinRecipe) r).orElse(null);
						if (v != null) {
							var p = new DimChunkPos(lvl, x, z);
							chunkData.put(p, new OreVeinInfo(p, v));
						}
					});
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		chunkData.forEach((k, data) -> {
			if (!k.dimension.equals(level.dimension())) return;
			showOverlay(data.getOverlay());
			chunkOverlays.put(k, data.getOverlay());
		});
	}

	public void showOverlay(Displayable overlay) {
		try {
			api.show(overlay);
		} catch (Throwable t) {
			CreateOreExcavation.LOGGER.error(String.valueOf(t));
		}
	}

	public void showOverlays(Collection<? extends Displayable> overlays) {
		overlays.forEach(this::showOverlay);
	}

	public void removeOverlays(Collection<? extends Displayable> overlays) {
		overlays.forEach(o -> api.remove(o));
	}

	public void onMappingStopped() {
		File modFolder = api.getDataPath(CreateOreExcavation.MODID);
		File veins = new File(modFolder, "veins.json");
		modFolder.mkdirs();
		try {
			Map<ResourceLocation, List<Map<String, Object>>> dimMap = new HashMap<>();
			chunkData.forEach((k, v) -> {
				Map<String, Object> m = new HashMap<>();
				m.put("x", k.x);
				m.put("z", k.z);
				m.put("id", v.id.toString());
				dimMap.computeIfAbsent(k.dimension.location(), __ -> new ArrayList<>()).add(m);
			});
			Map<String, Object> root = new HashMap<>();
			List<Map<String, Object>> vs = new ArrayList<>();
			root.put("veins", vs);
			dimMap.forEach((k, v) -> {
				Map<String, Object> d = new HashMap<>();
				vs.add(d);
				d.put("dim", k.toString());
				d.put("veins", v);
			});
			try (PrintWriter w = new PrintWriter(veins)) {
				gson.toJson(root, w);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		chunkData.clear();
		chunkOverlays.clear();
		oreDistanceInfos.clear();
		oreNearbyInfos.clear();
	}

	private void toggleOverlay() {
		if (isActivated()) {
			removeOverlays(chunkOverlays.values());
			oreDistanceInfos.forEach(e -> api.remove(e.getOverlay()));
			oreNearbyInfos.forEach(e -> api.remove(e.getOverlay()));
		} else {
			showOverlays(chunkOverlays.values());
			oreDistanceInfos.forEach(e -> showOverlay(e.getOverlay()));
			oreNearbyInfos.forEach(e -> showOverlay(e.getOverlay()));
		}

		activated = !isActivated();
	}

	public boolean isActivated() {
		return activated;
	}

	public void toggle(IThemeButton button) {
		toggleOverlay();
		button.setToggled(activated);
	}

	public void setVeinInfo(ChunkPos posIn, ResourceLocation id) {
		DimChunkPos pos = new DimChunkPos(mc.level, posIn);
		if (chunkData.containsKey(pos)) {
			PolygonOverlay ov = chunkOverlays.remove(pos);
			if (activated && ov != null)api.remove(ov);
		}
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		VeinRecipe vein = mngr.byKey(id).filter(r -> r instanceof VeinRecipe).map(r -> (VeinRecipe) r).orElse(null);
		if (vein != null) {
			var info = new OreVeinInfo(pos, vein);
			chunkData.put(pos, info);
			if (activated) showOverlay(info.getOverlay());
			chunkOverlays.put(pos, info.getOverlay());
		}
	}

	public void addVeinNearbyInfo(ChunkPos center, ResourceLocation id) {
		if (oreDistanceInfos.size() > 2) {
			OreDistanceInfo ov = oreDistanceInfos.remove(0);
			if (activated && ov != null)api.remove(ov.getOverlay());
		}
		long time = mc.level.getGameTime();
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		VeinRecipe vein = mngr.byKey(id).filter(r -> r instanceof VeinRecipe).map(r -> (VeinRecipe) r).orElse(null);
		if (vein != null) {
			var v = new OreNearbyInfo(mc.level.dimension(), center, vein, time + 30 * 20);
			oreNearbyInfos.add(v);
			if (activated) showOverlay(v.getOverlay());
		}
	}

	public void addVeinDistanceInfo(BlockPos center, int radius, ResourceLocation id) {
		if (oreDistanceInfos.size() > 2) {
			OreDistanceInfo ov = oreDistanceInfos.remove(0);
			if (activated && ov != null)api.remove(ov.getOverlay());
		}
		long time = mc.level.getGameTime();
		RecipeManager mngr = Minecraft.getInstance().getConnection().getRecipeManager();
		VeinRecipe vein = mngr.byKey(id).filter(r -> r instanceof VeinRecipe).map(r -> (VeinRecipe) r).orElse(null);
		if (vein != null) {
			var v = new OreDistanceInfo(mc.level.dimension(), center, radius, vein, time + 30 * 20);
			oreDistanceInfos.add(v);
			if (activated) showOverlay(v.getOverlay());
		}
	}

	public void tick() {
		if (!oreDistanceInfos.isEmpty()) {
			long time = mc.level.getGameTime();
			oreDistanceInfos.removeIf(i -> {
				var t = i.timedOut(time);
				if (t)api.remove(i.getOverlay());
				return t;
			});
		}
		if (!oreNearbyInfos.isEmpty()) {
			long time = mc.level.getGameTime();
			oreNearbyInfos.removeIf(i -> {
				var t = i.timedOut(time);
				if (t)api.remove(i.getOverlay());
				return t;
			});
		}
	}

	public static void addOreInfoToMap(CompoundTag tag) {
		int x = tag.getInt("x");
		int z = tag.getInt("z");
		BlockPos pos = new BlockPos(x, 0, z);
		ChunkPos center = new ChunkPos(pos);
		if (tag.contains("found")) {
			ResourceLocation id = ResourceLocation.tryParse(tag.getString("found"));
			OreVeinsOverlay.INSTANCE.setVeinInfo(center, id);
		}
		if (tag.contains("nearby")) {
			ResourceLocation id = ResourceLocation.tryParse(tag.getString("nearby"));
			OreVeinsOverlay.INSTANCE.addVeinNearbyInfo(center, id);
		}
		if (tag.contains("far")) {
			ResourceLocation id = ResourceLocation.tryParse(tag.getString("far"));
			int dist = tag.getInt("dist");
			OreVeinsOverlay.INSTANCE.addVeinDistanceInfo(pos, dist, id);
		}
	}
}
