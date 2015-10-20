package countdowntimer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Savepoint;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;

import blingclock.controls.GlowButton;
import blingclock.controls.GlowButton.Style;
import blingclock.digits.SpeedupGlowButtonAdapter;
import blingclock.layout.BlingAttachmentSide;
import blingclock.util.GlobalImageCache;
import blingclock.util.MouseMovingListener;
import blingclock.util.MouseTrackingService;
import blingclock.visualiser.TimeBarVisualiserPanel;
import blingclock.visualiser.TimeSelectionListener;

import countdowntimer.controls.FadeMessagePanel;
import countdowntimer.dragndrop.DropFileAcceptor;
import countdowntimer.dragndrop.DropListener;
import countdowntimer.glasspane.CustomGlassPane;
import countdowntimer.mini.MiniWindow;
import countdowntimer.mini.WindowMover;
import countdowntimer.preferences.Preferences;
import countdowntimer.preferences.PreferencesListener;
import countdowntimer.preferences.PrefsPanel;
import countdowntimer.preferences.TimerPreference;
import countdowntimer.preferences.UserLocalFileAccess;
import countdowntimer.registration.Registration;
import countdowntimer.registration.RegistrationListener;
import countdowntimer.registration.RegistrationThankyouPanel;
import countdowntimer.registration.RegistrationWatcher;
import countdowntimer.sound.SoundCanceller;
import countdowntimer.sound.SoundPlayer;
import countdowntimer.timer.CountdownEvent;
import countdowntimer.timer.CountdownRegistry;
import countdowntimer.timer.CountdownTimer;
import countdowntimer.timer.CountdownTimerListener;
import countdowntimer.timer.gui.CurrentTimersPanel;
import countdowntimer.timer.internal.SubMode;
import countdowntimer.tweaks.TweakPanelControl;
import countdowntimer.tweaks.TweakRegistry;
import countdowntimer.update.BrowserLaunching;
import countdowntimer.update.UpdateCheckListener;
import countdowntimer.update.UpdateChecker;
import countdowntimer.util.TimedRunner;
import countdowntimer.util.TimedTask;
import countdowntimer.visualiser.SingleTimeBarVisualiserPanel;


public class CountdownWindow extends NewCountdownFrame2 implements CountdownTimerListener, PreferencesListener, TimeSelectionListener, KeyboardInputTarget {

	private CountdownTimer countdownTimer = CountdownRegistry.getInstance().createTimer();
	private TimerPreference timerPreference;
	
	private float pausePlayButtonFade = Preferences.getInstance().getButtonPlayPauseDisabledButtonFade();
    private boolean fracEnabled;
	
	private boolean muted = false;
	private int layoutMode = 0;
	
	private static int VERSION = Branding.getCurrentVersion();
	
	private boolean isFullscreen;
	private boolean isHideWindowDecorations;
	private CountdownWindow restoreWindow;
	private boolean compactButtons;
	
	private static boolean veryFirstRun = true;

	private WindowMover windowMover = new WindowMover();
	private GlowButton closeButton;
	private GlowButton minButton;
	private GlowButton maxButton;
	private GlowButton resizeButton;
	
	private List<GlowButton> fadingButtons = new ArrayList<GlowButton>();
	
	private TweakPanelControl tweakPanelControl = new TweakPanelControl();
	
	private int clickCount;
	private Timer flashTimeModeTimer;
	private int flashCount;
	
	public CountdownWindow(boolean fullscreen,boolean noWindowDecorations,int timerInstance,boolean timeMode,boolean stopWatchMode) {
		super(fullscreen,noWindowDecorations,timerInstance);
		this.isFullscreen = fullscreen;
		this.isHideWindowDecorations = noWindowDecorations;
		timerPreference = Preferences.getInstance().getTimerPreference(timerInstance);
		fracEnabled = timerPreference.isFractionDigitsShown();
		timerPreference.setRemoveTitlebar(noWindowDecorations);
				
		if (!fullscreen) {
			windowMover.addComponent(this);
		}
		
		addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) {
			clickCount++;
			MouseTrackingService.getInstance().mouseHasBeenClicked();
		}});
		
		countdownTimer.setSeconds(60);
		countdownTimer.setTargetSeconds(60);
		
		CountdownEvent event = CountdownRegistry.getInstance().getTimerEvent(timerInstance);
		event.programTimer(countdownTimer);
		countdownTimer.setLinkedEvent(event);
		if (!countdownTimer.isRunning() && countdownTimer.getHundreths() == 0) {
			countdownTimer.setTargetSeconds(5*60);
			countdownTimer.setSeconds(5*60);
		}
		if (Registration.getInstance().isTrialModeOrRegistered()) {
			if (timeMode) {
				countdownTimer.startTimeMode();
			} else if (stopWatchMode) {
				countdownTimer.startStopwatchMode();
			}
		}
		
		bottomTimerPanel.setBackground(Color.BLACK);
		getContentPane().setBackground(new Color(0,0,0));
		countdownTimer.addListener(this);
		prefsButton.setVisible(Features.getInstance().isPrefsEnabled());
		onTopButton.setVisible(Features.getInstance().isOnTopEnabled());
		muteButton.setVisible(Features.getInstance().isMuteButtonEnabled());
		visualiseButton.setVisible(Features.getInstance().isShowHideButtonEnabled());
		fullButton.setVisible(Features.getInstance().isFullViewAvailable());
		
		MouseMovingListener moveListener = new MouseMovingListener() {
			
			public void onMouseMoving() {
				fadeInButtons();
				unhideMousePointer();
			}
			
			public void onMouseStopped3Seconds() {
				fadeOutButtons();
			}
			
			public void onMouseStopped6Seconds() {
				hideMousePointer();
			}
			
		};
		
		MouseTrackingService.getInstance().registerWindow(this);
		MouseTrackingService.getInstance().addListener(moveListener);
			
		muted = Preferences.getInstance().isAlarmSoundMuted();
		countdownTimer.setSubMode(timerPreference.getSubMode());
		compactButtons = timerPreference.isCompactDigitsLayout();
		
		setAlwaysOnTop(timerPreference.isOnTop());
		setMuteButton();
		setFracButton();
		setLoopButton();
		setTopButton();
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onWindowResized();
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				onWindowMoved();
			}
		});
		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				CountdownKeyboardSupport.getInstance().gotFocus(CountdownWindow.this);
			}
			public void windowLostFocus(WindowEvent e) {
				CountdownKeyboardSupport.getInstance().lostFocus(CountdownWindow.this);
			}
		});
		if (Preferences.getInstance().getRememberPositionOnScreen()) {
			Rectangle pos = timerPreference.getLastWindowPosition();
			if (pos == null) {
				pos = new Rectangle(210,172,820,342);
			}
			setLocation(pos.x, pos.y);
			setSize(pos.width,pos.height);
		}
//		setMessageBarLabelVisibility();
		Preferences.getInstance().addListener(this);
		if (Preferences.getInstance().shouldCheckForNewVersion()) {
			UpdateChecker.asyncFindCurrentVersion(new UpdateCheckListener() {
				public void onCurrentVersion(String version) {
					onCurrentVersionIs(version);
				}
				public void onUnableToCheck() {}
			});
		}
		setTitle(timerPreference.getWindowTitle());
		bottomTimerPanel.setListener(this);
		layoutMode = timerPreference.getDisplayMode();
		getRootPane().setDefaultButton(null);
		CountdownKeyboardSupport.getInstance().registerListeners(this,prefsButton);
		addHideMouseSupport();
		digitsPanel.setShowDecimals(fracEnabled);
		Registration.getInstance().addListener(new RegistrationListener() { public void onRegistrationStatusChanged() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() { userJustRegistered(); }
			});
		}});
		if (isHideWindowDecorations && !isFullscreen) {
			addOwnWindowDecorations();
			sizeAndPositionWindowControls();
			removeTitleButton.setStyle(Style.ADD_TITLEBAR);
		}
		Styling.makeOnButtonColour(timeModeButton);
		setSubmodeButton();
		setTransparency();
		setupHoverHelp();
		setupFadingButtons();
		showRegisteredUser();
		setupDragAndDrop();
		setupTimeModeFlashTimer();
	}


	private void setupDragAndDrop() {
		// http://www.java-tips.org/java-se-tips/javax.swing/how-to-implement-drag-drop-functionality-in-your-applic.html
		DropTarget dt = new DropTarget(this, new DropFileAcceptor(new DropListener() {
			
			@Override
			public void onDropped(List<File> files) {
				try {
					System.out.println("DROP-LISTENER-> ON DROPPED "+files);
					if (files.isEmpty()) return;
					File f = files.get(0);
					String suffix = soundSuffix(f);
					if (suffix != null) {
						storeFileAsCustomAlarm(f,suffix);
					} else {
						FadeMessagePanel.showMiniMessage(CountdownWindow.this,"Custom alarms must be .wav or .mp3");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onDropFinished() {
				System.out.println("DROP-LISTENER-> ON DROP FINISHED");
			}
			
			@Override
			public void onDropEnter() {
				System.out.println("DROP-LISTENER-> ON DROP ENTER");
			}
		}));
	}
	
	protected void storeFileAsCustomAlarm(File f,String suffix) {
		if (!Registration.getInstance().isRegistered()) {
			FadeMessagePanel.showMiniMessage(CountdownWindow.this,"Only registered users can save a custom alarm");
			return;
		}
		try {
			String storeAsName = "custom-alarm1"+suffix;
			UserLocalFileAccess.deleteFilesWithPrefix("custom-alarm1");
			UserLocalFileAccess.copyFile(f, storeAsName);
			Preferences.getInstance().setCustomSoundName(f.getName());
			Preferences.getInstance().setStoredSoundName(storeAsName);
			FadeMessagePanel.showMiniMessage(CountdownWindow.this,"New custom alarm saved - "+f.getName());
			Preferences.getInstance().setSoundFile("CUSTOM_ALARM");
			Preferences.getInstance().setSoundRepeat(1);
			Preferences.getInstance().setSoundDelay(0);
			SoundPlayer.playCustomAlarmSound();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String soundSuffix(File f) {
		if (f.isDirectory()) return null;
		String fn = f.getName().toLowerCase();
		if (fn.endsWith(".mp3")) return ".mp3";
		if (fn.endsWith(".wav")) return ".wav";
		return null;		
	}

	protected void userJustRegistered() {
		if (!isVisible()) return;
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			setTimeModeDisplay(); // To remove BUY button
			RegistrationThankyouPanel p = new RegistrationThankyouPanel();
	    	getLayeredPane().add(p,new Integer(1000));
	    	getLayeredPane().validate();
	    	int w = getWidth();
	    	int h = getHeight();
	    	p.setSize(500, 200);
	    	p.setLocation((w-500)/2, 20);
	    	p.setVisible(true);
	    	buyButton.setVisible(false);
		}});
		UpdateChecker.asyncJustRegisteredCheckCurrentVersion();
	}

	private void setupFadingButtons() {
		fadingButtons.add(plusButton);
		fadingButtons.add(minusButton);
		fadingButtons.add(prefsButton);
		fadingButtons.add(visualiseButton);
		fadingButtons.add(fracButton);
		fadingButtons.add(miniButton);
		fadingButtons.add(compactButton);
		fadingButtons.add(removeTitleButton);
		fadingButtons.add(fullButton);
		fadingButtons.add(buyButton);
		fadingButtons.add(tweakButton);	
	}
	
	private void setupHoverHelp() {
		CustomGlassPane p = new CustomGlassPane();
		p.addHover(prefsButton, Messages.getString("CountdownWindow.HoverPreferencesButton"), Messages.getString("CountdownWindow.HoverPreferencesButtonDetail")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(visualiseButton, Messages.getString("CountdownWindow.HoverVisualiseButton"), Messages.getString("CountdownWindow.HoverVisualiseButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(miniButton,Messages.getString("CountdownWindow.HoverMiniMode"),Messages.getString("CountdownWindow.HoverMiniModeDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(fullButton,Messages.getString("CountdownWindow.HoverFullButton"),Messages.getString("CountdownWindow.HoverFullButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(compactButton,Messages.getString("CountdownWindow.HoverCompactButton"),Messages.getString("CountdownWindow.HoverCompactDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(removeTitleButton,Messages.getString("CountdownWindow.HoverRemoveTitleButton"),Messages.getString("CountdownWindow.HoverRemoveTitleButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(muteButton,Messages.getString("CountdownWindow.HoverMuteButton"),Messages.getString("CountdownWindow.HoverMuteButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(onTopButton,Messages.getString("CountdownWindow.HoverTopButton"),Messages.getString("CountdownWindow.HoverTopButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
//		p.addHover(loopButton,"Loop", "When the timer reaches zero, start again.");
		p.addHover(fracButton,Messages.getString("CountdownWindow.Hover100sButton"), Messages.getString("CountdownWindow.Hover100sButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(subModeButton,Messages.getString("CountdownWindow.HoverSubModeButton"), Messages.getString("CountdownWindow.HoverSubModeButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(timeModeButton, Messages.getString("CountdownWindow.HoverTimeModeButton"), Messages.getString("CountdownWindow.HoverTimeModeButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
_MARKER.mark();		
//		p.addTweakButton(debugButton1);
		p.addTweakButton(tweakButton);
		
		p.addHover(closeButton,Messages.getString("CountdownWindow.HoverCloseButton"),Messages.getString("CountdownWindow.HoverCloseButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(minButton,Messages.getString("CountdownWindow.HoverMiniButton"), Messages.getString("CountdownWindow.HoverMiniButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		p.addHover(maxButton,Messages.getString("CountdownWindow.HoverMaxButton"), Messages.getString("CountdownWindow.HoverMaxButtonDetails")); //$NON-NLS-1$ //$NON-NLS-2$
		
    	setGlassPane(p);
    	p.startMouseTracking();
    	getGlassPane().setVisible(true); 	
   }
	
	private void showRegisteredUser() {
		if (!veryFirstRun) return;
		
		/*
		 * Registered
		 */
		if (Registration.getInstance().isRegistered()) {
			String who = Registration.getInstance().getRegisteredName();
			if (who.trim().length() > 0) {
				MessageFormat mf = new MessageFormat(Messages.getString("CountdownWindow.Msg_RegisteredTo")); //$NON-NLS-1$
				String msg = mf.format(new Object[] { who });
				FadeMessagePanel.showMiniMessage(this,msg); 
			}
			return;
		}
		
		RegistrationWatcher watcher = new RegistrationWatcher();
		watcher.start();
		
		String msg = null;
		if (Registration.getInstance().isTrialMode()) {
			int trials = Registration.getInstance().getTrialRunsRemaining();
			if (trials == 1) {
				msg = Messages.getString("CountdownWindow.Msg_Trial1MoreGo"); //$NON-NLS-1$
			} else {
				MessageFormat mf = new MessageFormat(Messages.getString("CountdownWindow.Msg_TrialMode")); //$NON-NLS-1$
				msg = mf.format(new Object[] { trials });
			}
		} else {
			int x = Registration.getInstance().getDaysTillUnlockAgainApprox();
			if (x >= 7) {
				msg = Messages.getString("CountdownWindow.Msg_FreeMode"); //$NON-NLS-1$
			} else {
				MessageFormat mf = new MessageFormat(Messages.getString("CountdownWindow.Msg_FreeModeTrialLater")); //$NON-NLS-1$
				msg = mf.format(new Object[] { x });
			}
		}
		FadeMessagePanel.showMessageWide(this,msg);
	}
	
	void setCountdownTimer(CountdownTimer countdownTimer) {
		if (countdownTimer == this.countdownTimer) return;
		if (this.countdownTimer != null && countdownTimer != this.countdownTimer) {
			this.countdownTimer.removeListener(this);
		}
		this.countdownTimer = countdownTimer;
		this.countdownTimer.addListener(this);
		this.countdownTimer.callbackListeners();
	}
	
	private void addOwnWindowDecorations() {
		closeButton = new GlowButton(0.2,0.4);
		closeButton.setInvisibleWhenDimmed(true);
		closeButton.setStyle(Style.CLOSE);
		closeButton.setVisible(true);
		getLayeredPane().add(closeButton,new Integer(999));
		closeButton.setForeground(new Color(187,67,67));
		closeButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
			CountdownWindow.this.setVisible(false);
			CountdownWindow.this.dispose();
			System.exit(0);
		}});
		
		minButton = new GlowButton(0.3,0.3);
		minButton.setInvisibleWhenDimmed(true);
		minButton.setStyle(Style.MINIMIZE);
		minButton.setVisible(true);
		getLayeredPane().add(minButton,new Integer(999));
		minButton.setForeground(new Color(226,135,60));
		minButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
			CountdownWindow.this.setState(Frame.ICONIFIED);
		}});
		
		maxButton = new GlowButton(0.4,0.2);
		maxButton.setInvisibleWhenDimmed(true);
		maxButton.setStyle(Style.MAXIMIZE);
		maxButton.setVisible(true);
		getLayeredPane().add(maxButton,new Integer(999));
		maxButton.setForeground(new Color(117,187,69));
		maxButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
			int state = CountdownWindow.this.getExtendedState();
			if (state == Frame.MAXIMIZED_BOTH) {
				CountdownWindow.this.setExtendedState(Frame.NORMAL);
			} else {
				CountdownWindow.this.setExtendedState(Frame.MAXIMIZED_BOTH);
			}
		}});
		
		resizeButton = new GlowButton(0.3,0.2);
		resizeButton.setInvisibleWhenDimmed(true);
		resizeButton.setStyle(Style.RESIZE);
		resizeButton.setVisible(true);
		getLayeredPane().add(resizeButton,new Integer(999));
		//resizeButton.setLocation(11,1);
		resizeButton.setSize(12,12);
		resizeButton.setForeground(new Color(226,135,60));
		hookupResizeListeners(resizeButton);
	}
	
	private void sizeAndPositionWindowControls() { 
		if (closeButton == null) return; // No Window controls
		int wg[] = TweakRegistry.getInstance().getCloseButtonMetrics();
		int w = wg[0];
		int g = wg[1];
		closeButton.setLocation(g,g);
		closeButton.setSize(w,w);
		minButton.setLocation(w+g+g,g);
		minButton.setSize(w,w);
		maxButton.setLocation(g + (w+g)*2,g);
		maxButton.setSize(w,w);
	
//			closeButton.setLocation(1,1);
//			closeButton.setSize(10,10);
//			minButton.setLocation(11,1);
//			minButton.setSize(10,10);
//			maxButton.setLocation(22,1);
//			maxButton.setSize(10,10);			
		
	}
	
	private static final int MOVE_W = 3;
	private boolean pressed;
	private int startMoveX, startMoveY;
	private int startMoveWidth, startMoveHeight;
	private void hookupResizeListeners(final GlowButton button) {
		button.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if (e.getX() > MOVE_W && e.getY() > MOVE_W) {
					pressed = true;
					Point p = button.getLocationOnScreen();
					startMoveX = p.x + e.getX();
					startMoveY = p.y + e.getY();
					startMoveWidth = getWidth();
					startMoveHeight = getHeight();
				}
			}
			@Override public void mouseReleased(MouseEvent e) {
				pressed = false;
			}	
			@Override
			public void mouseExited(MouseEvent e) {
				button.setCursor(null);
			}
		});
		
		button.addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseDragged(final MouseEvent e) {
				if (pressed && button.isVisible()) { 
					Point p = button.getLocationOnScreen();
					int currentX = p.x + e.getX();
					int currentY = p.y + e.getY();
					int dx = currentX - startMoveX;
					int dy = currentY - startMoveY;
					int newWidth = startMoveWidth+dx;
					int newHeight = startMoveHeight+dy;
					Dimension min = getMinimumDesiredSize();
					if (newWidth < min.width) {
						newWidth = min.width;
					}
					if (newHeight < min.height) {
						newHeight = min.height;
					}
					setSize(newWidth,newHeight);	
					Cursor c = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
					button.setCursor(c);
				}
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getX() > MOVE_W && e.getY() > MOVE_W) {
					Cursor c = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
					button.setCursor(c);
				} else {
					button.setCursor(null);
				}
			}
		});
	}

	private boolean hidingPointer = false;
	private Cursor transparentCursor;

	private void addHideMouseSupport() {
		int[] pixels = new int[16 * 16];
		Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
		transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor"); //$NON-NLS-1$
	}
	
	private void hideMousePointer() {
		if (Preferences.getInstance().isHideMouseEnabled() && !hidingPointer) {
			hidingPointer = true;
			System.out.println("Hiding mouse pointer");
			CountdownWindow.this.setCursor(transparentCursor);
		}						
	}
	
	private void unhideMousePointer() {
		if (Preferences.getInstance().isHideMouseEnabled()) {
			hidingPointer = false;
			System.out.println("Unhiding mouse pointer");
			CountdownWindow.this.setCursor(null);	
		}
	}

	protected void onCurrentVersionIs(String version) {
		try {
			int v = Integer.parseInt(version);
			if (v > VERSION) {
				try { Thread.sleep(7000); } catch (Exception e) {};
				FadeMessagePanel.showMessageWide(this,Messages.getString("CountdownWindow.Msg_NewVersionAvailable1")); //$NON-NLS-1$
				try { Thread.sleep(2000); } catch (Exception e) {};
				displayMiniMessage(Messages.getString("CountdownWindow.Msg_NewVersionAvailable2")); //$NON-NLS-1$
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private void setMessageBarLabelVisibility() {
//		if (Preferences.getInstance().getShowMessagebarSecondsRemaining()) {
//			Styling.setMainForeground(secondsLabel);
//		} else {
//			Styling.setMakeInvisible(secondsLabel);
//		}
//		
//		if (Preferences.getInstance().getShowMessagebarTimeRemaining()) {
//			Styling.setMainForeground(timeLabel);
//		} else {
//			Styling.setMakeInvisible(timeLabel);
//		}
//	}

	protected void onWindowResized() {
		if (!didJustForceResize()) {
			ensureMinimumSize();
			if (prefsPanel != null) {
				resizePrefsPanel();
			}
			if (timersPanel != null) {
				resizeCurrentTimersPanel();
			}
			if (resizeButton != null) {
				resizeButton.setLocation(getWidth()-12,getHeight()-12);
			}
			rememberThisPosition();
			int fontForTitle = getHeight() / 20;
			titleLabel.setFont(new Font(Styling.FONT, Font.BOLD + Font.ITALIC, fontForTitle));
		}
		GlobalImageCache.getInstance().clearCache();
	}
	
	private void ensureMinimumSize() {
		int w = getWidth();
		int h = getHeight();
		Dimension min = getMinimumDesiredSize();
		if (w > min.width && h > min.height) return;
		int neww = Math.max(w,min.width);
		int newh = Math.max(h,min.height);
		setSize(neww,newh);
	}
	
	private Dimension getMinimumDesiredSize() { 
		int minw = 520;
		int minh = 280;
		boolean titleVisible = digitsPanel.isTitleVisible();
//System.out.println("LayoutMode "+layoutMode);
		switch (layoutMode) {
		case 0:
//			minw = 320;
//			minh = 280;
			if (compactButtons) {
				minw = 310;
				minh = 260;
			} else {
				minw = 420;
				minh = 260;
			}
			break;
		case 1:
			if (compactButtons) {
				minw = 310;
				minh = 260;
			} else {
				minw = 380;
				minh = 280;
			}
			break;
		case 2:
			if (compactButtons) {
				minw = 310;
				minh = 260;
			} else {
				minw = 380;
				minh = 260;
			}
			break;
		case 3:
			minw = 600;
			//minh = 180;
			if (compactButtons) {
				minh = titleVisible ? 150 : 130; 
			} else {
				minh = 155;
			}
			break;
		case 4:
			if (compactButtons) {
				minw = 500;
				minh = 140;
			} else {				
				minw = 600;
				minh = 160;
			}
			break;
		case 5:
			if (compactButtons) {
				minw = 500;
				minh = 140;
			} else {				
				minw = 600;
				minh = 160;
			}
			break;
		case 6:
			if (compactButtons) {
				//minw = 380;
				minw = 360;
				//minh = 150;
				minh = 140;
			} else {
				minw = 420;
				minh = 155;
			}
			break;
		}
		return new Dimension(minw,minh);
	}

	protected void onWindowMoved() {
		rememberThisPosition();
	}
	
	private void rememberThisPosition() {
		if (isFullscreen) return;
		if (Preferences.getInstance().getRememberPositionOnScreen()) {
			if (isShowing()) {
				Point p = getLocationOnScreen();
				Dimension size = getSize();
				Rectangle sizeAndPos = new Rectangle(p.x,p.y,size.width,size.height);
				timerPreference.setLastPosition(sizeAndPos);
				//System.out.println("XZZZZZX " + sizeAndPos);
			}
		}
	}
	
	protected void fadeInButtons() {	
		for (GlowButton b : fadingButtons) {
			b.fadeIn();
		}
		onTopButton.fadeIn();
		muteButton.fadeIn();
		subModeButton.fadeIn();
		timeModeButton.fadeIn();
		//loopButton.fadeIn();
		if (Features.getInstance().fadePlayPauseButtonsWhenMouseStationary()) {
			pauseButton.fadeIn();
			playButton.fadeIn();
			resetButton.fadeIn();
		}
		if (closeButton != null) closeButton.fadeIn();
		if (minButton != null) minButton.fadeIn();
		if (maxButton != null) maxButton.fadeIn();
		if (resizeButton != null) resizeButton.fadeIn();
	}
	
	protected void fadeOutButtons() {
		if (TweakRegistry.getInstance().isFadeButtonsDisabled()) return;
		
		boolean fadeAll = TweakRegistry.getInstance().isFadeAllEnabled();
		for (GlowButton b : fadingButtons) {
			b.fadeOut();
		}
		fadeButtonConditionally(!resetButton.isEnabled(),resetButton,fadeAll);
		fadeButtonConditionally(!isAlwaysOnTop(),onTopButton,fadeAll);
		fadeButtonConditionally(!muted,muteButton,fadeAll);	
		fadeButtonConditionally(!countdownTimer.isTimeMode(), subModeButton, fadeAll);
		fadeButtonConditionally(!countdownTimer.isStopwatchMode(), timeModeButton, fadeAll);
		pauseButton.fadeOut();
		playButton.fadeOut();
		resetButton.fadeOut();

		if (closeButton != null) closeButton.fadeOut();
		if (minButton != null) minButton.fadeOut();
		if (maxButton != null) maxButton.fadeOut();
		if (resizeButton != null) resizeButton.fadeOut();
		
		CustomGlassPane customPane = (CustomGlassPane)getGlassPane();
		customPane.startTweakAnimationsOnce();
	}
	
	private void fadeButtonConditionally(boolean shouldFade,GlowButton b,boolean fadeRegardless) {
		if (shouldFade) {
			b.setDelayBeforeFade(0);
			b.fadeOut();
			return;
		}
		// Would normally keep on screen but told to fade anyway! We delay the fade by 5 seconds.
		if (fadeRegardless) {
			b.setDelayBeforeFade(5000);
			b.fadeOut();
		}
	}
	
	private void setMuteButton() {
		if (muted)  {
			Styling.makeOnButtonColour(muteButton);
		} else {
			Styling.makeOffButtonColour(muteButton);
		}
	}
	
	private void setFracButton() {
		if (fracEnabled)  {
			Styling.makeOnButtonColour(fracButton);
		} else {
			Styling.makeOffButtonColour(fracButton);
		}
	}
	
	private void setLoopButton() {
//		if (countdownTimer.isLoop())  {
//			Styling.makeOnButtonColour(loopButton);
//		} else {
//			Styling.makeOffButtonColour(loopButton);
//		}
	}
	
	private void setSubmodeButton() {
		SubMode sm = countdownTimer.getSubMode();
		switch (sm) {
		case LOOP:
			subModeButton.setInverted(true);
			subModeButton.setText(Messages.getString("CountdownWindow.ButtonText_LOOP")); //$NON-NLS-1$
			Styling.makeLeftButtonColour(subModeButton);
			return;
		case OVERRUN:
			subModeButton.setInverted(true);
			subModeButton.setText(Messages.getString("CountdownWindow.ButtonText_OVERRUN")); //$NON-NLS-1$
			Styling.makeLeftButtonColour(subModeButton);
			return;
		case STICKY:	
			subModeButton.setInverted(true);
			subModeButton.setText(Messages.getString("CountdownWindow.ButtonText_STICKY")); //$NON-NLS-1$
			Styling.makeLeftButtonColour(subModeButton);
			return;
		case STOP:
			subModeButton.setInverted(false);
			subModeButton.setText(Messages.getString("CountdownWindow.ButtonText_STOP")); //$NON-NLS-1$
			Styling.makeLeftButtonColour(subModeButton);
		}
	}
	
	private void setTopButton() {
		if (isAlwaysOnTop())  {
			Styling.makeOnButtonColour(onTopButton);
		} else {
			Styling.makeOffButtonColour(onTopButton);
		}
	}
	
	private int lastLayout = -1;
	private void setTimeModeDisplay() {
		if (countdownTimer.isTimeMode()) {
			//Styling.makeOnButtonColour(subModeButton);
			lastLayout = layoutMode;
			layoutMode = 5; 
			adjustLayout();
			layoutMode = 6;
			adjustLayout(); // Hack
			fullButton.setVisible(true); 
			visualiseButton.setVisible(true);
			miniButton.setVisible(false);
//			loopButton.setVisible(false);
			subModeButton.setVisible(false);
//			timeModeButton.setVisible(false);
			fracButton.setVisible(false);
			digitsPanel.setShowDecimals(false);
			digitsPanel.setAllowPlusMinus(false);
		} else if (countdownTimer.isStopwatchMode()) {
//			Styling.makeOnButtonColour(timeModeButton);
			lastLayout = layoutMode;
			layoutMode = 5; 
			adjustLayout();
			layoutMode = 6;
			adjustLayout(); // Hack
			fullButton.setVisible(true);
			visualiseButton.setVisible(true);
			miniButton.setVisible(false);
//			loopButton.setVisible(false);
			subModeButton.setVisible(false);
//			timeModeButton.setVisible(true);
			//fracButton.setVisible(false);
			//digitsPanel.setShowDecimals(true);
			digitsPanel.setShowDecimals(fracEnabled);
			digitsPanel.setAllowPlusMinus(false);
		} else {
			//Styling.makeOffButtonColour(subModeButton);
//			Styling.makeOffButtonColour(timeModeButton);
			if (lastLayout != -1) {
				layoutMode = lastLayout;
			}
			fullButton.setVisible(true);
			visualiseButton.setVisible(true);
			miniButton.setVisible(true);
//			loopButton.setVisible(true);
			subModeButton.setVisible(true);
//			timeModeButton.setVisible(true);
			fracButton.setVisible(true);
			digitsPanel.setShowDecimals(fracEnabled);
			digitsPanel.setAllowPlusMinus(true);
		}
		plusButton.setVisible(!countdownTimer.isStopwatchMode());
		minusButton.setVisible(!countdownTimer.isStopwatchMode());
//		subModeButton.setVisible(!countdownTimer.isStopwatchMode());
		buyButton.setVisible(!Registration.getInstance().isRegistered());
		doLayoutChanged();
	}

	public void start() {
		adjustLayout();
		setTimeModeDisplay();
		setVisible(true);
		visualiseTimeLeft();
		plusButton.addListener(new SpeedupGlowButtonAdapter()  {
			public void onClicked() {
				clickCount++;
                onPlusButtonPressed();
            }
        });
		minusButton.addListener(new SpeedupGlowButtonAdapter()  {
			public void onClicked() {
				clickCount++;
                onMinusButtonPressed();
            }
        });

		playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onPlayButtonPressed();
            }
        });
		pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onPauseButtonPressed();
            }
        });
		resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onResetButtonPressed();
            }
        });
		onTopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onTopButtonPressed();
            }
        });
		prefsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onPrefButtonPressed();
            }
        });
		visualiseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onChangeVisualisationButtonPressed(true);
            }
        });
		muteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onMuteButtonPressed();
            }
        });
//		loopButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onLoopButtonPressed();
//            }
//        });
		subModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onSubmodeButtonPressed();
            }
        });		
		timeModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onTimeModeButtonPressed();
            }
        });
		
		fracButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onFracButtonPressed();
            }
        });
		miniButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onMiniButtonPressed();
            }
        });
		fullButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onFullButtonPressed();
            }
        });
		buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onBuyButtonPressed();
            }
        });
		compactButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onCompactButtonPressed();
            }
        });
		removeTitleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onRemoveTitleButtonPressed();
            }
        });
		debugButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onDebugButton1Pressed();
            }
        });
		tweakButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	clickCount++;
                onTweakButtonPressed();
            }
        });
		updateTimerPanelTime();
		if (!veryFirstRun) {
			if (!Preferences.getInstance().isAlarmSoundMuted()) {
				playAudioClip("power-up-dark.wav",1,0,true); //$NON-NLS-1$
			}
		}
		veryFirstRun = false;
	}
	


	public void keyboardPausePlay() {
		clickCount++;
		if (countdownTimer.isRunning()) {
			onPauseButtonPressed();
		} else if (!countdownTimer.isStopwatchMode() && countdownTimer.getSeconds() == 0) {
			onResetButtonPressed();
		} else {
			onPlayButtonPressed();
		}
	}


	protected void onPlayButtonPressed() {
    	if (!countdownTimer.isStopwatchMode() && countdownTimer.getSeconds()  == 0) return;
    	if (countdownTimer.isTimeMode()) return;
    	if (countdownTimer.isStopwatchMode() && countdownTimer.isRunning()) return;
    	countdownTimer.start();
    	playSmallClick();
    	resetButton.setEnabled(false);
    	resetButton.stopPulsing();
    	resetButton.fadeOut();
    	digitsPanel.startSeparatorBlink();
    	bottomTimerPanel.setTicking(true);
    	topTimerPanel.setTicking(true);
    	playButton.startPulsing(200);
    	playButton.setIntensity(1.0f);
    	playButton.fadeIn();
    	pauseButton.stopPulsing();
    	pauseButton.setIntensity(pausePlayButtonFade);
    }
    
    protected void onPauseButtonPressed() {
    	if (countdownTimer.getSeconds()  == 0) return;
    	if (countdownTimer.isTimeMode()) return;
    	countdownTimer.stop();
    	if (countdownTimer.isCountdownMode()) {
    		subModeButton.setVisible(true);
    	}
    	playSmallClick();
   		resetButton.setEnabled(true);
   		resetButton.fadeIn();
    	digitsPanel.stopSeparatorBlink();
    	bottomTimerPanel.setTicking(false);
    	topTimerPanel.setTicking(false);
   		playButton.stopPulsing();
   		playButton.setIntensity(pausePlayButtonFade);
   		pauseButton.startPulsing(100);
   		resetButton.startPulsing(75);
   		pauseButton.setIntensity(1.0f);
   		pauseButton.fadeIn();
    }

	public void onMinusButtonPressed() {
		if (countdownTimer.isTimeMode() || countdownTimer.isStopwatchMode()) return;
		int t = Preferences.getInstance().getTimeUnit();
		takeTimeFromClock(t);    
	}

	public void onPlusButtonPressed() {
		if (countdownTimer.isTimeMode() || countdownTimer.isStopwatchMode()) return;
		int t = Preferences.getInstance().getTimeUnit();
		addTimeToClock(t);
	}
	
	private void addTimeToClock(int t) {
		clickCount++;
		countdownTimer.increaseTime(t); 
		updateTimerPanelTime();
		visualiseTimeLeft();
		lastSelectedTime = 0;
	}
	
	private void takeTimeFromClock(int t) {
		clickCount++;
		countdownTimer.reduceTime(t); 
		updateTimerPanelTime();
		visualiseTimeLeft();
		lastSelectedTime = 0;
	}
	
	@Override public void onDayMinusClicked() {  takeTimeFromClock(24 * 60 * 60); }
	@Override public void onDayPlusClicked()  {  addTimeToClock(24 * 60 * 60); }
	
	@Override public void onHourMinusClicked() { takeTimeFromClock(60 * 60); }
	@Override public void onHourPlusClicked()  { addTimeToClock(60 * 60); }

	@Override public void onMinuteMinusClicked() { takeTimeFromClock(60); }
	@Override public void onMinutePlusClicked()  { addTimeToClock(60); }

	@Override public void onSecondMinusClicked() { takeTimeFromClock(1); }
	@Override public void onSecondPlusClicked()  { addTimeToClock(1); }

	protected void onFullButtonPressed() {
    	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
    		displayNotRegisteredAndInTrialMode();
    		return;
    	}
		if (isFullscreen) {
			restoreFromFullscreen();
		} else {
			CountdownWindow fullWindow = new CountdownWindow(true,true,timerInstance,countdownTimer.isTimeMode(),countdownTimer.isStopwatchMode());
			fullWindow.countdownTimer.cloneTimer(countdownTimer);
			fullWindow.isFullscreen = true;
			fullWindow.layoutMode = layoutMode;
			fullWindow.restoreWindow = this;
 			FullScreenUtil.fullScreenIfPossible(fullWindow);
 			fullWindow.start();
 			setVisible(false);
 			if (countdownTimer.isRunning()) {
 				countdownTimer.stop();
 			}
 			repaintLater(fullWindow,true);	
 	    }
	}
	
	private void restoreFromFullscreen() {
		if (!Preferences.getInstance().isAlarmSoundMuted()) {
			playAudioClip("power-down-dark.wav",1,0,true); //$NON-NLS-1$
		}
		FullScreenUtil.unFullScreen();
		setVisible(false);
		isFullscreen = false;			
		restoreWindow.countdownTimer.cloneTimer(countdownTimer);
		restoreWindow.layoutMode = layoutMode;
			if (countdownTimer.isRunning()) {
				countdownTimer.stop();
			}
			restoreWindow.adjustLayout();
		restoreWindow.setVisible(true);
		repaintLater(restoreWindow,true);			
		setVisible(false);
		dispose();		
	}
	
	
	protected void onBuyButtonPressed() { 
		BrowserLaunching.openURL(Branding.getStoreUrlBuyButton());
		onPrefButtonPressed();
		prefsPanel.jumpStraightToBuyPanel();
	}
	
	
	private long lastHackResize;
	private void repaintLater(final Component c,final boolean tweak) {
		Runnable r = new Runnable() { public void run() {
			if (tweak) {
				final Dimension[] d = new Dimension[1];
				lastHackResize = System.currentTimeMillis();
				try {
					SwingUtilities.invokeAndWait(new Runnable() { public void run() {
						d[0] = c.getSize();
						c.setSize(d[0].width-1,d[0].height-1);
					}});
				} catch (Exception e) {}
				//try { Thread.sleep(2); } catch (Exception e) {}
				SwingUtilities.invokeLater(new Runnable() { public void run() {
					//d[0] = c.getSize();
					c.setSize(d[0].width,d[0].height);
				}});
			}
			for (int i=0; i<4; i++) {
				try { Thread.sleep(500); } catch (Exception e) {}
				SwingUtilities.invokeLater(new Runnable() { public void run() {
					c.repaint();
				}});
			} 
		}};
		new Thread(r).start();
	}
	
	private boolean didJustForceResize() {
		return System.currentTimeMillis() - lastHackResize < 500;
	}
	
	private int lastSelectedTime = 0;  // Purely for looping, if you loop and haven't changed anything, you go back to last place you clicked
	public void onSelected(int minute, int seconds) {
		if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) {
			displayNotRegisteredAndInTrialMode();
			return;
		}
		if (!Preferences.getInstance().isAllowClickGridToSetTime()) {
			return;
		}
		int t = minute * 60 + seconds + 1;
		if (countdownTimer.getSubMode() == SubMode.LOOP && t < 6) {
			t = 6;
			displayMiniMessage(Messages.getString("CountdownWindow.Msg_MinLoopIs6Secs")); //$NON-NLS-1$
		}
		
		countdownTimer.setSecondsTo(t); 
		updateTimerPanelTime();
		visualiseTimeLeft();
		lastSelectedTime = t;
		//countdownTimer.setTargetSeconds(t);
	}
	
	private void displayMiniMessage(String msg) {
		FadeMessagePanel.showMiniMessage(this,msg);		
	}

	public void displayNotRegisteredAndInTrialMode() {
		displayMiniMessage(Messages.getString("CountdownWindow.Msg_SorryRegUsersOnly")); //$NON-NLS-1$
	}
	
	public void keyboardReset() {
		clickCount++;
		if (isFullscreen) {
			restoreFromFullscreen();
			return;
		}
		int state = CountdownWindow.this.getExtendedState();
		if (state == Frame.MAXIMIZED_BOTH) {
			CountdownWindow.this.setExtendedState(Frame.NORMAL);
			return;
		}
		if (resetButton.isEnabled()) {
			onResetButtonPressed();
		}
	}
	
	protected synchronized void onResetButtonPressed() {
		if (countdownTimer.isTimeMode()) return;
		countdownTimer.stop();
		countdownTimer.resetTime();
		playSmallClick();
		updateTimerPanelTime();
		visualiseTimeLeft();
		bottomTimerPanel.reset();
    	topTimerPanel.reset();
    	visualiseTimeLeft();
    	pauseButton.stopPulsing();
    	playButton.stopPulsing();
    	resetButton.stopPulsing();
    	playButton.fadeIn();
    	pauseButton.fadeIn();
    	lastSelectedTime = 0;
    }
	
    private void updateTimerPanelTime() {
    	long tenths = countdownTimer.getSecondsToday()*10;
    	long hundreths = tenths*10;
    	int days = countdownTimer.getDays();
    	bottomTimerPanel.setCountdownTimeRemaining(days,tenths,hundreths);
    	bottomTimerPanel.setTargetSeconds(countdownTimer.getTargetSeconds());
    	topTimerPanel.setCountdownTimeRemaining(days,tenths,hundreths);
    	topTimerPanel.setTargetSeconds(countdownTimer.getTargetSeconds());
    	if (countdownTimer.isTimeMode()) {
    		setBlinkingForSeparatorsInTimeMode();
    	} else if (countdownTimer.isRunning()) {
    		digitsPanel.startSeparatorBlink();
    	} else {
    		digitsPanel.stopSeparatorBlink();
    	}
	}
    
    private void setBlinkingForSeparatorsInTimeMode() { 
		if (Preferences.getInstance().getBlinkSeparatorsInTimeMode()) {
			digitsPanel.startSeparatorBlink();
		} else {
			digitsPanel.stopSeparatorBlink();
		}
    }
    
    protected synchronized void onTopButtonPressed() {
    	boolean onTop = isAlwaysOnTop();
    	setAlwaysOnTop(!onTop);
    	timerPreference.setOnTop(!onTop);
    	setTopButton();
    	// debug
    	//com.sun.awt.AWTUtilities.setWindowOpacity(this,0.3f);
    }
    
    protected synchronized void onPrefButtonPressed() {
    	openPrefsPanel();
    	resizePrefsPanel();
    	validate();
    }
    
    protected synchronized void onMuteButtonPressed() {
    	muted = !muted;
    	Preferences.getInstance().setAlarmSoundMuted(muted);
    	setMuteButton();
    }
    
//    protected synchronized void onLoopButtonPressed() {
//    	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
//    		displayNotRegisteredAndInTrialMode();
//    		return;
//    	}
//    	boolean looped = !countdownTimer.isLoop();
//    	timerPreference.setTimerLooped(looped);
//    	countdownTimer.setLoopMode(looped);
////    	
////    	if (looped) {
////    		long t = countdownTimer.getTargetSeconds();
////    		if (t < 10) {
////    			countdownTimer.setTargetSeconds(10);
////    		}
////    	}
//    	setLoopButton();
//    }
    
    protected synchronized void onSubmodeButtonPressed() {    	
//    	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
//    		displayNotRegisteredAndInTrialMode();
//    		return;
//    	}
    	SubMode subMode = countdownTimer.getSubMode();
    	SubMode newSubMode = null;
    	switch (subMode)  {
    		case STOP:
    			newSubMode = SubMode.STICKY;
    			break;
    		case STICKY:
    			newSubMode = SubMode.LOOP;
    			break;
    		case LOOP:
    			newSubMode = SubMode.OVERRUN;
    			break;
    		case OVERRUN:
    			newSubMode = SubMode.STOP;
    	}
  
    	timerPreference.setSubMode(newSubMode);
    	countdownTimer.setSubMode(newSubMode);
    	setSubmodeButton();
    	adjustLayout();
    }
    
    
    private int countdownLayoutMode = -1;
    protected synchronized void onTimeModeButtonPressed() {
    	String newMode = null;
    	if (countdownTimer.isCountdownMode()) {
    		countdownLayoutMode = layoutMode;
    	}
		playTimeSound();
    	if (countdownTimer.isTimeMode()) {
    		countdownTimer.startCountdownMode();
    		newMode = Messages.getString("CountdownWindow.Msg_CountdownMode"); //$NON-NLS-1$
    		if (countdownLayoutMode != -1) {
    			lastLayout = countdownLayoutMode;
    		}
    	} else if (countdownTimer.isStopwatchMode()) {
    		if (countdownTimer.isRunning()) {
    			flashTimeModeButtonQuickly();
    			return;
    		}
      		countdownTimer.startTimeMode();
    		newMode = Messages.getString("CountdownWindow.Msg_TimeMode"); //$NON-NLS-1$
    	} else {
    		countdownTimer.startStopwatchMode();
    		newMode = Messages.getString("CountdownWindow.Msg_StopwatchMode"); //$NON-NLS-1$
    	}
    	FadeMessagePanel.showMessage(this,newMode);
    	if (countdownTimer.isRunning()) {
    		onPlayButtonPressed();
       	} else {
       		onPauseButtonPressed();
    	}

		setTimeModeDisplay();
   		updateTimerPanelTime();
   		visualiseTimeLeft();    	
    	adjustLayout();

    }
    
	private void setupTimeModeFlashTimer() {
		flashTimeModeTimer = new Timer(160,new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				flashCount--;
				boolean b = flashCount % 2 == 0;
				timeModeButton.setVisible(b);
				if (flashCount == 0) {
					flashTimeModeTimer.stop();
				}
			}
		});
	}
    
    private void flashTimeModeButtonQuickly() {
    	if (!flashTimeModeTimer.isRunning()) {
    		flashCount = 6;
    		flashTimeModeTimer.start();
    	}	
	}

	private void playTimeSound() { 
		if (!Preferences.getInstance().isAlarmSoundMuted()) {
			playAudioClip("small_time_select.wav",1,0,true); //$NON-NLS-1$
		}
    }
    
    private void playSmallClick() { 
		if (!Preferences.getInstance().isAlarmSoundMuted()) {
			playAudioClip("small_click.wav",1,0,true); //$NON-NLS-1$
		}
    }
    
    protected synchronized void onFracButtonPressed() {
    	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
    		displayNotRegisteredAndInTrialMode();
    		return;
    	}
    	fracEnabled = !fracEnabled;
    	digitsPanel.setShowDecimals(fracEnabled);
    	timerPreference.setFractionDigitsShown(fracEnabled);
    	setFracButton();
    }
    
    protected synchronized void onMiniButtonPressed() {
    	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
    		displayNotRegisteredAndInTrialMode();
    		return;
    	}

    	MiniWindow window = new MiniWindow();
    	window.setCountdownTimer(countdownTimer);
    	window.setRestoreWindow(this);
    	//Point p  = miniButton.getLocationOnScreen();
    	setVisible(false);
    	//int x = p.x - 100; if (x < 0) x =0;
    	//window.setLocation(x, p.y);
		window.restoreToPreviousPosition();
		window.restoring();
    	window.setVisible(true);
    }
    
	public void keyboardVisualiseLeft() {
		onChangeVisualisationButtonPressed(false);
	}

	public void keyboardVisualiseRight() {
		onChangeVisualisationButtonPressed(true);
	}
	
	@Override
	public void keyboardRestoreWindowPosition() {
		setLocation(210,172);
		setSize(820,342);
		displayMiniMessage("Reset window position");
	}
    
    protected void onChangeVisualisationButtonPressed(boolean up) {
//       	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
//    		displayNotRegisteredAndInTrialMode();
//    		return;
//    	}
    	//System.out.println("AdjustLayout="+layoutMode);
    	// 0 = digits top, circle bottom
    	// 1 = digits top, bar bottom
    	// 2 = digits top, single bar bottom
    	
    	// 3 = circle left, digits right
    	// 4 = bar left, digits right
    	// 5 = single bar left, digits right
    	
    	// 6 = digits only
       	
       	if (up) {
	    	layoutMode++;
	    	if (layoutMode > 6) {
	    		layoutMode = 0;
	    	}
       	} else {
	    	layoutMode--;
	    	if (layoutMode < 0) {
	    		layoutMode = 6;
	    	}
       	}
       	if (!countdownTimer.isOverrunningNow()) {
	       	if (countdownTimer.isStopwatchMode() || countdownTimer.isTimeMode()) {
	       		while (layoutMode == 0 || layoutMode == 2 || layoutMode == 3 || layoutMode == 5) {
	       	       	if (up) {
	       		    	layoutMode++;
	       		    	if (layoutMode > 6) {
	       		    		layoutMode = 0;
	       		    	}
	       	       	} else {
	       		    	layoutMode--;
	       		    	if (layoutMode < 0) {
	       		    		layoutMode = 6;
	       		    	}
	       	       	}
	       		}
	       	}
       	}
       	timerPreference.setDisplayMode(layoutMode);
		if (!Preferences.getInstance().isAlarmSoundMuted()) {
			playAudioClip("viz-button-navigate_9.wav",1,0,true); //$NON-NLS-1$
		}
    	doLayoutChanged();
    	flashupLayoutMode();
    	repaintLater(this,false);
	}

	private void flashupLayoutMode() {
		if (!TweakRegistry.getInstance().isLayoutTitlesEnabled()) return;
		String msg = null;
		if (layoutMode == 0) msg = Messages.getString("CountdownWindow.Msg_LayoutPie"); //$NON-NLS-1$
		else if (layoutMode == 1) msg = Messages.getString("CountdownWindow.Msg_LayoutDots"); //$NON-NLS-1$
		else if (layoutMode == 2) msg = Messages.getString("CountdownWindow.Msg_LayoutBar"); //$NON-NLS-1$
		else if (layoutMode == 3) msg = Messages.getString("CountdownWindow.Msg_LayoutPieDigits"); //$NON-NLS-1$
		else if (layoutMode == 4) msg = Messages.getString("CountdownWindow.Msg_DotsDigits"); //$NON-NLS-1$
		else if (layoutMode == 5) msg = Messages.getString("CountdownWindow.Msg_BarDigits"); //$NON-NLS-1$
		else if (layoutMode == 6) msg = Messages.getString("CountdownWindow.Msg_Digits"); //$NON-NLS-1$
		if (msg != null) {
			FadeMessagePanel.showMessage(this,msg);
		}
	}

	private void doLayoutChanged() { 
    	adjustLayout();
    	layout.layoutContainer(this.getContentPane());
    	ensureMinimumSize();
    	setVisible(true);
    }
	
	protected void onCompactButtonPressed() {
//    	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
//    		displayNotRegisteredAndInTrialMode();
//    		return;
//    	}
		compactButtons = !compactButtons;
		timerPreference.setCompactDigitsLayout(compactButtons);
		doLayoutChanged();
	}
	
	protected void onRemoveTitleButtonPressed() {
//    	if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) { 
//    		displayNotRegisteredAndInTrialMode();
//    		return;
//    	}
    	reopenWindowAdjustingDecorations(!isHideWindowDecorations);
	}
		
	//private int counterIndex = 0;
	protected void onDebugButton1Pressed() {
// ALPHA1
		//createNewTimerWindow();
		//setupHoverHelp();
		//debugButton1.startPulsing(0);
/*
		TweakPanelControl pc = new TweakPanelControl();
		pc.setTitle("Test Tweaks");
		pc.addBooleanTweak("Enabled 1");
		pc.addBooleanTweak("Enabled 2");
		Point p = getLocationInFrame(debugButton1);
		System.out.println("Open at "+p);
		pc.open(this,p.x, p.y, 200,100);
*/		
//		FadeMessagePanel.showMessage(this,"Testing 123");
	}
	
	protected void onTweakButtonPressed() {
		Point p = getMouseLocationInFrame();
		System.out.println("Open tweak at "+p); //$NON-NLS-1$
		int w = 220;
		int h = 170;
		int maxX = getWidth() - w - 10;
		int maxY = getHeight() - h - (isHideWindowDecorations ? 10 : 30);
		int x = Math.min(maxX,p.x);
		int y = Math.min(maxY,p.y-50);
		System.out.println("maxY="+maxY+", p.y-50="+(p.y-50));
		tweakPanelControl.open(this,x,y,w,h);
	}
	
	private Point getMouseLocationInFrame() {
		Point p1 = MouseInfo.getPointerInfo().getLocation();
		Point p2 = getLocationOnScreen();
		p1.translate(-p2.x, -p2.y);
		return p1;
	}
	
//	private Point getLocationInFrame(GlowButton b) {
//		Point p1;
//		if (b.isVisible()) {
//			p1 = b.getLocationOnScreen();
//		} else {
//			p1 = b.getCompanionButton().getLocationOnScreen();
//		}

//	}

	private void reopenWindowAdjustingDecorations(boolean isHideWindowDecorations) {
		setVisible(false);
		rememberThisPosition();
		Preferences.getInstance().flush();
		CountdownWindow w = new CountdownWindow(isFullscreen,isHideWindowDecorations,timerInstance,countdownTimer.isTimeMode(),countdownTimer.isStopwatchMode());
		w.setCountdownTimer(countdownTimer);
		countdownTimer.removeListener(this);
		w.start();
		dispose();
	}

	private PopupPanel popupPanel;
	private void experimentPopupPanel() {
		popupPanel = new PopupPanel();
		getLayeredPane().add(popupPanel,new Integer(990));
		popupPanel.setLocation(20,20);
		popupPanel.setSize(100,100);
		popupPanel.open();
	}
	
	private void experimentRemoveSidePanels() { 
		layout.layoutFor(playPausePanel).setTop(0,15).setLeft(0).setRight(0).setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
        layout.layoutFor(optionsButtonPanel).setTop(0,15).setLeft(100).setRight(100).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
        layout.layoutFor(modeButtonPanel).setTop(0,15).setLeft(0).setRight(0).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
        layout.layoutFor(digitsPanel)
			.setTop(titleLabel,15,BlingAttachmentSide.BOTTOM)
			.setLeft(0)
			.setRight(100)
			.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
    	layout.layoutContainer(this.getContentPane());
    	ensureMinimumSize();
    	setVisible(true);
    	
	}
	
    private void adjustLayout() { 
    	//System.out.println("AdjustLayout="+layoutMode);
    	// 0 = digits top, circle bottom
    	// 1 = digits top, bar bottom
    	// 2 = digits top, single bar bottom
    	
    	// 3 = circle left, digits right
    	// 4 = bar left, digits right
    	// 5 = single bar left, digits right
    	
    	// 6 = digits only
    	//
    	boolean registeredFeature = false;
    	
    	if (countdownTimer.isTimeMode()) {
        	timeModeButton.setText(Messages.getString("CountdownWindow.ButtonText_TIME")); //$NON-NLS-1$
        	registeredFeature = true;
    	} else if (countdownTimer.isStopwatchMode() && !countdownTimer.isOverrunningNow()) {
    		timeModeButton.setText(Messages.getString("CountdownWindow.ButtonText_STOPW")); //$NON-NLS-1$
    		registeredFeature = true;
    	} else {
    		timeModeButton.setText(Messages.getString("CountdownWindow.ButtonText_COUNT")); //$NON-NLS-1$
    		if (countdownTimer.getSubMode() == SubMode.OVERRUN) {
    			registeredFeature = true;
    		}
    	}
   	
    	if (compactButtons) {
    		digitsPanel.getAboveDigitsPanel().setVisible(true);
    		digitsPanel.getBelowDigitsPanel().setVisible(true);
    	} else {
    		digitsPanel.getAboveDigitsPanel().setVisible(false);
    		digitsPanel.getBelowDigitsPanel().setVisible(false);
    	}
    	if (countdownTimer.isTimeMode()) {
    		playButton.setVisible(false);
    		pauseButton.setVisible(false);
    		resetButton.setVisible(false);
    		layout.layoutFor(playPausePanel).setTop(0,15).setLeft(0).setRight(0).setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
    		if (compactButtons) {
    			layout.layoutFor(optionsButtonPanel).setTop(0,15).setLeft(100).setRight(100).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    			layout.layoutFor(modeButtonPanel).setTop(0,15).setLeft(0).setRight(0).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    		} else {
       			layout.layoutFor(optionsButtonPanel).setTop(0,15).setLeft(100,-50).setRight(100,-5).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    			layout.layoutFor(modeButtonPanel).setTop(0,15).setLeft(0,10).setRight(0,60).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    		}
            digitsPanel.setTitleVisible(false);
    	} else {
    		playButton.setVisible(true);
    		pauseButton.setVisible(true);
    		resetButton.setVisible(true);
    		if (compactButtons) {
    			layout.layoutFor(playPausePanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(100).setRight(100).setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
	            layout.layoutFor(optionsButtonPanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(100).setRight(100).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
	            layout.layoutFor(modeButtonPanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(0).setRight(0).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    		} else {	    		
    			layout.layoutFor(playPausePanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(100,-90).setRight(100).setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
    			layout.layoutFor(optionsButtonPanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(100,-130).setRight(100,-90).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    			layout.layoutFor(modeButtonPanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(0,10).setRight(0,50).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    		}
            digitsPanel.setTitleVisible(TweakRegistry.getInstance().isShowDigitTitles());
            if (TweakRegistry.getInstance().isGuruTweakOn("PAINTJAM")) { //$NON-NLS-1$
            	digitsPanel.setTitleVisible(false);
            	digitsPanel.setFreeRatio(false);
            	digitsPanel.setPaintjamMode(true);
            } else if (TweakRegistry.getInstance().isDigitRatioFree()) {
            	digitsPanel.setPaintjamMode(false);
            	digitsPanel.setFreeRatio(true);
            } else {
            	digitsPanel.setFreeRatio(false);
            	digitsPanel.setPaintjamMode(false);
            }
    	}
    	
//    	if (compactButtons || isFullscreen) {
//    		layout.layoutFor(bottomPanel).setTop(100).setLeft(0).setRight(100).setBottom(100);
//    	}  else {
//           	layout.layoutFor(bottomPanel).setTop(100,-40).setLeft(0).setRight(100).setBottom(100);
//    	}
    	
    	String title = timerPreference.getTimerTitle();
    	titleLabel.setText(title);
    	if (title.length() == 0) {
        	layout.layoutFor(titleLabel).setTop(0).setBottom(0).setLeft(0).setRight(100); // Make invisible    		
    	} else {
        	layout.layoutFor(titleLabel).setTop(0).setBottom(8).setLeft(0).setRight(100);    		
    	}
    	if (layoutMode == 0 || layoutMode == 1 || layoutMode == 2) {
    		layout.layoutFor(topTimerPanel).setTop(titleLabel,2,BlingAttachmentSide.BOTTOM).setBottom(0).setRight(0).setLeft(0);
    		layout.layoutFor(bottomTimerPanel).setTop(50);
    		layout.layoutFor(digitsPanel)
        			.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
        			.setLeft(0,compactButtons ? 5 : 60)
        			.setRight(optionsButtonPanel,-5,BlingAttachmentSide.LEFT)
        			.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);	
            layout.layoutFor(playPausePanel)
    				.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
    				.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
            topTimerPanel.setUpdating(false);
            bottomTimerPanel.setUpdating(true);
            bottomTimerPanel.showPanel(layoutMode);
            if (layoutMode == 1) {  // stop blocks hitting bottom
            	layout.layoutFor(bottomTimerPanel).setBottom(100,-40);
            }
    	} else if (layoutMode == 3 || layoutMode == 4 || layoutMode == 5) {
    		layout.layoutFor(bottomTimerPanel).setTop(100); //bottomPanel,0,BlingAttachmentSide.TOP);
    		if (layoutMode == 4) {
                layout.layoutFor(topTimerPanel)
    				.setTop(titleLabel,10,BlingAttachmentSide.BOTTOM)
    				.setLeft(modeButtonPanel,5,BlingAttachmentSide.RIGHT)
    				.setRight(40)
    				.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
    		} else {
                layout.layoutFor(topTimerPanel)
    				.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
    				.setLeft(modeButtonPanel,5,BlingAttachmentSide.RIGHT)
    				.setRight(40)
    				.setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
    		}
            layout.layoutFor(digitsPanel)
    			.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
    			.setLeft(topTimerPanel,10,BlingAttachmentSide.RIGHT)
    			.setRight(optionsButtonPanel,-5,BlingAttachmentSide.LEFT)
    			.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
            layout.layoutFor(playPausePanel)
    			.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
    			.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
            topTimerPanel.setUpdating(true);
            bottomTimerPanel.setUpdating(false);
            topTimerPanel.showPanel(layoutMode-3);
    	// Just digits
    	} else {
    		layout.layoutFor(bottomTimerPanel).setTop(100);
    		if (countdownTimer.isTimeMode()) {
    			layout.layoutFor(titleLabel).setTop(0).setBottom(0).setLeft(0).setRight(100);
    		} else {
    			//layout.layoutFor(titleLabel).setTop(0).setBottom(8).setLeft(0).setRight(100);	
//    			layout.layoutFor(titleLabel).setTop(0).setBottom(0).setLeft(0).setRight(100);	
    		}
//    		layout.layoutFor(bottomTimerPanel).setTop(bottomPanel,0,BlingAttachmentSide.TOP);
            layout.layoutFor(digitsPanel)
    			.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
    			.setLeft(0,compactButtons ? 5 : 120)
    			.setRight(optionsButtonPanel,-5,BlingAttachmentSide.LEFT)
    			.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
            layout.layoutFor(playPausePanel)
				.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
				.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
            layout.layoutFor(topTimerPanel).setTop(0).setBottom(0).setRight(0).setLeft(0);
            topTimerPanel.setUpdating(false);
            bottomTimerPanel.setUpdating(false);
    	}
    	
    	if (layoutMode != 1) {
    		registeredFeature = true;
    	}
    	//System.out.println("LM = "+layoutMode+", reg = "+registeredFeature); //$NON-NLS-1$ //$NON-NLS-2$
    	setDisplayRegisteredFeature(registeredFeature);
    }
    
    private void setDisplayRegisteredFeature(boolean registeredFeature) {
    	boolean registered = Registration.getInstance().isTrialModeOrRegistered();
    	CustomGlassPane p = (CustomGlassPane)getGlassPane();
		p.setShowNeedsRegistered(!registered && registeredFeature);
		topTimerPanel.setShowNeedsRegistered(!registered && registeredFeature);
		bottomTimerPanel.setShowNeedsRegistered(!registered && registeredFeature);
		digitsPanel.setShowRegistered(!registered && registeredFeature);
	}

	private PrefsPanel prefsPanel;
    private void openPrefsPanel() {
    	closePrefsAndTimerPanel();
    	disableHoverHelp();
    	Dimension currentSize = getSize();
    	int nw = currentSize.width;
    	int nh = currentSize.height;
    	if (nw < 500) nw = 500;
    	if (nh < 375) nh = 400;
    	setSize(new Dimension(nw,nh));    	
    	TimeBarVisualiserPanel.globalDisableMouseMonitoring();  // Panel turns these off
    	SingleTimeBarVisualiserPanel.globalDisableMouseMonitoring();
    	repaint();
    	prefsPanel = new PrefsPanel();
    	prefsPanel.hideAll();
    	getLayeredPane().add(prefsPanel,new Integer(999));
    	prefsPanel.setSize(100,100);
    }
    
    private void disableHoverHelp() {
    	CustomGlassPane.globalDisableHoverHelp(true);
    }

    private void enableHoverHelp() {
    	CustomGlassPane.globalDisableHoverHelp(false);
	}

	private CurrentTimersPanel timersPanel;
    private void openCurrentTimersPanel() {
    	closePrefsAndTimerPanel();
    	Dimension currentSize = getSize();
    	int nw = currentSize.width;
    	int nh = currentSize.height;
    	if (nw < 500) nw = 500;
    	if (nh < 375) nh = 400;
    	setSize(new Dimension(nw,nh));
    	timersPanel = new CurrentTimersPanel();
    	timersPanel.hideAll();
    	getLayeredPane().add(timersPanel,new Integer(1000));
    	timersPanel.setSize(100,100);
    }

	private void closePrefsAndTimerPanel() {
       	if (prefsPanel != null) {
    		prefsPanel.setVisible(false);
    		getLayeredPane().remove(prefsPanel);
    		enableHoverHelp();
    	}
       	if (timersPanel != null) {       		
       		timersPanel.setVisible(false);
    		getLayeredPane().remove(timersPanel);
       	}
       	prefsPanel = null;   	
       	timersPanel = null;   	
    }


	private void resizePrefsPanel() {
    	final int w = getWidth()/8;
    	final int h = 35;
    	TimingTarget t = new TimingTarget() {
    		private int px,py;
    		private int pw,ph;
    		
    		private int dx,dy;
    		private int dw,dh;
    		
			public void begin() {
				if (prefsPanel != null) {
					px = prefsPanel.getX();
					py = prefsPanel.getY();
					pw = prefsPanel.getWidth();
					ph = prefsPanel.getHeight();
					prefsPanel.hideAll();
				} else {
					px = w;
					py = h;
					pw = 10;
					ph = 10;
				}
				dx = w - px;
				dy = h - py;
				dw = 6*w - pw;
				dh = 9*h - ph;
			}
			public void end() {
				prefsPanel.setPositions();
				prefsPanel.scrollOn();
			}
			public void repeat() {}

			public void timingEvent(float fraction) {
				int x = px + (int)(fraction * dx);
				int y = py + (int)(fraction * dy);
				prefsPanel.setLocation(x, y);
				prefsPanel.setSize((int)(pw + fraction*dw),(int)(ph + fraction*dh));
				//prefsPanel.validate();
			}
    		
    	};
        Animator a = new Animator(500, t);
        a.setStartDelay(50);
        a.setAcceleration(0.1f);
        a.setDeceleration(0.8f);
        a.start();
    }
    
    private void resizeCurrentTimersPanel() {
    	final int w = getWidth()/10;
    	final int h = getHeight()/10;
    	TimingTarget t = new TimingTarget() {
    		private int px,py;
    		private int pw,ph;
    		
    		private int dx,dy;
    		private int dw,dh;
    		
			public void begin() {
				if (timersPanel != null) {
					px = timersPanel.getX();
					py = timersPanel.getY();
					pw = timersPanel.getWidth();
					ph = timersPanel.getHeight();
					timersPanel.hideAll();
				} else {
					px = w;
					py = h;
					pw = 10;
					ph = 10;
				}
				dx = w - px;
				dy = h - py;
				dw = 8*w - pw;
				dh = 8*h - ph;
			}
			public void end() {
				timersPanel.setPositions();
				timersPanel.showContents();
			}
			public void repeat() {}

			public void timingEvent(float fraction) {
				int x = px + (int)(fraction * dx);
				int y = py + (int)(fraction * dy);
				timersPanel.setLocation(x, y);
				timersPanel.setSize((int)(pw + fraction*dw),(int)(ph + fraction*dh));
				//prefsPanel.validate();
			}
    		
    	};
        Animator a = new Animator(400, t);
        a.setStartDelay(50);
        a.setAcceleration(0.1f);
        a.setDeceleration(0.8f);
        a.start();
    }


	private void visualiseTimeLeft() {
		long countDownSeconds = countdownTimer.getSecondsToday();
		long countDownHundredths = countdownTimer.getHundreths();
		int days = countdownTimer.getDays();
		digitsPanel.setCounter(days,countDownSeconds,countDownHundredths,countdownTimer.isTimeMode());
		String t = timerPreference.getWindowTitle();
    	if (Preferences.getInstance().getIncludeTimeInTitle()) {
    		String label = SecondsConverter.secondsToHHMM(countDownSeconds);
    		setTitle("["+label+"] "+t); //$NON-NLS-1$ //$NON-NLS-2$
    	} else {
    		setTitle(t);
    	}
    	if (countdownTimer.isStopwatchMode() && countdownTimer.getSubMode() == SubMode.OVERRUN) {
    		subModeButton.fadeIn();
    		if (countDownHundredths % 150 < 75) {
    			subModeButton.setVisible(true);
    		} else {
    			subModeButton.setVisible(false);
    		}
    	}
    }
	

	public void onCountdownReachedZero() {
		bringWindowToFront();
		resetButton.setEnabled(true);
		digitsPanel.stopSeparatorBlink();
		bottomTimerPanel.setTicking(false);
		topTimerPanel.setTicking(false);
		playButton.stopPulsing();
		resetButton.fadeIn();
		playButton.setIntensity(pausePlayButtonFade);
		pauseButton.setIntensity(pausePlayButtonFade);
		new AlarmThread().start();
		if (countdownTimer.getSubMode() == SubMode.STICKY) {
			subModeButton.fadeIn();
		}
	}
	
	public void onCountdownZeroRestartingLoop() {
		bringWindowToFront();
		new AlarmThread().start();
		subModeButton.fadeIn();
		TimedRunner.getInstance().run(new TimedTask() {
			int d = 20;			
			@Override
			public void run() {
				d--;
				if (d < 0) {
					System.out.println("TMB---FO "+timeModeButton.getCurrentFade()); //$NON-NLS-1$
					if (timeModeButton.getCurrentFade() < 0.2f) {
						subModeButton.fadeOut();
					}
					stop();
				}
			}
		});
	}
	
	public void onCountdownReachedZeroEnteringOverrun() {
		bringWindowToFront();
		new AlarmThread().start();
		setTimeModeDisplay();
	}
	
	private void bringWindowToFront() { 
		if (!isFullscreen) {  
			if (Preferences.getInstance().getWindowToFrontOnAlarm()) {
				setState(NORMAL);
				toFront();
				if (!isAlwaysOnTop()) {
					setAlwaysOnTop(true);
					setAlwaysOnTop(false);
				}
			}
		}
	}


	public void onTick(int days,long remainingTenths,long hundredths) {
		bottomTimerPanel.setCountdownTimeRemaining(days,remainingTenths,hundredths);
		topTimerPanel.setCountdownTimeRemaining(days,remainingTenths,hundredths);
		visualiseTimeLeft();
	}
	
	private static AtomicLong threadKey = new AtomicLong(0);   
	
	private class MySoundCanceller implements SoundCanceller {
		
		private long key;
		public MySoundCanceller(long key) {
			this.key = key;
		}

		@Override
		public boolean isCancelled() {
			boolean b = key != threadKey.get();
			if (b) {
				System.out.println("Sound canceller says sound cancelled.");
			}
			return b; 
		}
		
	}
	
	private class AlarmThread extends Thread {
		
		private long myThreadkey = threadKey.incrementAndGet();
		
		private boolean shouldAbort() { 
			return myThreadkey != threadKey.get();
		}
		
		@Override
		public void run() {
			SoundCanceller canceller = new MySoundCanceller(myThreadkey);
			String clip = Preferences.getInstance().getSoundFile();
			int repeat = Preferences.getInstance().getSoundRepeat();
			int delay = Preferences.getInstance().getSoundDelay();
			if (clip.equals("CUSTOM_ALARM")) {
				playCustomAlarmSound(canceller);
			} else if (repeat > 0) {
				playAudioClip(clip,repeat,delay,false,canceller);
			}
			boolean sticky = false;
			boolean waitForClick = false;
			if (countdownTimer.getSubMode() == SubMode.STICKY) {
				sticky = true;
			}
			if (countdownTimer.getSubMode() == SubMode.OVERRUN && Preferences.getInstance().getFlashDigitsIfOverrunning()) {
				waitForClick = true;
			}
			if (waitForClick) {
				digitsPanel.startSlowDigitBlink();
			} else {
				digitsPanel.startDigitBlink();
			}
			try {
				int blinks = sticky ? Integer.MAX_VALUE : 4;
				int clickCountNow = clickCount;
				for (int i=0; i<blinks; i++) {
					bottomTimerPanel.flash(true);
					topTimerPanel.flash(true);
					try {
						sleep(1000);
					} catch (InterruptedException e) {}
					bottomTimerPanel.flash(false);
					topTimerPanel.flash(false);
					try {
						sleep(600);
					} catch (InterruptedException e) {}
					if (clickCount != clickCountNow || shouldAbort()) break;
				}
				if (waitForClick) {
					while (clickCount == clickCountNow && !shouldAbort()) {
						try {
							sleep(250);
						} catch (InterruptedException e) {}
					}
				}
			} finally {
				if (!shouldAbort()) {
					digitsPanel.stopDigitBlink();
				}
			}
		}

	}
	
	private void playCustomAlarmSound(SoundCanceller canceller) {
		if (muted) return;
		SoundPlayer.playCustomAlarmSound(canceller);
	}
	
	private void playAudioClip(final String name,final int repeat,final int delay,boolean canDitch,SoundCanceller canceller) {
		if (muted) return;
		URL url = getSoundUrl(name);
		SoundPlayer.playSound(url,repeat,delay,canDitch,canceller);
	}
	
	private void playAudioClip(final String name,final int repeat,final int delay,boolean canDitch) {
		if (muted) return;
		URL url = getSoundUrl(name);
		SoundPlayer.playSound(url,repeat,delay,canDitch);
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
	
	private boolean lastLayoutTitles = TweakRegistry.getInstance().isLayoutTitlesEnabled();
	public void onPreferencesChanged() {
		rememberThisPosition();
		sizeAndPositionWindowControls();
		adjustLayout();
		setTransparency();
		digitsPanel.relayout();
		visualiseTimeLeft();
    	if (countdownTimer.isTimeMode()) {
    		setBlinkingForSeparatorsInTimeMode();
    	}
    	boolean titlesNow = TweakRegistry.getInstance().isLayoutTitlesEnabled(); 
    	if (lastLayoutTitles != titlesNow) {
    		lastLayoutTitles = titlesNow;
    		FadeMessagePanel.showMessageWide(this, titlesNow ? Messages.getString("CountdownWindow.Msg_LayoutTitlesEnabled") : Messages.getString("CountdownWindow.Msg_LayoutTitlesDisabled")); //$NON-NLS-1$ //$NON-NLS-2$
    	}
    	repaintLater(this,true);
	}

	public void restoring() {
		updateTimerPanelTime();
		visualiseTimeLeft();
	}
	
	public void applyTime(int s, int m, int h, int d) {
		clickCount++;
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
		updateTimerPanelTime();
		visualiseTimeLeft();
	}

	public boolean isTimeMode() {
		return countdownTimer.isTimeMode();
	}
	
	private void setTransparency() {
//		if (AWTUtilitiesWrapper.isTransparentWindowSupported()) {
//			AWTUtilitiesWrapper.isTranslucencySupported(AWTUtilitiesWrapper.TRANSLUCENT);
//			String tweak = TweakRegistry.getInstance().getValue(TweakRegistry.TRANSPARENCY);
//			if (tweak.equals("75%")) {
//				AWTUtilitiesWrapper.setWindowOpacity(this,0.75f);
//			} else if (tweak.equals("50%")) {
//				AWTUtilitiesWrapper.setWindowOpacity(this,0.50f);
//			} else if (tweak.equals("25%")) {
//				AWTUtilitiesWrapper.setWindowOpacity(this,0.25f);
//			} else {
//				AWTUtilitiesWrapper.setWindowOpacity(this, 1f);
//			}
//		}
	}

	
    private void createNewTimerWindow() {
		rememberThisPosition();
		Preferences.getInstance().flush();
		int newInstance = CountdownRegistry.getInstance().getNewTimerInstanceId();
		CountdownWindow w = new CountdownWindow(false,isHideWindowDecorations,newInstance,false,false);
		w.start();
	}

	public Rectangle getDigitsPanelBounds() { return digitsPanel.getBounds(); }
	public JLayeredPane getTheLayeredPane() { return getLayeredPane(); }

	public void timePanelClosed() {}
	public void timePanelOpen() {}

	public void applyEnteredTweak(String tweakName) {
		if (tweakName.equalsIgnoreCase("*paintjam1")) { //$NON-NLS-1$
			TweakRegistry.getInstance().setGuruTweak("PAINTJAM",true); //$NON-NLS-1$
			displayMiniMessage("GuruTweak: PAINTJAM MODE ON"); //$NON-NLS-1$
		} else if (tweakName.equalsIgnoreCase("*paintjam0")) { //$NON-NLS-1$
			TweakRegistry.getInstance().setGuruTweak("PAINTJAM",false);		 //$NON-NLS-1$
			displayMiniMessage("GuruTweak: PAINTJAM MODE OFF"); //$NON-NLS-1$
		} 
	}



}
