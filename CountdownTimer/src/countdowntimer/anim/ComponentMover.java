package countdowntimer.anim;

import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

public class ComponentMover {
	
	private JComponent component;
	private Scaler sx;
	private Scaler sy;
	private Scaler sw;
	private Scaler sh;
	private int starty,endy;
	private int step;
	private int duration;
	
	private Timer timer;
	private double ratio;
	
	private long endTime;
	
	public ComponentMover(JComponent component,int step, int duration) {
		super();
		this.component = component;
		this.step = step;
		this.duration = duration;
	}
	
	public void setX(int start,int end) {
		sx = new Scaler();
		sx.start = start;
		sx.end = end;		
	}
	
	public void setY(int start,int end) {
		sy = new Scaler();
		sy.start = start;
		sy.end = end;		
	}
	
	public void setW(int start,int end) {
		sw = new Scaler();
		sw.start = start;
		sw.end = end;		
	}

	public void setH(int start,int end) {
		sh = new Scaler();
		sh.start = start;
		sh.end = end;		
	}

	public void start() {
		calculatePath();
		timer = new Timer(step,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onTimerFired();
		}});
		component.setLocation(sx.start, sy.start);
		timer.start();
	}

	private void calculatePath() {
		ratio = 0;
		endTime = System.currentTimeMillis() + duration;
	}

	private void setXY() {
		int x = sx.getValue(ratio);
		int y = sy.getValue(ratio);
		int w = sw.getValue(ratio);
		int h = sh.getValue(ratio);
		component.setBounds(x,y,w,h);
	}

	protected void onTimerFired() {
		long now = System.currentTimeMillis();
		ratio = 1.0 - ((endTime - now)/(float)duration);
		if (ratio >= 1) {
			component.setLocation(sx.end, sy.end);
			timer.stop();
			return;
		}
		setXY();	
	}
	
	private static class Scaler {
		int start,end;
		int getValue(double ratio) {
			return start + (int)(ratio * (end - start));
		}
	}
	
}
