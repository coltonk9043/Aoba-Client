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

package net.aoba.settings.types;

import net.aoba.gui.Rectangle;
import net.aoba.settings.Setting;
import java.util.function.Consumer;

public class RectangleSetting extends Setting<Rectangle> {

    public RectangleSetting(String ID, String description, Rectangle default_value) {
        super(ID, description, default_value);
        type = TYPE.RECTANGLE;
    }

    public RectangleSetting(String ID, String displayName, String description, Rectangle default_value) {
        super(ID, displayName, description, default_value);
        type = TYPE.RECTANGLE;
    }


    public RectangleSetting(String ID, String description, Rectangle default_value, Consumer<Rectangle> onUpdate) {
        super(ID, description, default_value, onUpdate);
        type = TYPE.RECTANGLE;
    }

    /**
     * Setter for the Rectangle size.
     *
     * @param size Size of the Rectangle.
     */
    public void setSize(Rectangle size) {
    	if(!value.equals(size)) {
    		value = size;
            update();
    	}
    }
    
    /**
     * Setter for the Rectangle X location.
     *
     * @param x X Coordinate.
     */
    public void setX(Float x) {
    	if(!value.getX().equals(x)) {
	    	Rectangle oldRect = value;
	    	value = new Rectangle(x, oldRect.getY(), oldRect.getWidth(), oldRect.getHeight());
	    	update();
    	}
    }
    
    /**
     * Setter for the Rectangle Y location.
     *
     * @param y Y Coordinate.
     */
    public void setY(Float y) {
    	if(!value.getY().equals(y)) {
	    	Rectangle oldRect = value;
	    	value = new Rectangle(oldRect.getX(), y, oldRect.getWidth(), oldRect.getHeight());
	    	update();
    	}
    }
    
    /**
     * Setter for the Rectangle width.
     *
     * @param width Width Coordinate.
     */
    public void setWidth(Float width) {
    	if(!value.getWidth().equals(width)) {
        	Rectangle oldRect = value;
        	value = new Rectangle(oldRect.getX(), oldRect.getY(), width, oldRect.getHeight());
        	update();
    	}
    }

    /**
     * Setter for the Rectangle height.
     *
     * @param height Height Coordinate.
     */
    public void setHeight(Float height) {
    	if(!value.getHeight().equals(height)) {
	    	Rectangle oldRect = value;
	    	value = new Rectangle(oldRect.getX(), oldRect.getY(), oldRect.getWidth(), height);
	    	update();
    	}
    }

    /**
     * Setter for the Rectangle size without calling the update function.
     *
     * @param x X Coordinate.
     */
    public void silentSetSize(Rectangle size) {
        value = size;
    }

    /**
     * Checks whether or not a value is with this setting's valid range.
     */
    @Override
    protected boolean isValueValid(Rectangle value) {
        return true;
    }
}
