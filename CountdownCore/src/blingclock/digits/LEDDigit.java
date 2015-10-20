package blingclock.digits;

import static blingclock.digits.DigitStyling.DIGIT_OFF;
import static blingclock.digits.DigitStyling.DIGIT_OFF2;
import static blingclock.digits.DigitStyling.DIGIT_OFF3;
import static blingclock.digits.DigitStyling.DIGIT_OFF4;
import static blingclock.digits.DigitStyling.DIGIT_ON;
import static blingclock.digits.DigitStyling.DIGIT_ON_GLOW;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import blingclock.controls.FastBlurFilter;
import blingclock.controls.RepaintController;
import blingclock.util.GlobalImageCache;
import blingclock.util.GraphicsUtil;

public class LEDDigit extends JComponent {
	
	/*
	 *     11111
	 *    2     3
	 *    2     3    
	 *     44444
	 *    5     6
	 *    5     6
	 *     77777   	  
	 */
	
	private String digit;
	private boolean showDigitNoBlink = true;
	private boolean[] elements = { false, false, false, false, false, false, false };
	private int w;
	private int h;
	private int xx;
	private int yy;
	private int h2;
	
	private boolean showPM;
	private boolean showAM;
	
	private boolean showRegistered = false;
	
	private static Map<String,boolean[]> charMap = new HashMap<String,boolean[]>();
	
	public LEDDigit() {
		charMap.put("0",new boolean[] { true, true, true, false, true, true, true }); //$NON-NLS-1$
		charMap.put("1",new boolean[] { false, false, true, false, false, true, false }); //$NON-NLS-1$
		charMap.put("2",new boolean[] { true, false, true, true, true, false, true }); //$NON-NLS-1$
		charMap.put("3",new boolean[] { true, false, true, true, false, true, true }); //$NON-NLS-1$
		charMap.put("4",new boolean[] { false, true, true, true, false, true, false }); //$NON-NLS-1$
		charMap.put("5",new boolean[] { true, true, false, true, false, true, true }); //$NON-NLS-1$
		charMap.put("6",new boolean[] { true, true, false, true, true, true, true }); //$NON-NLS-1$
		charMap.put("7",new boolean[] { true, false, true, false, false, true, false }); //$NON-NLS-1$
		charMap.put("8",new boolean[] { true, true, true, true, true, true, true }); //$NON-NLS-1$
		charMap.put("9",new boolean[] { true, true, true, true, false, true, true }); //$NON-NLS-1$
	}
	
	@Override
	public void repaint() {
		RepaintController.repaint(this);
	}
	
	public void setShowRegistered(boolean showRegistered) {
		this.showRegistered = showRegistered;
		repaint();
	}
	
	public void showPM(boolean showPM) {
		if (this.showPM != showPM) {
			this.showPM = showPM;
			repaint();
		}
	}
	
	public void showAM(boolean showAM) {
		if (this.showAM != showAM) {
			this.showAM = showAM;
			repaint();
		}
	}
		
	public void setCharacter(String c) {
		boolean changed = digit == null || !digit.equals(c);
		if (!changed) return;
		this.digit = c;
		elements = charMap.get(c);
		if (elements == null) elements = new boolean[]{ false, false, false, false, false, false, false };
		repaint();
	}
	
	public void setBlink(boolean blink) {
		this.showDigitNoBlink = blink;
		repaint();
	}
	
	private FastBlurFilter blurFilter1 = new FastBlurFilter(12);
	private FastBlurFilter blurFilter2 = new FastBlurFilter(6);
	
	@Override
	protected void paintComponent(Graphics g) {
		h = getHeight();
		w = getWidth();
		String key = digit + showDigitNoBlink + showAM + showPM + showRegistered;
		BufferedImage image = GlobalImageCache.getInstance().getImage(key, w, h);
		if (image == null) {
			image = GraphicsUtil.createTranslucentCompatibleImage(w,h);
			Graphics2D g2 = image.createGraphics();
			int m = Math.min(w/9,h/9);
			xx = m;
			yy = m;
			h2 = h / 2;
			w -= xx;
			
			boolean timeMode = showAM || showPM;
	
			if (showDigitNoBlink) {
				g2.setColor(showRegistered ? DigitStyling.GREY3 : DIGIT_ON_GLOW);
				if (!timeMode) {
					if (elements[0]) drawSegment1(g2);
					if (elements[1]) drawSegment2(g2);
					if (elements[3]) drawSegment4(g2);
					if (elements[4]) drawSegment5(g2);
					if (elements[6]) drawSegment7(g2);
				}
				if (elements[2]) drawSegment3(g2);
				if (elements[5]) drawSegment6(g2);
				image = blurFilter1.filter(image, null);
				image = blurFilter2.filter(image, null);		
				g2.dispose();
				g2 = image.createGraphics();
				GraphicsUtil.setRenderHints(g2);
				g2.setColor(showRegistered ? DigitStyling.GREY2 : DIGIT_ON);
				if (!timeMode) {
					if (elements[0]) drawSegment1(g2);
					if (elements[1]) drawSegment2(g2);
					if (elements[4]) drawSegment5(g2);
					if (elements[3]) drawSegment4(g2);
					if (elements[6]) drawSegment7(g2);
				}
				if (elements[2]) drawSegment3(g2);
				if (elements[5]) drawSegment6(g2);
			}
			if (showAM) {
				drawTopLeft(g2,Messages.getString("LEDDigit.AM"),1); //$NON-NLS-1$
			} else if (showPM) {
				drawTopLeft(g2,Messages.getString("LEDDigit.PM"),2); //$NON-NLS-1$
			}

			if (m > 12) {
				g2.setColor(showRegistered ? DigitStyling.GREY0 : DIGIT_OFF4);	
			} else if (m > 7) {
				g2.setColor(showRegistered ? DigitStyling.GREY0 : DIGIT_OFF3);		
			} else if (m > 4) {
				g2.setColor(showRegistered ? DigitStyling.GREY0 : DIGIT_OFF2);
			} else {
				g2.setColor(showRegistered ? DigitStyling.GREY0 : DIGIT_OFF);
			}
			if (!timeMode) {
				if (!elements[0] || !showDigitNoBlink) drawSegment1(g2);
				if (!elements[1] || !showDigitNoBlink) drawSegment2(g2);
				if (!elements[3] || !showDigitNoBlink) drawSegment4(g2);
				if (!elements[4] || !showDigitNoBlink) drawSegment5(g2);
				if (!elements[6] || !showDigitNoBlink) drawSegment7(g2);
			}
			if (!elements[2] || !showDigitNoBlink) drawSegment3(g2);
			if (!elements[5] || !showDigitNoBlink) drawSegment6(g2);
			g2.dispose();
			GlobalImageCache.getInstance().storeImage(key, getWidth(), h, image);
		}
		g.drawImage(image, 0,0,getWidth(),h,null);
	}
	
	private void drawTopLeft(Graphics2D g2d,String text,int multiplier) {
		int fontHeight = getHeight()/14;
		if (fontHeight < 9) fontHeight = 9;
		if (fontHeight > 15) fontHeight = 18;
		Font f = new Font(DigitStyling.FONT,Font.PLAIN,fontHeight);
		GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(),text);
		g2d.drawGlyphVector(gv,4,4 + multiplier * (float)gv.getVisualBounds().getHeight());
	}
	
	private void drawSegment1(Graphics2D g2d) {
		drawHorizonontalSegment(g2d, xx+xx+2, yy, w - 2);
	}
	
	private void drawSegment2(Graphics2D g2d) {
		drawVerticalSegment(g2d, xx + xx/2, yy + 2, h2 - 2,xx/2);
	}
	
	private void drawSegment3(Graphics2D g2d) {
		drawVerticalSegment(g2d, w - xx/2, yy + 2, h2 - 2,xx/2);
	}
	
	private void drawSegment4(Graphics2D g2d) {
		drawHorizonontalSegment(g2d, xx+xx/2+2, h2, w - xx/2 - 2);
	}
	
	private void drawSegment5(Graphics2D g2d) {
		drawVerticalSegment(g2d, xx, h2 + 2, h-xx-2, xx/2);
	}

	private void drawSegment6(Graphics2D g2d) {
		drawVerticalSegment(g2d, w - xx, h2+2, h-xx-2, xx/2);
	}

	private void drawSegment7(Graphics2D g2d) {
		drawHorizonontalSegment(g2d, xx+2, h-yy, w - xx - 2);
	}

	private void drawHorizonontalSegment(Graphics2D g2d,int x1,int y,int x2) {
		int px[] = new int[] { x1, x1 + xx, x2 - yy, x2, x2 - xx, x1 + xx };
		int py[] = new int[] { y, y - yy, y - yy, y, y + yy, y + yy };
		g2d.fillPolygon(px,py,6);
	}
	
	private void drawVerticalSegment(Graphics2D g2d,int x,int y1,int y2,int o) {
		int px[] = new int[] { x+o, x - xx+o, x - xx,  x,  x + xx,  x + xx + o  };
		int py[] = new int[] { y1,  y1 + yy,  y2 - yy, y2, y2 - yy, y1 + yy };
		g2d.fillPolygon(px,py,6);
	}


}
