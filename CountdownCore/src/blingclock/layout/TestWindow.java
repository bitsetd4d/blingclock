package blingclock.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class TestWindow extends JFrame {
	
	private JButton actionButtons;
	private JButton clock;
	private JButton playPause;
	private JButton timerBar;
	private JButton bottomBar;
	
	private BlingLayout layout;
	
	public static void main(String[] args) {
		new TestWindow().setVisible(true);
	}
	public TestWindow() {
		layout = new BlingLayout();
		setLayout(layout);
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		createControls();
		layoutControls();
		setSize(200,200);
	}
	
	private void createControls() {
		actionButtons = new JButton("actionButtons"); //$NON-NLS-1$
		clock = new JButton("clock"); //$NON-NLS-1$
		playPause = new JButton("play"); //$NON-NLS-1$
		timerBar = new JButton("bar"); //$NON-NLS-1$
		bottomBar = new JButton("bottom"); //$NON-NLS-1$
		getContentPane().add(actionButtons);
		getContentPane().add(clock);
		getContentPane().add(playPause);
		getContentPane().add(timerBar);
		getContentPane().add(bottomBar);
		clock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	layout.layoutFor(clock).setBottom(30);
            	layout.layoutContainer(getContentPane());
            	//clock.setSize(50,50);
            }
        });
	}
	
	private void layoutControls() {
		BlingLayoutData data = layout.layoutFor(actionButtons);
		data.setTop(0).setBottom(50).setLeft(0,10).setRight(0,30);

		data = layout.layoutFor(clock);
		data.setTop(0).setBottom(50).setLeft(0,30).setRight(100,-30);
		
		data = layout.layoutFor(playPause);
		data.setTop(0).setBottom(50).setLeft(100,-30).setRight(100,-10);
		
		data = layout.layoutFor(timerBar);
		data.setTop(clock,0,BlingAttachmentSide.BOTTOM).setBottom(100,-20).
			setLeft(actionButtons,0,BlingAttachmentSide.RIGHT).
			setRight(playPause,0,BlingAttachmentSide.LEFT);
		
		data = layout.layoutFor(bottomBar);
		data.setTop(100,-20).setBottom(100).setLeft(0).setRight(100);
		
	}

}
