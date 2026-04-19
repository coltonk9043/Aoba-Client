/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.components;

import java.util.function.Consumer;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.UIProperty;
import net.aoba.gui.types.Size;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class ScrollbarComponent extends Component {
	private static final float TRACK_WIDTH = 10f;
	private static final float MIN_THUMB_HEIGHT = 20f;

	private float viewportHeight = 0f;
	private float totalContentHeight = 0f;

	private boolean dragging = false;
	private float dragStartY = 0f;
	private float dragStartOffset = 0f;

	public static final UIProperty<Float> ScrollOffsetProperty = new UIProperty<>("ScrollOffset", 0f, false, true, ScrollbarComponent::onScrollOffsetChanged, ScrollbarComponent::coerceScrollOffset);

 	private Consumer<Float> onScrollChanged;

 	private static void onScrollOffsetChanged(UIElement sender, Float oldValue, Float newValue) {
 		if(sender instanceof ScrollbarComponent scrollbar) {
 			if (scrollbar.onScrollChanged != null)
 				scrollbar.onScrollChanged.accept(newValue);
 		}
 	}

 	private static Float coerceScrollOffset(UIElement sender, Float value) {
 		if (sender instanceof ScrollbarComponent sb && value != null) {
 	 		float max = sb.getMaxScroll();
 	 		return Math.max(0f, Math.min(max, value));
 		}
 		return value;
 	}
 	
	public ScrollbarComponent() {
		setProperty(UIElement.WidthProperty, TRACK_WIDTH);
		bindProperty(BackgroundProperty, GuiManager.panelBackgroundColor);
	}

	public void setScrollState(float viewportHeight, float totalContentHeight) {
		this.viewportHeight = viewportHeight;
		this.totalContentHeight = totalContentHeight;
	}

	public void setOnScrollChanged(Consumer<Float> onScrollChanged) {
		this.onScrollChanged = onScrollChanged;
	}

	public boolean isScrollable() {
		return totalContentHeight > viewportHeight;
	}

	private float getMaxScroll() {
		return Math.max(0f, totalContentHeight - viewportHeight);
	}

	private float getThumbHeight() {
		float trackHeight = actualSize.height();
		float ratio = viewportHeight / totalContentHeight;
		return Math.max(MIN_THUMB_HEIGHT, trackHeight * ratio);
	}

	private float getThumbY() {
		float trackY = actualSize.y();
		float trackHeight = actualSize.height();
		float thumbHeight = getThumbHeight();
		float maxScroll = getMaxScroll();
		Float scrollOffset = getProperty(ScrollOffsetProperty);
		float scrollRatio = maxScroll > 0 ? scrollOffset / maxScroll : 0f;
		return trackY + scrollRatio * (trackHeight - thumbHeight);
	}

	@Override
	public Size measure(Size availableSize) {
		return new Size(TRACK_WIDTH, availableSize.height());
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);

		if (!isVisible || !isScrollable())
			return;

		float x = actualSize.x();
		float y = actualSize.y();
		float w = actualSize.width();
		float h = actualSize.height();

		Shader bgEffect = getProperty(BackgroundProperty);
		if (bgEffect != null)
			renderer.drawBox(x, y, w, h, bgEffect);

		float thumbHeight = getThumbHeight();
		float thumbY = getThumbY();

		Shader thumbEffect = GuiManager.foregroundColor.getValue();

		renderer.drawRoundedBox(x + 1f, thumbY, w - 2f, thumbHeight, getProperty(CornerRadiusProperty), thumbEffect);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (!isScrollable())
			return;

		if (event.button == MouseButton.LEFT) {
			if (event.action == MouseAction.DOWN) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				if (actualSize.intersects(mouseX, mouseY)) {
					float thumbHeight = getThumbHeight();
					float thumbY = getThumbY();

					if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
						Float scrollOffset = getProperty(ScrollOffsetProperty);
						dragging = true;
						dragStartY = mouseY;
						dragStartOffset = scrollOffset;
					} else {
						float trackY = actualSize.y();
						float trackHeight = actualSize.height();
						float clickRatio = (mouseY - trackY - thumbHeight / 2f) / (trackHeight - thumbHeight);
						clickRatio = Math.max(0f, Math.min(1f, clickRatio));
						setProperty(ScrollbarComponent.ScrollOffsetProperty, clickRatio * getMaxScroll());
					}

					event.cancel();
				}
			} else if (event.action == MouseAction.UP) {
				dragging = false;
			}
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);

		if (!isScrollable()) {
			return;
		}

		float thumbHeight = getThumbHeight();

		if (dragging) {
			float trackHeight = actualSize.height();
			float scrollableTrack = trackHeight - thumbHeight;
			if (scrollableTrack > 0) {
				float deltaY = (float) event.getY() - dragStartY;
				float newOffset = dragStartOffset + (deltaY / scrollableTrack) * getMaxScroll();
				setProperty(ScrollbarComponent.ScrollOffsetProperty, newOffset);
			}
		}
	}
}
