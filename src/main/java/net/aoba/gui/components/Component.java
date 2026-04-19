/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.event.events.MouseScrollEvent;
import net.aoba.gui.UIElement;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.Size;
import net.aoba.rendering.Renderer2D;

public abstract class Component extends UIElement {
	public String header = null;
	private UIElement content;

	public Component() {
	}

	public UIElement getContent() {
		return content;
	}

	public void setContent(UIElement content) {
		if (this.content != null)
			this.content.setParent(null);

		this.content = content;

		if (content != null) {
			// Detach from previous parent.
			UIElement oldParent = content.getParent();
			if (oldParent instanceof Component oldComponent)
				oldComponent.removeChild(content);
			else if (oldParent instanceof PanelComponent oldPanel)
				oldPanel.removeChild(content);

			content.setParent(this);

			if (initialized && !content.isInitialized())
				content.initialize();
		}

		invalidateMeasure();
	}

	public void removeChild(UIElement child) {
		if (child != null && child == content) {
			content.setParent(null);
			content = null;
			invalidateMeasure();
		}
	}

	@Override
	public void initialize() {
		boolean wasInitialized = initialized;
		if (!wasInitialized) {
			initialized = true;
		}

		if (content != null)
			content.initialize();

		if (!wasInitialized) {
			onInitialized();
			invalidateMeasure();
		}
	}

	@Override
	public void update() {
		if (content != null)
			content.update();
	}

	@Override
	public void dispose() {
		if (content != null) {
			content.dispose();
			content.setParent(null);
			content = null;
		}
		super.dispose();
	}

	@Override
	public Size measure(Size availableSize) {
		if (content != null) {
			boolean isContentVisible = content.getProperty(UIElement.IsVisibleProperty);
			if(isContentVisible) {
				content.measureCore(availableSize);
				return new Size(content.getPreferredSize().width(), content.getPreferredSize().height());
			}
		}
		return new Size(0f, 0f);
	}

	@Override
	public void arrange(Rectangle finalSize) {
		super.arrange(finalSize);

		if (content != null) {
			Rectangle contentArea = getContentArea();
			content.arrange(contentArea);
		}
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		boolean clipToBounds = getProperty(UIElement.ClipToBoundsProperty);

		if (isVisible && content != null) {
			boolean isContentVisible = content.getProperty(UIElement.IsVisibleProperty);
			if(isContentVisible) {
				if (clipToBounds)
					renderer.beginClip(actualSize);
				content.draw(renderer, partialTicks);
				if (clipToBounds)
					renderer.endClip();
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// Propagate to children
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		boolean isHitTestVisible = getProperty(UIElement.IsHitTestVisibleProperty);
		if (isHitTestVisible && isVisible) {
			if (content != null)
				content.onMouseMove(event);
		}

		super.onMouseMove(event);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		// Propagate to children.
		if (content != null) {
			content.onMouseClick(event);
			if (event.isCancelled())
				return;
		}

		super.onMouseClick(event);
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (content != null) {
			content.onMouseScroll(event);
		}
	}
}
