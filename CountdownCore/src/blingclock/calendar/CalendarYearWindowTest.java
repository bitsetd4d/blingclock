package blingclock.calendar;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CalendarYearWindowTest extends JFrame {

	public CalendarYearWindowTest() { 
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


	public static void main(String[] args) {
 	 	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//System.setProperty("apple.awt.windowShadow","false");
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			CalendarYearWindowTest w = new CalendarYearWindowTest();
			w.setVisible(true);
			w.setLocation(50,30);
			w.setSize(800,600);
		}});
	}
		
}
