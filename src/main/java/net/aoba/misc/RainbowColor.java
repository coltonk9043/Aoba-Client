package net.aoba.misc;

import net.aoba.gui.Color;

public class RainbowColor {
	private Color color;
	private float timer = 0f;

	public RainbowColor() {
		this.color = new Color(255, 0, 0);
	}

	public void update(float timerIncrement) {
		if (timer >= (20 - timerIncrement)) {
			timer = 0f;
			this.color.setHSV(((this.color.hue + 1f) % 361), 1f, 1f);
		} else {
			timer++;
		}

	}

	public Color getColor() {
		return this.color;
	}
}
