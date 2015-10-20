package blingclock.controls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;

public class SimpleImageButton extends JButton {
	
	@Override
	protected void paintComponent(Graphics g) {
		if (getModel() == null) return;
		g.setColor(Color.BLACK);
		Rectangle r = g.getClipBounds();
		g.fillRect(r.x,r.y,r.width,r.height);
		Icon toDraw = getIcon();
		if (toDraw != null) {
			toDraw.paintIcon(this,g,0,0);
		}
	}
	
	@Override
	protected void paintBorder(Graphics g) {
	}

}
