package countdowntimer;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import countdowntimer.preferences.Preferences;
import countdowntimer.registration.Registration;
import countdowntimer.sound.SoundUtil;

public class CountdownKeyboardSupport {
	
	private KeyboardInputTarget focusCountdownWindow;
	private List<MyAction> myActions = new ArrayList<MyAction>();
	private boolean enabled;
	
	private Timer killInputTimer;
	
	private static CountdownKeyboardSupport INSTANCE = new CountdownKeyboardSupport();
	public static CountdownKeyboardSupport getInstance() { return INSTANCE; }
	
	private CountdownKeyboardSupport() {
		enabled = true;
		myActions.add(new MyAction(KeyStroke.getKeyStroke('+'),"+")); //$NON-NLS-1$
		myActions.add(new MyAction(KeyStroke.getKeyStroke('='),"+")); //$NON-NLS-1$
		myActions.add(new MyAction(KeyStroke.getKeyStroke('-'),"-")); //$NON-NLS-1$
		myActions.add(new MyAction(KeyStroke.getKeyStroke('_'),"-")); //$NON-NLS-1$
		for (int i=0; i<=9; i++) {
			String k = String.valueOf(i);
			myActions.add(new MyAction(KeyStroke.getKeyStroke(k),k));
		}
		
		for (int i=0;i<=9;i++) {
			String k = String.valueOf(i);
			myActions.add(new MyAction(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0+i,0),k));
		}

		myActions.add(new MyAction(KeyStroke.getKeyStroke(KeyEvent.VK_ADD,0),"+")); //$NON-NLS-1$
		myActions.add(new MyAction(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT,0),"-")); //$NON-NLS-1$

		//myActions.add(new MyAction(KeyStroke.getKeyStroke("ENTER"),"enter"));
		myActions.add(new MyAction(KeyStroke.getKeyStroke("SPACE"),"space"));		 //$NON-NLS-1$ //$NON-NLS-2$
		myActions.add(new MyAction(KeyStroke.getKeyStroke("LEFT"),"left"));		 //$NON-NLS-1$ //$NON-NLS-2$
		myActions.add(new MyAction(KeyStroke.getKeyStroke("RIGHT"),"right"));		 //$NON-NLS-1$ //$NON-NLS-2$
		myActions.add(new MyAction(KeyStroke.getKeyStroke("DELETE"),"del"));		 //$NON-NLS-1$ //$NON-NLS-2$
		myActions.add(new MyAction(KeyStroke.getKeyStroke("BACK_SPACE"),"del"));		 //$NON-NLS-1$ //$NON-NLS-2$
		myActions.add(new MyAction(KeyStroke.getKeyStroke("ESCAPE"),"esc"));	 //$NON-NLS-1$ //$NON-NLS-2$
		
		killInputTimer = new Timer(10*1000,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onKillTimerFired();
			}
		});
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (!enabled) return false;
				
				if (KeyEvent.KEY_RELEASED == e.getID() && e.getKeyCode() == KeyEvent.VK_X && e.getModifiers() == KeyEvent.CTRL_MASK) {
					System.out.println("CTRL_X");
					focusCountdownWindow.keyboardRestoreWindowPosition();
					return true;
				}
				if (KeyEvent.KEY_PRESSED != e.getID()) return false;
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					SwingUtilities.invokeLater(new Runnable() { public void run() {
						doEnter();
					}});
					return true;
				}
				for (MyAction a : myActions) {
					//System.out.println("KEY "+e.getKeyCode());
					char c = e.getKeyChar();
					if (e.getKeyCode() == a.keyStroke.getKeyCode() && e.getModifiers() == a.keyStroke.getModifiers() ||
						(c < 255 && c == a.keyStroke.getKeyChar())) {
							a.actionPerformed();
							return true;
					}
 				}
				char c = e.getKeyChar();
				if (c == '*') {
					doStartTweakAction();
					return true;
				} else if (Character.isJavaIdentifierStart(c)) {
					doCharTypedAction(c);
					return true;
				}
				return false;
			} 
		
		});
	}
	
	private void startKillInputTimer() {
		if (killInputTimer.isRunning()) {
			killInputTimer.stop();
		}
		killInputTimer.start();
	}
	
	private void stopKillInputTimer() {
		if (killInputTimer.isRunning()) {
			killInputTimer.stop();
		}
	}

	protected void onKillTimerFired() {
		if (timePanel != null && acceptingInputMode) {
			closeTimePanel();
		}
	}

	public void registerListeners(JFrame countdownWindow,JComponent component) {
		countdownWindow.getRootPane().setDefaultButton(null);
		countdownWindow.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"none"); //$NON-NLS-1$ //$NON-NLS-2$
		removeSpaceShortcut(countdownWindow.getComponents());		
//		for (MyAction a : myActions) {
//			component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(a.getKeyStroke(),a.getActionName());
//			component.getActionMap().put(a.getActionName(),a);
//		}
	}
	
	public void enableKeyboardSupport() { enabled = true; }
	public void disableKeyboardSupport() { enabled = false; }

	public void gotFocus(KeyboardInputTarget focusCountdownWindow) {
		this.focusCountdownWindow = focusCountdownWindow;
	}

	public void lostFocus(KeyboardInputTarget focusCountdownWindow) {
		if (this.focusCountdownWindow == focusCountdownWindow) {
			this.focusCountdownWindow = null;
		}
	}
	
	private void removeSpaceShortcut(Component[] components) {
		for (Component c : components) {
			if (c instanceof JButton) {
				((JButton)c).getInputMap().put(KeyStroke.getKeyStroke("SPACE"),"none"); //$NON-NLS-1$ //$NON-NLS-2$
				((JButton)c).getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"none"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (c instanceof Container) {
				removeSpaceShortcut(((Container)c).getComponents());
			}
		}
	}
	
	protected void doStartTweakAction() {
		if (Preferences.getInstance().isAllowSetTimeWithKeyboard() && !acceptingInputMode) {
			openDigitPanel();
			acceptingInputMode = true;
		}
		timePanel.appendCharacter('*');
	}
	
	protected void doCharTypedAction(char c) {
		timePanel.appendCharacter(c);
	}

	
	public void doKeyboardAction(String a) {
		startKillInputTimer();
		if (focusCountdownWindow == null) return;
		if (a.equals("recover-window")) {
			System.out.println("CTRL-X PRESSED!!");
		} else if (a.equals("left")) { //$NON-NLS-1$
			focusCountdownWindow.keyboardVisualiseLeft();
		} else if (a.equals("right")) { //$NON-NLS-1$
			focusCountdownWindow.keyboardVisualiseRight();
		} else if (a.equals("-")) { //$NON-NLS-1$
			focusCountdownWindow.onMinusButtonPressed();
		} else if (a.equals("+")) { //$NON-NLS-1$
			focusCountdownWindow.onPlusButtonPressed();
		} else if (a.equals("space")) { //$NON-NLS-1$
			focusCountdownWindow.keyboardPausePlay();
		} else if (a.equals("enter")) { //$NON-NLS-1$
//			if (digitMode) {
//				timePanel.applyTime(countdownWindow);
//				closeTimePanel();
//			} else {
//				countdownWindow.keyboardReset();
//			}
		} else if (a.equals("del")) { //$NON-NLS-1$
			if (acceptingInputMode) {
				timePanel.deleteDigit();
			}
		} else if (a.equals("esc")) { //$NON-NLS-1$
			if (acceptingInputMode) {
				SoundUtil.playAudioClip("small_delete_beep.wav",true); //$NON-NLS-1$
				closeTimePanel();
			} else {
				focusCountdownWindow.keyboardReset();
			}
		} else {
			if (!focusCountdownWindow.isTimeMode()) {
				if (Registration.getInstance().isNotRegisteredAndNotInTrialMode()) {
					focusCountdownWindow.displayNotRegisteredAndInTrialMode();
					return;
				}
				if (Preferences.getInstance().isAllowSetTimeWithKeyboard()) {
					try {
						int n = Integer.parseInt(a);
						processDigit(n);
					} catch (Exception e) {}
				}
			}
		}
	}

	private long lastEnterSeen;
	private void doEnter() { 
		long now = System.currentTimeMillis();
		if (now > lastEnterSeen + 500) {
			if (timePanel != null && timePanel.hasPendingDigits()) {
				timePanel.applyTime(focusCountdownWindow);
				closeTimePanel();
			} else {
				focusCountdownWindow.keyboardReset();
			}			
		} else {
			System.out.println("Too soon "+now+", lastSeen="+lastEnterSeen); //$NON-NLS-1$ //$NON-NLS-2$
		}
		lastEnterSeen = now;
	}
	
	boolean acceptingInputMode = false;
	private void processDigit(int n) {
		if (!acceptingInputMode) {
			openDigitPanel();
			acceptingInputMode = true;
		}
		timePanel.appendDigit(n);
	}
	
	private void closeTimePanel() { 
	   	if (timePanel != null) {
	   		timePanel.setVisible(false);
			focusCountdownWindow.getTheLayeredPane().remove(timePanel);
			timePanel = null;
			acceptingInputMode = false;
			focusCountdownWindow.timePanelClosed();
			stopKillInputTimer();
		}
	}

	private static SettingTimePanel timePanel;
	private void openDigitPanel() {
		timePanel = new SettingTimePanel();
		focusCountdownWindow.timePanelOpen();
		focusCountdownWindow.getTheLayeredPane().add(timePanel,new Integer(999));
		Rectangle b = focusCountdownWindow.getDigitsPanelBounds();
    	timePanel.setBounds(b);
    	startKillInputTimer();
	}

	private class MyAction {
		
		public MyAction(KeyStroke keyStroke, String actionName) {
			this.keyStroke = keyStroke;
			this.actionName = actionName;
		}

		private KeyStroke keyStroke;
		private String actionName;
		
		public KeyStroke getKeyStroke() { return keyStroke; }
		public String getActionName() { return actionName; }

		public void actionPerformed() {
			doKeyboardAction(actionName);
		}
				
	}


}
