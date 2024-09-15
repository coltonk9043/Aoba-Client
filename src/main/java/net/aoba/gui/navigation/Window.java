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

package net.aoba.gui.navigation;

import net.aoba.Aoba;
import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.Direction;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.gui.components.Component;
import net.aoba.settings.SettingManager;
import net.aoba.settings.types.RectangleSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.Iterator;
import org.joml.Matrix4f;

public class Window implements IGuiElement {
	protected static MinecraftClient MC = MinecraftClient.getInstance();

	protected String ID;
	protected String title;
	protected Page parent;

	public RectangleSetting position;
	public Float minWidth = 180.0f;
	public Float minHeight = 50.0f;
	public Float maxWidth = null;
	public Float maxHeight = null;

	public boolean isMouseOver = false;
	public boolean isMoving = false;
	public boolean isResizing = false;

	private Identifier icon = null;

	public boolean moveable = true;
	public boolean resizeable = true;

	public Direction grabDirection = Direction.None;
	protected boolean visible = false;

	// Mouse Variables
	protected boolean inheritHeightFromChildren = false;

	public ArrayList<Component> children = new ArrayList<>();

	public Window(String ID, float x, float y, float width, float height) {
		this.parent = null;
		this.ID = ID;
		this.title = ID;
		this.position = new RectangleSetting(ID + "_position", ID + "Position", new Rectangle(x, y, width, height),
				(Rectangle vec) -> UpdateAll());

		SettingManager.registerSetting(position, Aoba.getInstance().settingManager.configContainer);
	}

	public boolean getInheritsHeightFromChildren() {
		return this.inheritHeightFromChildren;
	}
	
	public void setInheritHeightFromChildren(boolean state) {
		this.inheritHeightFromChildren = state;
		UpdateAll();
	}
	
	public void UpdateAll() {
		if (this.inheritHeightFromChildren) {
			applyInherittedHeight();
		}
		for (Component component : this.children) {
			component.onParentChanged();
		}
	}

	public String getID() {
		return ID;
	}

	@Override
	public Rectangle getSize() {
		Rectangle position = this.position.getValue();
		return position;
	}

	@Override
	public Rectangle getActualSize() {
		Rectangle position = this.position.getValue();
		return position;
	}

	@Override
	public void setSize(Rectangle size) {
		this.position.setValue(size);
	}

	public void setX(float x) {
		position.setX(x);
	}

	public void setY(float y) {
		position.setY(y);
	}

	public float getX() {
		return position.getX();
	}

	public float getY() {
		return position.getY();
	}

	public void setWidth(float width) {
		position.setWidth(width);
	}

	public void setHeight(float height) {
		position.setHeight(height);
	}

	@Override
	public void onParentChanged() {
		// Do nothing because this should always be top level!!!
	}

	@Override
	public void onVisibilityChanged() {
		// Do nothing?
	}

	@Override
	public void onChildAdded(Component child) {
		if (this.inheritHeightFromChildren) {
			applyInherittedHeight();
		}
	}

	@Override
	public void onChildRemoved(Component child) {
		if (this.inheritHeightFromChildren) {
			applyInherittedHeight();
		}
	}
	
	@Override
	public void onChildChanged(Component changedChild) {
		if (this.inheritHeightFromChildren) {
			applyInherittedHeight();
		}
	}

	@Override
	public void addChild(Component child) {
		children.add(child);
		onChildAdded(child);
	}

	@Override
	public void removeChild(Component child) {
		children.remove(child);
		child.dispose();
		onChildRemoved(child);
	}

	public void dispose() {
		for (Component child : children) {
			child.dispose();
		}
		children.clear();
	}

	private void applyInherittedHeight() {
		float tempHeight = 0;
		for (Component child : children) {
			Float height = child.getSize().getHeight();
			if(height != null) {
				tempHeight += height.floatValue();
			}
			
		}
		setHeight(tempHeight);
		minHeight = tempHeight;
	}

	public boolean getVisible() {
		return this.visible;
	}

	public void setVisible(boolean state) {
		if (visible != state) {
			this.visible = state;
			for (Component component : children) {
				component.setVisible(state);
			}
		}
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public void update() {
		for (Component child : children) {
			child.update();
		}
	}

	public void draw(DrawContext drawContext, float partialTicks) {
		MatrixStack matrixStack = drawContext.getMatrices();
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

		Rectangle pos = position.getValue();
		if (pos.isDrawable()) {

			float x = pos.getX().floatValue();
			float y = pos.getY().floatValue();
			float width = pos.getWidth().floatValue();
			float height = pos.getHeight().floatValue();

			// Draws background depending on components width and height
			Render2D.drawRoundedBox(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(),
					GuiManager.backgroundColor.getValue());
			Render2D.drawRoundedBoxOutline(matrix4f, x, y, width, height, GuiManager.roundingRadius.getValue(),
					GuiManager.borderColor.getValue());

			if (icon != null) {
				Render2D.drawTexturedQuad(matrix4f, icon, x + 8, y + 4, 22, 22, GuiManager.foregroundColor.getValue());
				Render2D.drawString(drawContext, this.title, x + 38, y + 8, GuiManager.foregroundColor.getValue());
			} else
				Render2D.drawString(drawContext, this.title, x + 8, y + 8, GuiManager.foregroundColor.getValue());

			Render2D.drawLine(matrix4f, x, y + 30, x + width, y + 30, new Color(0, 0, 0, 100));

			for (Component child : children) {
				child.draw(drawContext, partialTicks);
			}
		}
	}

	protected void setResizing(boolean state, MouseClickEvent event, Direction direction) {
		if (state) {
			parent.moveToFront(this);
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
	
	public void onMouseMove(MouseMoveEvent event) {
		// Propagate to children.
		Iterator<Component> tabIterator = children.iterator();
		while (tabIterator.hasNext()) {
			tabIterator.next().onMouseMove(event);
		}

		if (!event.isCancelled() && getVisible()) {
			double mouseX = event.getX();
			double mouseY = event.getY();
			double mouseDeltaX = event.getDeltaX();
			double mouseDeltaY = event.getDeltaY();

			Rectangle pos = position.getValue();

			if (this.isMoving) {
				float targetX = this.getSize().getX() + (float) mouseDeltaX;
				float targetY = this.getSize().getY() + (float) mouseDeltaY;

				float currentX = this.getX();
				float currentY = this.getY();

				float interpolatedX = lerp(currentX, targetX, GuiManager.dragSmoothening.getValue());
				float interpolatedY = lerp(currentY, targetY, GuiManager.dragSmoothening.getValue());

				this.setX(interpolatedX);
				this.setY(interpolatedY);
			} else if (this.isResizing) {
				switch (grabDirection) {
				case Direction.Top:
					float newHeightTop = getSize().getHeight() - (float) mouseDeltaY;

					if (minHeight != null && newHeightTop < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightTop > maxHeight.floatValue())
						break;

					setY(getSize().getY() + (float) mouseDeltaY);
					setHeight(newHeightTop);

					break;
				case Direction.Bottom:
					float newHeightBottom = getSize().getHeight() + (float) mouseDeltaY;

					if (minHeight != null && newHeightBottom < minHeight.floatValue())
						break;

					if (maxHeight != null && newHeightBottom > maxHeight.floatValue())
						break;

					setHeight(newHeightBottom);
					break;
				case Direction.Left:
					float newWidthLeft = getSize().getWidth() - (float) mouseDeltaX;
					if (minWidth != null && newWidthLeft < minWidth.floatValue())
						break;

					if (maxWidth != null && newWidthLeft > maxWidth.floatValue())
						break;

					setX(getSize().getX() + (float) mouseDeltaX);
					setWidth(newWidthLeft);
					break;
				case Direction.Right:
					float newWidthRight = getSize().getWidth() + (float) mouseDeltaX;
					if (minWidth != null && newWidthRight < minWidth.floatValue())
						break;

					if (maxWidth != null && newWidthRight > maxWidth.floatValue())
						break;

					setWidth(newWidthRight);
					break;
				default:
					break;
				}
			}

			// Cancel the event if the mouse is over this HUD (prevents other from being
			// hovered if no action occurs)
			isMouseOver = pos.intersects((float) mouseX, (float) mouseY);
			if (isMouseOver)
				event.cancel();
		} else {
			isMouseOver = false;
		}
	}

	public void OnMouseClick(MouseClickEvent event) {
		// Propagate to children.
		Iterator<Component> tabIterator = children.iterator();
		while (tabIterator.hasNext()) {
			tabIterator.next().onMouseClick(event);
		}

		// Check to see if the event is cancelled. If not, execute branch.
		if (!event.isCancelled()) {
			if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
				float mouseX = (float) event.mouseX;
				float mouseY = (float) event.mouseY;

				Rectangle pos = position.getValue();
				
				if (resizeable) {
					Rectangle topHitbox = new Rectangle(pos.getX(), pos.getY() - 8, pos.getWidth(), 8.0f);
					Rectangle leftHitbox = new Rectangle(pos.getX() - 8, pos.getY(), 8.0f, pos.getHeight());
					Rectangle rightHitbox = new Rectangle(pos.getX() + pos.getWidth(), pos.getY(), 8.0f, pos.getHeight());
					Rectangle bottomHitbox = new Rectangle(pos.getX(), pos.getY() + pos.getHeight(), pos.getWidth(), 8.0f);

					if (leftHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Left);
					else if (rightHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Right);
					else if (topHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Top);
					else if (bottomHitbox.intersects(mouseX, mouseY))
						setResizing(true, event, Direction.Bottom);
					else
						setResizing(false, event, Direction.None);
				}

				if (moveable && !isResizing) {
					if (pos.intersects(mouseX, mouseY)) {
						GuiManager.setCursor(CursorStyle.Click);
						parent.moveToFront(this);
						isMoving = true;
						event.cancel();
						return;
					}
				}
			} else if (event.button == MouseButton.LEFT && event.action == MouseAction.UP) {
				if(isMoving || isResizing) {
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