package blingclock.digits;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import blingclock.controls.GlowButton;
import blingclock.layout.MetricsSharing;


public class DigitDecorationPanel extends JPanel {
	
	private boolean rightToleft;
	private List<GlowButton> buttons;
	private List<GlowButton> controls;
	
	public DigitDecorationPanel(boolean rightToLeft) {
		this.rightToleft = rightToLeft;
		setBackground(new Color(0,0,0));
		setLayout(null);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onWindowResized();
			}
			@Override
			public void componentMoved(ComponentEvent e) {}
		});
	}
	

	protected void onWindowResized() {
		layoutButtons();
	}


	public void setupButtons(List<GlowButton> list) {
		List<GlowButton> myList = new ArrayList<GlowButton>(list);
		if (rightToleft) {
			Collections.reverse(myList);
		}
		this.buttons = myList;
		addButtonsToSelf();
		layoutButtons();
	}
	
	public void setupControls(List<GlowButton> list) {
		this.controls = list;
		addButtonsToSelf();
		layoutButtons();
	}
	
	private void addButtonsToSelf() { 
		removeAll();
		for (GlowButton b : buttons) {
			add(b);
		}
		if (controls != null) {
			for (GlowButton b : controls) {
				add(b);
			}
		}
	}
	
	private void layoutButtons() { 
		final int buttonWidth = 32;
		if (buttons == null) return;
		if (rightToleft) {
			int rightw = (int)MetricsSharing.getInstance().getMetric("digits-rightfill"); //$NON-NLS-1$
			int x = getWidth() - buttonWidth - rightw;
			for (GlowButton b : buttons) {
				boolean tweakButton = b.getTag().equals("TWEAK");  //$NON-NLS-1$
				b.setLocation(x,tweakButton ? 3 : 2);
				x-=buttonWidth;
				b.setSize(buttonWidth,16);
				if (tweakButton) {
					x -= 10;
				}
			}
		} else {
			int rightw = 5;
			int x = rightw;
			for (GlowButton b : buttons) {
				b.setLocation(x,0);
				x+=buttonWidth;
				b.setSize(buttonWidth,16);
			}			
		}
		if (controls != null) {
			final int controlw = 25;
			int rightw = 5;
			int x = rightw;
			for (GlowButton b : controls) { 
				if (b.getTag().equals("RESET")) { //$NON-NLS-1$
					b.setLocation(x,3);
				} else {
					b.setLocation(x,0);
				}
				x+=controlw;
				b.setSize(controlw,16);
			}	
		}
	}
	
}
