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

package net.aoba.gui.tabs.components;

import net.aoba.Aoba;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.listeners.MouseMoveListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;

public abstract class Component implements IGuiElement, MouseMoveListener {
	protected boolean visible = false;
    protected boolean hovered = false;

    // NEW ui variables.
    protected IGuiElement parent;
    protected ArrayList<Component> children;
    
    protected Rectangle preferredSize = null;
    protected Rectangle size;
    protected Margin margin;
    protected Rectangle actualSize;


    public Component(IGuiElement parent) {
        this.parent = parent;
        this.children = new ArrayList<Component>();

        this.preferredSize = new Rectangle(null, null, null, null);
        this.size = preferredSize;
        this.margin = new Margin();

        remeasure();
    }
    
    public Component(IGuiElement parent, Rectangle preferredSize) {
        this.parent = parent;
        this.children = new ArrayList<Component>();

        this.preferredSize = preferredSize;
        this.size = preferredSize;
        this.margin = new Margin();

        remeasure();
    }

    
    @Override
    public Rectangle getSize() {
    	return this.size;
    }
    
    @Override
    public Rectangle getActualSize() {
    	return this.actualSize;
    }
    
    public Margin getMargin(){
        return this.margin;
    }

    
    @Override
    public void setSize(Rectangle newSize) {
    	if(size == null || !size.equals(newSize)) {
        	this.size = newSize;
        	remeasure();
    	}
    }
    
    private void setActualSize(Rectangle newSize) {
    	if(actualSize == null || !actualSize.equals(newSize)) {
    		this.actualSize = newSize;
    		
        	// If parent is not null, notify the parent.
            if (parent != null) {
               this.parent.onChildChanged(this);
            }
            
            // Notify all of the children.
            for(Component child : children){
            	child.onParentChanged();
            }
    	}
    }
    
    public void setMargin(Margin margin) {
    	if(!this.margin.equals(margin)) {
        	this.margin = margin;
        	remeasure();
    	}
    }
    
    @Override
    public void onParentChanged() {
    	remeasure();
    }
    
    /**
     * Remeasures the size of this component relative to it's parent.
     */
    public void remeasure() {
    	if(this.size == null)
    		return;
    	
    	// Set X
    	Float actualX = this.size.getX();
    	if(parent != null && actualX == null) 
    		actualX = this.parent.getActualSize().getX();
    	
    	if(actualX != null) {
    		if(margin.getLeft() != null)
    			actualX += margin.getLeft();
    	}else
    		actualX = 0f;
    	
    	// Set Y
    	Float actualY = this.size.getY();
    	if(parent != null && actualY == null) 
    		actualY = this.parent.getActualSize().getY();
    	
        if(actualY != null) {
        	if(margin.getTop() != null)
        		actualY += margin.getTop();
        }else
        	actualY = 0f;

    	// Set Width
    	Float actualWidth = this.size.getWidth();
    	if(parent != null && actualWidth == null) 
    		actualWidth = this.parent.getActualSize().getWidth();
    	
    	if(actualWidth != null) {
        	if(margin.getLeft() != null)
        		actualWidth -= margin.getLeft();
        	
        	if(margin.getRight() != null)
        		actualWidth -= margin.getRight();
    	}else
    		actualWidth = 0.0f;

    	// Set Height
    	Float actualHeight = this.size.getHeight();
    	if(parent != null && actualHeight == null) 
    		actualHeight = this.parent.getActualSize().getHeight();
    	
    	if(actualHeight != null) {
        	if(margin.getTop() != null)
        		actualHeight -= margin.getTop();
        	
        	if(margin.getBottom() != null)
        		actualHeight -= margin.getBottom();
    	}else
    		actualHeight = 0.0f;


    	this.setActualSize(new Rectangle(actualX, actualY, actualWidth, actualHeight));
    }
    
    public void setX(Float x) {
    	Rectangle oldRect = this.size;
    	this.setSize(new Rectangle(x, oldRect.getY(), oldRect.getWidth(), oldRect.getHeight()));
    }
    
    public void setY(Float y) {
    	Rectangle oldRect = this.size;
    	this.setSize(new Rectangle(oldRect.getX(), y, oldRect.getWidth(), oldRect.getHeight()));
    }
    
    public void setWidth(Float width) {
    	Rectangle oldRect = this.size;
    	this.setSize(new Rectangle(oldRect.getX(), oldRect.getY(), width, oldRect.getHeight()));
    }
    
    public void setHeight(Float height) {
    	Rectangle oldRect = this.size;
    	this.setSize(new Rectangle(oldRect.getX(), oldRect.getY(), oldRect.getWidth(), height));
    }
   
    @Override
    public void addChild(Component child) {
        children.add(child);
        onChildAdded(child);
    }
    
	@Override
	public void onChildChanged(Component child) { }

	@Override
	public void onChildAdded(Component child) { }
	
    /**
     * Returns the parent of the Component.
     *
     * @return Parent of the component as a ClickGuiTab.
     */
    public IGuiElement getParent() {
        return parent;
    }

    /**
     * Sets whether the component is visible or not
     *
     * @param bool State to set visibility to.
     */
    public void setVisible(boolean bool) {
        if (this.visible == bool)
            return;

        this.visible = bool;
        this.hovered = false;

        for (Component child : this.children) {
            child.setVisible(bool);
        }

        // If parent is not null, notify the parent.
        if (parent != null) {
            this.parent.onChildChanged(this);
        }


        // Register and Unregister event listener according to state.
        if (bool) {
            Aoba.getInstance().eventManager.AddListener(MouseMoveListener.class, this);
        } else {
            Aoba.getInstance().eventManager.RemoveListener(MouseMoveListener.class, this);
        }

        this.onVisibilityChanged();
    }

    /**
     * Whether or not the component is currently visible.
     *
     * @return Visibility state as a boolean.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Updates the offset (y position relative to parent) of the component.
     */
    public void update() {
        for (Component child : children) {
            if (child.visible) {
                child.update();
            }
        }
    }

    /**
     * Abstract method for drawing components onto the screen.
     *
     * @param drawContext  DrawContext of the game.
     * @param partialTicks Partial Ticks of the game.
     */
    public void draw(DrawContext drawContext, float partialTicks) {
        if (this.visible) {
            for (Component child : children) {
                if (child.visible) {
                    child.draw(drawContext, partialTicks);
                }
            }
        }
    }

    /**
     * Triggers when the mouse is moved.
     *
     * @param mouseMoveEvent Event fired.
     */
    @Override
    public void OnMouseMove(MouseMoveEvent mouseMoveEvent) {
        if (!visible || !Aoba.getInstance().hudManager.isClickGuiOpen()) {
            this.hovered = false;
        } else {

            float mouseX = (float)mouseMoveEvent.getX();
            float mouseY = (float)mouseMoveEvent.getY();

            this.hovered = actualSize.intersects(mouseX, mouseY);
        }
    }
}
