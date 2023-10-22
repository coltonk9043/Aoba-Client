// An interface containing the most abstract definition of a Hud Element
// that will appear on the screen. 
package net.aoba.gui;

public interface IHudElement {

	public float getX();
	public float getY();
	public float getWidth();
	public float getHeight();
	
	public void setX(float x);
	public void setY(float y);
	public void setWidth(float width);
	public void setHeight(float height);
	
	public void OnChildChanged(IHudElement child);
}
