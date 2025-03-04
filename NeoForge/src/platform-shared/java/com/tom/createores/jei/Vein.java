package com.tom.createores.jei;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import com.tom.createores.recipe.VeinRecipe;

public record Vein(RecipeHolder<VeinRecipe> recipe) {

	public ResourceLocation id() {
		return recipe().id();
	}

	public VeinRecipe recipe0() {
		return recipe().value();
	}

	public static final Codec<RecipeHolder<VeinRecipe>> HOLDER_CODEC = RecordCodecBuilder.<RecipeHolder<VeinRecipe>>mapCodec(b -> {
		return b.group(
				ResourceLocation.CODEC.fieldOf("id").forGetter(RecipeHolder::id),
				VeinRecipe.Serializer.CODEC.fieldOf("value").forGetter(RecipeHolder::value)
				).apply(b, RecipeHolder::new);
	}).codec();

	public static final Codec<Vein> CODEC = HOLDER_CODEC.xmap(Vein::new, Vein::recipe);
}
