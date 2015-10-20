package countdowntimer.mini;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

import countdowntimer.CountdownKeyboardSupport;
import countdowntimer.CountdownWindow;
import countdowntimer.KeyboardInputTarget;
import countdowntimer.SecondsConverter;
import countdowntimer.preferences.Preferences;
import countdowntimer.sound.SoundPlayer;
import countdowntimer.timer.CountdownTimer;
import countdowntimer.timer.CountdownTimerListener;


public class MiniWindow extends JFrame implements CountdownTimerListener, PanelFeedbackListener, KeyboardInputTarget {
	
	private boolean pressed;
	private int px,py,wx,wy;
	private Dimension scrnsize;
	
	private MiniControls controlsPanel;
	private MiniControls2 controlsPanel2;
	private MiniDigitPanel digitsPanel;
	private MiniProgressBarPanel progressPanel;
	private MiniRestoreOrResizePanel restorePanel;
	
	private CountdownTimer countdownTimer;
	private CountdownWindow restoreWindow; 
	
	private WindowMover windowMover = new WindowMover();
	private DropDownFrame dropDownFrame;
	
	private int timerInstance = 1;
	
	private boolean osx;
	
	public void setCountdownTimer(CountdownTimer countdownTimer) {
		if (countdownTimer == this.countdownTimer) return;
		if (this.countdownTimer != null && countdownTimer != this.countdownTimer) {
			this.countdownTimer.removeListener(this);
		}
		this.countdownTimer = countdownTimer;
		this.countdownTimer.addListener(this);
		this.countdownTimer.callbackListeners();
		showPausePlayButtons();
	}
	
	public void setRestoreWindow(CountdownWindow countdownWindow) {
		this.restoreWindow = countdownWindow;
	}
	
	public MiniWindow() {
		setBackground(new Color(0,0,0));
		setUndecorated(true);
		setAlwaysOnTop(true);
		
		setLayout(null);
		setupPanels();
		setupMovementListeners();
	
		// Get the default toolkit
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		scrnsize = toolkit.getScreenSize();		
		
		try {
			osx = System.getProperty("os.name").toLowerCase().startsWith("mac os x"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {}
	
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onWindowResized();
				savePosition();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				savePosition();
			}
		});
		
		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				CountdownKeyboardSupport.getInstance().gotFocus(MiniWindow.this);
			}
			public void windowLostFocus(WindowEvent e) {
				CountdownKeyboardSupport.getInstance().lostFocus(MiniWindow.this);
			}
		});

		
		if (!Preferences.getInstance().isAlarmSoundMuted()) {
			playAudioClip("mini-activate-navigate_67.wav"); //$NON-NLS-1$
		}
		
		dropDownFrame = new DropDownFrame(this);
		windowMover.addComponent(dropDownFrame);
	}
	
	private void playAudioClip(final String name) {
		URL url = getSoundUrl(name);
		SoundPlayer.playSound(url,5,true);
	}
	
	private URL getSoundUrl(String name) {
		Class cls = getClass();
		URL url = cls.getResource("/sounds/" + name); //$NON-NLS-1$
		try {
			if (url == null) {
				return new URL("file:sounds" + File.separator + name); //$NON-NLS-1$
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;	
	}

	protected void onWindowResized() {
		int w = getWidth();
		if (w < 30) w = 30;
		controlsPanel.setLocation(0,0);
		controlsPanel.setSize(18,18);

		digitsPanel.setLocation(18,0);
		digitsPanel.setSize(43,18);
		
		controlsPanel2.setLocation(61,0);
		controlsPanel2.setSize(18,18);

		int pgw = w - 75 - 20;
		progressPanel.setLocation(75,0);
		progressPanel.setSize(pgw,18);
		
		restorePanel.setLocation(w-20,0);
		restorePanel.setSize(20,18);	

	}

	private void setupPanels() {
		controlsPanel = new MiniControls(this);
		add(controlsPanel);

		digitsPanel = new MiniDigitPanel();
		add(digitsPanel);
		
		controlsPanel2 = new MiniControls2(this);
		add(controlsPanel2);

		progressPanel = new MiniProgressBarPanel(timerInstance);
		boolean showAsSeconds = Preferences.getInstance().getMiniMode().equals("SECONDS"); //$NON-NLS-1$
		progressPanel.setShowAsSeconds(showAsSeconds);
		add(progressPanel);
		
		restorePanel = new MiniRestoreOrResizePanel(this,this);
		add(restorePanel);
		
		onWindowResized();
	}
	
	private void openMiniFrame() {
		TimingTarget t = new TimingTarget() {
			public void begin() {
				dropDownFrame.setLocation(getX()+2, getY() + getHeight());
				dropDownFrame.setSize(0,0);
				dropDownFrame.setVisible(true);
			}
			public void end() {}

			public void repeat() {}

			public void timingEvent(float fraction) {
				int w = Math.min(200,getWidth()-4);
				int hNow = (int)Math.min(128*fraction, 25);
				dropDownFrame.setSize((int)(w * fraction), hNow);
			}
		};
		
        Animator a = new Animator(1000, t);
        a.setStartDelay(50);
        a.setAcceleration(0.1f);
        a.setDeceleration(0.8f);
        a.start();	
	}
	
	private void closeMiniFrame() {
		TimingTarget t = new TimingTarget() {
			int sw,sh;
			public void begin() {
				sw = dropDownFrame.getWidth();
				sh = dropDownFrame.getHeight();
			}
			public void end() {
				dropDownFrame.setVisible(false);
			}

			public void repeat() {}

			public void timingEvent(float fraction) {
				float f = 1 - fraction;
				dropDownFrame.setSize(sw,(int)(sh * f));
			}
		};
		
        Animator a = new Animator(400, t);
        a.setStartDelay(50);
        a.setAcceleration(0.1f);
        a.setDeceleration(0.8f);
        a.start();	
	}
	
	private void setupMovementListeners() {
		windowMover.addComponent(this);
	}

	public void onCountdownReachedZero() {
		digitsPanel.setTimeRemaining(0); 
		new AlarmThread().start();
		showPausePlayButtons();
	}
	
	public void onCountdownZeroRestartingLoop() {
//		digitsPanel.setTimeRemaining(0); 
		new AlarmThread().start();
	}
	
	public void onCountdownReachedZeroEnteringOverrun() {
		new AlarmThread().start();
		// Undo the stopwatch
		countdownTimer.stop();
		countdownTimer.startCountdownMode();
		showPausePlayButtons();
	}


	public void onTick(int days,long remaining,long hundredths) {
		updateDisplay();
	}
	
	private void updateDisplay() { 
		int tenths = (int)countdownTimer.getCountdownTenths();
		int s = (int)countdownTimer.getSeconds();
		int tot = (int)countdownTimer.getTargetSeconds();
		digitsPanel.setTimeRemaining(s);
		progressPanel.setTargetSeconds(tot);
		progressPanel.setTenths(tenths);
		String label = SecondsConverter.secondsToHHMM(s);
		setTitle("["+label+"] Bling Clock"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void savePosition() {
		if (Preferences.getInstance().getRememberPositionOnScreen()) {
			Rectangle r = getBounds();
			Preferences.getInstance().getTimerPreference(timerInstance).setLastMiniPosition(r); // FIXME to use pased in timer preference
		}
	}
	
	
//	public static void main(String[] args) {
// 	 	try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	//System.setProperty("apple.awt.windowShadow","false");
//		SwingUtilities.invokeLater(new Runnable() { public void run() {
//			MiniWindow w = new MiniWindow();
//			w.setVisible(true);
//			w.setLocation(50,30);
//			w.setSize(200,18);
//		}});
//	}
	
	public void showPausePlayButtons() {
		if (countdownTimer.isRunning()) {
			controlsPanel2.showPause();	
			controlsPanel2.hideReset();		
		} else {
			controlsPanel2.showPlay();
			controlsPanel2.showReset();
		}
	}

	public void onPausePlayButtonPressed() {
		if (countdownTimer.getSeconds()  == 0) return;
		if (countdownTimer.isRunning()) {
			countdownTimer.stop();
		} else {
			countdownTimer.start();
		}
		showPausePlayButtons();
		updateDisplay();
	}

	public void onResetButtonPressed() {
		countdownTimer.resetTime();
		updateDisplay();
	}

//	public void onPauseButtonPressed() {
//    	if (countdownTimer.getSeconds()  == 0) return;
//    	countdownTimer.stop();
//    	updateDisplay();
// 	}

	public void onPlusButtonPressed() {
		int t = Preferences.getInstance().getTimeUnit();
		countdownTimer.increaseTime(t); 
		updateDisplay();
	}
	
	public void onMinusButtonPressed() {
		int t = Preferences.getInstance().getTimeUnit();
		countdownTimer.reduceTime(t); 
		updateDisplay();
	}

	public void onRestoreButtonPressed() {
		if (restoreWindow != null) {
			setVisible(false);
			restoreWindow.restoring();
			restoreWindow.setVisible(true);
			if (!Preferences.getInstance().isAlarmSoundMuted()) {
				playAudioClip("mini-deactivate-menu_move_2.wav"); //$NON-NLS-1$
			}
		}
	}
	
	public void restoreToPreviousPosition() {
		Rectangle r = Preferences.getInstance().getTimerPreference(timerInstance).getLastMiniWindowPosition();
		if (r == null) {
			r = new Rectangle(20,20,220,18);
		} else {
			r.height = 18;
		}
		setBounds(r);
	}
	
	public void restoring() {
		updateDisplay();
		Runnable r = new Runnable() { public void run() {
			try { Thread.sleep(500); } catch (Exception e) {}
			SwingUtilities.invokeLater(new Runnable() { public void run() {
				progressPanel.repaint();
			}});
		}};
		new Thread(r).start();
	}
	
	private class AlarmThread extends Thread {
		@Override
		public void run() {
			for (int i=0; i<4; i++) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() { public void run() {
						progressPanel.setBackground(Color.RED);
						digitsPanel.setShowDigits(false);					
					}});
				} catch (InterruptedException e1) {} catch (InvocationTargetException e1) {}
				
				try {
					sleep(500);
				} catch (InterruptedException e) {}
				
				try {
					SwingUtilities.invokeAndWait(new Runnable() { public void run() {
						progressPanel.setBackground(Color.BLACK);
						digitsPanel.setShowDigits(true);
					}});
				} catch (InterruptedException e1) {} catch (InvocationTargetException e1) {}

				try {
					sleep(500);
				} catch (InterruptedException e) {}				
			}
		}
	}
	
	// -------------------------------------------------------
	// KeyboardInputTarget
	// -------------------------------------------------------
	public void applyTime(int s, int m, int h, int d) {
		//System.out.println("s="+s+", m="+m+", h="+h+", d="+d);
		int secs = s + m * 60 + h * 60 * 60 + d * 24 * 60 * 60;
		boolean running = false;
		if (countdownTimer.isRunning()) {
			countdownTimer.stop();
			running = true;
		}
		countdownTimer.setSeconds(secs);
		countdownTimer.setTargetSeconds(secs);
		if (running) {
			countdownTimer.start();
		}
		updateDisplay();
	}
	
	public JLayeredPane getTheLayeredPane() {
		return dropDownFrame.getLayeredPane(); 
	}

	public Rectangle getDigitsPanelBounds() {
		return new Rectangle(10,-78,100,100);
	}

	public boolean isTimeMode() { return false; }

	public void keyboardPausePlay() {
		onPausePlayButtonPressed();
	}

	public void keyboardReset() {
		if (!countdownTimer.isRunning()) {
			countdownTimer.resetTime();
			updateDisplay();
		}
	}

	public void keyboardVisualiseLeft() {}
	public void keyboardVisualiseRight() {}
	public void displayNotRegisteredAndInTrialMode() {}

	public void keyboardRestoreWindowPosition() {
		setLocation(20,20);
		setSize(400,18);
	}

	public void timePanelClosed() {
		closeMiniFrame();
	}

	public void timePanelOpen() {
		openMiniFrame();
	}

	public void applyEnteredTweak(String tweakName) {}

}
