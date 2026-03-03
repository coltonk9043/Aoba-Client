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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;

public class AltScreen extends Screen {
	protected static final CubeMap AOBA_PANORAMA_RENDERER = new CubeMap(TextureBank.mainmenu_panorama);
	protected static final PanoramaRenderer AOBA_ROTATING_PANORAMA_RENDERER = new PanoramaRenderer(
			AOBA_PANORAMA_RENDERER);

	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 64);
	private final Screen parentScreen;
	private Button editButton;
	private Button deleteButton;
	private AltSelectionList altListSelector;

	public AltScreen(Screen parentScreen) {
		super(Component.nullToEmpty("Alt Manager"));
		this.parentScreen = parentScreen;
	}

	public void init() {
		super.init();

		altListSelector = new AltSelectionList(this, minecraft, width, height, 32, 36);
		altListSelector.updateAlts();
		layout.addToContents(altListSelector);

		LinearLayout topRow = LinearLayout.horizontal().spacing(4);
		topRow.addChild(Button.builder(Component.nullToEmpty("Direct Login"), b -> minecraft.setScreen(new DirectLoginAltScreen(this)))
				.width(100).build());
		topRow.addChild(Button.builder(Component.nullToEmpty("Add Alt"), b -> minecraft.setScreen(new AddAltScreen(this)))
				.width(100).build());

		LinearLayout bottomRow = LinearLayout.horizontal().spacing(4);
		deleteButton = Button.builder(Component.nullToEmpty("Delete Alt"), b -> deleteSelected())
				.width(100).build();
		deleteButton.active = false;
		bottomRow.addChild(deleteButton);

		editButton = Button.builder(Component.nullToEmpty("Edit Alt"), b -> editSelected())
				.width(100).build();
		editButton.active = false;
		bottomRow.addChild(editButton);

		bottomRow.addChild(Button.builder(Component.nullToEmpty("Cancel"), b -> minecraft.setScreen(parentScreen))
				.width(100).build());

		LinearLayout footer = layout.addToFooter(LinearLayout.vertical().spacing(4));
		footer.addChild(topRow);
		footer.addChild(bottomRow);

		layout.arrangeElements();
		layout.visitWidgets(this::addRenderableWidget);
		altListSelector.updateSize(width, layout);
	}

	@Override
	protected void repositionElements() {
		layout.arrangeElements();
		if (altListSelector != null) {
			altListSelector.updateSize(width, layout);
		}
	}

	@Override
	public void tick() {
		AltSelectionList.Entry altselectionlist$entry = altListSelector.getSelected();
		if (altselectionlist$entry == null) {
		}
	}

	@Override
	public void render(GuiGraphics drawContext, int mouseX, int mouseY, float partialTicks) {
		super.render(drawContext, mouseX, mouseY, partialTicks);
		drawContext.drawCenteredString(font,
				"Currently Logged Into: " + Minecraft.getInstance().getUser().getName(), width / 2, 20,
				0xFFFFFFFF);
	}

	public List<Alt> getAltList() {
		return Aoba.getInstance().altManager.getAlts();
	}

	public void refreshAltList() {
		minecraft.setScreen(new AltScreen(parentScreen));
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
		AltSelectionList.Entry altselectionlist$entry = altListSelector.getSelected();
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
		Alt alt = ((AltSelectionList.NormalEntry) altListSelector.getSelected()).getAltData();
		if (alt == null) {
			return;
		}
		minecraft.setScreen(new EditAltScreen(this, alt));
	}

	public void deleteSelected() {
		Alt alt = ((AltSelectionList.NormalEntry) altListSelector.getSelected()).getAltData();
		if (alt == null) {
			return;
		}
		Aoba.getInstance().altManager.removeAlt(alt);
		refreshAltList();
	}

	@Override
	protected void renderPanorama(GuiGraphics context, float delta) {
		AOBA_ROTATING_PANORAMA_RENDERER.render(context, width, height, true);
	}
}
