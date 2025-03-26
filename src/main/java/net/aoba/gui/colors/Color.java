/*
 * Aoba Hacked Client
 * Copyright (C) 2019-2024 coltonk9043
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.aoba.gui.colors;

import org.apache.commons.lang3.StringUtils;
import org.joml.Vector3f;

public class Color {
	private int r;
	private int g;
	private int b;
	private int alpha = 255;

	private float hue;
	private float saturation;
	private float luminance;

	/**
	 * Color Constructor using RGB color space.
	 *
	 * @param r Red component of a Color.
	 * @param g Green component of a Color.
	 * @param b Blue component of a Color.
	 */
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;

		HSVFromRGB(r, g, b);
	}

	public Color(int r, int g, int b, int alpha) {
		this.r = r;
		this.g = g;
		this.b = b;

		HSVFromRGB(r, g, b);

		this.alpha = alpha;
	}

	public Color(float r, float g, float b, float alpha) {
		this.r = (int) (r * 255f);
		this.g = (int) (g * 255f);
		this.b = (int) (b * 255f);
		this.alpha = (int) (alpha * 255f);
	}

	public Color getAsSolid() {
		return new Color(r, g, b, 255);
	}

	/**
	 * Interpolates between two colors.
	 *
	 * @param color1 The first color.
	 * @param color2 The second color.
	 * @param factor The interpolation factor. 0.0 will return color1, 1.0 will
	 *               return color2.
	 * @return The interpolated color.
	 */
	public static Color interpolate(Color color1, Color color2, float factor) {
		int r = (int) (color1.r + (color2.r - color1.r) * factor);
		int g = (int) (color1.g + (color2.g - color1.g) * factor);
		int b = (int) (color1.b + (color2.b - color1.b) * factor);
		int alpha = (int) (color1.alpha + (color2.alpha - color1.alpha) * factor);
		return new Color(r, g, b, alpha);
	}

	private void HSVFromRGB(int r, int g, int b) {
		// Calculate HSV value
		float rPrime = r / 255.0f;
		float gPrime = g / 255.0f;
		float bPrime = b / 255.0f;

		float cMax = Math.max(rPrime, Math.max(gPrime, bPrime));
		float cMin = Math.min(rPrime, Math.min(gPrime, bPrime));

		float delta = cMax - cMin;

		// Calculate Hue
		if (delta == 0.0f) {
			hue = 0.0f;
		} else {
			if (cMax == rPrime) {
				hue = (60.0f * (((gPrime - bPrime) / delta) % 6));
			} else if (cMax == gPrime) {
				hue = (60.0f * (((bPrime - rPrime) / delta) + 2));
			} else if (cMax == bPrime) {
				hue = (60.0f * (((rPrime - gPrime) / delta) + 2));
			}
		}

		// Calculate Saturation
		if (cMax == 0.0f)
			saturation = 0.0f;
		else
			saturation = delta / cMax;

		// Calculate Luminance
		luminance = cMax;
	}

	/**
	 * Color Constructor using HSV color space.
	 *
	 * @param hue
	 * @param saturation
	 * @param luminance
	 */
	public Color(float hue, float saturation, float luminance) {
		setHSV(hue, saturation, luminance);
	}

	/**
	 * Gets the Hue of the color in HSV color space.
	 * 
	 * @return Hue of the color.
	 */
	public float getHue() {
		return hue;
	}

	/**
	 * Gets the Saturation of the color in HSV color space.
	 * 
	 * @return Saturation of the color.
	 */
	public float getSaturation() {
		return saturation;
	}

	/**
	 * Gets the Luminance of the color in HSV color space.
	 * 
	 * @return Luminance of the color.
	 */
	public float getLuminance() {
		return luminance;
	}

	/**
	 * Sets the RGB and HSV fields using inputs from an HSV color space.
	 *
	 * @param hue        The hue of the HSV color space.
	 * @param saturation The saturation of the HSV color space.
	 * @param luminance  The luminance of the HSV color space.
	 */
	public void setHSV(float hue, float saturation, float luminance) {
		this.hue = hue;
		this.saturation = saturation;
		this.luminance = luminance;
		Color vec = hsv2rgb(hue, saturation, luminance);
		if (vec != null) {
			r = vec.r;
			g = vec.g;
			b = vec.b;
		}
	}

	/**
	 * Sets the hue from HSV color space.
	 * 
	 * @param hue Value to set hue to.
	 */
	public void setHue(float hue) {
		this.hue = hue;
		Color vec = hsv2rgb(this.hue, saturation, luminance);
		if (vec != null) {
			r = vec.r;
			g = vec.g;
			b = vec.b;
		}
	}

	/**
	 * Sets the saturation from HSV color space.
	 * 
	 * @param saturation Value to set saturation to.
	 */
	public void setSaturation(float saturation) {
		this.saturation = saturation;
		Color vec = hsv2rgb(hue, this.saturation, luminance);
		if (vec != null) {
			r = vec.r;
			g = vec.g;
			b = vec.b;
		}
	}

	/**
	 * Sets the luminance from HSV color space.
	 * 
	 * @param luminance Value to set luminance to.
	 */
	public void setLuminance(float luminance) {
		this.luminance = luminance;
		Color vec = hsv2rgb(hue, saturation, this.luminance);
		if (vec != null) {
			r = vec.r;
			g = vec.g;
			b = vec.b;
		}
	}

	/**
	 * Sets the RGB fields using inputs from an RGB color space.
	 *
	 * @param r Red component of a Color.
	 * @param g Green component of a Color.
	 * @param b Blue component of a Color.
	 */
	public void setRGB(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void setRGBA(int r, int g, int b, int alpha) {
		this.r = r;
		this.g = b;
		this.b = b;
		this.alpha = alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	/**
	 * Returns the Color as a string in format RRRGGGBBB.
	 *
	 * @return
	 */
	public String getColorAsString() {
		String rs = Integer.toString(r);
		String gs = Integer.toString(g);
		String bs = Integer.toString(b);
		return rs + gs + bs;
	}

	/**
	 * Returns the color as an Integer for Minecraft Rendering.
	 *
	 * @return The color as an integer.
	 */
	public int getColorAsInt() {
		// Perform shifts and Bitwise AND to get color value in integer format.
		int Alpha = ((alpha) << 24) & 0xFF000000;
		int R = ((r) << 16) & 0x00FF0000;
		int G = ((g) << 8) & 0x0000FF00;
		int B = (b) & 0x000000FF;
		// Return the color as a combination of these values.
		return Alpha | R | G | B;
	}

	/**
	 * Returns the color as a string in Hex format.
	 *
	 * @return The color represented as Hex.
	 */
	public String getColorAsHex() {
		return String.format("#%06X", getColorAsInt());
	}

	/**
	 * Gets the Red component as a float.
	 *
	 * @return Red component as a float.
	 */
	public float getRed() {
		return ((float) r) / 255.0f;
	}

	/**
	 * Gets the Green component as a float.
	 *
	 * @return Green component as a float.
	 */
	public float getGreen() {
		return ((float) g) / 255.0f;
	}

	/**
	 * Gets the Blue component as a float.
	 *
	 * @return Blue component as a float.
	 */
	public float getBlue() {
		return ((float) b) / 255.0f;
	}

	public float getAlpha() {
		return ((float) alpha) / 255.0f;
	}

	public Color add(Color color) {
		return new Color(r + color.r, g + color.g, b + color.b);
	}

	public Color add(float r, float g, float b) {
		return new Color((int) Math.min(255, this.r + r), (int) Math.min(255, this.g + g),
				(int) Math.min(255, this.b + b));
	}

	/**
	 * Converts RGB codes to a String.
	 *
	 * @param r Red component.
	 * @param g Green component.
	 * @param b Blue component.
	 * @return Color as a String.
	 */
	public static String rgbToString(int r, int g, int b) {
		String rs = Integer.toString(r);
		String gs = Integer.toString(g);
		String bs = Integer.toString(b);
		return rs + gs + bs;
	}

	/**
	 * Converts RGB codes to an Integer.
	 *
	 * @param r Red component.
	 * @param g Green component.
	 * @param b Blue component.
	 * @return Color as an Integer.
	 */
	public static int rgbToInt(int r, int g, int b) {
		String rs = Integer.toString(r);
		String gs = Integer.toString(g);
		String bs = Integer.toString(b);
		return Integer.parseInt(rs + gs + bs);
	}

	/**
	 * Converts RGB codes to Hex.
	 *
	 * @param r Red component.
	 * @param g Green component.
	 * @param b Blue component.
	 * @return Color as Hex.
	 */
	public static int convertRGBToHex(int r, int g, int b) {
		String strr = StringUtils.leftPad(Integer.toHexString(r), 2, '0');
		String strg = StringUtils.leftPad(Integer.toHexString(g), 2, '0');
		String strb = StringUtils.leftPad(Integer.toHexString(b), 2, '0');
		String string = strr + strg + strb;
		return Integer.parseInt(string, 16);
	}

	/**
	 * Converts Hex codes to RGB.
	 *
	 * @param hex Color as Hex.
	 * @return Color from Hex Code.
	 */
	/**
	 * Converts Hex to RGB.
	 *
	 * @param hexColor The color as Hex.
	 * @return The color as an RGB vector.
	 */
	public static Color convertHextoRGB(String hexColor) {
		hexColor = hexColor.replace("#", "");
		if (hexColor.length() == 6) {
			int r = Integer.valueOf(hexColor.substring(0, 2), 16);
			int g = Integer.valueOf(hexColor.substring(2, 4), 16);
			int b = Integer.valueOf(hexColor.substring(4, 6), 16);
			return new Color(r, g, b);
		} else if (hexColor.length() == 8) {
			int alpha = Integer.valueOf(hexColor.substring(0, 2), 16);
			int r = Integer.valueOf(hexColor.substring(2, 4), 16);
			int g = Integer.valueOf(hexColor.substring(4, 6), 16);
			int b = Integer.valueOf(hexColor.substring(6, 8), 16);
			return new Color(r, g, b, alpha);
		} else {
			throw new IllegalArgumentException("Invalid hex color format. Expected 6 or 8 characters.");
		}
	}

	/**
	 * Converts HSV into RGB color space.
	 *
	 * @param hue        The hue of the HSV color space.
	 * @param saturation The saturation of the HSV color space.
	 * @param luminance  The luminance of the HSV color space.
	 * @return The color represented by HSV.
	 */
	public static Color hsv2rgb(float hue, float saturation, float luminance) {
		// Get the side that the colour is contained in.
		float h = (hue / 60);
		float chroma = luminance * saturation;
		float x = chroma * (1 - Math.abs((h % 2) - 1));

		// Depending on the side, set the Chroma component to the correct Color.
		Vector3f rgbVec;
		if (h >= 0 && h <= 1) {
			rgbVec = new Vector3f(chroma, x, 0);
		} else if (h >= 1 && h <= 2) {
			rgbVec = new Vector3f(x, chroma, 0);
		} else if (h >= 2 && h <= 3) {
			rgbVec = new Vector3f(0, chroma, x);
		} else if (h >= 3 && h <= 4) {
			rgbVec = new Vector3f(0, x, chroma);
		} else if (h >= 4 && h <= 5) {
			rgbVec = new Vector3f(x, 0, chroma);
		} else if (h >= 5 && h <= 6) {
			rgbVec = new Vector3f(chroma, 0, x);
		} else {
			rgbVec = null;
		}

		// If the Color does exist, add luminance and convert to RGB.
		if (rgbVec != null) {
			float m = luminance - chroma;
			return new Color((int) (255.0f * (rgbVec.x + m)), (int) (255.0f * (rgbVec.y + m)),
					(int) (255.0f * (rgbVec.z + m)));
		}
		return null;
	}
}
