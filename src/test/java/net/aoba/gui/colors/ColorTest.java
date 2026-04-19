/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.colors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class ColorTest {

    @Test
    @DisplayName("getColorAsInt packs ARGB in the expected byte order")
    void getColorAsInt_packsArgb() {
        Color c = new Color(0x11, 0x22, 0x33, 0x44);
        assertThat(c.getColorAsInt()).isEqualTo(0x44112233);
    }

    @Test
    @DisplayName("Three-arg RGB constructor defaults alpha to 255")
    void rgbConstructor_defaultsAlpha() {
        Color c = new Color(10, 20, 30);
        assertThat(c.getAlpha()).isEqualTo(1.0f);
        assertThat(c.getColorAsInt()).isEqualTo(0xFF0A141E);
    }

    @Test
    @DisplayName("Float constructor scales channels to 0..255")
    void floatConstructor_scalesTo255() {
        Color c = new Color(1.0f, 0.0f, 0.5f, 1.0f);
        assertThat(c.getRed()).isEqualTo(1.0f);
        assertThat(c.getGreen()).isEqualTo(0.0f);
        assertThat(c.getAlpha()).isEqualTo(1.0f);
    }

    @Test
    @DisplayName("getAsSolid preserves RGB and forces alpha to 255")
    void getAsSolid_forcesFullAlpha() {
        Color c = new Color(50, 60, 70, 10).getAsSolid();
        assertThat(c.getColorAsInt() >>> 24).isEqualTo(0xFF);
        assertThat(c.getRed() * 255f).isEqualTo(50f, offset(0.5f));
    }

    @Test
    @DisplayName("interpolate at factor=0 returns color1 channels")
    void interpolate_atZero_returnsStart() {
        Color start = new Color(0, 0, 0, 255);
        Color end = new Color(255, 255, 255, 255);
        Color mid = Color.interpolate(start, end, 0.0f);
        assertThat(mid.getColorAsInt()).isEqualTo(start.getColorAsInt());
    }

    @Test
    @DisplayName("interpolate at factor=1 returns color2 channels")
    void interpolate_atOne_returnsEnd() {
        Color start = new Color(0, 0, 0, 255);
        Color end = new Color(200, 100, 50, 200);
        Color mid = Color.interpolate(start, end, 1.0f);
        assertThat(mid.getColorAsInt()).isEqualTo(end.getColorAsInt());
    }

    @Test
    @DisplayName("interpolate at factor=0.5 linearly blends channels")
    void interpolate_atHalf_blends() {
        Color start = new Color(0, 0, 0, 0);
        Color end = new Color(200, 100, 50, 200);
        Color mid = Color.interpolate(start, end, 0.5f);
        assertThat(mid.getRed() * 255f).isEqualTo(100f, offset(1.0f));
        assertThat(mid.getGreen() * 255f).isEqualTo(50f, offset(1.0f));
        assertThat(mid.getBlue() * 255f).isEqualTo(25f, offset(1.0f));
        assertThat(mid.getAlpha() * 255f).isEqualTo(100f, offset(1.0f));
    }

    @Test
    @DisplayName("convertHextoRGB round-trips a 6-char hex string")
    void convertHextoRGB_sixCharHex() {
        Color c = Color.convertHextoRGB("FF8040");
        assertThat(c.getRed() * 255f).isEqualTo(255f, offset(0.5f));
        assertThat(c.getGreen() * 255f).isEqualTo(128f, offset(0.5f));
        assertThat(c.getBlue() * 255f).isEqualTo(64f, offset(0.5f));
    }

    @Test
    @DisplayName("convertHextoRGB strips a leading # before parsing")
    void convertHextoRGB_stripsHash() {
        Color c = Color.convertHextoRGB("#00FF00");
        assertThat(c.getGreen()).isEqualTo(1.0f);
        assertThat(c.getRed()).isEqualTo(0.0f);
        assertThat(c.getBlue()).isEqualTo(0.0f);
    }

    @Test
    @DisplayName("convertHextoRGB handles 8-char hex as alpha + RGB")
    void convertHextoRGB_eightCharHex() {
        Color c = Color.convertHextoRGB("80112233");
        assertThat(c.getAlpha() * 255f).isEqualTo(128f, offset(0.5f));
        assertThat(c.getRed() * 255f).isEqualTo(17f, offset(0.5f));
        assertThat(c.getGreen() * 255f).isEqualTo(34f, offset(0.5f));
        assertThat(c.getBlue() * 255f).isEqualTo(51f, offset(0.5f));
    }

    @Test
    @DisplayName("convertRGBToHex packs RGB into a single integer")
    void convertRGBToHex_packsValue() {
        int hex = Color.convertRGBToHex(0xFF, 0x80, 0x40);
        assertThat(hex).isEqualTo(0xFF8040);
    }

    @ParameterizedTest(name = "hsv2rgb({0}, 1.0, 1.0) yields RGB={1},{2},{3}")
    @CsvSource({
            "0,   255, 0,   0",
            "120, 0,   255, 0"
    })
    @DisplayName("hsv2rgb produces expected primary RGB values")
    void hsv2rgb_primaryHues(float hue, int expectedR, int expectedG, int expectedB) {
        Color c = Color.hsv2rgb(hue, 1.0f, 1.0f);
        assertThat(c.getRed() * 255f).isEqualTo(expectedR, offset(1.0f));
        assertThat(c.getGreen() * 255f).isEqualTo(expectedG, offset(1.0f));
        assertThat(c.getBlue() * 255f).isEqualTo(expectedB, offset(1.0f));
    }

    @Test
    @DisplayName("setAlpha updates only the alpha channel")
    void setAlpha_updatesAlphaOnly() {
        Color c = new Color(50, 60, 70, 255);
        int beforeRgb = c.getColorAsInt() & 0x00FFFFFF;
        c.setAlpha(64);
        assertThat(c.getColorAsInt() >>> 24).isEqualTo(64);
        assertThat(c.getColorAsInt() & 0x00FFFFFF).isEqualTo(beforeRgb);
    }
}
