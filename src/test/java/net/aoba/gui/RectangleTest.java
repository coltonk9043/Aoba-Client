/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleTest {

    @Test
    @DisplayName("Default constructor leaves all dimensions null and not drawable")
    void defaultConstructor_notDrawable() {
        Rectangle r = new Rectangle();
        assertThat(r.getX()).isNull();
        assertThat(r.getY()).isNull();
        assertThat(r.getWidth()).isNull();
        assertThat(r.getHeight()).isNull();
        assertThat(r.isDrawable()).isFalse();
    }

    @Test
    @DisplayName("isDrawable returns true when all dimensions are set")
    void isDrawable_allDimensionsSet() {
        Rectangle r = new Rectangle(0f, 0f, 10f, 10f);
        assertThat(r.isDrawable()).isTrue();
    }

    @Test
    @DisplayName("isDrawable returns false when any dimension is null")
    void isDrawable_missingDimension() {
        Rectangle r = new Rectangle(0f, 0f, 10f, null);
        assertThat(r.isDrawable()).isFalse();
    }

    @Test
    @DisplayName("intersects(x, y) includes points on the edges")
    void intersectsPoint_edgesIncluded() {
        Rectangle r = new Rectangle(10f, 20f, 30f, 40f);
        assertThat(r.intersects(10f, 20f)).isTrue();  // top-left corner
        assertThat(r.intersects(40f, 60f)).isTrue();  // bottom-right corner
        assertThat(r.intersects(25f, 40f)).isTrue();  // middle
    }

    @Test
    @DisplayName("intersects(x, y) returns false for points outside the rectangle")
    void intersectsPoint_outsideFalse() {
        Rectangle r = new Rectangle(10f, 20f, 30f, 40f);
        assertThat(r.intersects(9f, 40f)).isFalse();   // left of
        assertThat(r.intersects(41f, 40f)).isFalse();  // right of
        assertThat(r.intersects(25f, 19f)).isFalse();  // above
        assertThat(r.intersects(25f, 61f)).isFalse();  // below
    }

    @Test
    @DisplayName("Copy constructor produces an equal rectangle")
    void copyConstructor_equalsOriginal() {
        Rectangle original = new Rectangle(1f, 2f, 3f, 4f);
        Rectangle copy = new Rectangle(original);
        assertThat(copy).isEqualTo(original);
    }

    @Test
    @DisplayName("equals uses field-by-field comparison")
    void equals_fieldByField() {
        Rectangle a = new Rectangle(1f, 2f, 3f, 4f);
        Rectangle b = new Rectangle(1f, 2f, 3f, 4f);
        Rectangle different = new Rectangle(1f, 2f, 3f, 5f);

        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(different);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("not a rectangle");
    }

    @Test
    @DisplayName("Setters mutate in place")
    void setters_mutateInPlace() {
        Rectangle r = new Rectangle();
        r.setX(5f);
        r.setY(6f);
        r.setWidth(7f);
        r.setHeight(8f);

        assertThat(r.getX()).isEqualTo(5f);
        assertThat(r.getY()).isEqualTo(6f);
        assertThat(r.getWidth()).isEqualTo(7f);
        assertThat(r.getHeight()).isEqualTo(8f);
        assertThat(r.isDrawable()).isTrue();
    }

    @Test
    @DisplayName("INFINITE sentinel has all infinite dimensions and is considered drawable")
    void infiniteSentinel_isDrawable() {
        assertThat(Rectangle.INFINITE.isDrawable()).isTrue();
        assertThat(Rectangle.INFINITE.getX()).isEqualTo(Float.POSITIVE_INFINITY);
        assertThat(Rectangle.INFINITE.getWidth()).isEqualTo(Float.POSITIVE_INFINITY);
    }
}
