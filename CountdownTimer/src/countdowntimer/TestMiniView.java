package countdowntimer;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;

public class TestMiniView extends JFrame {
	
	private boolean dragging;
	private int bx,by;
	private int wx,wy;
	
	private int top = 20;
	
	public static void main(String[] args) {
		new TestMiniView().open();
	}
	
	public void open() {
		setUndecorated(true);
		setBackground(new Color(0,0,0));
		setSize(200,15);
		setVisible(true);
		GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		top = insets.top;
		addMouseListener(new MouseAdapter() { 
			@Override
			public void mousePressed(MouseEvent e) {
				dragging = true;
				bx = e.getX();
				by = e.getY();
				wx = getX();
				wy = getY();
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				dragging = false;			
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() { 
			@Override
			public void mouseDragged(MouseEvent e) {
				if (dragging) {
					int dx = e.getX() - bx;
					int dy = e.getY() - by;
					wx += dx;
					wy += dy;
					if (wy < top) wy = top;
					setLocation(wx,wy);
				}
			}
		});
	}

}
