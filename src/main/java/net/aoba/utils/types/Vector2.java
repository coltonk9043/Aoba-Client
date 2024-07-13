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

package net.aoba.utils.types;

/**
 * A simple 2D vector class representing points or directions in 2D space.
 */
public class Vector2 {
    public float x = 0;
    public float y = 0;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Adds another vector to this vector
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    // Subtracts another vector from this vector
    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    // Scales the vector by a scalar value
    public Vector2 scale(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    // Calculates the dot product with another vector
    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    // Calculates the length (magnitude) of the vector
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    // Normalizes the vector to a unit vector
    public Vector2 normalize() {
        float len = length();
        return len > 0 ? new Vector2(this.x / len, this.y / len) : new Vector2(0, 0);
    }

    // Returns the distance to another vector
    public float distance(Vector2 other) {
        return (float) Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    // Returns a string representation of the vector
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }

    // Checks for equality with another vector
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector2)) return false;
        Vector2 other = (Vector2) obj;
        return Float.compare(other.x, x) == 0 && Float.compare(other.y, y) == 0;
    }

    // Hash code for the vector
    @Override
    public int hashCode() {
        int result = Float.hashCode(x);
        result = 31 * result + Float.hashCode(y);
        return result;
    }
}
