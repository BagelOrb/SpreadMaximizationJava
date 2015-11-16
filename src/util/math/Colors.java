package util.math;

import java.awt.Color;

public class Colors {
	public static int gray(float brightness) {
		return Color.HSBtoRGB(0f, 0f, brightness);
	}
}
