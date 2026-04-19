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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerSettingTest {

    private static IntegerSetting build(int defaultValue, int min, int max) {
        return IntegerSetting.builder()
                .id("test_int")
                .displayName("Test Int")
                .description("Test description")
                .defaultValue(defaultValue)
                .minValue(min)
                .maxValue(max)
                .step(1)
                .onUpdate(v -> {})
                .build();
    }

    @Test
    @DisplayName("Builder exposes min, max, step and default")
    void builder_exposesBounds() {
        IntegerSetting setting = build(5, 0, 10);
        assertThat(setting.getValue()).isEqualTo(5);
        assertThat(setting.min_value).isEqualTo(0);
        assertThat(setting.max_value).isEqualTo(10);
        assertThat(setting.step).isEqualTo(1);
        assertThat(setting.type).isEqualTo(Setting.TYPE.INTEGER);
    }

    @Test
    @DisplayName("Builder defaults range to [1, 10] with step 1 when not specified")
    void builder_defaultsRange() {
        IntegerSetting setting = IntegerSetting.builder()
                .id("t").displayName("T").description("").defaultValue(1)
                .onUpdate(v -> {}).build();
        assertThat(setting.min_value).isEqualTo(1);
        assertThat(setting.max_value).isEqualTo(10);
        assertThat(setting.step).isEqualTo(1);
    }

    @ParameterizedTest(name = "value {0} is accepted within range [0, 10]")
    @ValueSource(ints = {0, 1, 5, 9, 10})
    @DisplayName("Values in range are accepted and update the setting")
    void setValue_inRange_updates(int value) {
        IntegerSetting setting = build(5, 0, 10);
        setting.setValue(value);
        assertThat(setting.getValue()).isEqualTo(value);
    }

    @ParameterizedTest(name = "value {0} is rejected (out of range [0, 10])")
    @ValueSource(ints = {-1, -100, 11, 1_000_000})
    @DisplayName("Values out of range are rejected and leave current value unchanged")
    void setValue_outOfRange_rejected(int value) {
        IntegerSetting setting = build(5, 0, 10);
        setting.setValue(value);
        assertThat(setting.getValue()).isEqualTo(5);
    }

    @ParameterizedTest(name = "isValueValid({0}) with range [{1}, {2}] == {3}")
    @CsvSource({
            "5,   0, 10, true",
            "0,   0, 10, true",
            "10,  0, 10, true",
            "-1,  0, 10, false",
            "11,  0, 10, false",
            "-5, -10, -1, true",
            "-11,-10, -1, false"
    })
    @DisplayName("isValueValid honors inclusive bounds")
    void isValueValid_respectsBounds(int value, int min, int max, boolean expected) {
        IntegerSetting setting = build(min, min, max);
        assertThat(setting.getClass()).isEqualTo(IntegerSetting.class);
        // isValueValid is protected; exercise via setValue + getValue
        int original = setting.getValue();
        setting.setValue(value);
        if (expected) {
            assertThat(setting.getValue()).isEqualTo(value);
        } else {
            assertThat(setting.getValue()).isEqualTo(original);
        }
    }

    @Test
    @DisplayName("resetToDefault restores the original default value")
    void resetToDefault_restores() {
        IntegerSetting setting = build(3, 0, 10);
        setting.setValue(9);
        setting.resetToDefault();
        assertThat(setting.getValue()).isEqualTo(3);
    }
}
