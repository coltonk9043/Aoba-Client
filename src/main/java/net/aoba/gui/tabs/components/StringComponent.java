package net.aoba.gui.tabs.components;

import java.util.ArrayList;
import net.aoba.gui.Color;
import net.aoba.gui.IGuiElement;
import net.aoba.misc.Colors;
import net.aoba.settings.types.ColorSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class StringComponent extends Component {
	private String originalText;
	private ArrayList<String> text;
	private boolean bold;
	private Color color;
	
	public StringComponent(String text, IGuiElement parent) {
		super(parent);
		setText(text);
		this.color = Colors.White;
		this.bold = false;
	}

	public StringComponent(String text, IGuiElement parent, boolean bold) {
		super(parent);
		setText(text);
		this.color = Colors.White;
		this.bold = bold;
	}
	
	public StringComponent(String text, IGuiElement parent, Color color, boolean bold) {
		super(parent);
		setText(text);
		this.color = color;
		this.bold = bold;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		int i = 0;
		for (String str : text) {
			if(bold)
				str = Formatting.BOLD + str;
			renderUtils.drawString(drawContext, str, actualX + 8, actualY + 8 + i, color.getColorAsInt());
			i += 30;
		}
	}

	/**
	 * Sets the text of the String Component.
	 * 
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.originalText = text;
		this.text = new ArrayList<String>();

		int strings = (int) ((text.length() * 5) / this.actualWidth);
		if (strings == 0) {
			this.text.add(text);
			this.setHeight(30);
		} else {
			int lengthOfEachSegment = text.length() / strings;

			for (int i = 0; i < strings; i++) {
				this.text.add(text.substring(lengthOfEachSegment * i, (lengthOfEachSegment * i) + lengthOfEachSegment));
			}
			this.setHeight(strings * 30);
		}
	}

	/**
	 * Gets the text of the String Component.
	 * 
	 * @return Text of the String Component as a string.
	 */
	
	public String getText() {
		return this.originalText;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnParentWidthChanged() {
		setText(originalText);
	}
}