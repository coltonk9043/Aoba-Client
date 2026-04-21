/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation.huds;

import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.navigation.HudWindow;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.ResizeMode;
import net.aoba.rendering.Renderer2D;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingHud extends HudWindow {
	private String pingText = null;

	public PingHud(int x, int y) {
		super("PingHud", x, y, 50, 24);
		setProperty(UIElement.MinWidthProperty, 50f);
		setProperty(UIElement.MinHeightProperty, 20f);
		setProperty(UIElement.MaxHeightProperty, 20f);
		resizeMode = ResizeMode.None;
	}

	@Override
	public void update() {
		ClientPacketListener networkHandler = MC.getConnection();
		if (networkHandler != null && MC.player != null) {
			PlayerInfo entry = networkHandler.getPlayerInfo(MC.player.getUUID());
			if (entry != null) {
				int ping = entry.getLatency();
				pingText = "Ping: " + ping + " ms";
			} else {
				pingText = "Ping: ?";
			}
		} else
			pingText = null;
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		super.draw(renderer, partialTicks);

		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		if (pingText != null && isVisible) {
			Rectangle pos = position.getValue();
			renderer.drawString(pingText, pos.x(), pos.y(),
					GuiManager.foregroundColor.getValue(), GuiManager.fontSetting.getValue().getRenderer());
		}
	}
}
