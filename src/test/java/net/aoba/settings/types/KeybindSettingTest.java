/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.aoba.settings.Setting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class KeybindSettingTest {

    private static KeybindSetting build(Key defaultKey) {
        return KeybindSetting.builder()
                .id("key.test")
                .displayName("Test Keybind")
                .description("Test description")
                .defaultValue(defaultKey)
                .onUpdate(k -> {})
                .build();
    }

    @Test
    @DisplayName("Builder accepts InputConstants.UNKNOWN as the default key")
    void builder_acceptsUnknownAsDefault() {
        KeybindSetting setting = build(InputConstants.UNKNOWN);
        assertThat(setting.getValue()).isEqualTo(InputConstants.UNKNOWN);
        assertThat(setting.getDefaultValue()).isEqualTo(InputConstants.UNKNOWN);
    }

    @Test
    @DisplayName("Setting type is KEYBIND")
    void type_isKeybind() {
        KeybindSetting setting = build(InputConstants.UNKNOWN);
        assertThat(setting.type).isEqualTo(Setting.TYPE.KEYBIND);
    }

    @Test
    @DisplayName("setValue updates the stored key and fires the consumer")
    void setValue_updatesAndFiresConsumer() {
        AtomicReference<Key> observed = new AtomicReference<>();
        KeybindSetting setting = KeybindSetting.builder()
                .id("key.test")
                .displayName("Test")
                .description("")
                .defaultValue(InputConstants.UNKNOWN)
                .onUpdate(observed::set)
                .build();

        Key shift = InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_SHIFT);
        setting.setValue(shift);

        assertThat(setting.getValue()).isEqualTo(shift);
        assertThat(observed.get()).isEqualTo(shift);
    }

    @Test
    @DisplayName("resetToDefault restores the default keybind")
    void resetToDefault_restoresDefault() {
        KeybindSetting setting = build(InputConstants.UNKNOWN);
        Key enter = InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_ENTER);
        setting.setValue(enter);
        assertThat(setting.getValue()).isEqualTo(enter);

        setting.resetToDefault();
        assertThat(setting.getValue()).isEqualTo(InputConstants.UNKNOWN);
    }

    @Test
    @DisplayName("Two KEYSYM keys for the same GLFW code are equal")
    void keysymFactory_isInterned() {
        Key first = InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_A);
        Key second = InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_A);
        assertThat(first).isEqualTo(second);
    }
}
