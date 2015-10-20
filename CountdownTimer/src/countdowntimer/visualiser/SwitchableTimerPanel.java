package countdowntimer.visualiser;

import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import blingclock.visualiser.TimeBarVisualiserPanel;
import blingclock.visualiser.TimeSelectionListener;
import blingclock.visualiser.VisualiserPanel;



import countdowntimer.preferences.TimerPreference;

public class SwitchableTimerPanel extends VisualiserPanel {
	
	private List<VisualiserPanel> allPanels = new ArrayList<VisualiserPanel>();
	private VisualiserPanel current;
	private int currentIndex = 0;
	
	private boolean lastFlash;
	private int lastCountDownDays;
	private long lastCountDownHundreths;
	private long lastCountDownTenths;
	private long lastTargetSeconds;
	
	private TimerPreference timerPref;
	
	public SwitchableTimerPanel(TimerPreference timerPref) {
		this.timerPref = timerPref;
		setLayout(null);
		setBackground(Color.BLACK);
		allPanels.add(new CircleTimeVisualiser());
		
		TimeBarVisualiserPanel barTimerPanel = new TimeBarVisualiserPanel();
        barTimerPanel.setBackground(Color.BLACK);
//        barTimerPanel.setListener(new TimeSelectionListener() { public void onSelected(int minute, int second) {
//        	if (listener != null) listener.onSelected(minute, second);
//        }});
		allPanels.add(barTimerPanel);	
        
		SingleTimeBarVisualiserPanel stp = new SingleTimeBarVisualiserPanel(timerPref);
		allPanels.add(stp);
		
		current = allPanels.get(0);
		for (VisualiserPanel p : allPanels) {
			add(p);
		}
		
		addListeners();
	}
	
	private void addListeners() {
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				for (VisualiserPanel p : allPanels) {
					p.setLocation(0,0);
					p.setSize(getSize());
				}
			}
			
		});
	}
	
	public void showPanel(int currentIndex) { 
		for (VisualiserPanel p : allPanels) {
			p.setLocation(0,0);
			p.setSize(0,0);
			p.setEnabled(false);
			p.setVisible(false);
		}
		this.currentIndex = currentIndex;		
		current = allPanels.get(currentIndex);
		current.flash(lastFlash);
		current.setCountdownTimeRemaining(lastCountDownDays,lastCountDownTenths,lastCountDownHundreths);
		current.setTargetSeconds(lastTargetSeconds);
		current.setVisible(true);
		current.setEnabled(true);
		current.setLocation(0,0);
		current.setSize(getSize());
	}

	public void flash(boolean onOrOff) {
		this.lastFlash = onOrOff;
		if (current != null) current.flash(onOrOff);
	}

	public void reset() {
		if (current != null) current.reset();
	}
	
	public void setShowNeedsRegistered(boolean registered) {
		if (current != null) current.setShowNeedsRegistered(registered);
	}

	public void setCountdownTimeRemaining(int days,long countDownTenths, long hundredths) {
		this.lastCountDownDays = days;
		this.lastCountDownTenths = countDownTenths;
		this.lastCountDownHundreths = hundredths;
		if (current != null) current.setCountdownTimeRemaining(days,countDownTenths,hundredths);
	}

	public void setTargetSeconds(long targetSeconds) {
		this.lastTargetSeconds = targetSeconds;
		if (current != null) current.setTargetSeconds(targetSeconds);
	}

}
