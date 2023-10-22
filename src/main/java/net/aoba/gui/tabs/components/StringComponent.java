package net.aoba.gui.tabs.components;

import java.util.ArrayList;
import net.aoba.gui.Color;
import net.aoba.gui.IHudElement;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.gui.DrawContext;

public class StringComponent extends Component {

	private boolean bold;
	private String originalText;
	private ArrayList<String> text;
	
	public StringComponent(String text, IHudElement parent) {
		super(parent);
		this.originalText = text;
		this.text = new ArrayList<String>();
		
		int strings = (int) ((text.length() * 15) / (parent.getWidth() - 20));
		System.out.println(text.length() * 5);
		
		if(strings <= 1) {
			this.text.add(text);
			this.setHeight(30);
		}else {
			int lengthOfEachSegment = text.length() / strings;
			
			for(int i = 0; i < strings; i++) {
				this.text.add(text.substring(lengthOfEachSegment * i, (lengthOfEachSegment * i) + lengthOfEachSegment));
			}
			this.setHeight(strings * 30);
		}
		
		this.bold = false;
	}
	
	public StringComponent(String text, IHudElement parent, boolean bold) {
		super(parent);
		this.originalText = text;
		this.text = new ArrayList<String>();
		
		int strings = (int) ((text.length() * 5) / parent.getWidth());
		System.out.println(text.length() * 5);
		
		if(strings == 0) {
			this.text.add(text);
			this.setHeight(30);
		}else {
			int lengthOfEachSegment = text.length() / strings;
			
			for(int i = 0; i < strings; i++) {
				this.text.add(text.substring(lengthOfEachSegment * i, (lengthOfEachSegment * i) + lengthOfEachSegment));
			}
			this.setHeight(strings * 30);
		}
		
		this.bold = bold;
	}

	@Override
	public void draw(DrawContext drawContext, float partialTicks, Color color) {
		int i = 0;
		for(String str : text) {
			renderUtils.drawString(drawContext, str, actualX + 10,
					actualY + 8 + i, 0xFFFFFF);
			i += 30;
		}
	}
	
	/**
	 * Sets the text of the String Component.
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.originalText = text;
		this.text = new ArrayList<String>();
		
		int strings = (int) ((text.length() * 5) / parent.getWidth());
		if(strings == 0) {
			this.text.add(text);
			this.setHeight(30);
		}else {
			int lengthOfEachSegment = text.length() / strings;
			
			for(int i = 0; i < strings; i++) {
				this.text.add(text.substring(lengthOfEachSegment * i, (lengthOfEachSegment * i) + lengthOfEachSegment));
			}
			this.setHeight(strings * 30);
		}
	}
	
	/**
	 * Gets the text of the String Component.
	 * @return Text of the String Component as a string.
	 */
	public String getText() {
		return this.originalText;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}