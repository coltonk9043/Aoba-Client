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
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.event.listeners.MouseScrollListener;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.tabs.components.StackPanelComponent.StackType;
import net.minecraft.registry.Registries;

public class ScrollComponent extends Component implements MouseScrollListener {
    protected StackType stackType = StackType.Vertical;
    
    protected int scroll = 0;
    protected int visibleElements = 5;
    
    /**
     * Scroll component allows elements to be placed inside of a scroll-like component. All children of ScrollComponent MUST have a width/height set
     * depending on which direction the stack type is. 
     * @param parent Parent component.
     */
    public ScrollComponent(IGuiElement parent) {
        super(parent);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onChildAdded(IGuiElement child) {
        this.RecalculateHeight();
    }

    @Override
    public void onChildChanged(IGuiElement child) {
        this.RecalculateHeight();
    }

    @Override
    public void onVisibilityChanged() {
        if(this.isVisible())
        	Aoba.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
        else
        	Aoba.getInstance().eventManager.RemoveListener(MouseScrollListener.class, this);
    }

    public void RecalculateHeight() {
        float height = 0;
        int childCount = children.size();
        
        for (int i = scroll; i < scroll + visibleElements; i++) {
        	if(i >= childCount)
        		break;
        	
            Component iChild = children.get(i);
            // If the child is visible, increase the height of the StackPanel.
            if (iChild.isVisible()) {
                height += iChild.getSize().getHeight();
            }
            // Move the Top of the child below to the top + height of the previous element.
            if (i + 1 != children.size()) {
                Component childBelow = children.get(i + 1);
                Margin margin = childBelow.getMargin();
                childBelow.setMargin(new Margin(margin.getLeft(), height, margin.getRight(), margin.getBottom()));
            }
        }
        setHeight(height);
    }

	@Override
	public void OnMouseScroll(MouseScrollEvent event) {
		if (Aoba.getInstance().hudManager.isClickGuiOpen() && this.hovered) {
			int childCount = children.size();
            if (event.GetVertical() > 0 && scroll > 0) {
                scroll--;
            } else if (event.GetVertical() < 0 && (scroll + visibleElements) < childCount) {
                scroll++;
            }
            event.cancel();
        }
	}
}