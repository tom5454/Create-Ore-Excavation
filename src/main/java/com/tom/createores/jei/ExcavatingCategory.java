package com.tom.createores.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Kind;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import com.tom.createores.recipe.ExcavatingRecipe;
import com.tom.createores.util.NumberFormatter;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public abstract class ExcavatingCategory<T extends ExcavatingRecipe> implements IRecipeCategory<T> {
	protected AnimatedBlock block;
	protected IDrawable background;
	protected IDrawable icon;
	protected IDrawable biomeWIcon, biomeBIcon;

	public ExcavatingCategory() {
		background = new EmptyBackground(177, 100);
		biomeWIcon = new ItemIcon(() -> new ItemStack(Items.OAK_SAPLING));
		biomeBIcon = new DoubleItemIcon(() -> new ItemStack(Items.OAK_SAPLING), () -> new ItemStack(Items.BARRIER));
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	@Override
	public abstract RecipeType<T> getRecipeType();

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
		builder
		.addSlot(RecipeIngredientRole.INPUT, 51, 3)
		.setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
		.addIngredients(recipe.getDrill());
	}

	@Override
	public Class<? extends T> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}

	@Override
	public void draw(T recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		block.draw(stack, 48, 35);
		biomeWIcon.draw(stack, 100, 5);
		biomeBIcon.draw(stack, 100, 25);
	}

	@Override
	public List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		List<Component> tooltip = new ArrayList<>();
		if(mouseX > 100 && mouseX < 118) {
			if(mouseY > 5 && mouseY < 23) {
				tooltip.add(new TranslatableComponent("tooltip.coe.biome.whitelist"));
				listBiomes(recipe.biomeWhitelist, tooltip);
			} else if(mouseY > 25 && mouseY < 43) {
				tooltip.add(new TranslatableComponent("tooltip.coe.biome.blacklist"));
				listBiomes(recipe.biomeBlacklist, tooltip);
			} else {
				biomePage = 0;
				lastBiomeChangeTime = 0;
			}
		}
		if(mouseX > 40 && mouseX < 80 && mouseY > 25 && mouseY < 60) {
			tooltip.add(recipe.getName());
			tooltip.add(new TranslatableComponent("tooltip.coe.processTime", recipe.getTicks()));
			if(recipe.isInfiniteClient())tooltip.add(new TranslatableComponent("tooltip.coe.infiniteVeins"));
			else tooltip.add(new TranslatableComponent("tooltip.coe.finiteVeins", NumberFormatter.formatNumber(recipe.getMinAmountClient()), NumberFormatter.formatNumber(recipe.getMaxAmountClient())));
		}
		return tooltip;
	}

	private static long lastBiomeChangeTime;
	private static int biomePage;
	private static void listBiomes(TagKey<Biome> tag, List<Component> tooltip) {
		boolean isShift = Screen.hasShiftDown();
		Minecraft.getInstance().getConnection().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getTag(tag).
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
				pg = new TranslatableComponent("tooltip.coe.page", biomePage + 1, (size / 16) + 1);
				s = s.skip(biomePage * 16).limit(16);
			}
			List<Component> comps = s.map(ExcavatingCategory::getBiomeId).
					map(b -> new TranslatableComponent("biome." + b.getNamespace() + "." + b.getPath())).
					collect(Collectors.toList());
			if(pg != null)comps.add(pg);
			return comps;
		}).ifPresent(tooltip::addAll);
	}

	private static ResourceLocation getBiomeId(Holder<Biome> h) {
		try {
			if(h.kind() == Kind.DIRECT) {
				return h.value().getRegistryName();
			} else {
				return ((Holder.Reference<Biome>)h).key().location();
			}
		} catch (Exception e) {
			return new ResourceLocation("null");
		}
	}
}
