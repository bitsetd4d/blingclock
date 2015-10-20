package blingclock.controls;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;

import blingclock.util.GraphicsUtil;

public class HQJLabel extends JLabel {
	
	public HQJLabel() {
		super();
	}

	public HQJLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
	}

	public HQJLabel(Icon image) {
		super(image);
	}

	public HQJLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
	}

	public HQJLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
	}

	public HQJLabel(String text) {
		super(text);
	}

	@Override
	protected void paintComponent(Graphics g) {
		GraphicsUtil.setRenderHintsForTextOnly((Graphics2D)g);
		super.paintComponent(g);
	}
	
	@Override
	public void repaint() {
		RepaintController.repaint(this);
	}

}
