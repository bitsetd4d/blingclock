package countdowntimer.controls;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

public class ScrollOnJPanel extends JPanel {
	
	private Map<Component,Animator> animators = new HashMap<Component,Animator>();
	
	public ScrollOnJPanel() {
		super();
		setOpaque(false);
	}
	
	public void hideAll() {
	    for (final Component c : getComponents()) {	
	    	c.setLocation(-100,-100);
	    }
		setLayout(null);
	}
	
	public void setPositions() { 
	    int h = getHeight()/(getComponentCount()+1);
	    int y = h;
	    for (final Component c : getComponents()) {	
	    	c.setBounds(getWidth(),y,(int)(getWidth()*0.7f),h);
	    	c.setVisible(true);
	    	if ("logo".equals(c.getName())) { //$NON-NLS-1$
		    	c.setBounds(getWidth(),y,(int)(getWidth()*0.7f),h*3);
	    	}
	    	y += h;
	    }
	}

	private void stopAnimation(Component c) {
		Animator old = animators.get(c);
		if (old != null) old.stop();
	}

	public void scrollOn() {
	    int h = getHeight()/(getComponentCount()+1);
	    int w = getWidth() / 6;
	    if (w < 5) w = 5;
	    int y = h;
	    int timeToTravel = 300;
	    for (final Component c : getComponents()) {
	    	stopAnimation(c);
	    	Point current = c.getLocation();
	    	int targetx = w;
	    	int targety = y;
	    	TimingTarget t = new ComponentTravelTimingTarget(c,targetx - current.x,targety - current.y);
		    Animator a = new Animator(timeToTravel, t);
		    a.setAcceleration(0.2f);
		    a.setDeceleration(0.6f);
		    a.start();
		    animators.put(c,a);
		    y += h;
		    timeToTravel += 50;
	 	}
	}
	
	public void fadeOutContents(Component fadeLast,final Runnable afterAction) {
		int fadeTime = 100;
		for (final Component c : getComponents()) {
			if (c == fadeLast) continue;
			stopAnimation(c);
			TimingTarget t = new ComponentFadeTimingTarget(c);
			Animator a = new Animator(fadeTime, t);
			a.start();
			animators.put(c,a);
			fadeTime += 50;
		}
		if (fadeLast != null) {
			TimingTarget t = new ComponentFadeTimingTarget(fadeLast);
			Animator a = new Animator(fadeTime, t);
			a.start();
		}
		
		Timer timer = new Timer(fadeTime,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			afterAction.run();
		}});
		timer.setRepeats(false);
		timer.start();
	}
		
	private static class ComponentTravelTimingTarget implements TimingTarget {
		
		private Component component;
		private int px,py;
		private int dx,dy;
		
		public ComponentTravelTimingTarget(Component component,int dx,int dy) {
			this.component = component;
			this.dx = dx;
			this.dy = dy;
		}

		public void begin() {
			px = component.getX();
			py = component.getY();
		}

		public void end() {
			component.validate();
		}

		public void repeat() {}

		public void timingEvent(float fraction) {
			int x = px + (int)(fraction * dx);
			int y = py + (int)(fraction * dy);
			component.setLocation(x, y);
			component.validate();
		}
		
	}
	
	private static class ComponentFadeTimingTarget implements TimingTarget {
		
		private Component component;
		
		public ComponentFadeTimingTarget(Component component) {
			this.component = component;
		}

		public void begin() {}

		public void end() {
			component.validate();
		}

		public void repeat() {}

		public void timingEvent(float fraction) {
			FadablePanel.fade(component, 1 - fraction);			
		}
		
	}

}
