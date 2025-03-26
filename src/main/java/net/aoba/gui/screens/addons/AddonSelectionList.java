/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
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

	private final AddonScreen parent;

	public AddonSelectionList(AddonScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
		parent = ownerIn;
		updateAddonList();
	}

	public void updateAddonList() {
		clearEntries();
		for (IAddon addon : AobaClient.addons) {
			AddonSelectionList.NormalEntry entry = new AddonSelectionList.NormalEntry(this, addon);
			addonList.add(entry);
		}
		addonList.forEach(this::addEntry);
	}

	public List<NormalEntry> getAddons() {
		return addonList;
	}

	public void setSelected(@Nullable NormalEntry entry) {
		super.setSelected(entry);
	}

	public void onClickEntry(@Nullable NormalEntry entry) {
		parent.setSelected(entry);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		Entry AltSelectionList$entry = getSelectedOrNull();
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
			owner = ownerIn;
			this.addon = addon;
			mc = MinecraftClient.getInstance();

			Optional<String> iconPathOptional = addon.getIcon();
			if (!iconPathOptional.isEmpty()) {
				String iconPath = iconPathOptional.get().replaceFirst("assets/", "");
				int firstDirectory = iconPath.indexOf('/');
				String modNamespace = iconPath.substring(0, firstDirectory);
				String modIconPath = iconPath.substring(firstDirectory + 1);
				iconIdentifier = Identifier.of(modNamespace, modIconPath);
			} else
				iconIdentifier = null;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean hovered, float tickDelta) {
			// Draws the strings onto the screen.
			TextRenderer textRenderer = mc.textRenderer;

			drawContext.fill(x + 7, y + 7, x + 41, y + 41, 0xFFFFFFFF);
			drawContext.fill(x + 8, y + 8, x + 40, y + 40, 0xFF000000);

			drawContext.drawTexture(RenderLayer::getGuiTextured, iconIdentifier, x + 8, y + 8, 0, 0, 32, 32, 32, 32);
			drawContext.drawTextWithShadow(textRenderer, addon.getName(), (x + 54), y + 10, 16777215);
			drawContext.drawTextWithShadow(textRenderer, addon.getDescription(), (x + 54), y + 22, 16777215);

		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d0 = mouseX - (double) getRowLeft();

			if (d0 < 32.0D && d0 > 16.0D) {
				owner.onClickEntry(this);
				return true;
			}
			owner.onClickEntry(this);
			return false;
		}

		@Override
		public Text getNarration() {
			return Text.of(addon.getName());
		}

		public IAddon getAddon() {
			return addon;
		}

		public Identifier getIcon() {
			return iconIdentifier;
		}
	}
}
