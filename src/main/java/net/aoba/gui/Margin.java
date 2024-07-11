package net.aoba.gui;

public class Margin {
	// Dimensions - We want nullable so that we can determine that these dimensions ARE NOT used.
	private Float left = null;
	private Float top= null;
	private Float right= null;
	private Float bottom= null;
	
	public Margin() {}
	
	public Margin(Float left, Float top, Float right, Float bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public Float getLeft() {
		return this.left;
	}
	
	public Float getTop() {
		return this.top;
	}
	
	public Float getRight() {
		return this.right;
	}
	
	public Float getBottom() {
		return this.bottom;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Margin) {
			Margin otherMargin = (Margin)other;
			
			if(left == null && left != otherMargin.left)
				return false;
			
			if(top == null && top != otherMargin.top)
				return false;
			
			if(right == null && right != otherMargin.right)
				return false;
			
			if(bottom == null && bottom != otherMargin.bottom)
				return false;
			
			return ((left == null && otherMargin.left == null) || left.equals(otherMargin.left)) && 
					((top == null && otherMargin.top == null) || top.equals(otherMargin.top)) &&
					((right == null && otherMargin.right == null) || right.equals(otherMargin.right)) &&
					((bottom == null && otherMargin.bottom == null) || bottom.equals(otherMargin.bottom));
		}
		else
			return false;
	}
}
