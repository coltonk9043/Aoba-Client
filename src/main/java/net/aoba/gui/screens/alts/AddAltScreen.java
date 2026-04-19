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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AddAltScreen extends Screen {

    private final AltScreen parent;

    private Button buttonAddAlt;
    private Checkbox toggleCracked;
    private EditBox textFieldAltUsername;
    
    public AddAltScreen(AltScreen parentScreen) {
        super(Component.nullToEmpty("Alt Manager"));
        parent = parentScreen;
    }

    public void init() {
        super.init();

        textFieldAltUsername = new EditBox(font, width / 2 - 100, height / 2 - 36, 200, 20,
                Component.nullToEmpty("Enter Name"));
        textFieldAltUsername.setValue("");
        addRenderableWidget(textFieldAltUsername);

        toggleCracked = Checkbox.builder(Component.nullToEmpty("Cracked Account?"), font).pos(width / 2 - 100, height / 2 - 12).build();
        addRenderableWidget(toggleCracked);

        buttonAddAlt = Button.builder(Component.nullToEmpty("Add Alt"), b -> onButtonAltAddPressed())
                .bounds(width / 2 - 100, height / 2 + 24, 200, 20).build();
        addRenderableWidget(buttonAddAlt);

        addRenderableWidget(Button.builder(Component.nullToEmpty("Cancel"), b -> onButtonCancelPressed())
                .bounds(width / 2 - 100, height / 2 + 46, 200, 20).build());
    }

    @Override
    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(font, "Add Alternate Account", width / 2, 20, 0xFFFFFFFF);
        graphics.centeredText(font, "Username:", width / 2 - 75, height / 2 - 50, 0xFFFFFFFF);
    }

    private void onButtonAltAddPressed() {
        Alt alt = new Alt(textFieldAltUsername.getValue(), toggleCracked.selected());
        Aoba.getInstance().altManager.addAlt(alt);
        
        if(!alt.isCracked())
        	alt.auth();
        
        parent.refreshAltList();
    }

    public void onButtonCancelPressed() {
        minecraft.setScreen(parent);
    }
}
