package countdowntimer.tweaks;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import blingclock.controls.HQJLabel;
import blingclock.util.MouseMovingListener;
import blingclock.util.MouseTrackingService;

import countdowntimer.Styling;
import countdowntimer.controls.FadablePanel;
import countdowntimer.registration.Registration;
import countdowntimer.util.TimedRunner;
import countdowntimer.util.TimedTask;

public class TweakPanel extends JPanel  {
	
	private Font tweakFont = new Font(Styling.FONT, 0, 12);
	private Font tweakFontSmall = new Font(Styling.FONT, 0, 10);
	
	private LabelAnimator animator = new LabelAnimator();
	private TweakMouseTracker mouseListener = new TweakMouseTracker();
	private TweakPanelControl tweakPanelControl;
	
	public TweakPanel() {
		super();
		init();
	}

	public TweakPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public TweakPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public TweakPanel(LayoutManager layout) {
		super(layout);
		init();
	}
	
	public void setTweakPanelControl(TweakPanelControl tweakPanelControl) {
		this.tweakPanelControl = tweakPanelControl;
	}

	
	/*
	 * Hover Help                 ENABLED/DISABLED
	 * Digit Titles               SHOW/HIDE
	 * Hide Mouse when stationary ENABLED/DISABLED
	 * Tweak buttons              ANIMATE/NORMAL
	 * Close Buttons              NORMAL/LARGE
	 */
	
	private void init() {		
		setOpaque(false);
		setLayout(new GridLayout(10,1));
		setBackground(new Color(0f,0.1f,0f,0.7f));
		addTitle(Messages.getString("TweakPanel.WindowTweaksTitle")); //$NON-NLS-1$
		addTweak(TweakConstants.HOVER_HELP);
		addTweak(TweakConstants.LAYOUT_TITLES);
		addTweak(TweakConstants.FADE_BUTTONS);
		addTweak(TweakConstants.COLOR_THEME);
		addTweak(TweakConstants.PERFORMANCE);
		addTweak(TweakConstants.DIGIT_TITLES);
		addTweak(TweakConstants.CLOSE_BUTTONS);
		addTweak(TweakConstants.GAMMA);
		addTweak(TweakConstants.DIGIT_RATIO);
//		if (AWTUtilitiesWrapper.isTransparentWindowSupported()) {
//			addTweak(TweakRegistry.TRANSPARENCY);
//		}
		trackMouseLeaving();
	}
	
	private void trackMouseLeaving() {
		MouseTrackingService.getInstance().addListener(mouseListener);
	}

	private void addTitle(String title) {
		HQJLabel l1 = new HQJLabel(title);
		l1.setForeground(new Color(0,1f,0));
		l1.setFont(tweakFont);
		add(l1);
	}
	
	private void addTweak(String tweakName) {
		String title = TweakRegistry.getInstance().getTweakTitle(tweakName);
		TweakValue value = TweakRegistry.getInstance().getValue(tweakName);
		boolean regOnly = Registration.getInstance().isNotRegisteredAndNotInTrialMode() && TweakRegistry.getInstance().isRegisteredOnly(tweakName);		
		HQJLabel titleLabel = new HQJLabel(title);
		HQJLabel valueLabel = new HQJLabel(regOnly ? Messages.getString("TweakPanel.RegisteredUsers") : value.getDisplayName()); //$NON-NLS-1$
		titleLabel.setFont(tweakFontSmall);
		valueLabel.setFont(tweakFontSmall);
		JPanel p = createPair(titleLabel,valueLabel);
		add(p);
		if (!regOnly) {
			hookLabel(titleLabel,valueLabel,tweakName);
		}
		animator.setNormalColor(titleLabel, Color.GREEN);
		animator.setNormalColor(valueLabel, Color.WHITE);
	}
 		
//	private void addTweakDummy(String title, String value) {
//		HQJLabel titleLabel = new HQJLabel(title);
//		HQJLabel valueLabel = new HQJLabel(value);
//		titleLabel.setFont(tweakFontSmall);
//		valueLabel.setFont(tweakFontSmall);
//		JPanel p = createPair(titleLabel,valueLabel);
//		add(p);
//		hookLabel(titleLabel,valueLabel,"");
//		animator.setNormalColor(titleLabel, Color.GREEN);
//		animator.setNormalColor(valueLabel, Color.WHITE);
//	}
	
	private void hookLabel(final JLabel key,final JLabel value,final String tweakName) {
		MouseAdapter ma = new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
				//System.out.println("CLICK: "+key.getText());
				TweakValue newValue = TweakRegistry.getInstance().setToNextValidValue(tweakName);
				value.setText(newValue.getDisplayName());
			}
			public void mouseEntered(MouseEvent e) {
				animator.resetLabelColors();
				animator.setTargetColor(key,Color.YELLOW);
				animator.setTargetColor(value,Color.YELLOW);
			}
			public void mouseExited(MouseEvent e) {
				animator.resetLabelColors();
			}			
		};	
		
		key.addMouseListener(ma);
		value.addMouseListener(ma);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final int h = 15;
		g.setColor(new Color(0f,1f,0f,0.2f));
		g.fillRect(0, 0, getWidth(), h);
		g.setColor(new Color(0f,0.1f,0f,0.7f));
		g.fillRect(0, h+1, getWidth(), getHeight() - h);
	}
	
	private JPanel createPair(JComponent comp1,JComponent comp2) {		
		JPanel p = new FadablePanel();
		p.setOpaque(false);

		GridBagLayout gbl = new GridBagLayout();
		p.setLayout(gbl);
		comp1.setForeground(new Color(0,255,0));
		comp2.setForeground(new Color(255,255,255));
				
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		p.add(comp1,gbc);
		
		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		p.add(comp2,gbc);
		
		return p;
	}

	public void closePanel() {
		fadeOutPanel();
	}

	private void fadeOutPanel() {
		MouseTrackingService.getInstance().removeListener(mouseListener);
		animator.fadeOut();
		TimedTask t = new TimedTask() { @Override public void run() {
			if (seconds > 0.4f) {
				Container parent = getParent();
				notifyPanelClosed();
				if (parent != null) {
					parent.remove(TweakPanel.this);
					parent.repaint();
				}
				stop();
			}
		}};
		TimedRunner.getInstance().run(t);
	}
		
	private void notifyPanelClosed() { 
		tweakPanelControl.panelClosed(this);
	}
	
	private class TweakMouseTracker extends MouseMovingListener {
		
		@Override
		public void onMouseStoppedMoving() {
			boolean over = MouseTrackingService.getInstance().isMouseOverComponent(TweakPanel.this,10);
			if (!over) {
				fadeOutPanel();
			}
			//System.out.println("STOP - "+over);
		}
		
	}
 
}
