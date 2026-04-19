/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.gui.UIElement;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;
import net.aoba.rendering.Renderer2D;

public class PanelComponent extends UIElement {
	private final ArrayList<UIElement> children = new ArrayList<>();
	private boolean isVirtualized = false;

	public PanelComponent() {
	}

	public boolean isVirtualized() {
		return isVirtualized;
	}

	public void setVirtualized(boolean virtualized) {
		this.isVirtualized = virtualized;
	}

	public List<UIElement> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public void addChild(UIElement child) {
		if (child == null)
			return;

		// Initialize the child if it has not been already.
		if (isInitialized() && !child.isInitialized())
			child.initialize();

		// Detach from previous parent if one exists.
		UIElement oldParent = child.getParent();
		if (oldParent instanceof Component oldComponent)
			oldComponent.removeChild(child);
		else if (oldParent instanceof PanelComponent oldPanel)
			oldPanel.removeChild(child);

		child.setParent(this);
		children.add(child);
		onChildAdded(child);
		invalidateMeasure();
	}

	public void removeChild(UIElement child) {
		if (child == null)
			return;

		child.setParent(null);
		children.remove(child);
		onChildRemoved(child);
		invalidateMeasure();
	}

	public void clearChildren() {
		for (UIElement child : children)
			child.setParent(null);
		children.clear();
		invalidateMeasure();
	}

	protected void onChildAdded(UIElement child) {
	}

	protected void onChildRemoved(UIElement child) {
	}

	@Override
	public void initialize() {
		boolean wasInitialized = initialized;
		if (!wasInitialized) {
			initialized = true;
		}

		for (UIElement child : children) {
			if (child != null)
				child.initialize();
		}

		if (!wasInitialized) {
			onInitialized();
			invalidateMeasure();
		}
	}

	@Override
	public void update() {
		for (UIElement child : children) {
			child.update();
		}
	}

	@Override
	public void dispose() {
		Iterator<UIElement> iter = getChildren().iterator();
		while (iter.hasNext()) {
			iter.next().dispose();
		}
		clearChildren();
	}

	@Override
	public Size measure(Size availableSize) {
		float maxWidth = 0f;
		float maxHeight = 0f;

		for (UIElement element : children) {
			if (!element.getProperty(UIElement.IsVisibleProperty))
				continue;

			element.measureCore(availableSize);
			Size resultingSize = element.getPreferredSize();

			if (resultingSize.width() > maxWidth)
				maxWidth = resultingSize.width();

			if (resultingSize.height() > maxHeight)
				maxHeight = resultingSize.height();
		}

		return new Size(maxWidth, maxHeight);
	}

	@Override
	public void arrange(Rectangle finalSize) {
		super.arrange(finalSize);

		Rectangle contentArea = getContentArea();
		for (UIElement element : children) {
			element.arrange(contentArea);
		}
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		if (getProperty(UIElement.IsVisibleProperty)) {
			if (getProperty(UIElement.ClipToBoundsProperty)) renderer.beginClip(actualSize);
			for (UIElement child : children) {
				if (child.getProperty(UIElement.IsVisibleProperty)) {
					child.draw(renderer, partialTicks);
				}
			}
			if (getProperty(UIElement.ClipToBoundsProperty)) renderer.endClip();
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// Propagate to children.
		boolean isHitTestVisible = getProperty(UIElement.IsHitTestVisibleProperty);
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);

		if (isHitTestVisible && isVisible) {
			Iterator<UIElement> iter = getChildren().iterator();
			while (iter.hasNext()) {
				iter.next().onMouseMove(event);
			}
		}

		super.onMouseMove(event);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		// Propagate to children.
		Iterator<UIElement> iter = getChildren().iterator();
		while (iter.hasNext()) {
			iter.next().onMouseClick(event);
			if (event.isCancelled())
				break;
		}

		super.onMouseClick(event);
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		// Propagate to children.
		Iterator<UIElement> iter = getChildren().iterator();
		while (iter.hasNext()) {
			iter.next().onMouseScroll(event);
			if (event.isCancelled())
				break;
		}
	}
}
