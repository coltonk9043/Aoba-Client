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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class AddonSelectionList extends ObjectSelectionList<AddonSelectionList.Entry> {
	private final List<NormalEntry> addonList = new ArrayList<AddonSelectionList.NormalEntry>();

	private final AddonScreen parent;

	public AddonSelectionList(AddonScreen ownerIn, Minecraft minecraftClient, int i, int j, int k, int l) {
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
	public boolean keyPressed(net.minecraft.client.input.KeyEvent keyEvent) {
		Entry entry = getSelected();
		return entry != null && entry.keyPressed(keyEvent) || super.keyPressed(keyEvent);
	}

	@Environment(value = EnvType.CLIENT)
	public static abstract class Entry extends ObjectSelectionList.Entry<net.aoba.gui.screens.addons.AddonSelectionList.Entry> implements AutoCloseable {
		@Override
		public void close() {
		}
	}

	public class NormalEntry extends net.aoba.gui.screens.addons.AddonSelectionList.Entry {
		private final AddonSelectionList owner;
		private final Minecraft mc;
		private final Identifier iconIdentifier;
		private final IAddon addon;

		protected NormalEntry(AddonSelectionList ownerIn, IAddon addon) {
			owner = ownerIn;
			this.addon = addon;
			mc = Minecraft.getInstance();

			Optional<String> iconPathOptional = addon.getIcon();
			if (!iconPathOptional.isEmpty()) {
				String iconPath = iconPathOptional.get().replaceFirst("assets/", "");
				int firstDirectory = iconPath.indexOf('/');
				String modNamespace = iconPath.substring(0, firstDirectory);
				String modIconPath = iconPath.substring(firstDirectory + 1);
				iconIdentifier = Identifier.fromNamespaceAndPath(modNamespace, modIconPath);
			} else
				iconIdentifier = null;
		}

		@Override
		public void renderContent(GuiGraphics drawContext, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			int x = getX();
			int y = getY();

			// Draws the strings onto the screen.
			Font textRenderer = mc.font;

			drawContext.fill(x + 7, y + 7, x + 41, y + 41, 0xFFFFFFFF);
			drawContext.fill(x + 8, y + 8, x + 40, y + 40, 0xFF000000);

			drawContext.blit(iconIdentifier, x + 8, y + 8, 32, 32, 0f, 0f, 1f, 1f);
			drawContext.drawString(textRenderer, addon.getName(), (x + 54), y + 10, 0xFFFFFFFF);
			drawContext.drawString(textRenderer, addon.getDescription(), (x + 54), y + 22, 0xFFFFFFFF);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
			owner.onClickEntry(this);
			return true;
		}

		@Override
		public Component getNarration() {
			return Component.nullToEmpty(addon.getName());
		}

		public IAddon getAddon() {
			return addon;
		}

		public Identifier getIcon() {
			return iconIdentifier;
		}
	}
}
