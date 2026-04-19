/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.utils.render;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TextUtilsTest {

    @Test
    @DisplayName("IDToName splits on underscores and capitalizes each word")
    void IDToName_splitsAndCapitalizes() {
        assertThat(TextUtils.IDToName("some_module_id")).isEqualTo("Some Module Id");
    }

    @Test
    @DisplayName("IDToName returns a capitalized single token when no underscores are present")
    void IDToName_singleWord() {
        assertThat(TextUtils.IDToName("hello")).isEqualTo("Hello");
    }

    @Test
    @DisplayName("IDToName preserves internal character casing other than the first character")
    void IDToName_preservesInternalCasing() {
        assertThat(TextUtils.IDToName("autoXP_farmer")).isEqualTo("AutoXP Farmer");
    }

    @ParameterizedTest(name = "Capitalize({0}) == {1}")
    @CsvSource({
            "hello, Hello",
            "HELLO, HELLO",
            "hELLO, HELLO"
    })
    @DisplayName("Capitalize uppercases the first character for multi-character strings")
    void Capitalize_multiChar(String input, String expected) {
        assertThat(TextUtils.Capitalize(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Capitalize returns single-character and empty inputs unchanged")
    void Capitalize_shortInputsUnchanged() {
        assertThat(TextUtils.Capitalize("")).isEqualTo("");
        assertThat(TextUtils.Capitalize("a")).isEqualTo("a");
        assertThat(TextUtils.Capitalize("Z")).isEqualTo("Z");
    }
}
