package net.aoba.gui.tabs.components;

import java.util.ArrayList;

import net.aoba.gui.Color;
import net.aoba.gui.tabs.ClickGuiTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class StringComponent extends Component {

	private boolean bold;
	private String originalText;
	private ArrayList<String> text;
	
	public StringComponent(String text, ClickGuiTab parent) {
		super();
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
		this.parent = parent;
	}
	
	public StringComponent(String text, ClickGuiTab parent, boolean bold) {
		super();
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
		this.parent = parent;
	}

	@Override
	public void draw(int offset, DrawContext drawContext, float partialTicks, Color color) {
		float parentX = this.parent.getX();
		float parentY = this.parent.getY();
		
		int i = 0;
		for(String str : text) {
			renderUtils.drawString(drawContext, str, parentX + 10,
					parentY + 8 + offset + i, 0xFFFFFF);
			i += 30;
		}
		
	}
	
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
	
	public String getText() {
		return this.originalText;
	}
}