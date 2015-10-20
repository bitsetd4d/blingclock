package blingclock.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

public class BlingLayout implements LayoutManager2 {
	
	private Set<Component> components = new HashSet<Component>();  
	private Map<Component,BlingLayoutData> layouts = new HashMap<Component,BlingLayoutData>();

	public void addLayoutComponent(String name, Component comp) {
		components.add(comp);
	}
	
	public void addLayoutComponent(Component comp, Object constraints) {
		components.add(comp);
	}
	public void removeLayoutComponent(Component comp) {
		components.remove(comp);
	}

	public float getLayoutAlignmentX(Container target) { 
		return 0;
	}
	public float getLayoutAlignmentY(Container target) { 
		return 0; 
	}

	public void invalidateLayout(Container target) {}
	
	public void layoutContainer(Container parent) {
		for (Component c : components) {
			BlingLayoutData bd = layouts.get(c);
			if (bd == null) continue;
			int w = parent.getWidth();
			int h = parent.getHeight();
			int x1 = bd.left.compute(w,this);
			int y1 = bd.top.compute(h,this);
			int x2 = bd.right.compute(w,this);
			int y2 = bd.bottom.compute(h,this);
			c.setLocation(x1, y1);
			c.setSize(x2 - x1,y2 - y1);
		}
	}
	
	public Dimension minimumLayoutSize(Container parent) {
		System.out.println("BlingLayout.minimumLayoutSize()"); //$NON-NLS-1$
		return null;
	}
	public Dimension preferredLayoutSize(Container parent) {
		System.out.println("BlingLayout.preferredLayoutSize()"); //$NON-NLS-1$
		return null;
	}
	public Dimension maximumLayoutSize(Container target) {
		System.out.println("BlingLayout.maximumLayoutSize()"); //$NON-NLS-1$
		return null;
	}

	public BlingLayoutData layoutFor(JComponent component) {
		BlingLayoutData dt = layouts.get(component);
		if (dt == null) {
			dt = new BlingLayoutData();
			layouts.put(component, dt);
		}
		return dt;
	}


}
