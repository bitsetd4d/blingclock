package blingclock.calendar;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

// Test change in GIT 31/12/2012
public class CalendarWindowTest extends JFrame {

	public CalendarWindowTest() { 
		setBackground(new Color(0,0,0));
		setLayout(new GridLayout(1,1));
		setupPanels();		
	}
	
	private void setupPanels() {
		MonthCalendarPanel p = new MonthCalendarPanel(4,2009,true);
		add(p);
	}


	public static void main(String[] args) {
 	 	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//System.setProperty("apple.awt.windowShadow","false");
		SwingUtilities.invokeLater(new Runnable() { public void run() {
			CalendarWindowTest w = new CalendarWindowTest();
			w.setVisible(true);
			w.setLocation(50,30);
			w.setSize(300,300);
		}});
	}
		
}
