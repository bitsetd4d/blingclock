package blingclock.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.Timer;

public class RepeatingButton extends JButton {
	
	private int delay = ButtonConstants.DELAY_BEFORE_BUTTON_REPEAT;
	private int repeat = ButtonConstants.BUTTON_REPEAT_INTERVAL;
	private Timer repeatTimer;
	private Timer actionTimer;
	
	public RepeatingButton() {
		super();
		initTimers();
	}

	public RepeatingButton(Action a) {
		super(a);
		initTimers();
	}

	public RepeatingButton(Icon icon) {
		super(icon);
		initTimers();
	}

	public RepeatingButton(String text, Icon icon) {
		super(text, icon);
		initTimers();
	}

	public RepeatingButton(String text) {
		super(text);
		initTimers();
	}

	private void initTimers() {
		repeatTimer = new Timer(delay,new ActionListener() { public void actionPerformed(ActionEvent e) {
			onRepeatTimerFired();
		}});
		repeatTimer.setRepeats(false);
		actionTimer = new Timer(repeat,new ActionListener() { public void actionPerformed(ActionEvent e) {
			onActionTimerFired();
		}});
		addMouseListener(new MouseAdapter() { 
			@Override
			public void mousePressed(MouseEvent e) {
				repeatTimer.restart();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				repeatTimer.stop();
				actionTimer.stop();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				repeatTimer.stop();
				actionTimer.stop();
			}
		
		});
	}

	private void onActionTimerFired() {
		for (ActionListener l : getActionListeners()) {
            ActionEvent evt = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,getActionCommand());
			l.actionPerformed(evt);
	    }
	}

	private void onRepeatTimerFired() {
		actionTimer.start();
		repeatTimer.stop();
	}
	
}
