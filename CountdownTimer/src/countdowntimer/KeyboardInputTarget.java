package countdowntimer;

import java.awt.Rectangle;

import javax.swing.JLayeredPane;

public interface KeyboardInputTarget {

	void keyboardVisualiseLeft();
	void keyboardVisualiseRight();

	void onMinusButtonPressed();
	void onPlusButtonPressed();   
	void keyboardPausePlay();

	boolean isTimeMode();
	void displayNotRegisteredAndInTrialMode();
	void keyboardReset();

	JLayeredPane getTheLayeredPane();
	Rectangle getDigitsPanelBounds();
	void applyTime(int s, int m, int h, int d);
	
	void timePanelClosed();
	void timePanelOpen();
	void applyEnteredTweak(String tweakName);
	void keyboardRestoreWindowPosition();

}
