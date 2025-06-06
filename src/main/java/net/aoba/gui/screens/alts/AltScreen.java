/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.alts;

import java.util.List;

import net.aoba.Aoba;
import net.aoba.managers.altmanager.Alt;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AltScreen extends Screen {
	protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
	protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(
			AOBA_PANORAMA_RENDERER);

	private final Screen parentScreen;
	private ButtonWidget editButton;
	private ButtonWidget deleteButton;
	private AltSelectionList altListSelector;

	public AltScreen(Screen parentScreen) {
		super(Text.of("Alt Manager"));
		this.parentScreen = parentScreen;
	}

	public void init() {
		super.init();

		altListSelector = new AltSelectionList(this, client, width, height, 32, 36);
		altListSelector.updateAlts();
		altListSelector.setDimensionsAndPosition(width, height - 64 - 32, 0, 32);
		addDrawableChild(altListSelector);

		deleteButton = ButtonWidget.builder(Text.of("Delete Alt"), b -> deleteSelected())
				.dimensions(width / 2 - 154, height - 28, 100, 20).build();
		deleteButton.active = false;
		addDrawableChild(deleteButton);

		addDrawableChild(
				ButtonWidget.builder(Text.of("Direct Login"), b -> client.setScreen(new DirectLoginAltScreen(this)))
						.dimensions(width / 2 - 50, height - 52, 100, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.of("Add Alt"), b -> client.setScreen(new AddAltScreen(this)))
				.dimensions(width / 2 + 4 + 50, height - 52, 100, 20).build());

		addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(parentScreen))
				.dimensions(width / 2 + 54, height - 28, 100, 20).build());

		editButton = ButtonWidget.builder(Text.of("Edit Alt"), b -> editSelected())
				.dimensions(width / 2 - 50, height - 28, 100, 20).build();
		editButton.active = false;
		addDrawableChild(editButton);
	}

	@Override
	public void tick() {
		AltSelectionList.Entry altselectionlist$entry = altListSelector.getSelectedOrNull();
		if (altselectionlist$entry == null) {
		}
	}

	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
		renderBackground(drawContext, mouseX, mouseY, partialTicks);
		altListSelector.render(drawContext, mouseX, mouseY, partialTicks);

		super.render(drawContext, mouseX, mouseY, partialTicks);
		drawContext.drawCenteredTextWithShadow(textRenderer,
				"Currently Logged Into: " + MinecraftClient.getInstance().getSession().getUsername(), width / 2, 20,
				16777215);
	}

	public List<Alt> getAltList() {
		return Aoba.getInstance().altManager.getAlts();
	}

	public void refreshAltList() {
		client.setScreen(new AltScreen(parentScreen));
	}

	public void setSelected(AltSelectionList.Entry selected) {
		altListSelector.setSelected(selected);
		setEdittable();
	}

	protected void setEdittable() {
		editButton.active = true;
		deleteButton.active = true;
	}

	public void loginToSelected() {
		AltSelectionList.Entry altselectionlist$entry = altListSelector.getSelectedOrNull();
		if (altselectionlist$entry == null) {
			return;
		}

		Alt alt = ((AltSelectionList.NormalEntry) altselectionlist$entry).getAltData();
		if (alt.isCracked()) {
			Aoba.getInstance().altManager.loginCracked(alt.getEmail());
		} else {
			Aoba.getInstance().altManager.login(alt);
		}
	}

	public void editSelected() {
		Alt alt = ((AltSelectionList.NormalEntry) altListSelector.getSelectedOrNull()).getAltData();
		if (alt == null) {
			return;
		}
		client.setScreen(new EditAltScreen(this, alt));
	}

	public void deleteSelected() {
		Alt alt = ((AltSelectionList.NormalEntry) altListSelector.getSelectedOrNull()).getAltData();
		if (alt == null) {
			return;
		}
		Aoba.getInstance().altManager.removeAlt(alt);
		refreshAltList();
	}

	@Override
	protected void renderPanoramaBackground(DrawContext context, float delta) {
		// AOBA_ROTATING_PANORAMA_RENDERER.render(context, width, height, 1.0f, delta);
	}
}
