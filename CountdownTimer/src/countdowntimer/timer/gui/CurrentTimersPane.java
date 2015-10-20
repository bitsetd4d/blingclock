package countdowntimer.timer.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class CurrentTimersPane extends JPanel {
	
	public CurrentTimersPane() {
		setBackground(Color.BLACK);
		setLayout(new GridLayout(0,1));
		populateTestData();
	}

	private void populateTestData() {
		add(new DisplayTimerPanel());
		add(new DisplayTimerPanel());
		add(new DisplayTimerPanel());
		add(new DisplayTimerPanel());
		// TODO Auto-generated method stub
		
	}

}
