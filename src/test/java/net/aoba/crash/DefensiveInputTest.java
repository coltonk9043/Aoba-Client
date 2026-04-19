/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.crash;

import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.IntegerSetting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Crash-prevention tests: verify defensive contracts for pure-logic classes so
 * malformed input or edge cases fail fast with documented exceptions rather
 * than leaking unexpected errors up to the Minecraft render thread.
 */
class DefensiveInputTest {

    @Test
    @DisplayName("convertHextoRGB throws IllegalArgumentException for wrong-length input")
    void convertHextoRGB_wrongLengthThrows() {
        assertThatThrownBy(() -> Color.convertHextoRGB("xyz"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Color.convertHextoRGB(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Color.convertHextoRGB("1234567"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("convertHextoRGB accepts a leading # without throwing")
    void convertHextoRGB_withHashDoesNotThrow() {
        assertThatCode(() -> Color.convertHextoRGB("#FFAABB"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Rectangle.isDrawable does not NPE on a default-constructed rectangle")
    void rectangle_defaultIsDrawableIsSafe() {
        Rectangle r = new Rectangle();
        assertThatCode(r::isDrawable).doesNotThrowAnyException();
        assertThat(r.isDrawable()).isFalse();
    }

    @Test
    @DisplayName("IntegerSetting.setValue silently discards out-of-range input")
    void integerSetting_outOfRangeIsIgnored() {
        IntegerSetting setting = IntegerSetting.builder()
                .id("t")
                .displayName("T")
                .description("")
                .defaultValue(5)
                .minValue(0)
                .maxValue(10)
                .step(1)
                .onUpdate(v -> {})
                .build();

        assertThatCode(() -> setting.setValue(999)).doesNotThrowAnyException();
        assertThat(setting.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("Color interpolation with extreme factor values does not throw")
    void color_interpolationExtremeFactorsSafe() {
        Color start = new Color(0, 0, 0, 0);
        Color end = new Color(255, 255, 255, 255);
        assertThatCode(() -> Color.interpolate(start, end, -1.0f)).doesNotThrowAnyException();
        assertThatCode(() -> Color.interpolate(start, end, 2.0f)).doesNotThrowAnyException();
    }
}
