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

package net.aoba.gui.screens.alts;

import net.aoba.Aoba;
import net.aoba.altmanager.Alt;
import net.aoba.utils.render.TextureBank;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class AltScreen extends Screen {
	protected static final CubeMapRenderer AOBA_PANORAMA_RENDERER = new CubeMapRenderer(TextureBank.mainmenu_panorama);
	protected static final RotatingCubeMapRenderer AOBA_ROTATING_PANORAMA_RENDERER = new RotatingCubeMapRenderer(AOBA_PANORAMA_RENDERER);
	
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

        this.altListSelector = new AltSelectionList(this, this.client, this.width, this.height, 32, 36);
        this.altListSelector.updateAlts();
        this.altListSelector.setDimensionsAndPosition(this.width, this.height - 64 - 32, 0, 32);
        this.addDrawableChild(this.altListSelector);

        this.deleteButton = ButtonWidget.builder(Text.of("Delete Alt"), b -> this.deleteSelected())
                .dimensions(this.width / 2 - 154, this.height - 28, 100, 20).build();
        this.deleteButton.active = false;
        this.addDrawableChild(this.deleteButton);

        this.addDrawableChild(ButtonWidget.builder(Text.of("Direct Login"), b -> client.setScreen(new DirectLoginAltScreen(this)))
                .dimensions(this.width / 2 - 50, this.height - 52, 100, 20).build());


        this.addDrawableChild(ButtonWidget.builder(Text.of("Add Alt"), b -> client.setScreen(new AddAltScreen(this)))
                .dimensions(this.width / 2 + 4 + 50, this.height - 52, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> client.setScreen(this.parentScreen))
                .dimensions(this.width / 2 + 54, this.height - 28, 100, 20).build());


        this.editButton = ButtonWidget.builder(Text.of("Edit Alt"), b -> this.editSelected())
                .dimensions(this.width / 2 - 50, this.height - 28, 100, 20).build();
        this.editButton.active = false;
        this.addDrawableChild(this.editButton);


        this.addDrawableChild(ButtonWidget.builder(Text.of("MCLeaks Login"), b -> client.setScreen(new MCLeaksLoginScreen(this)))
                .dimensions(this.width / 2 - 154, this.height - 52, 100, 20).build());
    }

    @Override
    public void tick() {
        AltSelectionList.Entry altselectionlist$entry = this.altListSelector.getSelectedOrNull();
        if (altselectionlist$entry == null)
            return;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(drawContext, mouseX, mouseY, partialTicks);
        this.altListSelector.render(drawContext, mouseX, mouseY, partialTicks);

        super.render(drawContext, mouseX, mouseY, partialTicks);
        drawContext.drawCenteredTextWithShadow(textRenderer, "Currently Logged Into: " + MinecraftClient.getInstance().getSession().getUsername(),
                this.width / 2, 20, 16777215);
    }

    public List<Alt> getAltList() {
        return Aoba.getInstance().altManager.getAlts();
    }

    public void refreshAltList() {
        this.client.setScreen(new AltScreen(this.parentScreen));
    }

    public void setSelected(AltSelectionList.Entry selected) {
        this.altListSelector.setSelected(selected);
        this.setEdittable();
    }

    protected void setEdittable() {
        this.editButton.active = true;
        this.deleteButton.active = true;
    }

    public void loginToSelected() {
        AltSelectionList.Entry altselectionlist$entry = this.altListSelector.getSelectedOrNull();
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
        Alt alt = ((AltSelectionList.NormalEntry) this.altListSelector.getSelectedOrNull()).getAltData();
        if (alt == null) {
            return;
        }
        client.setScreen(new EditAltScreen(this, alt));
    }

    public void deleteSelected() {
        Alt alt = ((AltSelectionList.NormalEntry) this.altListSelector.getSelectedOrNull()).getAltData();
        if (alt == null) {
            return;
        }
        Aoba.getInstance().altManager.removeAlt(alt);
        this.refreshAltList();
    }
    
	@Override
	protected void renderPanoramaBackground(DrawContext context, float delta) {
		AOBA_ROTATING_PANORAMA_RENDERER.render(context, this.width, this.height, 1.0f, delta);
	}
}
