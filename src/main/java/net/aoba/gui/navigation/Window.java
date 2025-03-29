/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.navigation;

import java.util.List;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.Direction;
import net.aoba.gui.GuiManager;
import net.aoba.gui.Rectangle;
import net.aoba.gui.ResizeMode;
import net.aoba.gui.Size;
import net.aoba.gui.UIElement;
import net.aoba.managers.SettingManager;
import net.aoba.settings.types.RectangleSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;

public class Window extends UIElement {
	protected String ID;
	protected Page parentPage;

	public RectangleSetting position;

	public boolean isMoving = false;
	public boolean isResizing = false;

	public boolean moveable = true;
	public ResizeMode resizeMode = ResizeMode.WidthAndHeight;

	public Direction grabDirection = Direction.None;

	public Window(String ID, float x, float y) {
		this(ID, x, y, 180f, 50f);
	}

	public Window(String ID, float x, float y, float width, float height) {
		this.ID = ID;
		minWidth = 180.0f;
		minHeight = 50.0f;

		visible = false;
		position = RectangleSetting.builder().id(ID + "_position").displayName(ID + "Position")
				.defaultValue(new Rectangle(x, y, width, height)).onUpdate((Rectangle vec) -> {
					setSize(vec.getWidth(), vec.getHeight());
					invalidateArrange();
				}).build();

		setSize(getWidth(), getHeight());
		SettingManager.registerGlobalSetting(position);
	}

	@Override
	public Rectangle getActualSize() {
		Rectangle newSize = actualSize;
		if (position.getX() != null)
			newSize.setX(position.getX());

		if (position.getY() != null)
			newSize.setY(position.getY());

		return newSize;
	}

	@Override
	public void setWidth(Float width) {
		position.setWidth(width);
	}

	@Override
	public void setHeight(Float height) {
		position.setHeight(height);
	}

	public String getID() {
		return ID;
	}

	public void draw(DrawContext drawContext, float partialTicks) {
		float actualX = getActualSize().getX();
		float actualY = getActualSize().getY();
		float actualWidth = getActualSize().getWidth();
		float actualHeight = getActualSize().getHeight();

		// Draws background depending on components width and height
		Render2D.drawOutlinedRoundedBox(drawContext, actualX, actualY, actualWidth, actualHeight,
				GuiManager.roundingRadius.getValue(), GuiManager.borderColor.getValue(),
				GuiManager.backgroundColor.getValue());
		List<UIElement> children = getChildren();
		for (UIElement child : children) {
			child.draw(drawContext, partialTicks);
		}
	}

	@Override
	protected Size getStartingSize(Size availableSize) {
		// Account for minimum size.

		return availableSize;
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

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// Propagate to children ONLY if the user is not moving or resizing the window.
		if (!isMoving && !isResizing) {
			super.onMouseMove(event);
		}

		if (!event.isCancelled() && isVisible()) {
			double mouseX = event.getX();
			double mouseY = event.getY();
			double mouseDeltaX = event.getDeltaX();
			double mouseDeltaY = event.getDeltaY();

			Rectangle pos = getActualSize();

			if (isMoving) {
				float targetX = pos.getX() + (float) mouseDeltaX;
				float targetY = pos.getY() + (float) mouseDeltaY;

				float currentX = position.getX();
				float currentY = position.getY();

				float interpolatedX = lerp(currentX, targetX, GuiManager.dragSmoothening.getValue());
				float interpolatedY = lerp(currentY, targetY, GuiManager.dragSmoothening.getValue());

				position.setX(interpolatedX);
				position.setY(interpolatedY);
			} else if (isResizing) {
				switch (grabDirection) {
				case Top:
					float newHeightTop = getActualSize().getHeight() - (float) mouseDeltaY;

					if (minHeight != null && newHeightTop < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightTop > maxHeight.floatValue())
						break;

					position.setY(getActualSize().getY() + (float) mouseDeltaY);
					position.setHeight(newHeightTop);
					break;
				case Bottom:
					float newHeightBottom = getActualSize().getHeight() + (float) mouseDeltaY;

					if (minHeight != null && newHeightBottom < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightBottom > maxHeight.floatValue())
						break;

					position.setHeight(newHeightBottom);
					break;
				case Left:
					float newWidthLeft = getActualSize().getWidth() - (float) mouseDeltaX;
					if (minWidth != null && newWidthLeft < minWidth.floatValue())
						break;

					if (maxWidth != null && newWidthLeft > maxWidth.floatValue())
						break;

					position.setX(getActualSize().getX() + (float) mouseDeltaX);
					position.setWidth(newWidthLeft);
					break;
				case Right:
					float newWidthRight = getActualSize().getWidth() + (float) mouseDeltaX;
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

	public void onMouseClick(MouseClickEvent event) {
		// Propagate to children.
		super.onMouseClick(event);

		// Check to see if the event is cancelled. If not, execute branch.
		if (!event.isCancelled()) {
			if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				Rectangle pos = getActualSize();

				if (resizeMode != ResizeMode.None) {
					Rectangle topHitbox = new Rectangle(pos.getX(), pos.getY() - 8, pos.getWidth(), 8.0f);
					Rectangle leftHitbox = new Rectangle(pos.getX() - 8, pos.getY(), 8.0f, pos.getHeight());
					Rectangle rightHitbox = new Rectangle(pos.getX() + pos.getWidth(), pos.getY(), 8.0f,
							pos.getHeight());
					Rectangle bottomHitbox = new Rectangle(pos.getX(), pos.getY() + pos.getHeight(), pos.getWidth(),
							8.0f);

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

	public float lerp(float start, float end, float alpha) {
		return start + alpha * (end - start);
	}
}