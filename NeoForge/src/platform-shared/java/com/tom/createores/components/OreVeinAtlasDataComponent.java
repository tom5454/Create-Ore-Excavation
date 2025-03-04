package com.tom.createores.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.tom.createores.util.DimChunkPos;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public record OreVeinAtlasDataComponent(List<ResourceLocation> discovered, List<Pair<DimChunkPos, OreVeinData>> veins, List<ResourceLocation> exclude, Optional<ResourceLocation> target) {
	public static final Codec<OreVeinAtlasDataComponent> CODEC = RecordCodecBuilder.<OreVeinAtlasDataComponent>mapCodec(b -> {
		return b.group(
				Codec.list(ResourceLocation.CODEC).fieldOf("discovered").forGetter(OreVeinAtlasDataComponent::discovered),
				Codec.list(Codec.pair(DimChunkPos.CODEC, OreVeinData.CODEC)).fieldOf("veins").forGetter(OreVeinAtlasDataComponent::veins),
				Codec.list(ResourceLocation.CODEC).fieldOf("exclude").forGetter(OreVeinAtlasDataComponent::exclude),
				ResourceLocation.CODEC.optionalFieldOf("target").forGetter(OreVeinAtlasDataComponent::target)
				).apply(b, OreVeinAtlasDataComponent::new);
	}).codec();

	public record OreVeinData(ResourceLocation id, float size, boolean hide) {
		public static final Codec<OreVeinData> CODEC = RecordCodecBuilder.<OreVeinData>mapCodec(b -> {
			return b.group(
					ResourceLocation.CODEC.fieldOf("id").forGetter(OreVeinData::id),
					Codec.FLOAT.fieldOf("size").forGetter(OreVeinData::size),
					Codec.BOOL.fieldOf("hide").forGetter(OreVeinData::hide)
					).apply(b, OreVeinData::new);
		}).codec();
	}

	public static class OreVeinAtlasData {
		private boolean edited;
		private List<ResourceLocation> discovered;
		private Map<DimChunkPos, OreVeinData> veins;
		private List<ResourceLocation> exclude;
		private ResourceLocation target;
		private List<Pair<DimChunkPos, OreVeinData>> veinsIndex;

		public OreVeinAtlasData(OreVeinAtlasDataComponent comp) {
			if (comp == null) {
				discovered = Collections.emptyList();
				veins = Collections.emptyMap();
				exclude = Collections.emptyList();
				veinsIndex = Collections.emptyList();
			} else {
				discovered = comp.discovered();
				veins = comp.veins().stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (a, b) -> {throw new IllegalStateException();}, Object2ObjectArrayMap::new));
				exclude = comp.exclude();
				target = comp.target().orElse(null);
				veinsIndex = comp.veins();
			}
		}

		private void makeEdited() {
			if (edited)return;
			discovered = new ArrayList<>(discovered);
			veins = new Object2ObjectArrayMap<>(veins);
			exclude = new ArrayList<>(exclude);
			veinsIndex = new ArrayList<>(veinsIndex);
			edited = true;
		}

		public void addDiscovered(ResourceLocation id) {
			makeEdited();
			discovered.add(id);
		}

		public List<ResourceLocation> discovered() {
			return discovered;
		}

		public Map<DimChunkPos, OreVeinData> veins() {
			return veins;
		}

		public void addVein(DimChunkPos pos, OreVeinData oreVeinData) {
			makeEdited();
			veins.put(pos, oreVeinData);
		}

		public boolean isEdited() {
			return edited;
		}

		public void addExclude(ResourceLocation id) {
			if (exclude.contains(id))return;
			makeEdited();
			exclude.add(id);
		}

		public void removeExclude(ResourceLocation id) {
			makeEdited();
			exclude.remove(id);
		}

		public void setTarget(ResourceLocation target) {
			makeEdited();
			this.target = target;
		}

		public void toggleHide(int id) {
			var vein = veinsIndex.get(id).getFirst();
			var vd = veins.get(vein);
			if (vd == null)return;
			makeEdited();
			veins.put(vein, new OreVeinData(vd.id(), vd.size(), !vd.hide()));
		}

		public OreVeinAtlasDataComponent finish() {
			return new OreVeinAtlasDataComponent(discovered, veins.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).collect(Collectors.toList()), exclude, Optional.ofNullable(target));
		}
	}
}
