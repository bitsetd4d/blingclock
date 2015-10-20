package countdowntimer.controls;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class GradientPanel extends JPanel {
	
	@Override
	protected void paintComponent(Graphics g) {		
		super.paintComponent(g);
		int w = getWidth();
		int h = getHeight();
		Graphics2D g2 = (Graphics2D)g;
	    GradientPaint painter = new GradientPaint(0.0f, 0.0f,
               new Color(0.0f, 0.0f, 0.0f, 0.0f),
               0.0f, h,
               new Color(0.15f, 0.15f, 0.15f, 1.0f));
	    g2.setPaint(painter);
	    g2.fill(new Rectangle2D.Double(0, 0, w, h));
	    paintChildren(g2);
	}

}
