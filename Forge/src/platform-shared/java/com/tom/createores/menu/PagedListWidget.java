package com.tom.createores.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class PagedListWidget<D, T> extends AbstractWidget {
	private PageButton forwardButton;
	private PageButton backButton;
	private int wh;
	private int page;

	protected Supplier<List<T>> list;
	protected T selected;
	private List<ListEntry> listEntries = new ArrayList<>();

	public PagedListWidget(int x, int y, int w, int h, int wh) {
		super(x, y, w, h, Component.empty());
		this.forwardButton = this.addWidgetToGUI(new PageButton(x + w - 24, y + h - 12, true, p_98297_ -> this.pageForward(), true));
		this.backButton = this.addWidgetToGUI(new PageButton(x + 4, y + h - 12, false, p_98287_ -> this.pageBack(), true));
		this.wh = wh;
		int wc = getLines();
		for (int i = 0;i<wc;i++) {
			listEntries.add(new ListEntry(i, makeElement(x, y + i * wh)));
		}
	}

	private void pageBack() {
		if (page > 0) {
			page--;
			updateContent();
		}
	}

	private void pageForward() {
		if ((page + 1) * getLines() < list.get().size()) {
			page++;
			updateContent();
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
	}

	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		if (visible) {
			Minecraft minecraft = Minecraft.getInstance();
			Component c = Component.literal((page + 1) + " / " + (Math.max(Mth.ceil(list.get().size() / (float) getLines()), 1)));
			pGuiGraphics.drawString(minecraft.font, c, getX() + getWidth() / 2 - minecraft.font.width(c) / 2, getY() + getHeight() - 8, 4210752, false);
		}
	}

	protected abstract <W extends AbstractWidget> W addWidgetToGUI(W w);
	protected abstract D makeElement(int x, int y);
	protected abstract void updateElement(D element, T data);

	public void updateContent() {
		listEntries.forEach(ListEntry::update);
		page = Mth.clamp(page, 0, Math.max(Mth.ceil(list.get().size() / (float) getLines()), 0));
		backButton.active = page > 0;
		forwardButton.active = (page + 1) * getLines() < list.get().size();
	}

	public int getLines() {
		return (height - 16) / wh;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		backButton.visible = visible;
		forwardButton.visible = visible;
		listEntries.forEach(ListEntry::update);
	}

	private class ListEntry {
		private final int id;
		private final D disp;

		public ListEntry(int id, D disp) {
			this.id = id;
			this.disp = disp;
		}

		private T getId() {
			if (!visible)return null;
			List<T> l = list.get();
			int j = page * getLines();
			if (j < 0) {
				j = 0;
			}
			if(this.id + j < l.size()) {
				return l.get(this.id + j);
			}
			return null;
		}

		private void update() {
			updateElement(disp, getId());
		}
	}
}
