package countdowntimer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import blingclock.controls.GlowButton;
import blingclock.controls.HQJLabel;
import blingclock.controls.GlowButton.Style;
import blingclock.digits.DigitsPanel;
import blingclock.digits.DigitsPanelListener;
import blingclock.layout.BlingAttachmentSide;
import blingclock.layout.BlingLayout;

import countdowntimer.preferences.Preferences;
import countdowntimer.preferences.TimerPreference;
import countdowntimer.tweaks.TweakRegistry;
import countdowntimer.visualiser.SwitchableTimerPanel;


public class NewCountdownFrame2 extends JFrame implements DigitsPanelListener {
	
	protected GlowButton muteButton;
	protected GlowButton onTopButton;
	//protected GlowButton loopButton;  kill me
	protected GlowButton subModeButton;
	protected GlowButton tweakButton;
	protected GlowButton timeModeButton;
	protected GlowButton fracButton;
	
	protected GlowButton prefsButton;
	protected GlowButton visualiseButton;
	protected GlowButton miniButton;
	protected GlowButton fullButton;
	protected GlowButton compactButton;
	protected GlowButton buyButton;
	protected GlowButton removeTitleButton;
	protected GlowButton debugButton1;
    
	protected GlowButton playButton;
    protected GlowButton pauseButton;
    protected GlowButton minusButton;
    protected GlowButton plusButton;
    protected GlowButton resetButton;
    
//    protected HQJLabel secondsLabel;
//    protected HQJLabel timeLabel;
//    protected JPanel bottomPanel;
    
    protected SwitchableTimerPanel bottomTimerPanel;
    protected SwitchableTimerPanel topTimerPanel;
    protected DigitsPanel digitsPanel;
    protected HQJLabel promoLabel;
    
    protected JPanel optionsButtonPanel;
    protected JPanel modeButtonPanel;
    protected JPanel playPausePanel;
    
    protected HQJLabel titleLabel;
    
    //private Color buttonColour = new Color(200,200,200);
    
    protected BlingLayout layout = new BlingLayout();
    
    protected final int TIME_PERCENT = 40;
	
//    protected MessageBarDisplayer messageBarDisplayer;
    
    private boolean fullScreenMode;
	protected LayoutUtil layoutUtil;
	
	protected int timerInstance;
    
	public NewCountdownFrame2(boolean fullScreenMode,boolean noWindowDecorations,int timerInstance) {		
		//setUndecorated(true);
		setUndecorated(noWindowDecorations);
		this.fullScreenMode = fullScreenMode;
		this.timerInstance = timerInstance;
		initComponents();
//		messageBarDisplayer = new MessageBarDisplayer(promoLabel,promoLabel);
//		messageBarDisplayer.start();
	}

	private void initComponents() {
		//setUndecorated(true);
		setTitle(Messages.getString("NewCountdownFrame2.Title")); //$NON-NLS-1$
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLayout(layout);
        Styling.applyTheme(TweakRegistry.getInstance().getColorTheme(),TweakRegistry.getInstance().getGamma());
        TimerPreference timerPref = Preferences.getInstance().getTimerPreference(timerInstance);
        onTopButton = new GlowButton();
        //loopButton = new GlowButton();
        subModeButton = new GlowButton();
        tweakButton = new GlowButton(1.5,2.3);
        timeModeButton = new GlowButton();
        fracButton = new GlowButton();
		prefsButton = new GlowButton();
		muteButton = new GlowButton();
		visualiseButton = new GlowButton();
		miniButton = new GlowButton(1.0,1.8);
		compactButton = new GlowButton();
		removeTitleButton = new GlowButton();
		fullButton = new GlowButton(1.2,2.3);
		buyButton = new GlowButton(1.0,3.0);
		debugButton1 = new GlowButton();
        plusButton = new GlowButton();
//        timeLabel = new HQJLabel();
        minusButton = new GlowButton();
        playButton = new GlowButton();
        pauseButton = new GlowButton();
//        secondsLabel = new HQJLabel();
        //barTimerPanel = new TimeBarVisualiserPanel();
        bottomTimerPanel = new SwitchableTimerPanel(timerPref);
        bottomTimerPanel.showPanel(0);
        //circleTimerPanel = new CircleTimeVisualiser();
        topTimerPanel = new SwitchableTimerPanel(timerPref);
        topTimerPanel.showPanel(1);
        resetButton = new GlowButton();
        resetButton.setTag("RESET"); //$NON-NLS-1$
        
        titleLabel = new HQJLabel();
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font(Styling.FONT, Font.BOLD, 32));
        titleLabel.setForeground(new Color(240,240,240));
        titleLabel.setText(""); //$NON-NLS-1$
        
        digitsPanel = new DigitsPanel(this);
        digitsPanel.setPreserveDigitsRatio(Preferences.getInstance().getPreserveDigitsRatio());

//        secondsLabel.setFont(new Font(Styling.FONT, 0, 20));
//        secondsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//        timeLabel.setFont(new Font(Styling.FONT, 0, 20));

//        promoLabel = new HQJLabel();
//        promoLabel.setForeground(new Color(200,200,200));
//        promoLabel.setText("");
//        promoLabel.setFont(new Font(Styling.FONT, 0, 10));
//        promoLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        promoLabel.setVerticalAlignment(SwingConstants.BOTTOM);
//        
        muteButton.setText(Messages.getString("NewCountdownFrame2.MUTE")); //$NON-NLS-1$
        muteButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        muteButton.setMinimumSize(new Dimension(20,20));
        muteButton.setMaximumSize(new Dimension(50,20));
        muteButton.setForeground(new Color(150,0,0));
        muteButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        onTopButton.setText(Messages.getString("NewCountdownFrame2.TOP")); //$NON-NLS-1$
        onTopButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        onTopButton.setMinimumSize(new Dimension(20,20));
        onTopButton.setMaximumSize(new Dimension(50,20));
        onTopButton.setForeground(new Color(205,105,22));
        onTopButton.setHorizontalAlignment(SwingConstants.LEFT);
        
//        loopButton.setText("LOOP");
//        loopButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
//        loopButton.setMinimumSize(new Dimension(20,20));
//        loopButton.setMaximumSize(new Dimension(50,20));
//        loopButton.setForeground(new Color(150,0,0));
//        loopButton.setHorizontalAlignment(SwingConstants.LEFT);
//        loopButton.setVisible(false);
        
        subModeButton.setText("SUBM"); //$NON-NLS-1$
        subModeButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        subModeButton.setMinimumSize(new Dimension(20,20));
        subModeButton.setMaximumSize(new Dimension(50,20));
        subModeButton.setForeground(new Color(100,0,0));
        subModeButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        tweakButton.setStyle(GlowButton.Style.TWEAK);
        tweakButton.setText(Messages.getString("NewCountdownFrame2.TWEAK")); //$NON-NLS-1$
        tweakButton.setMinimumSize(new Dimension(30,20));
        tweakButton.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour(tweakButton);
        tweakButton.setHorizontalAlignment(SwingConstants.LEFT);
        tweakButton.setDelayBeforeFade(5 * 1000);
        tweakButton.setTag("TWEAK"); //$NON-NLS-1$
        
        timeModeButton.setText("X MODE X"); //$NON-NLS-1$
        timeModeButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        timeModeButton.setMinimumSize(new Dimension(20,20));
        timeModeButton.setMaximumSize(new Dimension(50,20));
        timeModeButton.setForeground(new Color(100,0,0));
        timeModeButton.setHorizontalAlignment(SwingConstants.LEFT);
        timeModeButton.setInverted(true);
        
        fracButton.setText(Messages.getString("NewCountdownFrame2.100s")); //$NON-NLS-1$
        fracButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        fracButton.setMinimumSize(new Dimension(20,20));
        fracButton.setMaximumSize(new Dimension(50,20));
        fracButton.setForeground(new Color(100,0,0));
        fracButton.setHorizontalAlignment(SwingConstants.LEFT);
        fracButton.setMouseFollow(true);
        
        prefsButton.setText(Messages.getString("NewCountdownFrame2.PREF")); //$NON-NLS-1$
        prefsButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        prefsButton.setMinimumSize(new Dimension(20,20));
        prefsButton.setMaximumSize(new Dimension(50,20));
        //prefsButton.setForeground(new Color(205,105,22));
        Styling.makeLeftButtonColour(prefsButton);
        prefsButton.setHorizontalAlignment(SwingConstants.RIGHT);
        
        visualiseButton.setText(Messages.getString("NewCountdownFrame2.VIZ")); //$NON-NLS-1$
        visualiseButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        visualiseButton.setMinimumSize(new Dimension(20,20));
        visualiseButton.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour(visualiseButton);
        visualiseButton.setHorizontalAlignment(SwingConstants.RIGHT);
        visualiseButton.setMouseFollow(true);
        
        miniButton.setText(Messages.getString("NewCountdownFrame2.MINI")); //$NON-NLS-1$
        miniButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        miniButton.setMinimumSize(new Dimension(20,20));
        miniButton.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour(miniButton);
        miniButton.setHorizontalAlignment(SwingConstants.RIGHT);
        
        fullButton.setText(fullScreenMode ? Messages.getString("NewCountdownFrame2.NORM") : Messages.getString("NewCountdownFrame2.FULL")); //$NON-NLS-1$ //$NON-NLS-2$
        fullButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        fullButton.setMinimumSize(new Dimension(20,20));
        fullButton.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour(fullButton);
        fullButton.setHorizontalAlignment(SwingConstants.RIGHT);
        
        compactButton.setText(Messages.getString("NewCountdownFrame2.COMP")); //$NON-NLS-1$
        compactButton.setStyle(Style.COMPACT_MODE);
        compactButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        compactButton.setMinimumSize(new Dimension(20,20));
        compactButton.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour(compactButton);
        compactButton.setHorizontalAlignment(SwingConstants.RIGHT);
        compactButton.setMouseFollow(true);
        
        buyButton.setText(Messages.getString("NewCountdownFrame2.BUY")); //$NON-NLS-1$
        buyButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        buyButton.setMinimumSize(new Dimension(20,20));
        buyButton.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour2(buyButton);
        buyButton.setHorizontalAlignment(SwingConstants.RIGHT);

        removeTitleButton.setStyle(Style.REMOVE_TITLEBAR);
        removeTitleButton.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        removeTitleButton.setMinimumSize(new Dimension(20,20));
        removeTitleButton.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour(removeTitleButton);
        removeTitleButton.setHorizontalAlignment(SwingConstants.RIGHT);
        removeTitleButton.setVisible(true);

        debugButton1.setText("DEBUG1"); //$NON-NLS-1$
        //debugButton1.setStyle(GlowButton.Style.TWEAK);
        debugButton1.setFont(new Font(Styling.FONT, Font.BOLD, 9));
        debugButton1.setMinimumSize(new Dimension(20,20));
        debugButton1.setMaximumSize(new Dimension(50,20));
        Styling.makeLeftButtonColour(debugButton1);
        debugButton1.setHorizontalAlignment(SwingConstants.RIGHT);
        //debugButton1.setVisible(true);
        debugButton1.setVisible(false);
        
        onTopButton.setVisible(true);
//        loopButton.setVisible(true);
        subModeButton.setVisible(true);
        timeModeButton.setVisible(true);
        fracButton.setVisible(true);
        prefsButton.setVisible(true);
        muteButton.setVisible(true);
        visualiseButton.setVisible(true);
        
        plusButton.setText("+"); //$NON-NLS-1$
        plusButton.setStyle(GlowButton.Style.PLUS);
        plusButton.setFont(new Font(Styling.FONT, 0, 40));
        plusButton.setMinimumSize(new Dimension(20,20));
        plusButton.setMaximumSize(new Dimension(50,50));
        Styling.setButtonColour(plusButton);
        
        minusButton.setText("-"); //$NON-NLS-1$
        minusButton.setStyle(GlowButton.Style.MINUS);
        minusButton.setFont(new Font(Styling.FONT, 0, 40));
        minusButton.setMinimumSize(new Dimension(50,50));
        minusButton.setMaximumSize(new Dimension(50,50));
        Styling.setButtonColour(minusButton);
        
        resetButton.setText(Messages.getString("NewCountdownFrame2.RESET")); //$NON-NLS-1$
        resetButton.setFont(new Font(Styling.FONT, Font.BOLD, 8));
        resetButton.setMinimumSize(new Dimension(20,20));
        resetButton.setMaximumSize(new Dimension(50,20));
        Styling.setButtonColour(resetButton);
        resetButton.setHorizontalAlignment(SwingConstants.CENTER);
        
        playButton.setStyle(GlowButton.Style.PLAY);
        Styling.setButtonColour(playButton);
        pauseButton.setStyle(GlowButton.Style.PAUSE);
        Styling.setButtonColour(pauseButton);
        
        bottomTimerPanel.setBackground(new Color(0,0,0));
        bottomTimerPanel.setBorder(BorderFactory.createEtchedBorder());
        
        modeButtonPanel = new JPanel();
        optionsButtonPanel = new JPanel();
        layoutUtil = new LayoutUtil();
        layoutUtil.setControlButtons(pauseButton, playButton, resetButton);
        layoutUtil.setOptionsButtons(muteButton,/* loopButton, */fracButton,subModeButton,timeModeButton,onTopButton);
        layoutUtil.setTweakButton(tweakButton);
        layoutUtil.setModeButtons(prefsButton, visualiseButton, miniButton, fullButton, compactButton, removeTitleButton, buyButton, debugButton1);

        layoutUtil.layoutModePanel(modeButtonPanel,fullScreenMode);
        layoutUtil.layoutAboveDigitsPanel(digitsPanel.getAboveDigitsPanel(), fullScreenMode);
        
        layoutUtil.layoutOptionsPanel(optionsButtonPanel, fullScreenMode);
        layoutUtil.layoutBelowDigitsPanel(digitsPanel.getBelowDigitsPanel(), fullScreenMode);

        playPausePanel = new JPanel();
        GroupLayout ppLayout = new GroupLayout(playPausePanel);
        playPausePanel.setLayout(ppLayout);
        playPausePanel.setBackground(new Color(0,0,0));
        
        ppLayout.setHorizontalGroup(ppLayout.createSequentialGroup()
            	.add(ppLayout.createParallelGroup()
       				.add(minusButton,30,30,30)
       				.add(plusButton,30,30,30))
            	.add(ppLayout.createParallelGroup()
            		.add(playButton,60,100,100)
            		.add(pauseButton,60,100,100)
      				.add(resetButton)));
        ppLayout.setVerticalGroup(ppLayout.createParallelGroup()
            	.add(ppLayout.createSequentialGroup()
            		.add(plusButton,30,30,30)
            		.add(minusButton,30,30,30)
            		.add(LayoutStyle.RELATED,20,20))
    			.add(ppLayout.createSequentialGroup()
    				.add(playButton,20,50,50)
    				.add(pauseButton,20,50,50)
    				.add(resetButton,20,50,50)));
                        
//        bottomPanel = new GradientPanel();
//        bottomPanel.setBackground(Color.BLACK);
//        GroupLayout bottomLayout = new GroupLayout(bottomPanel);
//        bottomPanel.setLayout(bottomLayout);
//        bottomLayout.setVerticalGroup(bottomLayout.createSequentialGroup()
//        	.add(5,5,5)
//        	.add(bottomLayout.createParallelGroup(GroupLayout.CENTER) 
//        			.add(secondsLabel)
//        			.add(promoLabel)
//        			.add(timeLabel))
//        	.add(5,5,5));
//        bottomLayout.setHorizontalGroup(bottomLayout.createSequentialGroup()
//            .addContainerGap()
//            .add(timeLabel, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
//            .addPreferredGap(LayoutStyle.UNRELATED,10,Short.MAX_VALUE)
//            .add(promoLabel,LayoutStyle.UNRELATED,260,Short.MAX_VALUE)
//            .addPreferredGap(LayoutStyle.UNRELATED,10,Short.MAX_VALUE)
//            .add(secondsLabel, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
//            .addContainerGap());
        
        add(modeButtonPanel);
        add(optionsButtonPanel);
        add(playPausePanel);
//        add(bottomPanel);
        add(bottomTimerPanel);
        add(topTimerPanel);
        add(digitsPanel);
        add(titleLabel);
        
        final int percent = 0;
        
        // Actually, see adjustLayout() in CountdownWindow
        
        layout.layoutFor(titleLabel)
        	.setTop(0,0)
        	.setLeft(0)
        	.setRight(100)
        	.setBottom(0,2); 

        layout.layoutFor(modeButtonPanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(5).setRight(5,60).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
        layout.layoutFor(optionsButtonPanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(100-percent,-150).setRight(100-percent,-100).setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);
        layout.layoutFor(playPausePanel).setTop(titleLabel,15,BlingAttachmentSide.BOTTOM).setLeft(100-percent,-60).setRight(100-percent).setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
        //layout.layoutFor(playPausePanel).setTop(25).setLeft(100-percent,-100).setRight(100-percent).setBottom(75);
//        if (fullScreenMode) {
//        	layout.layoutFor(bottomPanel).setTop(100).setLeft(0).setRight(100).setBottom(100);
//        } else {
//        	layout.layoutFor(bottomPanel).setTop(100,-40).setLeft(0).setRight(100).setBottom(100);
//        }
//        layout.layoutFor(bottomTimerPanel).setTop(TIME_PERCENT).setLeft(0,10).setRight(100,-10).setBottom(bottomPanel,0,BlingAttachmentSide.TOP);        
        layout.layoutFor(bottomTimerPanel).setTop(TIME_PERCENT).setLeft(0,10).setRight(100,-10).setBottom(100);        
        layout.layoutFor(topTimerPanel)
        	.setTop(titleLabel,5,BlingAttachmentSide.BOTTOM)
        	.setLeft(modeButtonPanel,5,BlingAttachmentSide.RIGHT)
        	.setRight(40)
        	.setBottom(bottomTimerPanel,0,BlingAttachmentSide.TOP);

        layout.layoutFor(digitsPanel)
        	.setTop(titleLabel,0,BlingAttachmentSide.BOTTOM)
        	.setLeft(topTimerPanel,15,BlingAttachmentSide.RIGHT)
        	.setRight(optionsButtonPanel,-5,BlingAttachmentSide.LEFT)
        	.setBottom(bottomTimerPanel,-10,BlingAttachmentSide.TOP);
                
        setPreferredSize(new Dimension(700,350));
        pack();
		
	}

	public void onDayMinusClicked() {}
	public void onDayPlusClicked() {}

	public void onHourMinusClicked() {}
	public void onHourPlusClicked() {}

	public void onMinuteMinusClicked() {}
	public void onMinutePlusClicked() {}

	public void onSecondMinusClicked() {}
	public void onSecondPlusClicked() {}

}
