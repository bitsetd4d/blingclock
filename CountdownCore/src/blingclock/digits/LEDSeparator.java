package blingclock.digits;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import blingclock.controls.FastBlurFilter;
import blingclock.controls.RepaintController;
import blingclock.util.GraphicsUtil;


public class LEDSeparator extends JComponent {
	
	private RenderingHints renderHints;
	
	private FastBlurFilter blurFilter1 = new FastBlurFilter(5);
	private FastBlurFilter blurFilter2 = new FastBlurFilter(3);
	
	private BufferedImage bufferedDot = null;
	private int bufferedW = 0;
	private int bufferedH = 0;
	
	private boolean on;
	private boolean blink = true;
	private boolean forDay = false;
	
	private boolean showRegistered = false; 
	
	public LEDSeparator(boolean forDay) {
		this.forDay = forDay;
		renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	@Override
	public void repaint() {
		RepaintController.repaint(this);
	}
	
	public void setShowRegistered(boolean showRegistered) {
		this.showRegistered = showRegistered;
		repaint();
	}

	public void setOn(boolean on) {
		if (this.on != on) {
			this.on = on;
			repaint();
		}
	}

	public void setBlink(boolean blink) {
		if (this.blink != blink) {
			this.blink = blink;
			repaint();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHints(renderHints);
		int w = getWidth();
		int h = getHeight();
		int m = h / 16;
		int h2 = h / 2;
		
		if (on && blink) {
			if (bufferedDot == null || w != bufferedW || h != bufferedH) {
				bufferedDot = GraphicsUtil.createTranslucentCompatibleImage(w,h);
				Graphics2D g2 = bufferedDot.createGraphics();
				g2.setColor(showRegistered ? DigitStyling.GREY3 : DigitStyling.DIGIT_ON_GLOW);
				drawDots(w,m,h2,4,g2);
				bufferedDot = blurFilter1.filter(bufferedDot, null);
				bufferedDot = blurFilter2.filter(bufferedDot, null);
				g2.dispose();
				bufferedW = w;
				bufferedH = h;
			}
			g.drawImage(bufferedDot, 0,0,w,h,null);
			g2d.setColor(showRegistered ? DigitStyling.GREY2 : DigitStyling.DIGIT_ON);
		} else {
			g2d.setColor(showRegistered ? DigitStyling.GREY0 : DigitStyling.DIGIT_OFF);
		}
		drawDots(w,m,h2,0,g2d);
	}

	private void drawDots(int w,int m, int h2,int grow, Graphics2D g2d) {
		if (forDay) { 
			int x = (w - m) / 2;
			g2d.fillRect(x-grow/2, h2*2 - m - grow/2, m+grow, m+grow);
		} else {
			int x = (w - m) / 2;
			g2d.fillRect(x-grow/2, h2 - 3 * m - grow/2, m+grow, m+grow);
			g2d.fillRect(x-grow/2, h2 + 2 * m - grow/2, m+grow, m+grow);			
		}
	}
	
}
