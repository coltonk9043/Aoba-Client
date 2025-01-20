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
        this.parent = parentScreen;
    }

    public void init() {
        super.init();

        this.textFieldAltUsername = new TextFieldWidget(textRenderer, this.width / 2 - 100, height / 2 - 36, 200, 20,
                Text.of("Enter Name"));
        this.textFieldAltUsername.setText("");
        this.addDrawableChild(this.textFieldAltUsername);

        this.toggleCracked = CheckboxWidget.builder(Text.of("Cracked Account?"), textRenderer).pos(this.width / 2 - 100, height / 2 - 12).build();
        this.addDrawableChild(this.toggleCracked);

        this.buttonAddAlt = ButtonWidget.builder(Text.of("Add Alt"), b -> this.onButtonAltAddPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 24, 200, 20).build();
        this.addDrawableChild(this.buttonAddAlt);

        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), b -> this.onButtonCancelPressed())
                .dimensions(this.width / 2 - 100, this.height / 2 + 46, 200, 20).build());
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        drawContext.drawCenteredTextWithShadow(textRenderer, "Add Alternate Account", this.width / 2, 20, 16777215);
        drawContext.drawCenteredTextWithShadow(textRenderer, "Username:", this.width / 2 - 75, height / 2 - 50, 16777215);
    }

    private void onButtonAltAddPressed() {
        Alt alt = new Alt(this.textFieldAltUsername.getText(), toggleCracked.isChecked());
        Aoba.getInstance().altManager.addAlt(alt);
        
        if(!alt.isCracked())
        	alt.auth();
        
        this.parent.refreshAltList();
    }

    public void onButtonCancelPressed() {
        client.setScreen(this.parent);
    }
}
