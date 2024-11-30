/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.addons;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.aoba.AobaClient;
import net.aoba.api.IAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AddonSelectionList extends AlwaysSelectedEntryListWidget<AddonSelectionList.Entry> {
	private final List<NormalEntry> addonList = new ArrayList<AddonSelectionList.NormalEntry>();

	private AddonScreen parent;

	public AddonSelectionList(AddonScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
		parent = ownerIn;
		updateAddonList();
	}

	public void updateAddonList() {
		this.clearEntries();
		for (IAddon addon : AobaClient.addons) {
			AddonSelectionList.NormalEntry entry = new AddonSelectionList.NormalEntry(this, addon);
			addonList.add(entry);
		}
		this.addonList.forEach(this::addEntry);
	}

	public List<NormalEntry> getAddons() {
		return this.addonList;
	}

	public void setSelected(@Nullable NormalEntry entry) {
		super.setSelected(entry);
	}

	public void onClickEntry(@Nullable NormalEntry entry) {
		parent.setSelected(entry);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		Entry AltSelectionList$entry = this.getSelectedOrNull();
		return AltSelectionList$entry != null && AltSelectionList$entry.keyPressed(keyCode, scanCode, modifiers)
				|| super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Environment(value = EnvType.CLIENT)
	public static abstract class Entry extends AlwaysSelectedEntryListWidget.Entry<Entry> implements AutoCloseable {
		@Override
		public void close() {
		}
	}

	public class NormalEntry extends Entry {
		private final AddonSelectionList owner;
		private final MinecraftClient mc;
		private final Identifier iconIdentifier;
		private final IAddon addon;

		protected NormalEntry(AddonSelectionList ownerIn, IAddon addon) {
			this.owner = ownerIn;
			this.addon = addon;
			this.mc = MinecraftClient.getInstance();

			Optional<String> iconPathOptional = addon.getIcon();
			if (!iconPathOptional.isEmpty()) {
				String iconPath = iconPathOptional.get().replaceFirst("assets/", "");
				int firstDirectory = iconPath.indexOf('/');
				String modNamespace = iconPath.substring(0, firstDirectory);
				String modIconPath = iconPath.substring(firstDirectory + 1);
				this.iconIdentifier = Identifier.of(modNamespace, modIconPath);
			} else
				this.iconIdentifier = null;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean hovered, float tickDelta) {
			// Draws the strings onto the screen.
			TextRenderer textRenderer = this.mc.textRenderer;

			drawContext.drawTexture(RenderLayer::getGuiTextured, iconIdentifier, x + 8, y + 8, 0, 0, 32, 32, 32, 32);
			drawContext.drawTextWithShadow(textRenderer, addon.getName(), (x + 54), y + 10, 16777215);
			drawContext.drawTextWithShadow(textRenderer, addon.getDescription(), (x + 54), y + 22, 16777215);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d0 = mouseX - (double) AddonSelectionList.this.getRowLeft();

			if (d0 < 32.0D && d0 > 16.0D) {
				this.owner.onClickEntry(this);
				return true;
			}
			this.owner.onClickEntry(this);
			return false;
		}

		@Override
		public Text getNarration() {
			return Text.of(addon.getName());
		}

		public IAddon getAddon() {
			return this.addon;
		}

		public Identifier getIcon() {
			return this.iconIdentifier;
		}
	}
}
