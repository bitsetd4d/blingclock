package countdowntimer.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import countdowntimer.Styling;

import blingclock.controls.HQJLabel;


public class FadeMessagePanel extends JPanel  {
	
	private HQJLabel messageLabel;
	private Timer timer;
	private float backgroundAlpha;
	
	private static FadeMessagePanel lastPanel;
	
	private static final int FADEOUT_MS = 3000;
	
	public FadeMessagePanel() {
		super();
		init();
	}
	public FadeMessagePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public FadeMessagePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public FadeMessagePanel(LayoutManager layout) {
		super(layout);
		init();
	}
	
	private void init() {		
		setOpaque(false);
		setVisible(false);
		setLayout(new BorderLayout());
		backgroundAlpha = 0.1f;
		messageLabel = new HQJLabel();
		messageLabel.setFont(new Font(Styling.FONT, 0, 43));
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageLabel.setForeground(new Color(0,0,0,0));
		messageLabel.setText(""); //$NON-NLS-1$
		add(messageLabel,BorderLayout.CENTER);
		setupTimer();
		validate();
	}
	
	private void setupTimer() { 
		timer = new Timer(100,new ActionListener() { public void actionPerformed(ActionEvent e) {
			onTimerFired();
		}});
	}
	
	public static void showMessage(JFrame frame,String msg) {
		showMessage(frame,msg,false,false);
	}
	
	public static void showMessageWide(JFrame frame,String msg) {
		showMessage(frame,msg,true,false);
	}
		
	public static void showMiniMessage(JFrame frame,String msg) {
		showMessage(frame,msg,true,true);
	}
	
	private static void showMessage(JFrame frame,String msg,boolean wide,boolean mini) {
		if (lastPanel != null) {
			lastPanel.goAwayQuickly();
		}
		FadeMessagePanel fade = new FadeMessagePanel();
    	frame.getLayeredPane().add(fade,new Integer(1000));
    	int w = frame.getWidth();
    	int h = frame.getHeight();
    	double f = wide ? 0.9 : 0.5;
    	int x = (int)(w * ((1 - f)/2));
    	int l = (int)(w*f);
    	int wh =  mini ? 20 : (int)(h*0.25);
    	int y = mini ? h - 60 : (int)(h*0.5);
    	fade.setLocation(x,y);
    	fade.setSize(l,wh);
    	fade.setVisible(true);
    	frame.getLayeredPane().validate();
    	fade.setMessage(msg);
    	fade.animate();
    	lastPanel = fade;
	}

	private void goAwayQuickly() {
		if (alpha > 0.5f) alpha = 0.5f;
		stage = Anim.FADE_OUT;
		timerStarted = System.currentTimeMillis() - FADEOUT_MS;
	}
	
	private void setMessage(String msg) {
    	messageLabel.setText(msg);
    	int sz = 43;
    	int preferredSize = getWidth() / msg.length();
    	int preferredHeightSize = (int)(getHeight() * 0.9);
 		sz = Math.min(sz,preferredSize);
 		sz = Math.min(sz,preferredHeightSize);
    	messageLabel.setFont(new Font(Styling.FONT, 0, sz));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(0.8f,0,0,backgroundAlpha));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
	
	// -------------------------------------------------
	// Fade effect
	// -------------------------------------------------
	public enum Anim { FADE_IN, HOLD, FADE_OUT, FINAL_FADE };
	private float alpha;
	private Anim stage;	
	private long timerStarted;
	protected void onTimerFired() {
		long now = System.currentTimeMillis();
		long t = now - timerStarted; 
		switch (stage) {
		case FADE_IN:			
			doFadeIn(t);
			return;
		case HOLD:
			doHold(t);
			return;
		case FADE_OUT:
			doFadeOut(t);
			return;
		case FINAL_FADE:			
		default:
			doFinalFade(t);
		}
	} 
	
	private void doFadeIn(long t) {
		alpha = t / 1000f;
		if (alpha > 1.0f) {
			alpha = 1.0f;
			stage = Anim.HOLD;
		}
		messageLabel.setForeground(new Color(1f,1f,1f,alpha));
		setVisible(true);		
	}
	
	private void doHold(long t) { 
		if (t >= FADEOUT_MS) {
			stage = Anim.FADE_OUT;
		}
	}

	private void doFadeOut(long t) {
		long t2 = t - FADEOUT_MS;
		alpha = 1.0f - (t2 / 1000.0f);
		if (alpha < 0) {
			alpha = 0;
		}
		messageLabel.setForeground(new Color(1f,1f,1f,alpha));
		if (alpha == 0) {
			stage = Anim.FINAL_FADE;
		}
	}

	
	private void doFinalFade(long t) {
		backgroundAlpha -= 0.01f;
		if (backgroundAlpha >= 0) {
			repaint();	
		} else {
			setVisible(false);
			tidyUpPanel();
			timer.stop();
		}
	}
	
	private void tidyUpPanel() {
		Container c = getParent();
		if (c instanceof JLayeredPane) {
			JLayeredPane jlp = (JLayeredPane)c;
			jlp.remove(this);
			jlp.validate();
		}
	}
	
	
	private void animate() {
		stage = Anim.FADE_IN;
		alpha = 0;
		timerStarted = System.currentTimeMillis();
		timer.start();
	}
	
		
}
