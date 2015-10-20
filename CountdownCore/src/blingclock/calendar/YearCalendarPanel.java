package blingclock.calendar;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class YearCalendarPanel extends JPanel {

	private List<MonthCalendarPanel> myPanels = new ArrayList<MonthCalendarPanel>();
	
	public YearCalendarPanel() { 
		setBackground(new Color(0,0,0));
		setLayout(new GridLayout(3,4));
		setupPanels();
	}

	private void setupPanels() {
		for (int i=1; i<=12; i++) {
			MonthCalendarPanel p = new MonthCalendarPanel(i,2009,false);
			add(p);
		}
	}
	
	public void setYear(int year) {
		for (MonthCalendarPanel p : myPanels) {
			p.setYear(year);
		}
	}
	
}
