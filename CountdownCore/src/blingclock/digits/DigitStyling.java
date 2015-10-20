package blingclock.digits;

import java.awt.Color;

public class DigitStyling {
	
	public static Color DIGIT_ON = new Color(0,240,0); 
	public static Color DIGIT_ON_GLOW = new Color(0,255,0); 
	public static Color DIGIT_OFF = new Color(0,35,0); 
	public static Color DIGIT_OFF2 = new Color(0,30,0); 
	public static Color DIGIT_OFF3 = new Color(0,25,0); 
	public static Color DIGIT_OFF4 = new Color(0,20,0); 
	
	public static Color GREY0 = new Color(15,15,15);
	public static Color GREY1 = new Color(40,40,40);
	public static Color GREY2 = new Color(60,60,60);
	public static Color GREY3 = new Color(80,80,80);
	
	public static String FONT = "Dialog"; //$NON-NLS-1$
	
	public static Color BAR_LED0 = new Color(0,100,0);
	public static Color BAR_LED1 = new Color(0,200,0);
	public static Color BAR_LED2 = new Color(0,245,0);
	public static Color BAR_HIGHLIGHT = new Color(255,0,0);
	public static Color BAR_GRAYED = new Color(20,30,20);
	public static Color BAR_COLOUR = new Color(0,255,0);
	public static int[] B_COLOUR_BASE = new int[] { 20, 30, 20 };
	public static int[] B_COLOUR_FRAC = new int[] { 0, 220, 0 };
	
	public static Color getBarFadeColour(double fraction) {
		double proportion = fraction / 100.0;
		return new Color(
				B_COLOUR_BASE[0] + (int)(B_COLOUR_FRAC[0] * proportion),
				B_COLOUR_BASE[1] + (int)(B_COLOUR_FRAC[1] * proportion),
				B_COLOUR_BASE[2] + (int)(B_COLOUR_FRAC[2] * proportion));
	}
	
	public static Color darkenColor(Color c,double amount) {
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		if (amount < 0) amount = 0;
		if (amount > 1) amount = 1;
		return new Color(Math.max((int)(r * amount), 0), 
				 Math.max((int)(g * amount), 0),
				 Math.max((int)(b * amount), 0));
		
	}

}
