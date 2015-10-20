package countdowntimer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

import blingclock.util.GraphicsUtil;

public class PopupPanel extends JPanel {
	
	private float openPhase;
	
	public PopupPanel() {
		setBackground(new Color(0,0,255));
		setLayout(null);
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = GraphicsUtil.prepareGraphics(g);
		int w = getWidth()-1;
		int h = getHeight()-1;
		int cx = w / 2;
		int cy = h / 2;
		float dx = cx * openPhase;
		float dy = cy * openPhase;
		Rectangle2D.Float rect = new Rectangle2D.Float(cx-dx,cy-dy,dx*2,dy*2);
		g2d.setColor(new Color(0,0,0,openPhase * 0.8f));
		g2d.fill(rect);
		int c = 50 + (int)(200 * (1 - openPhase)); 
		g2d.setColor(new Color(c,c,c));
		g2d.draw(rect);
	}

	public void open() {
    	TimingTarget t = new TimingTarget() {	
			public void begin() {
				setVisible(true);
			}
			public void end() {}
			public void repeat() {}
			public void timingEvent(float fraction) {
				openPhase = fraction;
				repaint();
			}
    	};
        Animator a = new Animator(800, t);
        a.setStartDelay(10);
        a.setAcceleration(0.4f);
        a.setDeceleration(0.4f);
        a.start();
	}

}
