package countdowntimer;

import java.awt.Color;
import java.awt.Window;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import countdowntimer.tweaks.TweakValue;

import blingclock.digits.DigitStyling;
import blingclock.util.GlobalImageCache;

public class Styling {

	private static Color onColour = new Color(220,0,0);
	private static Color offColour = new Color(100,0,0);
	private static Color buttonColour = new Color(200,200,200);
	private static Color leftButtonColour = new Color(205,105,22);
	private static Color leftButtonColour2 = new Color(245,145,82);
	
	private static Set<JComponent> onColourSet = new HashSet<JComponent>();
	private static Set<JComponent> offColourSet = new HashSet<JComponent>();
	private static Set<JComponent> buttonColourSet = new HashSet<JComponent>();
	private static Set<JComponent> leftButtonColourSet = new HashSet<JComponent>();
	private static Set<JComponent> leftButtonColour2Set = new HashSet<JComponent>();
	
//	public static Color DIGIT_ON = new Color(0,240,0); 
//	public static Color DIGIT_ON_GLOW = new Color(0,255,0); 
//	public static Color DIGIT_OFF = new Color(0,35,0); 
//	public static Color DIGIT_OFF2 = new Color(0,30,0); 
//	public static Color DIGIT_OFF3 = new Color(0,25,0); 
//	public static Color DIGIT_OFF4 = new Color(0,20,0); 
	
	static {
		buildDigitsColours(255,0,0,1f);
		buildBarColours(0,255,0);
		buildCircleColours(255,0,0);
	}
	
//	public static Color BAR_LED0 = new Color(0,100,0);
//	public static Color BAR_LED1 = new Color(0,200,0);
//	public static Color BAR_LED2 = new Color(0,245,0);
//	public static Color BAR_HIGHLIGHT = new Color(255,0,0);
//	public static Color BAR_GRAYED = new Color(20,30,20);
//	public static Color BAR_COLOUR = new Color(0,255,0);


	public static Color CIRCLE_COLOUR = new Color(255,0,0);
	//public static Color CIRCLE_HIGHLIGHT_COLOUR = new Color(255,50,50);
	public static Color CIRCLE_HIGHLIGHT_COLOUR = new Color(255,220,220);
	
	public static Color GREY0 = new Color(15,15,15);
	public static Color GREY1 = new Color(40,40,40);
	public static Color GREY2 = new Color(60,60,60);
	public static Color GREY3 = new Color(80,80,80);
	
	//public static String FONT = "Verdana";
	public static String FONT = "Dialog"; //$NON-NLS-1$
	
	public static void hotApplyTheme(TweakValue theme,float gamma) {		
		applyTheme(theme,gamma);
		GlobalImageCache.getInstance().clearCache();
		for (Window w : Window.getWindows()) {
			w.repaint();
		}
	}
	
	public static void applyTheme(TweakValue theme,float gamma) {
		if (theme == null) theme = TweakValue.COLOR_THEME_DEFAULT;
		if (theme == TweakValue.COLOR_THEME_RED) { 
			buildDigitsColours(255,0,0,gamma);
			buildBarColours(255,0,0);
			buildCircleColours(255,0,0);
			onColour = new Color(220,0,0);
			offColour = new Color(100,0,0);
			buttonColour = new Color(200,200,200);
			leftButtonColour = new Color(205,55,11);
		} else if (theme == TweakValue.COLOR_THEME_GREEN) {
			buildDigitsColours(0,255,0,gamma);
			buildBarColours(0,255,0);
			buildCircleColours(0,255,0);
			onColour = new Color(0,220,0);
			offColour = new Color(0,100,0);
			buttonColour = new Color(200,200,200);
			leftButtonColour = new Color(105,205,22);
		} else if (theme == TweakValue.COLOR_THEME_BLUE) { 
			buildDigitsColours(0,0,255,gamma);
			buildBarColours(0,0,255);
			buildCircleColours(0,0,255);
			onColour = new Color(0,0,220);
			offColour = new Color(0,0,100);
			buttonColour = new Color(200,200,200);
			leftButtonColour = new Color(105,22,205);
		} else if (theme == TweakValue.COLOR_THEME_THEME1) {
			buildDigitsColours(240,30,30,gamma);
			buildBarColours(0,200,50);
			buildCircleColours(0,100,0);
			onColour = new Color(0,220,0);
			offColour = new Color(0,100,0);
			buttonColour = new Color(180,180,180);
			leftButtonColour = new Color(0,100,0);
			DigitStyling.B_COLOUR_FRAC = new int[] { 220, 0, 0 } ;
		} else if (theme == TweakValue.COLOR_THEME_THEME2) { 
			buildDigitsColours(0,170,0,gamma);
			buildBarColours(225,0,0);
			buildCircleColours(200,0,0);
			onColour = new Color(220,0,0);
			offColour = new Color(100,0,0);
			buttonColour = new Color(180,180,180);
			leftButtonColour = new Color(100,0,0);
			DigitStyling.B_COLOUR_FRAC = new int[] { 0, 220, 0 } ;
		} else if (theme == TweakValue.COLOR_THEME_HIGHC) { 
			buildDigitsColours(240,240,240,gamma);
			buildBarColours(0,255,0);
			buildCircleColours(255,100,100);
			onColour = new Color(220,0,0);
			offColour = new Color(100,0,0);
			buttonColour = new Color(180,180,180);
			leftButtonColour = new Color(100,0,0);
			DigitStyling.B_COLOUR_FRAC = new int[] { 0, 220, 0 } ;
		} else { // default
			buildDigitsColours(255,0,0,gamma);
			buildBarColours(0,255,0);
			buildCircleColours(255,0,0);
			onColour = new Color(220,0,0);
			offColour = new Color(100,0,0);
			buttonColour = new Color(200,200,200);
			leftButtonColour = new Color(205,105,22);
		}
		applyColours();
	}
		
	public static void makeOnButtonColour(JComponent c) {
		c.setForeground(onColour);
		onColourSet.add(c);
		offColourSet.remove(c);
		leftButtonColourSet.remove(c);
	}
	public static void makeOffButtonColour(JComponent c) {
		c.setForeground(offColour);
		onColourSet.remove(c);
		offColourSet.add(c);
		leftButtonColourSet.remove(c);
	}
	public static void makeLeftButtonColour(JComponent c) {
		c.setForeground(leftButtonColour);
		leftButtonColourSet.add(c);
		onColourSet.remove(c);
		offColourSet.remove(c);
	}
	public static void makeLeftButtonColour2(JComponent c) {
		c.setForeground(leftButtonColour2);
		leftButtonColour2Set.add(c);
	}
	public static void setMainForeground(JComponent c) {
		c.setForeground(Color.GREEN);
	}
	public static void setMakeInvisible(JComponent c) {
		c.setForeground(new Color(0,0,0,0));
	}
	public static void setButtonColour(JComponent c) {
		c.setForeground(buttonColour);
		buttonColourSet.add(c);
	}
	
	private static void applyColours() {
		for (JComponent c : onColourSet) c.setForeground(onColour);
		for (JComponent c : offColourSet) c.setForeground(offColour);
		for (JComponent c : buttonColourSet) c.setForeground(buttonColour);
		for (JComponent c : leftButtonColourSet) c.setForeground(leftButtonColour);
	}
	
	private static void buildDigitsColours(int r,int g, int b, float gamma) {
		DigitStyling.DIGIT_ON_GLOW = makeColor(r,g,b,1); 
		DigitStyling.DIGIT_ON = makeColor(r,g,b,0.94);
		DigitStyling.DIGIT_OFF = makeColor(r,g,b,0.14 * gamma);
		DigitStyling.DIGIT_OFF2 = makeColor(r,g,b,0.12 * gamma);
		DigitStyling.DIGIT_OFF3 = makeColor(r,g,b,0.10 * gamma);
		DigitStyling.DIGIT_OFF4 = makeColor(r,g,b,0.06 * gamma);
	}
	
	private static void buildBarColours(int r,int g, int b) {
		DigitStyling.BAR_LED0 = makeColor(r,g,b,0.4);
		DigitStyling.BAR_LED1 = makeColor(r,g,b,0.78);
		DigitStyling.BAR_LED2 = makeColor(r,g,b,1);
		DigitStyling.BAR_HIGHLIGHT = new Color(g,b,r);
		DigitStyling.BAR_COLOUR = new Color(r,g,b); 
		DigitStyling.B_COLOUR_BASE = new int[] { 20, 20, 20 };
		DigitStyling.B_COLOUR_FRAC = new int[] { (int)(230 * (r / 255.0)), (int)(230 * (g / 255.0)), (int)(230 * (b / 255.0)) };
	}

	private static void buildCircleColours(int r,int g, int b) {
		CIRCLE_COLOUR = makeColor(r,g,b,1);
		CIRCLE_HIGHLIGHT_COLOUR = makeColor(r,g+40,b+40,1.0);
	}
	
	private static Color makeColor(int r,int g,int b,double f) {
		int newr = (int)(r*f);
		int newg = (int)(g*f);
		int newb = (int)(b*f);
		if (newr > 255) newr = 255;
		if (newg > 255) newg = 255;
		if (newb > 255) newb = 255;
		return new Color(newr,newg,newb);
	}

	
}
