/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 * And this maded by relesk.1
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.screens;

import org.apache.commons.lang3.math.NumberUtils;

import net.aoba.gui.components.widgets.AobaButtonWidget;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

public class ProtocolScreen extends Screen {

    public static String BRAND = null;

    public static String OVERRIDE_NAME = null;
    public static int OVERRIDE_PROTOCOL = -1;
    public static int OVERRIDE_PACK_VERSION = -1;

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
        line1Widget.setBordered(false);
        line1Widget.setEditable(false);
        line1Widget.setTextColor(0xFFFF5555);
        this.addRenderableWidget(line1Widget);

        EditBox line2Widget = new EditBox(this.font, this.width / 2 - 220, 22, 1000, 18, Component.empty());
        line2Widget.setValue("ViaVersion servers");
        line2Widget.setBordered(false);
        line2Widget.setEditable(false);
        line2Widget.setTextColor(0xFF55FF55);
        this.addRenderableWidget(line2Widget);

        EditBox line3Widget = new EditBox(this.font, this.width / 2 - 220, 34, 1000, 18, Component.empty());
        line3Widget.setValue("It only change what the client");
        line3Widget.setBordered(false);
        line3Widget.setEditable(false);
        line3Widget.setTextColor(0xFF5555FF);
        this.addRenderableWidget(line3Widget);

        EditBox line4Widget = new EditBox(this.font, this.width / 2 - 220, 46, 1000, 18, Component.empty());
        line4Widget.setValue("says to the servers");
        line4Widget.setBordered(false);
        line4Widget.setEditable(false);
        line4Widget.setTextColor(0xFFFFFF55);
        this.addRenderableWidget(line4Widget);
        //idk why this cuts the text in the screen but works also you can solve it but i can´t


        EditBox vLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 - 55, 75, 18, Component.empty());
        vLabel.setValue("Version:");
        vLabel.setBordered(false);
        vLabel.setEditable(false);
        vLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(vLabel);

        EditBox pLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 - 30, 75, 18, Component.empty());
        pLabel.setValue("Protocol:");
        pLabel.setBordered(false);
        pLabel.setEditable(false);
        pLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(pLabel);
//this change the protocol butttt to do it more confortable add a option selector for 26.1.2 1.21.11 1.21.1 1.20 etc
        EditBox pkLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 - 5, 75, 18, Component.empty());
        pkLabel.setValue("Pack Ver:");
        pkLabel.setBordered(false);
        pkLabel.setEditable(false);
        pkLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(pkLabel);

        EditBox bLabel = new EditBox(this.font, this.width / 2 - 180, this.height / 2 + 20, 75, 18, Component.empty());
        bLabel.setValue("Brand:");
        bLabel.setBordered(false);
        bLabel.setEditable(false);
        bLabel.setTextColor(0xFFAAAAAA);
        this.addRenderableWidget(bLabel);

        this.addButton = new AobaButtonWidget(this.width / 2 - 100, this.height / 2 + 50, 196, 20, Component.literal("Done"));
        this.addButton.setPressAction(button -> {
            OVERRIDE_NAME = this.versionField.getValue();
            OVERRIDE_PROTOCOL = Integer.parseInt(this.protocolField.getValue());
            OVERRIDE_PACK_VERSION = Integer.parseInt(this.packVerField.getValue());
            BRAND = this.brandField.getValue();

            this.onClose();
        });
        this.addRenderableWidget(this.addButton);

        AobaButtonWidget cancelButton = new AobaButtonWidget(this.width / 2 - 100, this.height / 2 + 73, 196, 20, Component.literal("Cancel"));
        cancelButton.setPressAction(button -> this.onClose());
        this.addRenderableWidget(cancelButton);

        WorldVersion currentVersion = SharedConstants.getCurrentVersion();

        this.versionField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 98, this.height / 2 - 60, 196, 18, Component.empty()));
        this.versionField.setValue(OVERRIDE_NAME != null ? OVERRIDE_NAME : currentVersion.name());

        this.protocolField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 98, this.height / 2 - 35, 196, 18, Component.empty()));
        this.protocolField.setValue(Integer.toString(OVERRIDE_PROTOCOL != -1 ? OVERRIDE_PROTOCOL : currentVersion.protocolVersion()));
        this.protocolField.setResponder(text -> this.updateAddButton());

        this.packVerField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 98, this.height / 2 - 10, 196, 18, Component.empty()));

        int currentPackVer = 18;
        try {
            Object packObj = currentVersion.packVersion(PackType.CLIENT_RESOURCES);
            if (packObj != null) {
                String rawNum = packObj.toString().replaceAll("\\D+", "");
                if (!rawNum.isEmpty()) {
                    currentPackVer = Integer.parseInt(rawNum);
                }
            }
        } catch (Exception ignored) {}

        this.packVerField.setValue(Integer.toString(OVERRIDE_PACK_VERSION != -1 ? OVERRIDE_PACK_VERSION : currentPackVer));
        this.packVerField.setResponder(text -> this.updateAddButton());

        this.brandField = this.addRenderableWidget(new EditBox(this.font, this.width / 2 - 98, this.height / 2 + 15, 128, 18, Component.empty()));
        this.brandField.setValue(BRAND != null ? BRAND : ClientBrandRetriever.getClientModName());

        AobaButtonWidget vButton = new AobaButtonWidget(this.width / 2 + 33, this.height / 2 + 14, 20, 20, Component.literal("V"));
        vButton.setPressAction(button -> this.brandField.setValue("vanilla"));
        this.addRenderableWidget(vButton);

        AobaButtonWidget faButton = new AobaButtonWidget(this.width / 2 + 56, this.height / 2 + 14, 20, 20, Component.literal("Fa"));
        faButton.setPressAction(button -> this.brandField.setValue("fabric"));
        this.addRenderableWidget(faButton);

        AobaButtonWidget foButton = new AobaButtonWidget(this.width / 2 + 79, this.height / 2 + 14, 20, 20, Component.literal("Fo"));
        foButton.setPressAction(button -> this.brandField.setValue("forge"));
        this.addRenderableWidget(foButton);
// i am thinking in add more options like quilit neoforge etc
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
