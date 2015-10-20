package countdowntimer.tweaks;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.Timer;

public class LabelAnimator {
	
	private Timer labelTimer;
	private Map<JLabel,Color> normalColor = new HashMap<JLabel,Color>();
	private Map<JLabel,Color> desiredColor = new HashMap<JLabel,Color>();
	
	public LabelAnimator() { 
		labelTimer = new Timer(50,new ActionListener() {public void actionPerformed(ActionEvent e) {
			try {
				onTimerFired();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}});
	}

	protected void onTimerFired() {
		List<JLabel> finishedLabels = new ArrayList<JLabel>();
		for (Entry<JLabel,Color> en : desiredColor.entrySet()) {
			JLabel l = en.getKey();
			Color desired = en.getValue();
			boolean done = changeColorTowardsDesired(l,desired);
			if (done) {
				finishedLabels.add(l);
			}	
		}
		for (JLabel l : finishedLabels) {
			desiredColor.remove(l);
		}
		if (desiredColor.isEmpty()) {
			labelTimer.stop();
			System.out.println("LabelAnimator>> stopTimer"); //$NON-NLS-1$
		}
	}

	private boolean changeColorTowardsDesired(JLabel l, Color desired) {
		Color colorNow = l.getForeground();
		int dr = scaleColor(desired.getRed(),colorNow.getRed());
		int dg = scaleColor(desired.getGreen(),colorNow.getGreen());
		int db = scaleColor(desired.getBlue(),colorNow.getBlue());
		Color newColor = new Color(dr,dg,db);
		l.setForeground(newColor);
		if (newColor.equals(desired)) {
			return true;
		}
		return false;
	}
	
	private int scaleColor(int desired,int current) {
		int delta = desired - current;
		int cd = 0;
		if (delta < 0) 
			cd = Math.max(-40,delta);
		if (delta > 0)
			cd = Math.min(30,delta);
		int newColor = current + cd;
		if (newColor < 0) newColor = 0;
		if (newColor > 255) newColor = 255;
		return newColor;
	}
	
	public void resetLabelColors() {
		for (Entry<JLabel,Color> en : normalColor.entrySet()) {
			JLabel l = en.getKey();
			Color c = en.getValue();
			setTargetColor(l,c);
		}
	}
	
	public void fadeOut() {
		for (Entry<JLabel,Color> en : normalColor.entrySet()) {
			JLabel l = en.getKey();
			setTargetColor(l,Color.BLACK);
		}
	}


	public void setTargetColor(JLabel label,Color desired) {
		Color currentColor = label.getForeground();
		if (!currentColor.equals(desired)) {
			queueLabelForColorChange(label,desired);
		}
	}
	
	public void setNormalColor(JLabel label,Color color) {
		normalColor.put(label,color);
	}

	private void queueLabelForColorChange(JLabel label, Color c) {
		desiredColor.put(label, c);
		if (!labelTimer.isRunning()) {
			labelTimer.start();
			System.out.println("LabelAnimator>> startTimer"); //$NON-NLS-1$
		}
		
	}

}
