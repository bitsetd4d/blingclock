package countdowntimer.mini;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WindowMover {
	
	private Map<Component,ComponentInfo> components = new HashMap<Component,ComponentInfo>();
	private boolean osx = false;
	private boolean pressed = false;
	private int dragStartX,dragStartY;
	private Component dragComponent;
	
	public WindowMover() {
		try {
			osx = System.getProperty("os.name").toLowerCase().startsWith("mac os x"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {}
	}
		
	public void addComponent(final Component toMove) {
		final ComponentInfo ci = new ComponentInfo();
		components.put(toMove, ci);
		toMove.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if (pressed) return;
				pressed = true;
				dragComponent = toMove;
				dragStartX = toMove.getX() + e.getX();
				dragStartY = toMove.getY() + e.getY();
				for (Entry<Component,ComponentInfo> entry : components.entrySet()) {
					ComponentInfo ci = entry.getValue();
					Component component = entry.getKey();
					ci.wx = component.getX();
					ci.wy = component.getY();
					Cursor c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
					component.setCursor(c);
				}
			}	
			@Override public void mouseReleased(MouseEvent e) {
				pressed = false;
				for (Component c : components.keySet()) {
					c.setCursor(null);
				}
				dragComponent = null;
			}	
		});
		
		toMove.addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseDragged(MouseEvent e) {
				if (pressed && toMove == dragComponent) { 
					int mouseX = toMove.getX() + e.getX();
					int mouseY = toMove.getY() + e.getY();
					for (Entry<Component,ComponentInfo> entry : components.entrySet()) {
						ComponentInfo ci = entry.getValue();
						Component component = entry.getKey();
						int dx = mouseX - dragStartX;
						int dy = mouseY - dragStartY;
						int newx = ci.wx + dx;
						int newy = ci.wy + dy;
						if (osx) {
							if (newy < 22) newy = 22;						
						} else {
							if (newy < 0) newy = 0;						
						}
						component.setLocation(newx,newy);
					}
				}
			}
		});
	}
/*	
	public void addComponent(final Component component) {
		final ComponentInfo ci = new ComponentInfo();
		components.put(component, ci);
		component.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if (pressed) return;
				pressed = true;
				dragComponent = component;
				dragStartX = component.getX() + e.getX();
				dragStartY = component.getY() + e.getY();
				for (Entry<Component,ComponentInfo> entry : components.entrySet()) {
					ComponentInfo ci = entry.getValue();
					Component component = entry.getKey();
					ci.wx = component.getX();
					ci.wy = component.getY();
					Cursor c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
					component.setCursor(c);
				}
			}	
			@Override public void mouseReleased(MouseEvent e) {
				pressed = false;
				for (Component c : components.keySet()) {
					c.setCursor(null);
				}
				dragComponent = null;
			}	
		});
		
		component.addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseDragged(MouseEvent e) {
				if (pressed && component == dragComponent) { 
					int mouseX = component.getX() + e.getX();
					int mouseY = component.getY() + e.getY();
					for (Entry<Component,ComponentInfo> entry : components.entrySet()) {
						ComponentInfo ci = entry.getValue();
						Component component = entry.getKey();
						int dx = mouseX - dragStartX;
						int dy = mouseY - dragStartY;
						int newx = ci.wx + dx;
						int newy = ci.wy + dy;
						if (osx) {
							if (newy < 22) newy = 22;						
						} else {
							if (newy < 0) newy = 0;						
						}
						component.setLocation(newx,newy);
					}
				}
			}
		});
	}
*/
	
	private static class ComponentInfo {
		private int wx,wy;
	}

}
