package com.tom.createores.rei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import net.createmod.catnip.gui.element.GuiGameElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.systems.RenderSystem;

import com.tom.createores.Registration;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.NumberFormatter;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;

public class VeinDefinition implements EntryDefinition<VeinRecipe>, EntrySerializer<VeinRecipe> {
	@Environment(EnvType.CLIENT)
	private EntryRenderer<VeinRecipe> renderer;

	public VeinDefinition() {
		EnvExecutor.runInEnv(Env.CLIENT, () -> () -> Client.init(this));
	}

	@Environment(EnvType.CLIENT)
	private static class Client {
		private static void init(VeinDefinition definition) {
			definition.renderer = definition.new VeinEntryRenderer();
		}
	}

	@Override
	public Class<VeinRecipe> getValueType() {
		return VeinRecipe.class;
	}

	@Override
	public EntryType<VeinRecipe> getType() {
		return REIPlugin.VEIN_TYPE;
	}

	@Override
	public EntryRenderer<VeinRecipe> getRenderer() {
		return renderer;
	}

	@Override
	public @Nullable ResourceLocation getIdentifier(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		return value != null ? value.getId() : null;
	}

	@Override
	public boolean isEmpty(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		return value == null;
	}

	@Override
	public VeinRecipe copy(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		return value;
	}

	@Override
	public VeinRecipe normalize(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		return value;
	}

	@Override
	public VeinRecipe wildcard(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		return value;
	}

	@Override
	public long hash(EntryStack<VeinRecipe> entry, VeinRecipe value, ComparisonContext context) {
		return value.getId().hashCode();
	}

	@Override
	public boolean equals(VeinRecipe o1, VeinRecipe o2, ComparisonContext context) {
		return o1.getId().equals(o2.getId());
	}

	@Override
	public @Nullable EntrySerializer<VeinRecipe> getSerializer() {
		return this;
	}

	@Override
	public Component asFormattedText(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		return value.getName();
	}

	@Override
	public Stream<? extends TagKey<?>> getTagsFor(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		return Stream.empty();
	}

	@Override
	public boolean supportSaving() {
		return true;
	}

	@Override
	public boolean supportReading() {
		return true;
	}

	@Override
	public CompoundTag save(EntryStack<VeinRecipe> entry, VeinRecipe value) {
		CompoundTag tag = new CompoundTag();
		tag.putString("id", value.getId().toString());
		return tag;
	}

	@Override
	public VeinRecipe read(CompoundTag tag) {
		ResourceLocation r = ResourceLocation.tryParse(tag.getString("id"));
		if (r == null)return null;
		return Minecraft.getInstance().level.getRecipeManager().byKey(r).map(e -> e instanceof VeinRecipe v ? v : null).orElse(null);
	}

	@Environment(EnvType.CLIENT)
	public class VeinEntryRenderer implements EntryRenderer<VeinRecipe> {
		private ItemStack drill = new ItemStack(Registration.NORMAL_DRILL_ITEM.get());

		@Override
		public void render(EntryStack<VeinRecipe> entry, GuiGraphics guiGraphics, Rectangle bounds, int mouseX, int mouseY,
				float delta) {
			VeinRecipe ingredient = entry.getValue();
			RenderSystem.enableDepthTest();
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(bounds.x, bounds.y, 0);

			GuiGameElement.of(ingredient.icon)
			.render(guiGraphics);

			guiGraphics.pose().pushPose();
			float s = 0.5f;
			guiGraphics.pose().translate(8, 8, 100);
			guiGraphics.pose().scale(s, s, s);
			GuiGameElement.of(drill)
			.render(guiGraphics);
			guiGraphics.pose().popPose();

			guiGraphics.pose().popPose();
		}

		@Override
		public @Nullable Tooltip getTooltip(EntryStack<VeinRecipe> entry, TooltipContext context) {
			VeinRecipe ingredient = entry.getValue();
			List<Component> tooltip = new ArrayList<>();
			tooltip.add(ingredient.veinName);
			if(ingredient.isInfiniteClient())tooltip.add(Component.translatable("tooltip.coe.infiniteVeins"));
			else tooltip.add(Component.translatable("tooltip.coe.finiteVeins", NumberFormatter.formatNumber(ingredient.getMinAmountClient()), NumberFormatter.formatNumber(ingredient.getMaxAmountClient())));
			return Tooltip.create(tooltip);
		}

	}
}
