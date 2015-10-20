package countdowntimer.mini;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MiniControls2 extends JPanel {

	private PanelFeedbackListener listener;
	
	private JButton pausePlayButton = new JButton();
	private JButton resetButton = new JButton();
	
	public MiniControls2(PanelFeedbackListener myListener) {
		super();
		this.listener = myListener;
		setBackground(new Color(0,0,0));
		pausePlayButton.setIcon(ImageLoader.loadImage("tiny_play.png")); //$NON-NLS-1$
		resetButton.setIcon(ImageLoader.loadImage("tiny_reset.png")); //$NON-NLS-1$
		add(pausePlayButton);
		add(resetButton);
		
		setLayout(null);
		pausePlayButton.setLocation(6,3);
		pausePlayButton.setSize(6,6);
		resetButton.setLocation(6,10);
		resetButton.setSize(6,6);
		
		pausePlayButton.addActionListener(new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			listener.onPausePlayButtonPressed();
		}});
		resetButton.addActionListener(new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			listener.onResetButtonPressed();
		}});
	}
	
	public void showPlay() {
		pausePlayButton.setIcon(ImageLoader.loadImage("tiny_play.png")); //$NON-NLS-1$
	}
	
	public void showPause() { 
		pausePlayButton.setIcon(ImageLoader.loadImage("tiny_pause.png")); //$NON-NLS-1$
	}

	public void hideReset() {
		resetButton.setVisible(false);
	}

	public void showReset() {
		resetButton.setVisible(true);	
	}
	
}
