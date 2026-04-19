/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.settings.types;

import net.aoba.settings.Setting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanSettingTest {

    private static BooleanSetting build(boolean defaultValue) {
        return BooleanSetting.builder()
                .id("test_bool")
                .displayName("Test Boolean")
                .description("Test description")
                .defaultValue(defaultValue)
                .onUpdate(v -> {})
                .build();
    }

    @Test
    @DisplayName("Builder produces a setting with the supplied default value")
    void builder_setsDefault() {
        BooleanSetting setting = build(true);
        assertThat(setting.getValue()).isTrue();
        assertThat(setting.getDefaultValue()).isTrue();
    }

    @Test
    @DisplayName("Setting type is BOOLEAN")
    void type_isBoolean() {
        BooleanSetting setting = build(false);
        assertThat(setting.type).isEqualTo(Setting.TYPE.BOOLEAN);
    }

    @Test
    @DisplayName("toggle() flips the value on each call")
    void toggle_flipsValue() {
        BooleanSetting setting = build(false);
        setting.toggle();
        assertThat(setting.getValue()).isTrue();
        setting.toggle();
        assertThat(setting.getValue()).isFalse();
    }

    @Test
    @DisplayName("setValue fires the onUpdate consumer with the new value")
    void setValue_firesConsumer() {
        AtomicReference<Boolean> observed = new AtomicReference<>();
        BooleanSetting setting = BooleanSetting.builder()
                .id("t")
                .displayName("T")
                .description("")
                .defaultValue(false)
                .onUpdate(observed::set)
                .build();

        setting.setValue(true);
        assertThat(observed.get()).isTrue();
    }

    @Test
    @DisplayName("toggle() invokes the onUpdate consumer exactly once per call")
    void toggle_invokesConsumerOnce() {
        AtomicInteger calls = new AtomicInteger();
        BooleanSetting setting = BooleanSetting.builder()
                .id("t")
                .displayName("T")
                .description("")
                .defaultValue(false)
                .onUpdate(v -> calls.incrementAndGet())
                .build();

        setting.toggle();
        setting.toggle();
        assertThat(calls.get()).isEqualTo(2);
    }

    @Test
    @DisplayName("resetToDefault restores the default value")
    void resetToDefault_restoresDefault() {
        BooleanSetting setting = build(false);
        setting.setValue(true);
        assertThat(setting.getValue()).isTrue();

        setting.resetToDefault();
        assertThat(setting.getValue()).isFalse();
    }

    @Test
    @DisplayName("silentSetValue updates the value without firing the consumer")
    void silentSetValue_doesNotFireConsumer() {
        AtomicInteger calls = new AtomicInteger();
        BooleanSetting setting = BooleanSetting.builder()
                .id("t")
                .displayName("T")
                .description("")
                .defaultValue(false)
                .onUpdate(v -> calls.incrementAndGet())
                .build();

        setting.silentSetValue(true);
        assertThat(setting.getValue()).isTrue();
        assertThat(calls.get()).isZero();
    }
}
