/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.UIElement;
import net.aoba.gui.components.Component;
import net.aoba.gui.types.Direction;
import net.aoba.gui.types.Rectangle;
import net.aoba.gui.types.ResizeMode;
import net.aoba.gui.types.Size;
import net.aoba.gui.types.SizeToContent;
import net.aoba.gui.types.Thickness;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.RectangleSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.rendering.Renderer2D;
import net.aoba.rendering.shaders.Shader;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;

public class Window extends Component {
	protected String ID;
	protected Page parentPage;

	public RectangleSetting position;

	public boolean isMoving = false;
	public boolean isResizing = false;

	protected boolean moveable = true;
	protected SizeToContent sizeToContent = SizeToContent.None;
	protected ResizeMode resizeMode = ResizeMode.WidthAndHeight;

	public Direction grabDirection = Direction.None;

	public Window(String ID, float x, float y) {
		this(ID, x, y, 180f, 50f);
	}

	public Window(String ID, float x, float y, float width, float height) {
		super();
		this.ID = ID;
		setProperty(UIElement.MinWidthProperty, 180.0f);
		setProperty(UIElement.MinHeightProperty, 50.0f);
		setProperty(UIElement.PaddingProperty, new Thickness(12f));
		setProperty(UIElement.IsVisibleProperty, false);

		bindProperty(ForegroundProperty, GuiManager.foregroundColor);
		bindProperty(BackgroundProperty, GuiManager.windowBackgroundColor);
		bindProperty(BorderProperty, GuiManager.windowBorderColor);
		bindProperty(CornerRadiusProperty, GuiManager.roundingRadius);
		bindProperty(BorderThicknessProperty, GuiManager.lineThickness);
		bindProperty(FontProperty, GuiManager.fontSetting);

		position = RectangleSetting.builder().id(ID + "_position").displayName(ID + "Position")
				.defaultValue(new Rectangle(x, y, width, height)).onUpdate((Rectangle _) -> {
					invalidateMeasure();
				}).build();

		SettingManager.registerGlobalSetting(position);
	}

	@Override
	protected void onInitialized() {
		Float minWidth = getProperty(UIElement.MinWidthProperty);
		Float maxWidth = getProperty(UIElement.MaxWidthProperty);
		Float minHeight = getProperty(UIElement.MinHeightProperty);
		Float maxHeight = getProperty(UIElement.MaxHeightProperty);
		Thickness padding = getProperty(UIElement.PaddingProperty);

		boolean autoWidth = sizeToContent == SizeToContent.Width || sizeToContent == SizeToContent.Both;
		boolean autoHeight = sizeToContent == SizeToContent.Height || sizeToContent == SizeToContent.Both;

		float w = position.getWidth();
		if (w == 0f)
			w = 180f;
		if (minWidth != null && w < minWidth)
			w = minWidth;
		if (maxWidth != null && w > maxWidth)
			w = maxWidth;

		float h = position.getHeight();
		if (h == 0f)
			h = 50f;

		if (autoWidth || autoHeight) {
			UIElement content = getContent();
			if (content != null) {
				float contentW = w;
				if (padding != null)
					contentW -= padding.horizontalSum();
				content.measureCore(new Size(contentW, Float.MAX_VALUE));
				Size ps = content.getPreferredSize();
				if (autoWidth) {
					w = ps.width();
					if (padding != null)
						w += padding.horizontalSum();
				}
				if (autoHeight) {
					h = ps.height();
					if (padding != null)
						h += padding.verticalSum();
				}
			}
		}

		if (minWidth != null && w < minWidth)
			w = minWidth;
		if (maxWidth != null && w > maxWidth)
			w = maxWidth;
		if (minHeight != null && h < minHeight)
			h = minHeight;
		if (maxHeight != null && h > maxHeight)
			h = maxHeight;

		position.setWidth(w);
		position.setHeight(h);
	}

	@Override
	public Rectangle getActualSize() {
		return new Rectangle(position.getX(), position.getY(), actualSize.width(), actualSize.height());
	}

	public SizeToContent getSizeToContent() {
		return sizeToContent;
	}

	public void setSizeToContent(SizeToContent sizeToContent) {
		if (this.sizeToContent != sizeToContent) {
			this.sizeToContent = sizeToContent;
			invalidateMeasure();
		}
	}

	@Override
	public void invalidateMeasure() {
		if (!initialized)
			return;

		measureDirty = true;
		Float minWidth = getProperty(UIElement.MinWidthProperty);
		Float maxWidth = getProperty(UIElement.MaxWidthProperty);
		Float minHeight = getProperty(UIElement.MinHeightProperty);
		Float maxHeight = getProperty(UIElement.MaxHeightProperty);

		float w = position.getWidth();
		float h = position.getHeight();

		// Apply min/max constraints.
		if (minWidth != null && w < minWidth)
			w = minWidth;
		if (maxWidth != null && w > maxWidth)
			w = maxWidth;
		if (minHeight != null && h < minHeight)
			h = minHeight;
		if (maxHeight != null && h > maxHeight)
			h = maxHeight;

		measureCore(new Size(w, h));

		// Fit to content size if enabled.
		if (sizeToContent != SizeToContent.None) {
			Size ps = getPreferredSize();
			boolean sizeWidth = sizeToContent == SizeToContent.Width || sizeToContent == SizeToContent.Both;
			boolean sizeHeight = sizeToContent == SizeToContent.Height || sizeToContent == SizeToContent.Both;

			if (sizeWidth) {
				w = ps.width();
				if (minWidth != null && w < minWidth)
					w = minWidth;
				if (maxWidth != null && w > maxWidth)
					w = maxWidth;
				position.setWidth(w);
			}

			if (sizeHeight) {
				h = ps.height();
				if (minHeight != null && h < minHeight)
					h = minHeight;
				if (maxHeight != null && h > maxHeight)
					h = maxHeight;
				position.setHeight(h);
			}
		}

		float x = position.getX();
		float y = position.getY();
		arrange(new Rectangle(x, y, w, h));
	}

	public String getID() {
		return ID;
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		Rectangle size = getActualSize();
		float actualX = size.x();
		float actualY = size.y();
		float actualWidth = size.width();
		float actualHeight = size.height();

		Float radius = getProperty(CornerRadiusProperty);
		Float borderThickness = getProperty(BorderThicknessProperty);
		float r = radius != null ? radius : 0f;
		float t = borderThickness != null ? borderThickness : 0f;

		Shader bgEffect = getProperty(BackgroundProperty);
		Shader bdEffect = getProperty(BorderProperty);

		if (bgEffect != null) {
			renderer.drawRoundedBox(actualX, actualY, actualWidth, actualHeight, r, bgEffect);
		}
		if (bdEffect != null) {
			renderer.drawRoundedBoxOutline(actualX, actualY, actualWidth, actualHeight, r, t, bdEffect);
		}

		super.draw(renderer, partialTicks);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (!isMoving && !isResizing) {
			super.onMouseMove(event);
		}

		if (!event.isCancelled() && getProperty(UIElement.IsVisibleProperty)) {
			double mouseX = event.getX();
			double mouseY = event.getY();
			double mouseDeltaX = event.getDeltaX();
			double mouseDeltaY = event.getDeltaY();

			Float minWidth = getProperty(UIElement.MinWidthProperty);
			Float maxWidth = getProperty(UIElement.MaxWidthProperty);
			Float minHeight = getProperty(UIElement.MinHeightProperty);
			Float maxHeight = getProperty(UIElement.MaxHeightProperty);

			Rectangle pos = getActualSize();

			setProperty(UIElement.IsHoveredProperty, pos.intersects((float) mouseX, (float) mouseY));

			if (isMoving) {
				float targetX = pos.x() + (float) mouseDeltaX;
				float targetY = pos.y() + (float) mouseDeltaY;

				position.setX(targetX);
				position.setY(targetY);
			} else if (isResizing) {
				float posHeight = pos.height();
				float posWidth = pos.width();
				switch (grabDirection) {
				case Top:
					float newHeightTop = posHeight - (float) mouseDeltaY;

					if (minHeight != null && newHeightTop < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightTop > maxHeight.floatValue())
						break;

					position.setY(pos.y() + (float) mouseDeltaY);
					position.setHeight(newHeightTop);
					break;
				case Bottom:
					float newHeightBottom = posHeight + (float) mouseDeltaY;

					if (minHeight != null && newHeightBottom < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightBottom > maxHeight.floatValue())
						break;

					position.setHeight(newHeightBottom);
					break;
				case Left:
					float newWidthLeft = posWidth - (float) mouseDeltaX;
					if (minWidth != null && newWidthLeft < minWidth.floatValue())
						break;

					if (maxWidth != null && newWidthLeft > maxWidth.floatValue())
						break;

					position.setX(pos.x() + (float) mouseDeltaX);
					position.setWidth(newWidthLeft);
					break;
				case Right:
					float newWidthRight = posWidth + (float) mouseDeltaX;
					if (minWidth != null && newWidthRight < minWidth.floatValue())
						break;

					if (maxWidth != null && newWidthRight > maxWidth.floatValue())
						break;
					position.setWidth(newWidthRight);
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (!event.isCancelled()) {
			if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				Rectangle pos = getActualSize();

				if (resizeMode != ResizeMode.None) {
					Rectangle topHitbox = new Rectangle(pos.x(), pos.y() - 8, pos.width(), 8.0f);
					Rectangle leftHitbox = new Rectangle(pos.x() - 8, pos.y(), 8.0f, pos.height());
					Rectangle rightHitbox = new Rectangle(pos.x() + pos.width(), pos.y(), 8.0f, pos.height());
					Rectangle bottomHitbox = new Rectangle(pos.x(), pos.y() + pos.height(), pos.width(), 8.0f);

					boolean resizableWidth = resizeMode == ResizeMode.Width || resizeMode == ResizeMode.WidthAndHeight;
					boolean resizableHeight = resizeMode == ResizeMode.Height
							|| resizeMode == ResizeMode.WidthAndHeight;

					if (resizableWidth && leftHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Left);
					else if (resizableWidth && rightHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Right);
					else if (resizableHeight && topHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Top);
					else if (resizableHeight && bottomHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Bottom);
					else
						setResizing(false, event, Direction.None);
				}

				if (moveable && !isResizing) {
					if (pos.intersects(mouseX, mouseY)) {
						GuiManager.setCursor(CursorStyle.Click);
						parentPage.moveToFront(this);
						isMoving = true;
						event.cancel();
					}
				}
			} else if (event.button == MouseButton.LEFT && event.action == MouseAction.UP) {
				if (isMoving || isResizing) {
					isMoving = false;
					isResizing = false;
					GuiManager.setCursor(CursorStyle.Default);
				}
			}
		}
	}

	protected void setResizing(boolean state, MouseClickEvent event, Direction direction) {
		if (state) {
			parentPage.moveToFront(this);
			switch (direction) {
			case Left:
			case Right:
				GuiManager.setCursor(CursorStyle.HorizonalResize);
				break;
			case Top:
			case Bottom:
				GuiManager.setCursor(CursorStyle.VerticalResize);
				break;
			case None:
			default:
				break;
			}
			event.cancel();
		}
		isMoving = false;
		isResizing = state;
		grabDirection = direction;
	}

	public float lerp(float start, float end, float alpha) {
		return start + alpha * (end - start);
	}
}
