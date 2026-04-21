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
import com.mojang.authlib.GameProfile;

import net.aoba.managers.altmanager.Alt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class AltSelectionList extends ObjectSelectionList<AltSelectionList.Entry> {
	private final AltScreen owner;
	private final List<NormalEntry> altList = new ArrayList<AltSelectionList.NormalEntry>();

	public AltSelectionList(AltScreen ownerIn, Minecraft minecraftClient, int i, int j, int k, int l) {
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

	public void setSelected(@Nullable AltSelectionList.Entry entry) {
		super.setSelected(entry);
		if (entry != null) {
			owner.setEdittable();
		}
	}

	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		Entry entry = getSelected();
		return entry != null && entry.keyPressed(keyEvent) || super.keyPressed(keyEvent);
	}

	@Environment(value = EnvType.CLIENT)
	public static abstract class Entry extends ObjectSelectionList.Entry<AltSelectionList.Entry> implements AutoCloseable {
		@Override
		public void close() {
		}
	}

	public class NormalEntry extends AltSelectionList.Entry {
		private final AltScreen owner;
		private final Minecraft mc;
		private final Alt alt;
		private long lastClickTime;
		private PlayerInfo entry;

		protected NormalEntry(AltScreen ownerIn, Alt alt) {
			owner = ownerIn;
			this.alt = alt;
			mc = Minecraft.getInstance();

			try {
				String name = alt.getUsername();
				if (name.isEmpty()) {
					name = "Steve";
				}

				UUID uuid = UUIDUtil.createOfflinePlayerUUID(name);
                entry = new PlayerInfo(new GameProfile(uuid, name), false);

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

		private void drawHead(GuiGraphicsExtractor drawContext, int x, int y) {
			PlayerFaceExtractor.extractRenderState(drawContext, entry.getSkin(), x, y, 24);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
			owner.setSelected(this);
			if (Util.getMillis() - lastClickTime < 250L) {
				owner.loginToSelected();
			}
			lastClickTime = Util.getMillis();
			return true;
		}

		@Override
		public Component getNarration() {
			return Component.nullToEmpty(alt.getUsername());
		}

		@Override
		public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
			int x = getX();
			int y = getY();

			String description;

			// Generates the description of an alt.
			if (alt.isCracked()) {
				description = "Cracked Account";
			} else {
				description = "Microsoft Account";
			}

			// Draws the strings onto the screen.
			Font textRenderer = mc.font;
			graphics.text(textRenderer, "Username: " + alt.getEmail(), (x + 32 + 3), (y + 2),
					0xFFFFFFFF);
			graphics.text(textRenderer, description, (x + 32 + 3), (y + 12),
					alt.isCracked() ? 0xFFFF0000 : 0xFF00FF00, true);

			// Draws the respective player head.
			drawHead(graphics, x + 4, y + 4);
		}
	}
}
