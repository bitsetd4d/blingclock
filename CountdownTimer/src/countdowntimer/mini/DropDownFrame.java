package countdowntimer.mini;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import countdowntimer.CountdownKeyboardSupport;
import countdowntimer.KeyboardInputTarget;

public class DropDownFrame extends JFrame {
	
	private KeyboardInputTarget keyboardInput;
	private JPanel backLabel;
	
	public DropDownFrame(KeyboardInputTarget keyboardInput) {
		setBackground(new Color(0,0,0));
		setUndecorated(true);
		setAlwaysOnTop(true);
		setLayout(null);
		this.keyboardInput = keyboardInput;
		addFocusListener();
		backLabel = new JPanel();
		backLabel.setBackground(new Color(0,0,0));
		add(backLabel);
		addResizeListener();
		onWindowResized();
	}

	private void addFocusListener() {
		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				CountdownKeyboardSupport.getInstance().gotFocus(keyboardInput);
			}
			public void windowLostFocus(WindowEvent e) {
				CountdownKeyboardSupport.getInstance().lostFocus(keyboardInput);
			}
		});
	}
	
	private void addResizeListener() { 
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onWindowResized();
			}
		});
	}
	
	protected void onWindowResized() {
		Rectangle r = getBounds();
		backLabel.setBounds(0,0,r.width,r.height);
		//System.out.println("BL "+r.width+","+r.height);
	}

//	@Override
//	public void paintComponents(Graphics g) {
//		g.setColor(Color.BLACK);
//		Rectangle clip = g.getClipBounds();
//		g.fillRect(clip.x,clip.y, clip.width, clip.height);
//		super.paintComponents(g);
//	}
	
}
