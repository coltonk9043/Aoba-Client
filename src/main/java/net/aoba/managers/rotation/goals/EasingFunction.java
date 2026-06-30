package net.aoba.managers.rotation.goals;

public enum EasingFunction {
	Linear, SineEaseIn, SineEaseOut, SineEaseInOut, CircEaseIn, CircEaseOut, CircEaseInOut, CubicEaseIn, CubicEaseOut,
	CubicEaseInOut, QuartEaseIn, QuartEaseOut, QuartEaseInOut, QuintEaseIn, QuintEaseOut, QuintEaseInOut, QuadEaseIn,
	QuadEaseOut, QuadEaseInOut, BackEaseIn, BackEaseOut, BackEaseInOut, ElasticEaseIn, ElasticEaseOut, ElasticEaseInOut,
	BounceEaseIn, BounceEaseOut, BounceEaseInOut, ExpoEaseIn, ExpoEaseOut, ExpoEaseInOut;

	private static final int STEPS = 512;
	private double peakSlope = 1.0;

	// Calculate the PEAK slope for each Easing Function
	static {
		for (EasingFunction function : values())
			function.peakSlope = computePeakSlope(function);
	}

	/**
	 * Gets the peak slope for the Easing Function
	 * @return Peak Slope
	 */
	public double getPeakSlope() {
		return peakSlope;
	}

	/**
	 * Samples and returns the highest slope value of the easing function.
	 * @param function Function to sample.
	 * @return Highest slope value.
	 */
	private static double computePeakSlope(EasingFunction function) {
	
		double peak = 0.0;
		double previous = ease(function, 0.0);
		for (int i = 1; i <= STEPS; i++) {
			double t = i / (double) STEPS;
			double current = ease(function, t);
			double slope = Math.abs(current - previous) * STEPS;
			if (slope > peak)
				peak = slope;
			previous = current;
		}
		return peak;
	}

	public static double ease(EasingFunction easing, double t) {
		switch (easing) {
		case Linear:
			return linear(t);
		case SineEaseIn:
			return sineEaseIn(t);
		case SineEaseOut:
			return sineEaseOut(t);
		case SineEaseInOut:
			return sineEaseInOut(t);
		case CircEaseIn:
			return circEaseIn(t);
		case CircEaseOut:
			return circEaseOut(t);
		case CircEaseInOut:
			return circEaseInOut(t);
		case CubicEaseIn:
			return cubicEaseIn(t);
		case CubicEaseOut:
			return cubicEaseOut(t);
		case CubicEaseInOut:
			return cubicEaseInOut(t);
		case QuartEaseIn:
			return quartEaseIn(t);
		case QuartEaseOut:
			return quartEaseOut(t);
		case QuartEaseInOut:
			return quartEaseInOut(t);
		case QuintEaseIn:
			return quintEaseIn(t);
		case QuintEaseOut:
			return quintEaseOut(t);
		case QuintEaseInOut:
			return quintEaseInOut(t);
		case QuadEaseIn:
			return quadEaseIn(t);
		case QuadEaseOut:
			return quadEaseOut(t);
		case QuadEaseInOut:
			return quadEaseInOut(t);
		case BackEaseIn:
			return backEaseIn(t);
		case BackEaseOut:
			return backEaseOut(t);
		case BackEaseInOut:
			return backEaseInOut(t);
		case ElasticEaseIn:
			return elasticEaseIn(t);
		case ElasticEaseOut:
			return elasticEaseOut(t);
		case ElasticEaseInOut:
			return elasticEaseInOut(t);
		case BounceEaseIn:
			return bounceEaseIn(t);
		case BounceEaseOut:
			return bounceEaseOut(t);
		case BounceEaseInOut:
			return bounceEaseInOut(t);
		case ExpoEaseIn:
			return expoEaseIn(t);
		case ExpoEaseOut:
			return expoEaseOut(t);
		case ExpoEaseInOut:
			return expoEaseInOut(t);
		default:
			return t;
		}
	}

	public static double linear(double t) {
		return t;
	}

	public static double quadEaseIn(double t) {
		return t * t;
	}

	public static double quadEaseOut(double t) {
		return 1 - quadEaseIn(1 - t);
	}

	public static double quadEaseInOut(double t) {
		if (t < 0.5)
			return quadEaseIn(t * 2) / 2;
		return 1 - quadEaseIn((1 - t) * 2) / 2;
	}

	public static double cubicEaseIn(double t) {
		return t * t * t;
	}

	public static double cubicEaseOut(double t) {
		return 1 - cubicEaseIn(1 - t);
	}

	public static double cubicEaseInOut(double t) {
		if (t < 0.5)
			return cubicEaseIn(t * 2) / 2;
		return 1 - cubicEaseIn((1 - t) * 2) / 2;
	}

	public static double quartEaseIn(double t) {
		return t * t * t * t;
	}

	public static double quartEaseOut(double t) {
		return 1 - quartEaseIn(1 - t);
	}

	public static double quartEaseInOut(double t) {
		if (t < 0.5)
			return quartEaseIn(t * 2) / 2;
		return 1 - quartEaseIn((1 - t) * 2) / 2;
	}

	public static double quintEaseIn(double t) {
		return t * t * t * t * t;
	}

	public static double quintEaseOut(double t) {
		return 1 - quintEaseIn(1 - t);
	}

	public static double quintEaseInOut(double t) {
		if (t < 0.5)
			return quintEaseIn(t * 2) / 2;
		return 1 - quintEaseIn((1 - t) * 2) / 2;
	}

	public static double sineEaseIn(double t) {
		return 1 - Math.cos(t * Math.PI / 2);
	}

	public static double sineEaseOut(double t) {
		return Math.sin(t * Math.PI / 2);
	}

	public static double sineEaseInOut(double t) {
		return (Math.cos(t * Math.PI) - 1) / -2;
	}

	public static double expoEaseIn(double t) {
		return Math.pow(2, 10 * (t - 1));
	}

	public static double expoEaseOut(double t) {
		return 1 - expoEaseIn(1 - t);
	}

	public static double expoEaseInOut(double t) {
		if (t < 0.5)
			return expoEaseIn(t * 2) / 2;
		return 1 - expoEaseIn((1 - t) * 2) / 2;
	}

	public static double circEaseIn(double t) {
		return -(Math.sqrt(1 - t * t) - 1);
	}

	public static double circEaseOut(double t) {
		return 1 - circEaseIn(1 - t);
	}

	public static double circEaseInOut(double t) {
		if (t < 0.5)
			return circEaseIn(t * 2) / 2;
		return 1 - circEaseIn((1 - t) * 2) / 2;
	}

	public static double elasticEaseIn(double t) {
		return 1 - elasticEaseOut(1 - t);
	}

	public static double elasticEaseOut(double t) {
		float p = 0.3f;
		return Math.pow(2, -10 * t) * Math.sin((t - p / 4) * (2 * Math.PI) / p) + 1;
	}

	public static double elasticEaseInOut(double t) {
		if (t < 0.5)
			return elasticEaseIn(t * 2) / 2;
		return 1 - elasticEaseIn((1 - t) * 2) / 2;
	}

	public static double backEaseIn(double t) {
		double s = 1.70158;
		return t * t * ((s + 1) * t - s);
	}

	public static double backEaseOut(double t) {
		return 1 - backEaseIn(1 - t);
	}

	public static double backEaseInOut(double t) {
		if (t < 0.5)
			return backEaseIn(t * 2) / 2;
		return 1 - backEaseIn((1 - t) * 2) / 2;
	}

	public static double bounceEaseIn(double t) {
		return 1 - bounceEaseOut(1 - t);
	}

	public static double bounceEaseOut(double t) {
		double div = 2.75;
		double mult = 7.5625;

		if (t < 1 / div) {
			return mult * t * t;
		} else if (t < 2 / div) {
			t -= 1.5 / div;
			return mult * t * t + 0.75;
		} else if (t < 2.5 / div) {
			t -= 2.25 / div;
			return mult * t * t + 0.9375;
		} else {
			t -= 2.625 / div;
			return mult * t * t + 0.984375;
		}
	}

	public static double bounceEaseInOut(double t) {
		if (t < 0.5)
			return bounceEaseIn(t * 2) / 2;
		return 1 - bounceEaseIn((1 - t) * 2) / 2;
	}
}
