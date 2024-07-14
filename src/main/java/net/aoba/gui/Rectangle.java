package net.aoba.gui;

import org.jetbrains.annotations.Nullable;

public class Rectangle {
    private Float x = null;
    private Float y = null;
    private Float width = null;
    private Float height = null;

    public Rectangle() {
    }

    public Rectangle(Float x, Float y, Float width, Float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Nullable
    public Float getX() {
        return this.x;
    }

    @Nullable
    public Float getY() {
        return this.y;
    }

    @Nullable
    public Float getWidth() {
        return this.width;
    }

    @Nullable
    public Float getHeight() {
        return this.height;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Rectangle) {
            Rectangle otherRect = (Rectangle) other;

            if (x == null && x != otherRect.x)
                return false;

            if (y == null && y != otherRect.y)
                return false;

            if (width == null && width != otherRect.width)
                return false;

            if (height == null && height != otherRect.height)
                return false;

            return ((x == null && otherRect.x == null) || x.equals(otherRect.x)) &&
                    ((y == null && otherRect.y == null) || y.equals(otherRect.y)) &&
                    ((width == null && otherRect.width == null) || width.equals(otherRect.width)) &&
                    ((height == null && otherRect.height == null) || height.equals(otherRect.height));
        } else
            return false;
    }

    public boolean intersects(Rectangle rectangle) {
        return (Math.abs(x - rectangle.x) * 2 < (width + rectangle.width)) &&
                (Math.abs(y - rectangle.y) * 2 < (height + rectangle.height));
    }

    public boolean intersects(float x, float y) {
        float x2 = this.x + width;
        float y2 = this.y + height;

        return (x >= this.x && x <= x2 && y >= this.y && y <= y2);
    }

    public void setX(Float x) {
        this.x = x;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    /**
     * Returns whether or not this rectangle can be used for rendering, such that
     * the X, Y, Width, and Height dimensions are all non-null.
     *
     * @return Whether this rectangle can be used for rendering.
     */
    public boolean isDrawable() {
        return !(x == null ||
                y == null ||
                width == null ||
                height == null);
    }
}
