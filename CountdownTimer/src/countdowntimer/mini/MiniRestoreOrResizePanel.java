package countdowntimer.mini;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import blingclock.controls.SimpleImageButton;


public class MiniRestoreOrResizePanel extends JPanel {
	
	private PanelFeedbackListener listener;
	private SimpleImageButton restoreButton = new SimpleImageButton();
	
	private boolean pressed;
	
	private MiniWindow window;
	private int startX;
	private int baseWidth;
	private boolean overActionArea;
	
	private static final int MOVE_W = 10;
	private static final int MIN_W = 150;

	public MiniRestoreOrResizePanel(MiniWindow window,PanelFeedbackListener myListener) {
		super();
		this.window = window;
		this.listener = myListener;
		setBackground(new Color(0,0,0));
		setLayout(null);

		restoreButton.setIcon(ImageLoader.loadImage("tiny_restore.png")); //$NON-NLS-1$
		add(restoreButton);
		restoreButton.setLocation(4,4);
		restoreButton.setSize(20,10);
		
		restoreButton.addActionListener(new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			if (overActionArea) {
				listener.onRestoreButtonPressed();
			}
		}});
		hookupMouse();
	}
	
	private void hookupMouse() {
		restoreButton.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if (e.getX() > MOVE_W) {
					pressed = true;
					Point p = restoreButton.getLocationOnScreen();
					startX = p.x + e.getX();
					baseWidth = window.getWidth();
				}
			}
			@Override public void mouseReleased(MouseEvent e) {
				pressed = false;
			}	
			@Override
			public void mouseExited(MouseEvent e) {
				restoreButton.setCursor(null);
			}
		});
		
		restoreButton.addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseDragged(final MouseEvent e) {
				if (pressed) { 
					final Dimension sz = window.getSize();
					Point p = restoreButton.getLocationOnScreen();
					int currentX = p.x + e.getX();
					int dx = currentX - startX;
					int newWidth = baseWidth+dx;
					if (newWidth < MIN_W) {
						newWidth = MIN_W;
					}
					window.setSize(newWidth,sz.height);	
					Cursor c = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
					restoreButton.setCursor(c);
				}
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getX() > MOVE_W) {
					overActionArea = false;
					Cursor c = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
					restoreButton.setCursor(c);
				} else {
					overActionArea = true;
					restoreButton.setCursor(null);
				}
			}
		});
	}
	
	
}
