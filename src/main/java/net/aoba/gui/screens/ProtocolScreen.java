/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 * This maded by Donalp012
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens;

import org.apache.commons.lang3.math.NumberUtils;

import net.aoba.managers.ProtocolManager;
import net.aoba.gui.components.widgets.AobaButtonWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ProtocolScreen extends Screen {

    private EditBox versionField;
    private EditBox protocolField;
    private EditBox packVerField;
    private EditBox brandField;
    private AobaButtonWidget addButton;
    private final Screen parent;

    public ProtocolScreen(Screen parent) {
        super(Component.literal("Protocol Screen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        EditBox line1Widget = new EditBox(this.font, this.width / 2 - 220, 10, 1000, 18, Component.empty());
        line1Widget.setValue("NOTE: Protocol may bug on some");
        line1Widget.setBordered(false); line1Widget.setEditable(false); line1Widget.setTextColor(0xFFFF5555);
        this.addRenderableWidget(line1Widget);

        EditBox line2Widget = new EditBox(this.font, this.width / 2 - 220, 22, 1000, 18, Component.empty());
        line2Widget.setValue("ViaVersion servers");
        line2Widget.setBordered(false); line2Widget.setEditable(false); line2Widget.setTextColor(0xFF55FF55);
        this.addRenderableWidget(line2Widget);

        EditBox vLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 - 55, 75, 18, Component.empty());
        vLabel.setValue("Version:"); vLabel.setBordered(false); vLabel.setEditable(false); vLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(vLabel);

        EditBox pLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 - 30, 75, 18, Component.empty());
        pLabel.setValue("Protocol:"); pLabel.setBordered(false); pLabel.setEditable(false); pLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(pLabel);

        EditBox pkLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 - 5, 75, 18, Component.empty());
        pkLabel.setValue("Pack Ver:"); pkLabel.setBordered(false); pkLabel.setEditable(false); pkLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(pkLabel);

        EditBox bLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 + 20, 75, 18, Component.empty());
        bLabel.setValue("Brand:"); bLabel.setBordered(false); bLabel.setEditable(false); bLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(bLabel);

        this.versionField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 98, this.height / 2 - 60, 196, 18, Component.empty()));
        this.versionField.setValue(ProtocolManager.getVersionName());

        this.protocolField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 98, this.height / 2 - 35, 196, 18, Component.empty()));
        this.protocolField.setValue(Integer.toString(ProtocolManager.getProtocolVersion()));
        this.protocolField.setResponder(text -> this.updateAddButton());

        this.packVerField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 196, 18, Component.empty()));
        this.packVerField.setValue(Integer.toString(ProtocolManager.getPackVersion()));
        this.packVerField.setResponder(text -> this.updateAddButton());

        this.brandField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 98, this.height / 2 + 15, 80, 18, Component.empty()));
        this.brandField.setValue(ProtocolManager.getClientBrand());

        int startX = this.width / 2 - 14;

        AobaButtonWidget vButton = new AobaButtonWidget(startX, this.height / 2 + 14, 18, 20, Component.literal("Va"));
        vButton.setPressAction(b -> this.brandField.setValue("vanilla"));
        this.addRenderableWidget(vButton);

        AobaButtonWidget faButton = new AobaButtonWidget(startX + 20, this.height / 2 + 14, 22, 20, Component.literal("Fa"));
        faButton.setPressAction(b -> this.brandField.setValue("fabric"));
        this.addRenderableWidget(faButton);

        AobaButtonWidget foButton = new AobaButtonWidget(startX + 44, this.height / 2 + 14, 22, 20, Component.literal("Fo"));
        foButton.setPressAction(b -> this.brandField.setValue("forge"));
        this.addRenderableWidget(foButton);

        AobaButtonWidget qButton = new AobaButtonWidget(startX + 68, this.height / 2 + 14, 18, 20, Component.literal("Qu"));
        qButton.setPressAction(b -> this.brandField.setValue("quilt"));
        this.addRenderableWidget(qButton);

        AobaButtonWidget nfButton = new AobaButtonWidget(startX + 88, this.height / 2 + 14, 22, 20, Component.literal("Nf"));
        nfButton.setPressAction(b -> this.brandField.setValue("neoforge"));
        this.addRenderableWidget(nfButton);

        AobaButtonWidget lButton = new AobaButtonWidget(startX + 112, this.height / 2 + 14, 18, 20, Component.literal("Li"));
        lButton.setPressAction(b -> this.brandField.setValue("liteloader"));
        this.addRenderableWidget(lButton);
// neoforge liteloader and quilit added
// json for register the changes i tried to do with xml but ide say me stackoverflow 
        this.addButton = new AobaButtonWidget(this.width / 2 - 100, this.height / 2 + 50, 196, 20, Component.literal("Done"));
        this.addButton.setPressAction(button -> {
            ProtocolManager.OVERRIDE_NAME = this.versionField.getValue();
            ProtocolManager.OVERRIDE_PROTOCOL = Integer.parseInt(this.protocolField.getValue());
            ProtocolManager.OVERRIDE_PACK_VERSION = Integer.parseInt(this.packVerField.getValue());
            ProtocolManager.BRAND = this.brandField.getValue();

            ProtocolManager.save();
            this.onClose();
        });
        this.addRenderableWidget(this.addButton);

        // Botón Cancelar
        AobaButtonWidget cancelButton = new AobaButtonWidget(this.width / 2 - 100, this.height / 2 + 73, 196, 20, Component.literal("Cancel"));
        cancelButton.setPressAction(button -> this.onClose());
        this.addRenderableWidget(cancelButton);

        this.updateAddButton();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta) {
        super.extractRenderState(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private void updateAddButton() {
        if (this.addButton != null) {
            this.addButton.active = NumberUtils.isDigits(this.protocolField.getValue())
                    && NumberUtils.isDigits(this.packVerField.getValue());
        }
    }
}
