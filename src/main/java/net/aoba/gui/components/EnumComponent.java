package net.aoba.gui.components;

import net.aoba.event.events.MouseClickEvent;
import net.aoba.event.events.MouseMoveEvent;
import net.aoba.gui.GuiManager;
import net.aoba.gui.IGuiElement;
import net.aoba.gui.Margin;
import net.aoba.gui.Rectangle;
import net.aoba.gui.colors.Color;
import net.aoba.settings.types.EnumSetting;
import net.aoba.utils.input.CursorStyle;
import net.aoba.utils.render.Render2D;
import net.aoba.utils.types.MouseAction;
import net.aoba.utils.types.MouseButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;


public class EnumComponent<T extends Enum<T>> extends Component {
	private EnumSetting<T> enumSetting;

	private boolean hoveringLeftButton;
	private boolean hoveringRightButton;
	
	public EnumComponent(IGuiElement parent, EnumSetting<T> enumSetting) {
		super(parent, new Rectangle(null, null, null, 60f));
		this.enumSetting = enumSetting;

		this.setMargin(new Margin(8f, 2f, 8f, 2f));
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks) {
		super.draw(drawContext, partialTicks);

		float actualX = actualSize.getX();
		float actualY = actualSize.getY();
		float actualWidth = actualSize.getWidth();

		Render2D.drawString(drawContext, enumSetting.displayName, actualX, actualY + 8, 0xFFFFFF);
		
		// Left Arrow and Right Arrow
		Render2D.drawString(drawContext, "<", actualX + 4, actualY + 34, hoveringLeftButton ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);
		Render2D.drawString(drawContext, ">", actualX + actualWidth - 8, actualY + 34, hoveringRightButton ? GuiManager.foregroundColor.getValue().getColorAsInt() : 0xFFFFFF);
		
		// Text
		String enumValue = this.enumSetting.getValue().toString();
		float stringWidth = Render2D.getStringWidth(enumValue);
		Render2D.drawString(drawContext, enumValue, actualX + (actualWidth / 2.0f) - stringWidth, actualY + 30, 0xFFFFFF);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			if (hovered) {
				T currentValue = enumSetting.getValue();
				T[] enumConstants = currentValue.getDeclaringClass().getEnumConstants();
				int currentIndex = java.util.Arrays.asList(enumConstants).indexOf(currentValue);
				int enumCount = enumConstants.length;
	
				float actualX = actualSize.getX();
				float actualY = actualSize.getY();
				float actualWidth = actualSize.getWidth();
				
				Rectangle leftArrowHitbox = new Rectangle(actualX, actualY + 34, 16.0f, 16.0f);
				Rectangle rightArrowHitbox = new Rectangle(actualX + actualWidth - 18.0f, actualY + 34.0f, 16.0f, 16.0f);
				if(leftArrowHitbox.intersects((float)event.mouseX, (float)event.mouseY)) 
					currentIndex = (currentIndex - 1 + enumCount) % enumCount;
				else if (rightArrowHitbox.intersects((float)event.mouseX, (float)event.mouseY)) 
					currentIndex = (currentIndex + 1) % enumCount;
				
				enumSetting.setValue(enumConstants[currentIndex]);
				event.cancel();
			}
		}
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		super.onMouseMove(event);
		
		float actualX = actualSize.getX();
		float actualY = actualSize.getY();
		float actualWidth = actualSize.getWidth();
		
		Rectangle leftArrowHitbox = new Rectangle(actualX, actualY + 34, 16.0f, 16.0f);
		Rectangle rightArrowHitbox = new Rectangle(actualX + actualWidth - 18.0f, actualY + 34.0f, 16.0f, 16.0f);
		
		boolean wasHoveringLeftButton = hoveringLeftButton;
		boolean wasHoveringRightButton = hoveringRightButton;
		hoveringLeftButton = leftArrowHitbox.intersects((float)event.getX(), (float)event.getY());
		hoveringRightButton = rightArrowHitbox.intersects((float)event.getX(), (float)event.getY());
		
		if(hoveringLeftButton || hoveringRightButton)
			GuiManager.setCursor(CursorStyle.Click);
		else if(wasHoveringLeftButton || wasHoveringRightButton){
			GuiManager.setCursor(CursorStyle.Default);
		}
	}
}
