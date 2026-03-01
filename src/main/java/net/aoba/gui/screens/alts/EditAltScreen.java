/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens.alts;

import net.aoba.Aoba;
import net.aoba.managers.altmanager.Alt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditAltScreen extends Screen {

    private final AltScreen parent;
    private final Alt alt;

    private Button buttonSaveAlt;
    private Checkbox toggleCracked;
    private EditBox textFieldAltUsername;


    public EditAltScreen(AltScreen parentScreen, Alt alt) {
        super(Component.nullToEmpty("Alt Manager"));
        parent = parentScreen;
        this.alt = alt;
    }

    public void init() {
        super.init();
        textFieldAltUsername = new EditBox(font, width / 2 - 100, height / 2 - 36, 200, 20,
                Component.nullToEmpty("Enter Name"));
        textFieldAltUsername.setValue(alt == null ? "" : alt.getEmail());
        addRenderableWidget(textFieldAltUsername);

        toggleCracked = Checkbox.builder(Component.nullToEmpty("Cracked Account?"), font).pos(width / 2 - 100, height / 2 - 12).build();
        addRenderableWidget(toggleCracked);


        buttonSaveAlt = Button.builder(Component.nullToEmpty("Save Alt"), b -> onButtonAltEditPressed())
                .bounds(width / 2 - 100, height / 2 + 24, 200, 20).build();
        addRenderableWidget(buttonSaveAlt);
        addRenderableWidget(Button.builder(Component.nullToEmpty("Cancel"), b -> onButtonCancelPressed())
                .bounds(width / 2 - 100, height / 2 + 46, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float partialTicks) {
        super.render(drawContext, mouseX, mouseY, partialTicks);
        drawContext.drawCenteredString(font, "Edit Alternate Account", width / 2, 20, 0xFFFFFFFF);
        drawContext.drawString(font, "Username:", width / 2 - 100, height / 2 - 50, 0xFFFFFFFF);
        
    }

    private void onButtonAltEditPressed() {
        alt.setEmail(textFieldAltUsername.getValue());
        Aoba.getInstance().altManager.saveAlts();
        alt.auth();
        parent.refreshAltList();
    }

    public void onButtonCancelPressed() {
        minecraft.setScreen(parent);
    }
}