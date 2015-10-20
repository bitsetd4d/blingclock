package countdowntimer.visualiser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import blingclock.visualiser.SegmentUtils;
import blingclock.visualiser.VisualiserPanel;



import countdowntimer.Styling;
import countdowntimer.registration.Registration;

public class CircleTimeVisualiser extends VisualiserPanel {
	
	private long countdownSeconds;
	private long countdownTenths;
	private long countdownHundreths;
	private long targetSeconds;
	private long segments;
	
	private boolean flash;
	
//	private boolean registeredMode;
	
	private Color DARK_GRAY = new Color(30,30,30);
	
	private static final boolean DEBUG = false;
		
	public CircleTimeVisualiser() {	
//		registeredMode = Registration.getInstance().isTrialModeOrRegistered();
	}

	public void flash(boolean onOrOff) {
		this.flash = onOrOff;
	}

	public void reset() {
		animatingSegments = false;
	}
	
	@Override
	public void setTicking(boolean ticking) {
		super.setTicking(ticking);
		updateDisplay(true);
	}

	private static final long HUNDREDS_24_HOURS = 24 * 60 * 60 * 100;
	public void setCountdownTimeRemaining(int days,long countDownTenths,long hundredths) {
		long totalTenths = HUNDREDS_24_HOURS * days + countDownTenths;
		this.countdownSeconds = totalTenths / 10;
		this.countdownTenths = totalTenths;
		this.countdownHundreths = hundredths;
		updateDisplay(DEBUG);
	}

	public void setTargetSeconds(long targetSeconds) {
		this.targetSeconds = targetSeconds;
		segments = SegmentUtils.getSegmentCount(targetSeconds);
		//registeredMode = Registration.getInstance().isTrialModeOrRegistered();
		updateDisplay(true);
	}
	
	private void updateDisplay(boolean full) {
		if (!updating) return;
		if (targetSeconds < 60) {
			repaint();
			return;
		}
		if (!full && !animatingSegments) {
			Rectangle r = computeDamage();
			repaint(r);
			return;
		}
		repaint();
	}
	
	
	private static final int indent = 10;
	@Override
	public void paintComponent(Graphics g) {
//		if (registeredMode) {
			paintCompoundCircle(g);
//		} else {
//			paintUnregisteredCircle(g);
//		}
	}
	
	private int segCounter = 0;
	private boolean animatingSegments = false;
	private int segIntervalCounter = 0;
	
	private void paintCompoundCircle(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Dimension d = getSize();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int w = Math.min(d.width,d.height) - 2*indent;
		int x = (d.width - w) / 2;
		int y = (d.height - w) / 2;

		int cx = x + w / 2;
		int cy = y + w / 2;		
		int degrees = 360;
		if (segments > 1) {
			degrees = 360 / (int)segments;
		}
		
		Rectangle clip = g.getClipBounds();
		g.setColor(Color.BLACK);
		g.fillRect(clip.x,clip.y,clip.width,clip.height);
		
		g.setColor(showRegistered ? Styling.GREY1 : Styling.CIRCLE_COLOUR);
		double angle = 360 * (1.0 * countdownHundreths / (targetSeconds * 100)); 
		Arc2D.Double arc = new Arc2D.Double(x,y,w,w,0,angle,Arc2D.PIE);
		g2d.fill(arc);
		
		if (countdownTenths % 100 == 2) {
			animatingSegments = true;	
			segCounter = (int)segments;
		}
		// Rotating segment
		segCounter = segCounter - 1;
		if (segCounter <= 0) {
			segCounter = (int)segments;
			segIntervalCounter--;
			animatingSegments = false;
		}
		if (animatingSegments) {
			if ((100 * segCounter * (targetSeconds / segments)) < countdownHundreths) { 
				double angleSegmentStart = 360 * segCounter / segments; 
				double segmentAngle = -(360.0 / segments);
				g.setColor(showRegistered ? Styling.GREY2 : Styling.CIRCLE_HIGHLIGHT_COLOUR);
				Arc2D.Double arc2 = new Arc2D.Double(x,y,w,w,angleSegmentStart,segmentAngle, Arc2D.PIE);
				g2d.fill(arc2);
			}
		}
		 
		// Draw segment lines
		g.setColor(Color.BLACK);
		angle = 0;
		for (int i=0; i<segments; i++) {
			double radians = Math.toRadians(angle+90);
			int dx = (int)(w * Math.sin(radians));
			int dy = (int)(w * Math.cos(radians));
			g2d.drawLine(cx, cy, cx + dx,cy + dy);
			angle += degrees;
		}
		
		// debug - compute damage
		if (DEBUG) {
			Rectangle r = computeDamage();
			g.setColor(Color.GREEN);
			Rectangle2D.Double rect = new Rectangle2D.Double(r.x, r.y, r.width, r.height);
			g2d.draw(rect);	
		}
	}
	
	private void paintUnregisteredCircle(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Dimension d = getSize();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int w = Math.min(d.width,d.height) - 2*indent;
		int x = (d.width - w) / 2;
		int y = (d.height - w) / 2;

		int cx = x + w / 2;
		int cy = y + w / 2;		
		int degrees = 360;
		if (segments > 1) {
			degrees = 360 / (int)segments;
		}
		
		Rectangle clip = g.getClipBounds();
		g.setColor(Color.BLACK);
		g.fillRect(clip.x,clip.y,clip.width,clip.height);
		
		g.setColor(DARK_GRAY);
		double angle = 360; 
		Arc2D.Double arc = new Arc2D.Double(x,y,w,w,0,angle,Arc2D.PIE);
		g2d.fill(arc);
		 
		// Draw segment lines
		g.setColor(Color.BLACK);
		angle = 0;
		for (int i=0; i<segments; i++) {
			double radians = Math.toRadians(angle+90);
			int dx = (int)(w * Math.sin(radians));
			int dy = (int)(w * Math.cos(radians));
			g2d.drawLine(cx, cy, cx + dx,cy + dy);
			angle += degrees;
		}
		Font f = new Font(Styling.FONT, Font.BOLD,8);
		GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(),Messages.getString("CircleTimeVisualiser.RegisteredUsersOnly")); //$NON-NLS-1$
		g2d.setColor(Styling.CIRCLE_HIGHLIGHT_COLOUR);
		Rectangle2D r = gv.getLogicalBounds();
		g2d.drawGlyphVector(gv, (float)(cx - r.getCenterX()), (float)(cy - r.getCenterY()));
	}

	
	private Rectangle computeDamage()  {
		Dimension d = getSize();
		int w = Math.min(d.width,d.height);
		int w2 = w/2;
		int x = (d.width - w) / 2;
		int y = (d.height - w) / 2;
		int cx = x + w / 2;
		int cy = y + w / 2;
		double miniAngle = 360 * (1.0 * countdownHundreths / (targetSeconds * 100)); 
		double miniRadians = Math.toRadians(90 + miniAngle);
		double x2 = cx + w2 * Math.sin(miniRadians);
		double y2 = cy + w2 * Math.cos(miniRadians);
		return makeRectangle(cx,cy,(int)x2,(int)y2,8);
	}
	
	private Rectangle makeRectangle(int x1,int y1,int x2,int y2,int f) {
		int x,y,w,h;
		if (x1 < x2) {
			x = x1;
			w = x2 - x1;
		} else {
			x = x2;
			w = x1 - x2;
		}
		if (y1 < y2) {
			y = y1;
			h = y2 - y1;
		} else {
			y = y2;
			h = y1 - y2;
		}
		return new Rectangle(x-f,y-f,w+2*f,h+2*f);
	}

}
