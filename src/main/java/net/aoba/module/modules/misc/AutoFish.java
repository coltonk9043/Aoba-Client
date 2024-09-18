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

/**
 * AutoFish Module
 */
package net.aoba.module.modules.misc;

import net.aoba.Aoba;
import net.aoba.event.events.ReceivePacketEvent;
import net.aoba.event.listeners.ReceivePacketListener;
import net.aoba.module.Category;
import net.aoba.module.Module;
import net.aoba.settings.types.BooleanSetting;
import net.aoba.settings.types.KeybindSetting;
import net.aoba.utils.FindItemResult;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class AutoFish extends Module implements ReceivePacketListener {
    private BooleanSetting autoSwitch;
    private BooleanSetting autoToggle;

    public AutoFish() {
        super(new KeybindSetting("key.autofish", "AutoFish Key", InputUtil.fromKeyCode(GLFW.GLFW_KEY_UNKNOWN, 0)));

        this.setName("AutoFish");
        this.setCategory(Category.of("Misc"));
        this.setDescription("Automatically fishes for you.");

        autoSwitch = new BooleanSetting("autofish_autoswitch", "Auto Switch", "Automatically switch to fishing rod before casting.", true);
        autoToggle = new BooleanSetting("autofish_autotoggle", "Auto Toggle", "Automatically toggles off if no fishing rod is found in the hotbar.", true);

        this.addSetting(autoSwitch);
        this.addSetting(autoToggle);
    }

    @Override
    public void onDisable() {
        Aoba.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
    }

    @Override
    public void onEnable() {
        Aoba.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);

        FindItemResult rod = find(Items.FISHING_ROD);

        if (autoSwitch.getValue()) {
            if (rod.found() && rod.isHotbar()) {
                swap(rod.slot(), false);
            } else {
                if (!autoToggle.getValue()) return;

                toggle();
            }
        }
    }

    @Override
    public void onToggle() {

    }

    private void castRod(int count) {
        FindItemResult rod = find(Items.FISHING_ROD);

        if (autoSwitch.getValue()) {
            if (rod.found() && rod.isHotbar()) {
                swap(rod.slot(), false);
            } else {
                if (!autoToggle.getValue()) return;

                toggle();
            }
        }

        for (int i = 0; i < count; i++) {
            MC.interactionManager.interactItem(MC.player, Hand.MAIN_HAND);
        }
    }

    @Override
    public void OnReceivePacket(ReceivePacketEvent readPacketEvent) {
        Packet<?> packet = readPacketEvent.GetPacket();

        if (packet instanceof PlaySoundS2CPacket) {
            PlaySoundS2CPacket soundPacket = (PlaySoundS2CPacket) packet;
            if (soundPacket.getSound().value().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH)) {
                castRod(2);
            }
        }
    }
}
