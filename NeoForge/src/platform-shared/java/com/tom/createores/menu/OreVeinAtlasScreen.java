package com.tom.createores.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import com.tom.createores.CreateOreExcavation;
import com.tom.createores.components.OreVeinAtlasDataComponent.OreVeinData;
import com.tom.createores.network.NetworkHandler;
import com.tom.createores.network.OreVeinAtlasClickPacket;
import com.tom.createores.network.OreVeinAtlasClickPacket.Option;
import com.tom.createores.recipe.VeinRecipe;
import com.tom.createores.util.DimChunkPos;
import com.tom.createores.util.NumberFormatter;
import com.tom.createores.util.ThreeState;

public class OreVeinAtlasScreen extends AbstractContainerScreen<OreVeinAtlasMenu> {
	private static final ResourceLocation GUI_TEXTURES = ResourceLocation.tryBuild(CreateOreExcavation.MODID, "textures/gui/atlas.png");
	private static final ResourceLocation EXCLUDE = ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_exclude");
	private static final ResourceLocation TARGET = ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_target");
	private static final ResourceLocation SHOW = ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_show");
	private static final ResourceLocation HIDE = ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_hidden");

	protected static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
			ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_button"),
			ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_button_disabled"),
			ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_button_hovered")
			);

	protected static final WidgetSprites ACTION_BUTTON_SPRITES = new WidgetSprites(
			ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_action_button"),
			ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_action_button_disabled"),
			ResourceLocation.tryBuild(CreateOreExcavation.MODID, "atlas_action_button_hovered")
			);

	private static final Button.OnPress NULL_PRESS = b -> {};

	private List<Vein> veinsList = new ArrayList<>();
	private List<RecipeHolder<VeinRecipe>> veinTypesList = new ArrayList<>();
	private List<Vein> veinsListSorted = new ArrayList<>();
	private List<RecipeHolder<VeinRecipe>> veinTypesListSorted = new ArrayList<>();
	private Set<ResourceLocation> excluded = new HashSet<>();
	private String target;
	private VeinsListWidget discovered;
	private VeinTypesListWidget veinTypes;
	private Component veinTypesTitle = Component.translatable("info.coe.atlas.vein_types");
	private PageButton backButton;
	private Vein selected;
	private boolean showHidden;
	private ToggleHideButton toggleHideButton;

	public OreVeinAtlasScreen(OreVeinAtlasMenu p_97741_, Inventory p_97742_, Component p_97743_) {
		super(p_97741_, p_97742_, p_97743_);
	}

	@Override
	protected void renderBg(GuiGraphics gr, float partial, int mx, int my) {
		gr.blit(GUI_TEXTURES, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 512, 256);
	}

	@Override
	protected void init() {
		imageWidth = 282;
		imageHeight = 182;
		super.init();
		int i = this.leftPos;
		int j = this.topPos;
		discovered = addRenderableWidget(new VeinsListWidget(i + 20, j + 24, 110, 140));
		veinTypes = addRenderableWidget(new VeinTypesListWidget(i + 150, j + 24, 110, 140));
		backButton = addRenderableWidget(new PageButton(i + 20 + 4, j + 24 + 140 - 12, false, p_98287_ -> this.pageBack(), true));
		toggleHideButton = addRenderableWidget(new ToggleHideButton(i + 118, j + 9, b -> toggleHide()));
		toggleHideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.coe.atlas.show_hide")));
		backButton.active = false;
		discovered.list = () -> veinsListSorted;
		veinTypes.list = () -> veinTypesListSorted;
		fillLists();
		updateVeinsList();
		updateVeinTypesList();
		discovered.updateContent();
		veinTypes.updateContent();
	}

	private void toggleHide() {
		if (selected != null) {
			toggleHideButton.hideMode = selected.hidden = !selected.hidden;
			send2(Option.TOGGLE_HIDE, veinsList.indexOf(selected));
			updateVeinsList();
		} else {
			toggleHideButton.hideMode = showHidden = !showHidden;
			updateVeinsList();
		}
	}

	private void pageBack() {
		backButton.active = false;
		discovered.setVisible(true);
		selected = null;
		toggleHideButton.hideMode = showHidden;
		toggleHideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.coe.atlas.show_hide")));
		toggleHideButton.setX(leftPos + 118);
		toggleHideButton.setY(topPos + 9);
	}

	public void setSelected(Vein selected) {
		this.selected = selected;
		toggleHideButton.hideMode = selected.hidden;
		toggleHideButton.setTooltip(Tooltip.create(Component.translatable("tooltip.coe.atlas.hide_vein")));
		toggleHideButton.setX(leftPos + 18);
		toggleHideButton.setY(topPos + 24);
	}

	private void updateVeinTypesList() {
		veinTypesListSorted.clear();
		veinTypesListSorted.addAll(veinTypesList);
		veinTypes.updateContent();
	}

	private void updateVeinsList() {
		veinsListSorted.clear();
		veinsList.stream().filter(e -> e.hidden == showHidden).forEach(veinsListSorted::add);
		discovered.updateContent();
	}

	private void fillLists() {
		veinsList.clear();
		veinTypesList.clear();
		excluded.clear();
		ItemStack is = menu.getHeldItem();
		var tag = is.get(CreateOreExcavation.ORE_VEIN_ATLAS_DATA_COMPONENT);
		if (tag == null)return;
		target = tag.target().map(ResourceLocation::toString).orElse(null);

		for (ResourceLocation v : tag.discovered()) {
			Minecraft.getInstance().level.getRecipeManager().byKey(v).ifPresent(vr -> {
				if (vr.value() instanceof VeinRecipe) {
					veinTypesList.add((RecipeHolder<VeinRecipe>) vr);
				}
			});
		}

		for (Pair<DimChunkPos, OreVeinData> e : tag.veins()) {
			var vid = e.getSecond().id();
			var pos = e.getFirst();
			float size = e.getSecond().size();
			boolean hide = e.getSecond().hide();
			Minecraft.getInstance().level.getRecipeManager().byKey(vid).ifPresent(vr -> {
				if (vr.value() instanceof VeinRecipe) {
					veinsList.add(new Vein(pos, (RecipeHolder<VeinRecipe>) vr, size, hide));
				}
			});
		}

		for (ResourceLocation v : tag.exclude()) {
			excluded.add(v);
		}
	}

	@Override
	public void render(GuiGraphics st, int mouseX, int mouseY, float partialTicks) {
		this.renderTransparentBackground(st);
		super.render(st, mouseX, mouseY, partialTicks);

		if (selected != null) {
			st.pose().pushPose();
			st.pose().translate(this.leftPos + 60, this.topPos + 40, 0);
			float f = 2f;
			st.pose().scale(f, f, f);
			st.renderItem(getSelectedRecipe().icon, 0, 0);
			st.pose().popPose();

			var t = getSelectedRecipe().veinName;
			st.drawString(font, t, this.leftPos + 75 - font.width(t) / 2, this.topPos + 80, 4210752, false);

			long size = getVeinSize();
			if (size == 0)
				t = Component.translatable("info.coe.atlas.vein_size.infinite");
			else
				t = Component.literal("~" + NumberFormatter.formatNumber(size));
			t = Component.translatable("info.coe.atlas.vein_size", t);
			st.drawString(font, t, this.leftPos + 15, this.topPos + 100, 4210752, false);

			t = Component.translatable("info.coe.atlas.location");
			st.drawString(font, t, this.leftPos + 15, this.topPos + 110, 4210752, false);
			t = Component.translatable("info.coe.atlas.location2", Math.round((selected.pos.x() * 16 + 8) / 10f) * 10, Math.round((selected.pos.z() * 16 + 8) / 10f) * 10);
			st.drawString(font, t, this.leftPos + 25, this.topPos + 120, 4210752, false);
			t = Component.translatable("info.coe.atlas.dimension", selected.pos.dimension().location().toString());
			st.drawString(font, t, this.leftPos + 15, this.topPos + 130, 4210752, false);
		}
	}

	private VeinRecipe getSelectedRecipe() {
		return selected.recipe.value();
	}

	private long getVeinSize() {
		if (getSelectedRecipe().isFinite() != ThreeState.NEVER) {
			if (getSelectedRecipe().isFinite() == ThreeState.DEFAULT && menu.isDefaultInfinite())return 0L;
			double mul = (getSelectedRecipe().getMaxAmount() - getSelectedRecipe().getMinAmount()) * selected.size + getSelectedRecipe().getMinAmount();
			long am = Math.round(mul * menu.getFiniteBase());
			return am;
		}
		return 0L;
	}

	@Override
	protected void renderLabels(GuiGraphics gr, int pMouseX, int pMouseY) {
		gr.drawString(font, title, 75 - font.width(title) / 2, 16, 4210752, false);
		gr.drawString(font, veinTypesTitle, 205 - font.width(veinTypesTitle) / 2, 16, 4210752, false);
	}

	private static class Vein {
		private final DimChunkPos pos;
		private final RecipeHolder<VeinRecipe> recipe;
		private final float size;
		private boolean hidden;

		public Vein(DimChunkPos pos, RecipeHolder<VeinRecipe> recipe, float size, boolean hidden) {
			this.pos = pos;
			this.recipe = recipe;
			this.size = size;
			this.hidden = hidden;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			result = prime * result + ((recipe == null) ? 0 : recipe.id().hashCode());
			result = prime * result + Float.floatToIntBits(size);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Vein other = (Vein) obj;
			if (pos == null) {
				if (other.pos != null) return false;
			} else if (!pos.equals(other.pos)) return false;
			if (recipe == null) {
				if (other.recipe != null) return false;
			} else if (!recipe.id().equals(other.recipe.id())) return false;
			if (Float.floatToIntBits(size) != Float.floatToIntBits(other.size)) return false;
			return true;
		}
	}

	private static record VeinWidgetHolder(VeinInfoButton btn) {}

	private class VeinsListWidget extends PagedListWidget<VeinWidgetHolder, Vein> {

		public VeinsListWidget(int x, int y, int w, int h) {
			super(x, y, w, h, 20);
		}

		@Override
		protected <W extends AbstractWidget> W addWidgetToGUI(W w) {
			return addRenderableWidget(w);
		}

		@Override
		protected VeinWidgetHolder makeElement(int x, int y) {
			return new VeinWidgetHolder(addRenderableWidget(new VeinInfoButton(x, y, 110)));
		}

		@Override
		protected void updateElement(VeinWidgetHolder element, Vein data) {
			element.btn.visible = data != null;
			if (data != null) {
				element.btn.setVein(data);
			}
		}
	}

	private class VeinTypeButton extends Button {
		protected RecipeHolder<VeinRecipe> recipe;

		private VeinTypeButton(int pX, int pY, int pWidth, int pHeight) {
			super(pX, pY, pWidth, pHeight, Component.empty(), NULL_PRESS, DEFAULT_NARRATION);
		}

		protected VeinRecipe getRecipe() {
			return recipe.value();
		}

		@Override
		protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
			Minecraft minecraft = Minecraft.getInstance();
			pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			pGuiGraphics.blitSprite(BUTTON_SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
			pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			int i = isHovered ? 0xFCFCA0 : 0xC0C0C0;
			this.renderScrollingText(pGuiGraphics, minecraft.font, 20, i | Mth.ceil(this.alpha * 255.0F) << 24);
			renderItem(pGuiGraphics);
		}

		protected void renderItem(GuiGraphics pGuiGraphics) {
			pGuiGraphics.renderItem(getRecipe().icon, getX() + 1, getY() + 1);
		}

		protected void renderScrollingText(GuiGraphics pGuiGraphics, Font pFont, int pWidth, int pColor) {
			int i = this.getX() + pWidth;
			int j = this.getX() + this.getWidth();
			renderScrollingString(pGuiGraphics, pFont, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), pColor);
		}

		public Tooltip createTooltip() {
			return null;
		}

		public void updateTooltip() {
			setTooltip(createTooltip());
		}

		@Override
		public void onPress() {
		}
	}

	private class ToggleHideButton extends Button {
		private boolean hideMode;

		private ToggleHideButton(int pX, int pY, Button.OnPress pOnPress) {
			super(pX, pY, 16, 16, Component.empty(), pOnPress, DEFAULT_NARRATION);
		}

		@Override
		protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
			pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			pGuiGraphics.blitSprite(BUTTON_SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
			pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
			pGuiGraphics.blitSprite(hideMode ? HIDE : SHOW, this.getX(), this.getY(), 16, 16);
		}
	}

	private class VeinInfoButton extends VeinTypeButton {
		private Vein vein;

		private VeinInfoButton(int pX, int pY, int pWidth) {
			super(pX, pY, pWidth, 20);
		}

		@Override
		public void onPress() {
			discovered.setVisible(false);
			backButton.active = true;
			setSelected(vein);
		}

		public void setVein(Vein vein) {
			this.vein = vein;
			recipe = vein.recipe;
			setMessage(getRecipe().getName());
		}
	}

	private class VeinTargetButton extends VeinTypeButton {
		private Tooltip TT_TARGET;
		private Tooltip TT_SWITCH_TARGET;
		private Tooltip TT_SET_TARGET;

		private VeinTargetButton(int pX, int pY, int pWidth) {
			super(pX, pY, pWidth, 10);
			TT_TARGET = Tooltip.create(Component.translatable("tooltip.coe.atlas.target"));
			TT_SWITCH_TARGET = Tooltip.create(Component.translatable("tooltip.coe.atlas.switch_target"));
			TT_SET_TARGET = Tooltip.create(Component.translatable("tooltip.coe.atlas.set_target"));
		}

		@Override
		protected void renderItem(GuiGraphics pGuiGraphics) {
			pGuiGraphics.pose().pushPose();
			pGuiGraphics.pose().translate(getX() + 1, getY() + 1, 0);
			float f = 0.5f;
			pGuiGraphics.pose().scale(f, f, f);
			pGuiGraphics.renderItem(getRecipe().icon, 0, 0);
			pGuiGraphics.pose().popPose();

			if (target != null && !target.isEmpty() && target.equals(recipe.id().toString())) {
				pGuiGraphics.blitSprite(TARGET, this.getX() + 10, this.getY() + 1, 8, 8);
			}
		}

		@Override
		public Tooltip createTooltip() {
			if(target != null && !target.isEmpty()) {
				if (target.equals(recipe.id().toString()))
					return TT_TARGET;
				else
					return TT_SWITCH_TARGET;
			} else {
				return TT_SET_TARGET;
			}
		}

		@Override
		public void onPress() {
			if(target != null && !target.isEmpty()) {
				if (target.equals(recipe.id().toString()))target(null);
				else target(recipe);
			} else {
				target(recipe);
			}
		}
	}

	private class VeinExcludeButton extends Button {
		private RecipeHolder<VeinRecipe> data;
		private Tooltip TT_EXCLUDE;
		private Tooltip TT_INCLUDE;

		protected VeinExcludeButton(int pX, int pY) {
			super(pX, pY, 10, 10, Component.empty(), NULL_PRESS, DEFAULT_NARRATION);
			TT_EXCLUDE = Tooltip.create(Component.translatable("tooltip.coe.atlas.exclude"));
			TT_INCLUDE = Tooltip.create(Component.translatable("tooltip.coe.atlas.include"));
		}

		@Override
		protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
			pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			pGuiGraphics.blitSprite(ACTION_BUTTON_SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
			pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

			if (excluded.contains(data.id())) {
				pGuiGraphics.blitSprite(EXCLUDE, this.getX() + 1, this.getY() + 1, 8, 8);
			}
		}

		@Override
		public void onPress() {
			if(excluded.contains(data.id())) {
				exclude(data, false);
			} else {
				exclude(data, true);
			}
		}

		public Tooltip createTooltip() {
			if(excluded.contains(data.id())) {
				return TT_EXCLUDE;
			} else {
				return TT_INCLUDE;
			}
		}

		public void updateTooltip() {
			setTooltip(createTooltip());
		}
	}

	private static record VeinTypeWidgetHolder(VeinTargetButton btn, VeinExcludeButton trg) {}

	private class VeinTypesListWidget extends PagedListWidget<VeinTypeWidgetHolder, RecipeHolder<VeinRecipe>> {

		public VeinTypesListWidget(int x, int y, int w, int h) {
			super(x, y, w, h, 10);
		}

		@Override
		protected <W extends AbstractWidget> W addWidgetToGUI(W w) {
			return addRenderableWidget(w);
		}

		@Override
		protected VeinTypeWidgetHolder makeElement(int x, int y) {
			return new VeinTypeWidgetHolder(
					addRenderableWidget(new VeinTargetButton(x, y, 100)),
					addRenderableWidget(new VeinExcludeButton(x + 100, y))
					);
		}

		@Override
		protected void updateElement(VeinTypeWidgetHolder element, RecipeHolder<VeinRecipe> data) {
			element.btn.visible = data != null;
			element.trg.visible = data != null;
			if (data != null) {
				element.btn.recipe = data;
				element.btn.setMessage(data.value().getName());
				element.btn.updateTooltip();
				element.trg.data = data;
				element.trg.updateTooltip();
			}
		}
	}

	public void exclude(RecipeHolder<VeinRecipe> data, boolean ex) {
		if (ex) {
			excluded.add(data.id());
			target = null;
			send(Option.ADD_EXCLUDE, data.id());
		} else {
			excluded.remove(data.id());
			send(Option.REMOVE_EXCLUDE, data.id());
		}
		veinTypes.updateContent();
	}

	private void send(Option opt, ResourceLocation id) {
		NetworkHandler.sendDataToServer(new OreVeinAtlasClickPacket(opt, id));
	}

	private void send2(Option opt, int id) {
		NetworkHandler.sendDataToServer(new OreVeinAtlasClickPacket(opt, id));
	}

	public void target(RecipeHolder<VeinRecipe> data) {
		if (data != null) {
			target = data.id().toString();
			send(Option.SET_TARGET, data.id());
		} else {
			target = null;
			send(Option.REMOVE_TARGET, null);
		}
		veinTypes.updateContent();
	}
}
