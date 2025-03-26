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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AddAltScreen extends Screen {

    private final AltScreen parent;

    private ButtonWidget buttonAddAlt;
    private CheckboxWidget toggleCracked;
    private TextFieldWidget textFieldAltUsername;
    
    public AddAltScreen(AltScreen parentScreen) {
        super(Text.of("Alt Manager"));
        parent = parentScreen;
    }

    public void init() {
        super.init();

        textFieldAltUsername = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 36, 200, 20,
                Text.of("Enter Name"));
        textFieldAltUsername.setText("");
        addDrawableChild(textFieldAltUsername);

        toggleCracked = CheckboxWidget.builder(Text.of("Cracked Account?"), textRenderer).pos(width / 2 - 100, height / 2 - 12).build();
        addDrawableChild(toggleCracked);

        buttonAddAlt = ButtonWidget.builder(Text.of("Add Alt"), b -> onButtonAltAddPressed())
                .dimensions(width / 2 - 100, height / 2 + 24, 200, 20).build();
        addDrawableChild(buttonAddAlt);

        addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> onButtonCancelPressed())
                .dimensions(width / 2 - 100, height / 2 + 46, 200, 20).build());
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        drawContext.drawCenteredTextWithShadow(textRenderer, "Add Alternate Account", width / 2, 20, 16777215);
        drawContext.drawCenteredTextWithShadow(textRenderer, "Username:", width / 2 - 75, height / 2 - 50, 16777215);
    }

    private void onButtonAltAddPressed() {
        Alt alt = new Alt(textFieldAltUsername.getText(), toggleCracked.isChecked());
        Aoba.getInstance().altManager.addAlt(alt);
        
        if(!alt.isCracked())
        	alt.auth();
        
        parent.refreshAltList();
    }

    public void onButtonCancelPressed() {
        client.setScreen(parent);
    }
}
