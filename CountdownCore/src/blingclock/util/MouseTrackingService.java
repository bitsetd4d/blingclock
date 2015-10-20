package blingclock.util;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.Timer;

public class MouseTrackingService {
	
	private static final MouseTrackingService INSTANCE = new MouseTrackingService();
	
	private List<Component> myWindows = new CopyOnWriteArrayList<Component>();
	private List<MouseMovingListener> listeners = new CopyOnWriteArrayList<MouseMovingListener>();
	private Timer mouseTimer;
	private Timer mouseStoppedTimer;
	private Timer mouseFollowsButtonTimer;
	private int stopCount;
	
	private Point lastMouseLocation = new Point(0,0);
	private boolean mouseMoving = true;
	private int lastClickCount = 0;
	private int clickCount = 0;
	
	private Robot robot;
	private Component mouseComponent;
	private Point componentLocation;
	private Point mouseForComponent;
	
	public static MouseTrackingService getInstance() { return INSTANCE; }
	
	public void addListener(MouseMovingListener l) {
		listeners.add(l);
		if (mouseTimer == null) {
			start();
		}
	}
	
	public void removeListener(MouseMovingListener l) {
		listeners.remove(l);
		if (listeners.isEmpty()) {
			stop();
		}
	}
	
	public boolean isMouseOverComponent(JComponent c,int margin) {
		if (!c.isVisible()) return false;
		Point loc = c.getLocationOnScreen();
		int w = c.getWidth();
		int h = c.getHeight();
		if (lastMouseLocation.x > loc.x - margin && lastMouseLocation.x < loc.x + w + margin &&
			lastMouseLocation.y > loc.y - margin && lastMouseLocation.y < loc.y + h + margin) {
			return true;
		}
		return false;
	}
	
	public void registerWindow(Component countdownWindow) {
		myWindows.add(countdownWindow);
	}
	
	private void start() { 
		mouseTimer = new Timer(250,new ActionListener() { public void actionPerformed(ActionEvent e) {
			try {
				onTimerFired();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}});
		
		mouseStoppedTimer = new Timer(1000,new ActionListener() { public void actionPerformed(ActionEvent e) {
			try {
				onStoppedTimerFired();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}});
		mouseTimer.start();
		
		mouseFollowsButtonTimer = new Timer(250,new ActionListener() { public void actionPerformed(ActionEvent e) {
			try {
				onMouseFollowsButtonTimerFired();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}});
		
	}
	
	private void stop() { 
		mouseTimer.stop();
		mouseTimer = null;
	}
		
	protected void onTimerFired() {
		Point mouseLocation = getMousePosition();
		if (!lastMouseLocation.equals(mouseLocation) || clickCount != lastClickCount) {
			lastMouseLocation = mouseLocation;
			lastClickCount = clickCount;
			if (!mouseMoving) {
				if (isMouseOverWindowICareAbout(mouseLocation)) {
					mouseMoving = true;
					informMouseMoving();
				}
			}
		} else {
			if (mouseMoving) {
				mouseMoving = false;
				informMouseStoppedMoving();
			}
		}
		lastMouseLocation = mouseLocation;	
	}
	
	public void mouseHasBeenClicked() {
		clickCount++;
	}
	
	private void createRobot() {
		try {
			if (robot == null) {
				robot = new Robot();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void moveMousePointer(int x,int y) {
		System.out.println("Moving mouse to "+x+","+y);
		createRobot();
		if (robot == null) return;
		robot.mouseMove(x, y);
	}

	private boolean isMouseOverWindowICareAbout(Point mouseLocation) {
		try {
			for (Component c : myWindows) {
				if (c.isDisplayable()) {
					Point loc = c.getLocationOnScreen();
					int w = c.getWidth();
					int h = c.getHeight();
					if (mouseLocation.x > loc.x &&
							mouseLocation.x < loc.x + w &&
							mouseLocation.y > loc.y &&
							mouseLocation.y < loc.y + h) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public Point getMousePosition() {
		return MouseInfo.getPointerInfo().getLocation();
	}

	protected void onStoppedTimerFired() {
		stopCount++;
		if (stopCount == 7) {
			//System.out.println("Mouse stopped - done!");
			mouseStoppedTimer.stop();
			return;
		}
		for (MouseMovingListener l : listeners) {
			try {
				informMouseStopped(l);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void informMouseStopped(MouseMovingListener l) {
		if (stopCount == 1) {
			l.onMouseStopped1Second();
		} else if (stopCount == 2) {
			l.onMouseStopped2Seconds();
		} else if (stopCount == 3) {
			l.onMouseStopped3Seconds();
		} else if (stopCount == 4) {
			l.onMouseStopped4Seconds();
		} else if (stopCount == 5) {
			l.onMouseStopped5Seconds();
		} else if (stopCount == 6) {
			l.onMouseStopped6Seconds();
		}
	}

	private void informMouseMoving() {
		for (MouseMovingListener l : listeners) {
			try {
				l.onMouseMoving();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mouseStoppedTimer.stop();
	}

	private void informMouseStoppedMoving() {
		List<MouseMovingListener> brokenListeners = new ArrayList<MouseMovingListener>();
		for (MouseMovingListener l : listeners) {
			try {
				l.onMouseStoppedMoving();
			} catch (Exception e) {
				e.printStackTrace();
				brokenListeners.add(l);
			}
		}
		stopCount = 0;
		mouseStoppedTimer.start();
		listeners.removeAll(brokenListeners);
	}

	public void mouseFollows(Component c) {
		try {
			if (mouseFollowsButtonTimer.isRunning()) return;
			componentLocation = c.getLocationOnScreen();
			mouseComponent = c;
			mouseForComponent = getMousePosition();
			mouseFollowsButtonTimer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void onMouseFollowsButtonTimerFired() {
		mouseFollowsButtonTimer.stop();
		Point currentPos = mouseComponent.getLocationOnScreen();
		if (currentPos.equals(componentLocation)) {
			return;
		}		
		int dx = componentLocation.x - currentPos.x;
		int dy = componentLocation.y - currentPos.y;
		Point p = mouseForComponent.getLocation();
		p.translate(-dx, -dy);
		moveMousePointer(p.x, p.y);
	}

		
}
