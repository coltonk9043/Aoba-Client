/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.alts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;

import net.aoba.managers.altmanager.Alt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;

public class AltSelectionList extends AlwaysSelectedEntryListWidget<AltSelectionList.Entry> {
	private final AltScreen owner;
	private final List<NormalEntry> altList = new ArrayList<AltSelectionList.NormalEntry>();

	public AltSelectionList(AltScreen ownerIn, MinecraftClient minecraftClient, int i, int j, int k, int l) {
		super(minecraftClient, i, j, k, l);
		owner = ownerIn;
	}

	public void updateAlts() {
		clearEntries();
		for (Alt alt : owner.getAltList()) {
			AltSelectionList.NormalEntry entry = new AltSelectionList.NormalEntry(owner, alt);
			altList.add(entry);
		}
		setList();
	}

	private void setList() {
		altList.forEach(this::addEntry);
	}

	public void setSelected(@Nullable Entry entry) {
		super.setSelected(entry);
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
		private final AltScreen owner;
		private final MinecraftClient mc;
		private final Alt alt;
		private long lastClickTime;
		private PlayerListEntry entry;

		protected NormalEntry(AltScreen ownerIn, Alt alt) {
			owner = ownerIn;
			this.alt = alt;
			mc = MinecraftClient.getInstance();

			try {
				String name = alt.getUsername();
				if (name.isEmpty()) {
					name = "Steve";
				}

				UUID uuid = Uuids.getOfflinePlayerUuid(name);
                entry = new PlayerListEntry(new GameProfile(uuid, name), false);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void getAltList() {
			owner.getAltList();
		}

		public Alt getAltData() {
			return alt;
		}

		@Override
		public void render(DrawContext drawContext, int index, int y, int x, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean hovered, float tickDelta) {

			String description;

			// Generates the description of an alt.
			if (alt.isCracked()) {
				description = "Cracked Account";
			} else {
				description = "Microsoft Account";
			}

			// Draws the strings onto the screen.
			TextRenderer textRenderer = mc.textRenderer;
			drawContext.drawTextWithShadow(textRenderer, "Username: " + alt.getEmail(), (x + 32 + 3), (y + 2),
					16777215);
			drawContext.drawTextWithShadow(textRenderer, "Username: " + alt.getEmail(), (x + 32 + 3), (y + 2),
					16777215);
			drawContext.drawText(textRenderer, description, (x + 32 + 3), (y + 12),
					alt.isCracked() ? 0xFF0000 : 0x00FF00, true);

			// Draws the respective player head.
			drawHead(drawContext, x + 4, y + 4);
		}

		private void drawHead(DrawContext drawContext, int x, int y) {
			GL11.glEnable(GL11.GL_BLEND);
			RenderSystem.setShaderColor(1, 1, 1, 1);

			// Face
			int fw = 192;
			int fh = 192;
			float u = 24;
			float v = 24;

			drawContext.drawTexture(RenderLayer::getGuiTextured, entry.getSkinTextures().texture(), x, y, u, v, 24, 24,
					fw, fh);

			// Hat
			fw = 192;
			fh = 192;
			u = 120;
			v = 24;

			drawContext.drawTexture(RenderLayer::getGuiTextured, entry.getSkinTextures().texture(), x, y, u, v, 24, 24,
					fw, fh);

			GL11.glDisable(GL11.GL_BLEND);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			double d0 = mouseX - (double) getRowLeft();

			if (d0 <= 32.0D) {
				if (d0 < 32.0D && d0 > 16.0D) {
					owner.setSelected(this);
					owner.loginToSelected();
					return true;
				}
			}
			owner.setSelected(this);
			if (Util.getMeasuringTimeMs() - lastClickTime < 250L) {
				owner.loginToSelected();
			}
			lastClickTime = Util.getMeasuringTimeMs();
			return false;
		}

		@Override
		public Text getNarration() {
			return Text.of(alt.getUsername());
		}
	}
}
