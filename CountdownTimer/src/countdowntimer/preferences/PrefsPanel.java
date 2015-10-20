package countdowntimer.preferences;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Stack;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import blingclock.controls.HQJLabel;
import blingclock.visualiser.TimeBarVisualiserPanel;

import countdowntimer.Branding;
import countdowntimer.CountdownKeyboardSupport;
import countdowntimer.Features;
import countdowntimer.FullScreenUtil;
import countdowntimer.Styling;
import countdowntimer.controls.FadablePanel;
import countdowntimer.controls.ScrollOnJPanel;
import countdowntimer.glasspane.CustomGlassPane;
import countdowntimer.registration.Registration;
import countdowntimer.sound.SoundPlayer;
import countdowntimer.update.BrowserLaunching;
import countdowntimer.visualiser.SingleTimeBarVisualiserPanel;

public class PrefsPanel extends ScrollOnJPanel {
	
	private int prefsAlpha = 0;
	private String SHOP_URL = Branding.getStoreUrl();
	
	private boolean windows = FullScreenUtil.isWindows();
	private int windowsBoost = windows ? 2 : 0;
	
	private Font prefsFont = new Font(Styling.FONT, 0, 18 + windowsBoost);
	private Font smallFont = new Font(Styling.FONT, 0, 9 + windowsBoost);
	private Font small2Font = new Font(Styling.FONT, 0, 11 + windowsBoost);
	private Font buttonFont = new Font(Styling.FONT, 0, 14 + windowsBoost);
	
	private boolean registered = Registration.getInstance().isRegistered();
	private boolean trialMode = Registration.getInstance().isTrialMode();
	private boolean jumpToBuyPanel = false;
	
	private boolean updating = false;
	
	private Runnable backAction = new Runnable() { public void run() {
		doBackAction();
	}};
	
	private Stack<Runnable> menus = new Stack<Runnable>();
	
	public PrefsPanel() {
		super();
		prefsAlpha = 240;
		CountdownKeyboardSupport.getInstance().disableKeyboardSupport();
		createMainMenu();
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(new Color(20,20,20,prefsAlpha));
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}
	
	public void jumpStraightToBuyPanel() {
		jumpToBuyPanel = true;
	}
	
	@Override
	public void scrollOn() {
		if (jumpToBuyPanel) {
			createRegistrationMenu();
			jumpToBuyPanel = false;
		}
		super.scrollOn();
	}
	
	private void createMainMenu() {
		addHeading(Messages.getString("PrefsPanel.Preferences")); //$NON-NLS-1$
		addChangeLink(Messages.getString("PrefsPanel.SettingTime"),new Runnable() { public void run() { //$NON-NLS-1$
			createTimeIncrementSettings();
		}});

		addChangeLink(Messages.getString("PrefsPanel.WindowSettings"),new Runnable() { public void run() { //$NON-NLS-1$
			createWindowMenu();
		}});
		
//		addChangeLink("Performance",new Runnable() { public void run() {
//			createPerformanceMenu();
//		}});
		
		if (Features.getInstance().isCheckForUpdatesSupported()) {
			addChangeLink(Messages.getString("PrefsPanel.CheckForNewVersions"),new Runnable() { public void run() { //$NON-NLS-1$
				createCheckForUpdatesMenu();
			}});
		}
		
		addChangeLink(Messages.getString("PrefsPanel.TimerDisplay"),new Runnable() { public void run() { //$NON-NLS-1$
			createDisplayAndColourMenu();
		}});
		addChangeLink(Messages.getString("PrefsPanel.MiniTimerBar"),new Runnable() { public void run() { //$NON-NLS-1$
			createMiniTimerBarMenu();
		}});
		addChangeLink(Messages.getString("PrefsPanel.AlarmSoundSettings"),new Runnable() { public void run() { //$NON-NLS-1$
			createAlarmSoundsSettings();
		}});
		addClickableLink(Messages.getString("PrefsPanel.SettingsToDefaults"),Messages.getString("PrefsPanel.Reset"),new Runnable() { public void run() { //$NON-NLS-1$ //$NON-NLS-2$
			createResetSettings();
		}});
//      Not this release
//		addChangeLink("Colour settings",new Runnable() { public void run() {
//			createColorSettings();
//		}});
//		addChangeLink("Advanced settings",new Runnable() { public void run() {
//			createAdvancedSettings();
//		}});		
		addFiller();
		String label = Registration.getInstance().isRegistered() ? Messages.getString("PrefsPanel.Show") : Messages.getString("PrefsPanel.Register"); //$NON-NLS-1$ //$NON-NLS-2$
		addClickableLink(Messages.getString("PrefsPanel.Registration"),label,new Runnable() { public void run() { //$NON-NLS-1$
			createRegistrationMenu();
		}});
		addClickableLink(Messages.getString("PrefsPanel.About"),Messages.getString("PrefsPanel.Show"),new Runnable() { public void run() { //$NON-NLS-1$ //$NON-NLS-2$
			createAboutMenu();
		}});
		addFiller();
		addButton(Messages.getString("PrefsPanel.Close"),new Runnable() { public void run() { //$NON-NLS-1$
			closePrefsPanel();
		}});
		addFiller();
		menus.clear();
	}
	

//	protected void createPerformanceMenu() {
//		Runnable r = new Runnable() { public void run() {
//			addHeading("Frame Rate");
//			if (registered) {
//				ButtonGroup group = new ButtonGroup();
//				addFrameRadioButton("Low - 10 Frames per second",group,100);
//				addFrameRadioButton("Normal - 13 Frames per second",group,75);
//				addFrameRadioButton("Medium - 25 Frames per second",group,40);
//				addFrameRadioButton("High - 50 Frames per second",group,20);
//			} else {
//				addRegisteredUsersOnly("Low - 10 Frames per second");
//				addRegisteredUsersOnly("Normal - 13 Frames per second (current)");
//				addRegisteredUsersOnly("Medium - 25 Frames per second");
//				addRegisteredUsersOnly("High - 50 Frames per second");
//			}
//			addLabel("A higher value gives smoother animation but uses more CPU");
//			addFiller();
//			addButton("<< Back",backAction);
//			addFiller();
//		}};
//		createMenu(r);
//	}

	public void createWindowMenu() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.WindowSettings")); //$NON-NLS-1$
			
			addCheck(Messages.getString("PrefsPanel.RememberPositionOnScreen"), new CheckOption() {  //$NON-NLS-1$
				public boolean getCurrent() { return Preferences.getInstance().getRememberPositionOnScreen(); }
				public void onClicked(boolean b) { Preferences.getInstance().setRememberPositionOnScreen(b); }
			});
			
			if (registered) {
				addCheck(Messages.getString("PrefsPanel.BringWindowFrontAlarm"), new CheckOption() {  //$NON-NLS-1$
					public boolean getCurrent() { return Preferences.getInstance().getWindowToFrontOnAlarm(); }
					public void onClicked(boolean b) { Preferences.getInstance().setWindowToFrontOnAlarm(b); }
				});	
			} else {
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.BringWindowFrontAlarm")); //$NON-NLS-1$
			}

			addCheck(Messages.getString("PrefsPanel.HideMousePointerStationary"), new CheckOption() {  //$NON-NLS-1$
				public boolean getCurrent() { return Preferences.getInstance().isHideMouseEnabled(); }
				public void onClicked(boolean b) { Preferences.getInstance().setHideMouseEnabled(b); }
			});	

			
			if (registered || trialMode) {				
				addCheck(Messages.getString("PrefsPanel.FullScreenApiUse"), new CheckOption() {  //$NON-NLS-1$
					public boolean getCurrent() { return Preferences.getInstance().getUseFullScreenAPI(); }
					public void onClicked(boolean b) { Preferences.getInstance().setUseFullScreenAPI(b); }
				});

				addTextField(Messages.getString("PrefsPanel.WindowTitle"), new TextPreference() { //$NON-NLS-1$
					public String getCurrent() {
						//return "BROKEN";
						//return Preferences.getInstance().getWindowTitle(1); // FIXME
						return Preferences.getInstance().getTimerPreference(1).getWindowTitle();
					}
					public void onChanged(String txt) {
						Preferences.getInstance().getTimerPreference(1).setWindowTitle(txt); // FIXME
						//Preferences.getInstance().setWindowTitle(1,txt); // FIXME
					}}); 
				
				addCheck(Messages.getString("PrefsPanel.IncludeTimerInTile"), new CheckOption() {  //$NON-NLS-1$
					public boolean getCurrent() { return Preferences.getInstance().getIncludeTimeInTitle(); }
					public void onClicked(boolean b) { Preferences.getInstance().setIncludeTimeInTitle(b); }
				});
				
				addTextField(Messages.getString("PrefsPanel.TimerTitle"), new TextPreference() { //$NON-NLS-1$
					public String getCurrent() {
						//return "BROKEN";
						//return Preferences.getInstance().getTimerTitle(1); // FIXME
						return Preferences.getInstance().getTimerPreference(1).getTimerTitle();
					}
					public void onChanged(String txt) {
						Preferences.getInstance().getTimerPreference(1).setTimerTitle(txt);
						//Preferences.getInstance().setTimerTitle(1,txt);  // FIXME  
					}}); 
				
			} else {
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.FullScreenUseFullScreenAPI")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.ChangeWindowTitle")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.IncludeTimeInTitile")); //$NON-NLS-1$
			}

			addFiller();
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	
	}
	

	public void createDisplayAndColourMenu() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.LedDisplayHeading")); //$NON-NLS-1$
			addCheck(Messages.getString("PrefsPanel.HideHourDigits"), new CheckOption() {  //$NON-NLS-1$
				public boolean getCurrent() { return Preferences.getInstance().getHideHourDigitsIfPossible(); }
				public void onClicked(boolean b) { Preferences.getInstance().setHideHourDigitsIfPossible(b); }
			});
			addHeading(Messages.getString("PrefsPanel.OverrunModeHeading")); //$NON-NLS-1$
			addCheck(Messages.getString("PrefsPanel.FlashDigitsWhenOverrunning"), new CheckOption() { //$NON-NLS-1$
				public boolean getCurrent() { return Preferences.getInstance().getFlashDigitsIfOverrunning(); }
				public void onClicked(boolean b) { Preferences.getInstance().setFlashDigitsIfOverrunning(b); }
			});
			
			addHeading(Messages.getString("PrefsPanel.DefaultAtStartupHeading")); //$NON-NLS-1$
			ButtonGroup group = new ButtonGroup();
			
			addStartupRadioButton(Messages.getString("PrefsPanel.CountdownTimerRadio"),group,"COUNTDOWN"); //$NON-NLS-1$ //$NON-NLS-2$
			addStartupRadioButton(Messages.getString("PrefsPanel.StopwatchRadio"),group,"STOPWATCH"); //$NON-NLS-1$ //$NON-NLS-2$
			addStartupRadioButton(Messages.getString("PrefsPanel.ClockRadio"),group,"CLOCK");			 //$NON-NLS-1$ //$NON-NLS-2$
			addHeading(Messages.getString("PrefsPanel.TimeDisplayHeading")); //$NON-NLS-1$
			addCheck(Messages.getString("PrefsPanel.Show24HourClock"),new CheckOption() {  //$NON-NLS-1$
				public boolean getCurrent() {
					return Preferences.getInstance().getShow24Clock();
				};
				public void onClicked(boolean value) {
					Preferences.getInstance().setShow24Clock(value);
				};
			});
			
			addCheck(Messages.getString("PrefsPanel.BlinkSeparatorsInTimeMode"),new CheckOption() {  //$NON-NLS-1$
				public boolean getCurrent() {
					return Preferences.getInstance().getBlinkSeparatorsInTimeMode();
				};
				public void onClicked(boolean value) {
					Preferences.getInstance().setBlinkSeparatorsInTimeMode(value);
				};
			});
//			addHeading("Color Theme");
//			if (!registered) {
//				addRegisteredUsersOnly("Default Theme");
//				addRegisteredUsersOnly("Rusty Red");
//				addRegisteredUsersOnly("Eighties Green");
//				addRegisteredUsersOnly("Deep Blue");
//				addRegisteredUsersOnly("Straight and True");
//				addRegisteredUsersOnly("Classic Country");
//			} else {
//				ButtonGroup group = new ButtonGroup();
//				addThemeRadioButton("Default Theme",group,"default");
//				addThemeRadioButton("Rusty Red",group,"red");
//				addThemeRadioButton("Eighties Green",group,"green");
//				addThemeRadioButton("Deep Blue",group,"blue");
//				addThemeRadioButton("Straight and True",group,"theme1");
//				addThemeRadioButton("Classic Country",group,"theme2");
//			}	
//			addHeading("Message Bar");
//			if (registered) {
//				addCheck("Show seconds remaining?", new CheckOption() { 
//					public boolean getCurrent() { return Preferences.getInstance().getShowMessagebarSecondsRemaining(); }
//					public void onClicked(boolean b) { Preferences.getInstance().setShowMessagebarSecondsRemaining(b); }
//				});	
//			} else {
//				addRegisteredUsersOnly("Show seconds remaining?");
//			}
//			
//			if (registered) {
//				addCheck("Show time remaining?", new CheckOption() { 
//					public boolean getCurrent() { return Preferences.getInstance().getShowMessagebarTimeRemaining(); }
//					public void onClicked(boolean b) { Preferences.getInstance().setShowMessagebarTimeRemaining(b); }
//				});	
//			} else {
//				addRegisteredUsersOnly("Show time remaining?");
//			}

			addFiller();
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}
	
	protected void createMiniTimerBarMenu() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.MiniTimerHeading")); //$NON-NLS-1$
			ButtonGroup group = new ButtonGroup();
			addMiniBarMode(Messages.getString("PrefsPanel.BarFractionHelp"),group,"PROPORTIONAL"); //$NON-NLS-1$ //$NON-NLS-2$
			addMiniBarMode(Messages.getString("PrefsPanel.BarMinutesSeconds"),group,"SECONDS"); //$NON-NLS-1$ //$NON-NLS-2$
			addFiller();
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}
	
	protected void addStartupRadioButton(String text, ButtonGroup group, String value) {
		addRadio(text,group,new StartupOption(value));
	}
	
	protected void addMiniBarMode(String title, ButtonGroup group, String value) {
		addRadio(title,group,new MiniBarModeCheckOption(value));
	}


	protected void createCheckForUpdatesMenu() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.NewVersionCheckingHeading")); //$NON-NLS-1$
			ButtonGroup group = new ButtonGroup();
			addRadio(Messages.getString("PrefsPanel.DontCheck"),group,new UpdateCheckOption("NONE")); //$NON-NLS-1$ //$NON-NLS-2$
			addRadio(Messages.getString("PrefsPanel.CheckDaily"),group,new UpdateCheckOption("DAILY")); //$NON-NLS-1$ //$NON-NLS-2$
			addRadio(Messages.getString("PrefsPanel.CheckWeekly"),group,new UpdateCheckOption("WEEKLY")); //$NON-NLS-1$ //$NON-NLS-2$
			addFiller();
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}
	
	protected void createAlarmSoundsSettings() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.AlarmSound")); //$NON-NLS-1$
			ButtonGroup group = new ButtonGroup();
			if (registered) { 
				addRadio(Messages.getString("PrefsPanel.RedAlertDesc"),group,new SoundOption("alarm_loop_1.wav",1,0)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.BlerAhDesc"),group,new SoundOption("alert_35.wav",1,0)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.DinDongDesc"),group,new SoundOption("alert_47.wav",1,0)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.AlertAlertDesc"),group,new SoundOption("Warning+Alarm.wav",1,0)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.AncientBellsDesc"),group,new SoundOption("alarm-ancient-bells.wav",3,1500)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.EasternChimesDesc"),group,new SoundOption("alarm-eastern-chimes.wav",1,0)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.TrumpetFanfareDesc"),group,new SoundOption("alarm-trumpet-fanfare.wav",1,0)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.MaleAlarmDesc"),group,new SoundOption("robot-alarm-male.wav",4,650)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.FemaleAlarmDesc"),group,new SoundOption("robot-alarm-female.wav",4,900)); //$NON-NLS-1$ //$NON-NLS-2$
				addRadio(Messages.getString("PrefsPanel.SilentDesc"),group,new SoundOption("",0,0)); //$NON-NLS-1$ //$NON-NLS-2$	
				String customAlarmDesc;
				String name = Preferences.getInstance().getCustomSoundFileName();
				if (name == null) {
					customAlarmDesc = Messages.getString("PrefsPanel.CustomAlarmDescNotSet");
				} else {
					customAlarmDesc = MessageFormat.format(Messages.getString("PrefsPanel.CustomAlarmDesc"), name);
				}
				addRadio(customAlarmDesc,group,new SoundOption("CUSTOM_ALARM",1,0)); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.RedAlertDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.BlerAhDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.DinDongDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.AlertAlertCurrent")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.AncientBellsDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.EasternChimesDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.TrumpetFanfareDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.MaleAlarmDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.FemaleAlarmDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.SilentDesc")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.CustomAlarmDescNotSet")); //$NON-NLS-1$
			}
			addFiller();
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}
	
	protected void createTimeIncrementSettings() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.SettingTime")); //$NON-NLS-1$
			ButtonGroup group = new ButtonGroup();
//			if (registered) {
//				addCheck(Messages.getString("PrefsPanel.ClickGridSetsTime"), new CheckOption() {  //$NON-NLS-1$
//					public boolean getCurrent() { return Preferences.getInstance().isAllowClickGridToSetTime(); }
//					public void onClicked(boolean b) { Preferences.getInstance().setAllowClickGridToSetTime(b); }
//				});	
//			} else if (trialMode) {
//				addEnabledForTrialMode(Messages.getString("PrefsPanel.ClickGridSetsTime")); //$NON-NLS-1$
//			} else {
//				addRegisteredUsersOnly(Messages.getString("PrefsPanel.ClickGridSetsTime")); //$NON-NLS-1$
//			}
			
			String mo = Messages.getString("PrefsPanel.AllowSetTimeWithKeyboard"); //$NON-NLS-1$
			if (registered) {
				addCheck(mo, new CheckOption() { 
					public boolean getCurrent() { return Preferences.getInstance().isAllowSetTimeWithKeyboard(); }
					public void onClicked(boolean b) { Preferences.getInstance().setAllowSetTimeWithKeyboard(b); }
				});	
			} else if (trialMode) {
				addEnabledForTrialMode(mo);
			} else {
				addRegisteredUsersOnly(mo);
			}
			
			addHeading(Messages.getString("PrefsPanel.PlusMinusChange")); //$NON-NLS-1$
			if (registered) { 
				addRadio(Messages.getString("PrefsPanel.1Second"),group,new TimeChangeOption(1)); //$NON-NLS-1$
				addRadio(Messages.getString("PrefsPanel.5Seconds"),group,new TimeChangeOption(5)); //$NON-NLS-1$
				addRadio(Messages.getString("PrefsPanel.10Seconds"),group,new TimeChangeOption(10)); //$NON-NLS-1$
				addRadio(Messages.getString("PrefsPanel.30Seconds"),group,new TimeChangeOption(30)); //$NON-NLS-1$
				addRadio(Messages.getString("PrefsPanel.60Seconds"),group,new TimeChangeOption(60)); //$NON-NLS-1$
			} else {
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.1Second")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.5Second")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.10Second")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.30Second")); //$NON-NLS-1$
				addRegisteredUsersOnly(Messages.getString("PrefsPanel.60Second")); //$NON-NLS-1$
			}
			addFiller();
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}

	protected void createAboutMenu() {
		Runnable r = new Runnable() { public void run() {
			addHeading(""); //$NON-NLS-1$
			addLogo();
			addFiller();
			addFiller();
			addLabel("www.blingclock.com", new Color(220,220,220)); //$NON-NLS-1$
			addLabel(Messages.getString("PrefsPanel.Version")+getFormattedVersion(), new Color(120,120,120)); //$NON-NLS-1$
			addFiller();
			addSmallLabel(Messages.getString("PrefsPanel.Copyright")); //$NON-NLS-1$
			addSmallestLabel(Messages.getString("PrefsPanel.UsageLicense")); //$NON-NLS-1$
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}
	
	
	protected String getFormattedVersion() {
		DecimalFormat df = new DecimalFormat("0.00"); //$NON-NLS-1$
		return df.format(Branding.getCurrentVersion() / 100f);
	}

	protected void createRegistrationMenu() { 
		if (Registration.getInstance().isRegistered()) {
			createRegisteredMenu();
		} else {
			createUnregisteredMenu();
		}
		
	}
	
	private void createUnregisteredMenu() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.RegisteredHeading")); //$NON-NLS-1$
			addSmallLabel(Messages.getString("PrefsPanel.BuyingHtml")); //$NON-NLS-1$
			addLinkMainLabel(Messages.getString("PrefsPanel.BuyingPricesHtml"),new Runnable() { public void run() { //$NON-NLS-1$
				BrowserLaunching.openURL(SHOP_URL);
			}});
			final JLabel regLabel = addSmallestLabel(Messages.getString("PrefsPanel.PasteCodeBelow")); //$NON-NLS-1$
			addRegistrationEntryField(regLabel);
			addSmallestLabel(Messages.getString("PrefsPanel.RegisteringUnlocksFeatures")); //$NON-NLS-1$
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}
	
	private void buildThankyou() {
		Runnable r = new Runnable() { public void run() {
			addHeading(""); //$NON-NLS-1$
			addLogo();
			addFiller();
			addLabel("www.blingclock.com", new Color(220,220,220)); //$NON-NLS-1$
			addFiller();
			String who = Registration.getInstance().getRegisteredName();
			if (who.trim().length() > 0) {
				addLabel(who,new Color(0,255,0));
			}
			String email = Registration.getInstance().getRegisteredEmail();
			if (email.trim().length() > 0) {
				addLabel(email,new Color(0,255,0));
			}
			addLabel(Messages.getString("PrefsPanel.Register_Thankyou1")); //$NON-NLS-1$
			addSmallestLabel(Messages.getString("PrefsPanel.Register_Thankyou2")); //$NON-NLS-1$
			addButton(Messages.getString("PrefsPanel.Register_Thankyou_Close"), new Runnable() { public void run() { //$NON-NLS-1$
				closePrefsPanel();
			}});
			addFiller();
		}};
		createMenu(r);		
	}

	
	private void addLinkMainLabel(String txt,final Runnable r) {
		final JLabel label = new HQJLabel();
		label.setText(txt);
		label.setForeground(Color.YELLOW);
		add(label);
		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					r.run();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			public void mouseEntered(MouseEvent e) {
				label.setForeground(Color.WHITE);
			}
			public void mouseExited(MouseEvent e) {
				label.setForeground(Color.YELLOW);
			}			
		});
	}

	protected void addRegistrationEntryField(final JLabel regLabel) {
		final JTextArea textArea = new JTextArea();
		textArea.setText(Messages.getString("PrefsPanel.PasteRegistrationCodeHere")); //$NON-NLS-1$
		textArea.setLineWrap(true);
		textArea.setForeground(new Color(255,255,255));
		textArea.setBackground(new Color(0,0,0));		
		textArea.setFont(smallFont);
		add(textArea);
		textArea.addFocusListener(new FocusAdapter() { public void focusGained(FocusEvent e) {
			textArea.setText(""); //$NON-NLS-1$
			textArea.paste();
		}});
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {}
			public void insertUpdate(DocumentEvent e) {
				String licence = textArea.getText();
				licence = cleanLicense(licence);
				if (isValid(licence)) {
					regLabel.setText(Messages.getString("PrefsPanel.LicenseRegistered")); //$NON-NLS-1$
					processValidLicense(licence);
				} else {
					regLabel.setText(Messages.getString("PrefsPanel.LicenseNotValid")); //$NON-NLS-1$
				}
			}
			public void removeUpdate(DocumentEvent e) {} 
		});
	}
	


	protected void processValidLicense(String licence) {
		Registration.getInstance().register(licence);
		buildThankyou();
	}


	protected String cleanLicense(String licence) { 
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<licence.length(); i++) {
			char c = licence.charAt(i);
			if (isValidChar(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	
	private boolean isValidChar(char c) {
		char[] valid = new char[] { 'A', 
			'B', 'C', 'D', 'E', 'F', 'G',
		    'H', 'I', 'J', 'K', 'L', 'M', 'N',
		    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 
		    'V', 'W', 'X', 'Y', 'Z',
		    'a', 'b', 'c', 'd', 'e', 'f', 'g',
		    'h', 'i', 'j', 'k', 'l', 'm', 'n',
		    'o', 'p', 'q', 'r', 's', 't', 'u', 
		    'v', 'w', 'x', 'y', 'z',
		    '0', '1', '2', '3', '4', '5', 
		    '6', '7', '8', '9', '+', '/',
		    '=', 
		};
		for (char x : valid) {
			if (x == c) return true;
		}
		return false;
	}


	protected boolean isValid(String licence) { 
		return Registration.getInstance().isValid(licence);
	}

	private void createRegisteredMenu() {
		Runnable r = new Runnable() { public void run() {
			addFiller();
			addFiller();
			addLabel("www.blingclock.com", new Color(220,220,220)); //$NON-NLS-1$
			String who = Registration.getInstance().getRegisteredName();
			if (who.trim().length() > 0) {
				addSmallestLabel(Messages.getString("PrefsPanel.RegisteredTo")); //$NON-NLS-1$
				addLabel(who,new Color(0,255,0));
			}
			String email = Registration.getInstance().getRegisteredEmail();
			if (email.trim().length() > 0) {
				addLabel(email,new Color(0,255,0));
			}
			String special1 = Registration.getInstance().getSpecialMessage1();
			if (special1.trim().length() > 0) {
				addLabel(special1,new Color(0,255,0));
			}
			String special2 = Registration.getInstance().getSpecialMessage2();
			if (special2.trim().length() > 0) {
				addLabel(special2,new Color(0,255,0));
			}
			addFiller();
			addSmallestLabel(Messages.getString("PrefsPanel.RegisteredThankyou")); //$NON-NLS-1$
			addButton(Messages.getString("PrefsPanel.BackButton"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}

	protected void createResetSettings() {
		Runnable r = new Runnable() { public void run() {
			addHeading(Messages.getString("PrefsPanel.ResetToDefaults")); //$NON-NLS-1$
			addClickableLink(Messages.getString("PrefsPanel.ResetQuestion"), Messages.getString("PrefsPanel.ClickHere"), new Runnable() { public void run() { //$NON-NLS-1$ //$NON-NLS-2$
				onResetClicked();
			}});
			addFiller();
			addButton(Messages.getString("PrefsPanel.BackButton2"),backAction); //$NON-NLS-1$
			addFiller();
		}};
		createMenu(r);
	}
	
	private void onResetClicked() {
		Preferences.getInstance().resetToDefault();
		fadeOutContents(lastClicked, new Runnable() { public void run() {
			removeAll();
			addHeading(Messages.getString("PrefsPanel.SettingsReset")); //$NON-NLS-1$
			addLabel(Messages.getString("PrefsPanel.SettingsHaveBeenReset")); //$NON-NLS-1$
			setPositions();
			scrollOn();
			goBackToMainMenu(2000);
		}});
	}

	protected void goBackToMainMenu(int delay) {
		Timer timer = new Timer(delay,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			doBackAction();
			doBackAction();
		}});
		timer.setRepeats(false);
		timer.start();
	}

	private class SoundOption implements CheckOption {
		
		private String sound;
		private int repeat;
		private int repeatDelay;
		
		SoundOption(String sound,int repeat,int repeatDelay) {
			this.sound = sound;
			this.repeat = repeat;
			this.repeatDelay = repeatDelay;
		}
		
		public boolean getCurrent() {
			return Preferences.getInstance().getSoundFile().equals(sound);
		}

		public void onClicked(boolean value) {
			if (value) {
				Preferences.getInstance().setSoundFile(sound);
				Preferences.getInstance().setSoundRepeat(repeat);
				Preferences.getInstance().setSoundDelay(repeatDelay);
				if (repeat > 0) {
					playSound(sound);
				}
			}
		}
	}
	
	private class TimeChangeOption implements CheckOption {
		
		private int amount;
		TimeChangeOption(int amount) {
			this.amount = amount;
		}
		
		public boolean getCurrent() {
			return Preferences.getInstance().getTimeUnit() == amount;
		}

		public void onClicked(boolean value) {
			if (value) {
				Preferences.getInstance().setTimeUnit(amount);
			}
		}
	}
	
	private class UpdateCheckOption implements CheckOption {
		
		private String option;
		UpdateCheckOption(String x) {
			this.option = x;
		}
		
		public boolean getCurrent() {
			return Preferences.getInstance().getUpdateOption().equals(option);
		}

		public void onClicked(boolean value) {
			if (value) {
				Preferences.getInstance().setUpdateOption(option);
			}
		}
	}

	private class StartupOption implements CheckOption {

		private String value;
		StartupOption(String value) {
			this.value = value;
		}
		
		public boolean getCurrent() {
			return Preferences.getInstance().getStartupDisplay().equals(value);
		}

		public void onClicked(boolean yes) {
			Preferences.getInstance().setStartupDisplay(value);
		}	
		
	}	
	
	private class MiniBarModeCheckOption implements CheckOption {
		
		private String option;
		MiniBarModeCheckOption(String x) {
			this.option = x;
		}
		
		public boolean getCurrent() {
			return Preferences.getInstance().getMiniMode().equals(option);
		}

		public void onClicked(boolean value) {
			if (value) {
				Preferences.getInstance().setMiniMode(option);
			}
		}
		
	}
	
	public void playSound(final String name) {
		URL url = getSoundUrl(name);
		SoundPlayer.playSound(url,0,false);
	}
	
	private URL getSoundUrl(String name) { 
		Class cls = getClass();
		URL url = cls.getResource("/sounds/" + name); //$NON-NLS-1$
		try {
			if (url == null) {
				return new URL("file:sounds" + File.separator + name); //$NON-NLS-1$
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;	
	}
	
	protected void createColorSettings() {
		Runnable r = new Runnable() { public void run() {
			addHeading("Colour Settings"); //$NON-NLS-1$
			addCheck("TO DO",null); //$NON-NLS-1$
			addCheck("TO DO",null); //$NON-NLS-1$
			addFiller();
			addButton("<< Back",backAction); //$NON-NLS-1$
			addFiller();		
		}};
		createMenu(r);
	}
	
	// ------------------------------------------------------------
	// Global actions
	// ------------------------------------------------------------	
	private void createMenu(final Runnable r) {
		Runnable r2 = new Runnable() { public void run() {
			removeAll();
			r.run();
			setPositions();
			scrollOn();
		}};
		fadeOutContents(lastClicked,r2);
		menus.push(r2);
	}

	protected void doBackAction() {
		if (menus.size() <= 1) {
			fadeOutContents(lastClicked, new Runnable() { public void run() {
				removeAll();
				createMainMenu();
				setPositions();
				scrollOn();
			}});
			return;
		}
		Runnable lastMenu = menus.pop();
		fadeOutContents(lastClicked,lastMenu);
	}
	
	protected void closePrefsPanel() {
		CountdownKeyboardSupport.getInstance().enableKeyboardSupport();
		fadeOutContents(lastClicked,new Runnable() { public void run() {
			fadeOutPanel();
		}});
	}
	
	private Timer timer;
	private void fadeOutPanel() {
		timer = new Timer(40,new ActionListener() { public void actionPerformed(ActionEvent e) {
			prefsAlpha -= 30;
			if (prefsAlpha < 0) {
				setVisible(false);
		    	TimeBarVisualiserPanel.globalEnableMouseMonitoring();
		    	SingleTimeBarVisualiserPanel.globalEnableMouseMonitoring();
		    	CustomGlassPane.globalDisableHoverHelp(false);
				timer.stop();
				return;
			}
			repaint();
		}});
		timer.start();
	}

	// ------------------------------------------------------------
	// Create controls
	// ------------------------------------------------------------
	private void addFiller() {
		JLabel label = new JLabel();
		label.setText(""); //$NON-NLS-1$
		label.setForeground(new Color(255,255,255));
		add(label);
	}

	private void addLabel(String text,Color c) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setForeground(c);
		add(label);
	}
	
	private void addLogo() {
		Icon icon = loadImage("d3bug_invert_mini2.png"); //$NON-NLS-1$
		JLabel label = new HQJLabel();
		label.setIcon(icon);
		label.setName("logo"); //$NON-NLS-1$
		add(label);
	}
	
	private void addLabel(String text) {
		addLabel(text,new Color(255,0,0));
	}
	
	private void addSmallLabel(String text) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setForeground(new Color(255,0,0));
		label.setFont(small2Font);
		add(label);
	}

	private JLabel addSmallestLabel(String text) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setForeground(new Color(255,0,0));
		label.setFont(smallFont);
		add(label);
		return label;
	}
	
	private void addHeading(String text) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setForeground(new Color(255,255,255));
		label.setFont(prefsFont);
		add(label);
	}
	
	private void addButton(String text,final Runnable action) {
		final JLabel label = new HQJLabel();
		final Color white = new Color(230,230,230);
		final Color yellow = new Color(255,255,0);
		
		label.setText(text);
		label.setAlignmentX(CENTER_ALIGNMENT);
		label.setForeground(white);
		label.setFont(buttonFont);
		add(label);		
		label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					lastClicked = label;
					action.run();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			public void mouseEntered(MouseEvent e) {
				label.setForeground(yellow);
			}
			public void mouseExited(MouseEvent e) {
				label.setForeground(white);
			}			
		});
	}

	
	private void addRegisteredUsersOnly(String text) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setBackground(new Color(0,0,0));

		JLabel label2 = new JLabel();
		label2.setText(Messages.getString("PrefsPanel.RegisteredUsers")); //$NON-NLS-1$
		label2.setForeground(new Color(255,255,0));
		label2.setBackground(new Color(0,0,0));
		label2.setFont(smallFont);
		JPanel p = createPair(label, label2);
		add(p);
	}
	
	private void addEnabledForTrialMode(String text) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setBackground(new Color(0,0,0));

		JLabel label2 = new JLabel();
		label2.setText(Messages.getString("PrefsPanel.EnabledForTrialMode")); //$NON-NLS-1$
		label2.setForeground(new Color(255,255,0));
		label2.setBackground(new Color(0,0,0));
		label2.setFont(smallFont);
		JPanel p = createPair(label, label2);
		add(p);
	}

	private void addCheck(String text,final CheckOption checkOption) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setBackground(new Color(0,0,0));
		
		final JCheckBox check = new JCheckBox();
		check.setText(""); //$NON-NLS-1$
		check.setForeground(new Color(255,255,255));
		check.setBackground(new Color(0,0,0));		
		if (checkOption != null) {
			check.setSelected(checkOption.getCurrent());
			check.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					checkOption.onClicked(check.isSelected());
				}
			});
		}
		
		JPanel p = createPair(label, check);
		add(p);
	}
	
	private void addRadio(String text,ButtonGroup group,final CheckOption action) {
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setBackground(new Color(0,0,0));
		
		final JRadioButton check = new JRadioButton();
		check.setText(""); //$NON-NLS-1$
		check.setForeground(new Color(255,255,255));
		check.setBackground(new Color(0,0,0));		
		JPanel p = createPair(label, check);
		group.add(check);
		add(p);
		if (action != null) {
			if (action.getCurrent()) {
				check.setSelected(true);
			}
			check.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
				if (check.isSelected()) {
					action.onClicked(true);
				}
			}});
		}
	}
	
	private void addTextField(String text,final TextPreference textOption) {
		final Color green = new Color(0,255,0);
		final Color white = new Color(255,255,255);
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setForeground(white);

		final JTextField textField = new JTextField();
		final JPanel p = createPair2(label, textField);
		add(p);
		textField.setText(textOption.getCurrent());
		textField.setBackground(Color.BLACK);
		textField.setForeground(green);
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateText();
			}
			public void insertUpdate(DocumentEvent e) {
				updateText();
			}
			public void removeUpdate(DocumentEvent e) {
				updateText();			
			}
			private void updateText() {
				updating = true;
				try {
					String v = textField.getText();
					textOption.onChanged(v);
				} finally {
					updating = false;
				}
			}
			
		});
		
		textField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				textField.setBackground(new Color(0,0,0));
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				textField.setBackground(new Color(0,0,60));
			}
			
		});
	}
	
	private Component lastClicked;
	private void addChangeLink(String text,final Runnable action) {
		addClickableLink(text,Messages.getString("PrefsPanel.ChangeDotDot"),action); //$NON-NLS-1$
	}
	
	private void addClickableLink(String text,String linkText,final Runnable action) {
		final Color green = new Color(0,255,0);
		final Color white = new Color(255,255,255);
		JLabel label = new HQJLabel();
		label.setText(text);
		label.setForeground(white);

		final JLabel changeLabel = new HQJLabel();
		final JPanel p = createPair(label, changeLabel);
		add(p);
		changeLabel.setText(linkText);
		changeLabel.setForeground(green);
		changeLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					lastClicked = p;
					action.run();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			public void mouseEntered(MouseEvent e) {
				changeLabel.setForeground(white);
			}
			public void mouseExited(MouseEvent e) {
				changeLabel.setForeground(green);
			}			
		});
	}
	
	private JPanel createPair(JComponent comp1,JComponent comp2) {		
		JPanel p = new FadablePanel();
		p.setOpaque(false);

		GridBagLayout gbl = new GridBagLayout();
		p.setLayout(gbl);
		comp1.setForeground(new Color(255,0,0));
				
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		p.add(comp1,gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		p.add(comp2,gbc);
		
		return p;
	}
	
	private JPanel createPair2(JComponent comp1,JComponent comp2) {		
		JPanel p = new FadablePanel();
		p.setOpaque(false);

		GridBagLayout gbl = new GridBagLayout();
		p.setLayout(gbl);
		comp1.setForeground(new Color(255,0,0));
				
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		p.add(comp1,gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.EAST;
		p.add(comp2,gbc);
		
		return p;
	}
	
	
	private Icon loadImage(String name) {
		Class cls = getClass();
		URL url = cls.getResource("/images/" + name); //$NON-NLS-1$
		try {
			if (url == null) {
				url = new URL("file:images" + File.separator + name); //$NON-NLS-1$
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		return new ImageIcon(url);
	}

	private static interface CheckOption { 
		
		boolean getCurrent();
		void onClicked(boolean value);
		
	}
	
	private static interface TextPreference {
		String getCurrent();
		void onChanged(String txt);		
	}


}
