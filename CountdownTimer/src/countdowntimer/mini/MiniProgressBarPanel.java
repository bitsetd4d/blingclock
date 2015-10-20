package countdowntimer.mini;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import blingclock.digits.DigitStyling;
import blingclock.util.GraphicsUtil;
import blingclock.visualiser.SegmentUtils;

import countdowntimer.Styling;
import countdowntimer.preferences.Preferences;

public class MiniProgressBarPanel extends JPanel {
	
	private static final int H = 12;
	
	private int minutes;
	private int seconds;
	private int targetSeconds;
	
	private int tenths;
	private int segments;
	
	private boolean showAsSeconds = false;
	private double redRatio;
	private double amberRatio;
	
	private int currentPhase = -1;
	private GradientPaint currentPainter;
	
	private int timerInstance;  // FIXME NOT SET

	public MiniProgressBarPanel(int timerInstance) {
		super();
		this.timerInstance = timerInstance;
		setBackground(new Color(0,0,0));
		redRatio = Preferences.getInstance().getTimerPreference(timerInstance).getRedRatio();   // FIXME not using TimerPreference class
		amberRatio = Preferences.getInstance().getTimerPreference(timerInstance).getAmberRatio();
	}
	
	public void setShowAsSeconds(boolean showAsSeconds) {
		this.showAsSeconds = showAsSeconds;
	}

	public void setTargetSeconds(int targetSeconds) {
		int oldTarget = this.targetSeconds;
		this.targetSeconds = targetSeconds;
		if (targetSeconds != oldTarget) {
			this.segments = SegmentUtils.getSegmentCount(targetSeconds);
		}
	}
	
	public void setTenths(int tenths) {
		minutes = tenths / 600;
		seconds = (tenths - (minutes * 600))/10;
		this.tenths = tenths;
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Rectangle c = g.getClipBounds();
		g.setColor(getBackground());
		g.fillRect(c.x, c.y, c.width,c.height);
		if (showAsSeconds) {
			paintSecondsBar(g);
		} else {
			paintProportionalBar(g);
		}
	}
	
	private void paintSecondsBar(Graphics g) {
		g.setColor(DigitStyling.BAR_COLOUR);
		int i;
		for (i=0; i<minutes; i++) {
			g.drawLine(i*2, 3, i*2, 3 + H);
		}
		//System.out.println("mini seconds="+seconds);
		if (seconds > 0) {
			int secondsH = (seconds / 5);
			g.drawLine(i*2, 3, i*2, 3 + secondsH);		
		}
	}
	

	private void paintProportionalBar(Graphics g) {
		Graphics2D g2d = GraphicsUtil.prepareGraphics(g);	
		double ratio = tenths / ((double)targetSeconds*10);
		double w = ratio * getWidth();
		int phase = 0;
		if (ratio > amberRatio) {
			phase = 2;
		} else if (ratio > redRatio) {
			phase = 1;
		} else {
			phase = 0;
		}
				
		if (phase != currentPhase) {
			if (phase == 2) {
				currentPainter = new GradientPaint(0f, 00f,
						DigitStyling.BAR_LED2,
						0.0f, (float)(2.2 * H),
						new Color(255,0,0));
			} else if (phase == 1) {
				currentPainter = new GradientPaint(0f, 00f,
						Color.ORANGE,
						0.0f, (float)(1.8 * H),
						new Color(0,255,0));			
			} else {
				currentPainter = new GradientPaint(0f, 00f,
			            Color.RED,
			            0.0f, (float)(2.0 * H),
			            new Color(255,255,0));
			}
			this.currentPhase = phase;
		}
			
		g2d.setPaint(currentPainter);
		g2d.fill(new Rectangle2D.Double(0, 3, w, H));
		g2d.setPaint(new Color(0f,0f,0f,0.1f));
		
		for (int i=0; i<segments; i++) {
			double x1 = getWidth() * (i / (double)segments);
			double x2 = getWidth() * (i / (double)segments);
			g2d.draw(new Line2D.Double(x1,3,x2,H+3));
		}
	}


}
