package blingclock.controls;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import blingclock.util.GraphicsUtil;
import blingclock.util.MouseTrackingService;

public class GlowButton extends JButton implements ButtonFading {
	
	public enum Style { PLAY, PAUSE, TEXT, PLUS, MINUS, COMPACT_MODE, CLOSE, MINIMIZE, MAXIMIZE, REMOVE_TITLEBAR, ADD_TITLEBAR, RESIZE, TWEAK};
	
	private FastBlurFilter blurFilter1 = new FastBlurFilter(20);
	private FastBlurFilter blurFilter2 = new FastBlurFilter(7);
	private BufferedImage armedImage;
	private BufferedImage normalImage;
	private BufferedImage rolloverImage;
	private BufferedImage[] fadeImages = new BufferedImage[20];
	
	private static final int PULSE_SIZE = 6;
	private BufferedImage[] pulseImages;
	private boolean pulsing = false;
	private int pulseindex = 0;
	private int pulsedx = 1;
	
	private Style style = Style.TEXT;
	private boolean inverted;
	
	private double fadeInFactor = 1;
	private double fadeOutFactor = 1;
	private static final float FADE_PER_MS = 0.0016666f;
	private float fadeInPerMs;
	private float fadeOutPerMs;
	
	private Timer pulseTimer;
	private Timer buttonHoldTimer;
	
//	private Timer fadeInTimer;
	private long fadeInTimerStarted;
	
//	private Timer fadeOutTimer;
	private long fadeOutTimerStarted;
	
	private float fadeWhenTimerStarted;
	
	private List<GlowButtonListener> listeners = new CopyOnWriteArrayList<GlowButtonListener>();
	private GlowButton linkedCopy;
	private GlowButton masterButton;
	
	private boolean invisibleWhenDimmed;
	
	private long millisBeforeFade;
	private long millisBeforeFadeNow;
	
	private String tag = ""; //$NON-NLS-1$
	
	private boolean repaintPending;
	private boolean mouseFollow = false;
	
	public GlowButton() {
		super();
		init();
	}
	
	public GlowButton(double fadeInFactor,double fadeOutFactor) {
		super();
		this.fadeInFactor = fadeInFactor;
		this.fadeOutFactor = fadeOutFactor;
		init();
	}

	public GlowButton(Action a) {
		super(a);
		init();
	}

	public GlowButton(Icon icon) {
		super(icon);
		init();
	}

	public GlowButton(String text, Icon icon) {
		super(text, icon);
		init();
	}

	public GlowButton(String text) {
		super(text);
		init();
	}
	
	@Override
	public void repaint() {
		RepaintController.repaint(this);
	}
	
	public void setDelayBeforeFade(long millisBeforeFade) {
		this.millisBeforeFade = millisBeforeFade;
		if (linkedCopy != null) linkedCopy.setDelayBeforeFade(millisBeforeFade);
	}
	

	public void setMouseFollow(boolean mouseFollow) {
		this.mouseFollow = mouseFollow;
		if (linkedCopy != null) linkedCopy.setMouseFollow(mouseFollow);
	}

	
	public GlowButton makeLinkedCopy() {
		GlowButton gb = new GlowButton(fadeInFactor,fadeOutFactor);
		gb.setText(getText());
        gb.setFont(getFont());
        gb.setMinimumSize(getMinimumSize());
        gb.setMaximumSize(getMaximumSize());
        gb.setBackground(getBackground());
        gb.setForeground(getForeground());
        gb.setHorizontalAlignment(getHorizontalAlignment());
        gb.setStyle(style);
        gb.setInverted(inverted);
        gb.setDelayBeforeFade(millisBeforeFade);
        gb.setTag(tag);
        gb.setMouseFollow(mouseFollow);
        linkedCopy = gb;
        gb.setupMasterButton(this);
        return gb;
	}
	
	private void setupMasterButton(GlowButton b) {
		this.masterButton = b;
		addActionListener(new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			for (ActionListener l : masterButton.getActionListeners()) {
				l.actionPerformed(e);
			}
		}});
	}
	
	public void setTag(String tag) {
		this.tag = tag;
		if (linkedCopy != null) linkedCopy.setTag(tag);
	}
	
	public String getTag() { 
		return tag;
	}
	
	public boolean isLinkedCopy() { return masterButton != null; }
	
	public GlowButton getCompanionButton() {
		if (masterButton != null) return masterButton;
		return linkedCopy;
	}
	
	public void setInvisibleWhenDimmed(boolean invisibleWhenDimmed) {
		this.invisibleWhenDimmed = invisibleWhenDimmed;
		if (linkedCopy != null) linkedCopy.setInvisibleWhenDimmed(invisibleWhenDimmed);
	}
	
	public void setStyle(Style style) {
		this.style = style;
		if (linkedCopy != null) linkedCopy.setStyle(style);
	}
	
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
		if (linkedCopy != null) linkedCopy.setInverted(inverted);
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (linkedCopy != null) linkedCopy.setVisible(b);
	}
	
	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (linkedCopy != null) linkedCopy.setEnabled(b);
	}
	
	public void startPulsing(int delay) {
		pulsing = true;
		pulseTimer.setDelay(delay);
		pulseTimer.start();
		if (linkedCopy != null) linkedCopy.startPulsing(delay);
	}
	
	public void stopPulsing() {
		pulsing = false;
		pulseTimer.stop();
		repaintIfNeeded();
		if (linkedCopy != null) linkedCopy.stopPulsing();
	}
	
	private void onPulse() {
		pulseindex += pulsedx;
		if (pulseindex == 0 || pulseindex == PULSE_SIZE-1) {
			pulsedx *= -1;
		}
		repaintIfNeeded();
	}
	
	public float getCurrentFade() { 
		return fade;
	}
	
	private boolean faded;
	private BufferedImage fadeImage;
	private float fade = 1.0f;
	private float intensity = 1.0f;
	private Object fadeLock = new Object();
	private float fadeOutLimit = 0.1f;
	
	public void fadeIn() {
		if (fade >= intensity) return;
		synchronized (fadeLock) {
			fadeImage = null;
		}
		fadeInTimerStarted = System.currentTimeMillis();
		fadeWhenTimerStarted = fade;
//		fadeInTimer.start();
//		fadeOutTimer.stop();
		ButtonFadingController.getInstance().fadeIn(this);
		if (linkedCopy != null) linkedCopy.fadeIn();
	}
	
	public void setFadeOutLimit(float limit) {
		this.fadeOutLimit  = limit;
		if (linkedCopy != null) linkedCopy.setFadeOutLimit(limit);
	}
	
	public void fadeOut() {
		if (fade == fadeOutLimit) return;
		synchronized (fadeLock) {
			fadeImage = null;
		}	
		//fadeInTimer.stop();
		millisBeforeFadeNow = millisBeforeFade;
		fadeOutTimerStarted = System.currentTimeMillis();
		fadeWhenTimerStarted = fade;
		ButtonFadingController.getInstance().fadeOut(this);
		//fadeOutTimer.start();
		if (linkedCopy != null) linkedCopy.fadeOut();
	}
	
	public void setIntensity(float limit) {
		intensity = limit;
		fadeOut();
		if (linkedCopy != null) linkedCopy.setIntensity(limit);
	}
	
	
	private BufferedImage getFadeImage() {
		synchronized (fadeLock) {
			if (fadeImage == null) {
				int fadeIndex = (int)(fade*20);
				BufferedImage im = fadeImages[fadeIndex];
				if (im == null) {
					im = createTransparentImage(normalImage,fadeIndex/20f);
				}
				fadeImages[fadeIndex] = im;
				fadeImage = im;
			}
			return fadeImage;
		}
	}
	
	// ---------------------------------------------
	// Fade in/out timers
	// ---------------------------------------------	
	public void onFadeInTimerFired() {
		long t = System.currentTimeMillis() - fadeInTimerStarted;
		if (invisibleWhenDimmed) {
			setVisible(true);		
		}
		if (fade < intensity) {
			fade = fadeWhenTimerStarted + (t * fadeInPerMs);
			//fade += Preferences.getInstance().getFadeTransparencyStep();
			fadeImage = null;
			if (fade >= intensity) {
				fade = intensity;
				faded = intensity != 1.0f;
				ButtonFadingController.getInstance().stop(this);
				//fadeInTimer.stop();
			}
		}
		repaintIfNeeded();
	}
	
	private static int fadeOutCount;
	public void onFadeOutTimerFired() {
		long t = System.currentTimeMillis() - fadeOutTimerStarted;
		if (t < millisBeforeFadeNow) {
			//onWaitBeforeFadeOut();
			return;
		}
		t -= millisBeforeFade;
		if (fade > fadeOutLimit) {
			fade = fadeWhenTimerStarted - (t * fadeOutPerMs);
			fadeImage = null;
			if (fade < intensity) faded = true;
			if (fade <= fadeOutLimit) {
				fade = fadeOutLimit;
				//fadeOutTimer.stop();
				ButtonFadingController.getInstance().stop(this);
				if (invisibleWhenDimmed) {
					setVisible(false);
				}
			}
		}
		repaintIfNeeded();		
	}

//	private void onWaitBeforeFadeOut() {
//		int fadeOutInterval = (int)(fadeOutFactor * Preferences.getInstance().getFadeOutTimerInterval());
//		millisBeforeFadeNow -= fadeOutInterval;
//	}

	private void initFadeTimers() {
//		int fadeInInterval = (int)(fadeInFactor * Preferences.getInstance().getFadeInTimerInterval());
		/*
		fadeInTimer = new Timer(100,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onFadeInTimerFired();
		}});
		*/
//		int fadeOutInterval = (int)(50 * Preferences.getInstance().getFadeOutTimerInterval());
		/*
		fadeOutTimer = new Timer(100,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onFadeOutTimerFired();
		}});
		*/
	}
	
	// ------------------------------------------------------
	// Listeners
	// ------------------------------------------------------
	public void addListener(GlowButtonListener listener) {
		listeners.add(listener);
	}
	public void removeListener(GlowButtonListener listener) {
		listeners.remove(listener);
	}
	
	protected void onMousePressed() {
		MouseTrackingService.getInstance().mouseHasBeenClicked();
		if (masterButton != null) {
			masterButton.onMousePressed();
			return;
		}
		firstCallFromHoldTimer = true;
		buttonHoldTimer.start();
		for (GlowButtonListener l : listeners) {
			l.onClicked();
		}
	}

	protected void onMouseReleased() {
		if (masterButton != null) {
			masterButton.onMouseReleased();
			return;
		}
		buttonHoldTimer.stop();
		if (!firstCallFromHoldTimer) {
			for (GlowButtonListener l : listeners) {
				l.onStopHeldForPeriod();
			}
		}
	}

	private boolean firstCallFromHoldTimer = false;
	protected void onButtonHeldTimerFired() {
		MouseTrackingService.getInstance().mouseHasBeenClicked();
		if (firstCallFromHoldTimer) {
			for (GlowButtonListener l : listeners) {
				l.onStartHeldForPeriod();
			}
			firstCallFromHoldTimer = false;
		}
		for (GlowButtonListener l : listeners) {
			l.onHeldTick();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		this.paintComponent(g);
	}
	@Override
	protected void paintComponent(Graphics g) {
		repaintPending = false;
		if (getModel() == null) return;
		if (rolloverImage == null) {
			setupImages();
		}
		g.setColor(Color.BLACK);
		Rectangle r = g.getClipBounds();
		g.fillRect(r.x,r.y,r.width,r.height);
		// DEBUG Rectangle r = g.getClipRect(); g.setColor(new Color(127,127,127)); g.fillRect(r.x,r.y,r.width,r.height);
		BufferedImage toDraw = null;
		if (getModel().isArmed()) {
			toDraw = armedImage;
		} else if (getModel().isRollover()) {
			toDraw = rolloverImage;
		} else if (faded) {
			toDraw = getFadeImage();
		} else if (pulsing) {
			toDraw = pulseImages[pulseindex];			
		} else {
			toDraw = normalImage;
		}
		g.drawImage(toDraw,0,0,getWidth(),getHeight(),null);
	}
	
	private void repaintIfNeeded() { 
		if (repaintPending) {
			return;
		}
		repaintPending = true;
		repaint();
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		setupImages();
		if (linkedCopy != null) linkedCopy.setText(text);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		setupImages();
		if (linkedCopy != null) linkedCopy.setForeground(fg);
	}
	
	private void setupImages() {
		int w = getWidth();
		int h = getHeight();
		if (w == 0 || h == 0) return;
		
		normalImage = GraphicsUtil.createTranslucentCompatibleImage(w,h);
		Graphics2D g2 = normalImage.createGraphics();
		g2 = GraphicsUtil.prepareGraphics(g2);
		g2.setColor(getForeground());
		drawContent(g2,w,h,0);
		g2.dispose();
		
		boolean bigBlur = style == Style.PLAY || style == Style.PAUSE;
		
		rolloverImage = createGlowImage(w,h,255,255,255,55,bigBlur ? blurFilter1 : null,blurFilter2);
		armedImage = createGlowImage(w,h,180,180,255,0,blurFilter1,blurFilter2);
		
		createPulseImages(w,h);

		setIcon(new ImageIcon(normalImage));
		setRolloverIcon(new ImageIcon(rolloverImage));
		
		fadeImage = null;
		for (int i=0; i<fadeImages.length; i++) {
			fadeImages[i] = null;
		}
	}
	
	public void setHorizontalAlignment(int alignment)  {
		super.setHorizontalAlignment(alignment);
		setupImages();
	}
	
	private void createPulseImages(int w,int h) {
		pulseImages = new BufferedImage[PULSE_SIZE];
		for (int i=0; i<PULSE_SIZE; i++) {
			double frac = 1 - (double)i/PULSE_SIZE;
			FastBlurFilter bigBlur = new FastBlurFilter(13+(int)(frac*8));
			FastBlurFilter smallBlur = new FastBlurFilter(3+(int)(frac*4));
			int b = 205 + (int)(50 * (1-frac));
			BufferedImage image = createGlowImage(w,h,b,b,b,55,bigBlur,smallBlur);
			pulseImages[i] = image;
		}
	}
	
	private BufferedImage createTransparentImage(BufferedImage im,float transparency) {
		int w = im.getWidth();
		int h = im.getHeight();
		BufferedImage image = GraphicsUtil.createTranslucentCompatibleImage(w,h);
		Graphics2D g2d = image.createGraphics();
		g2d = GraphicsUtil.prepareGraphics(g2d);
		Graphics2D g2 = image.createGraphics(); 		    
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		g2.drawImage(im, 0,0, null);
		g2.dispose(); 
		return image;
	}

	private BufferedImage createGlowImage(int w,int h,int r,int g,int b,int dim,FastBlurFilter b1,FastBlurFilter b2) {
		BufferedImage image = GraphicsUtil.createTranslucentCompatibleImage(w,h);
		Graphics2D g2d = image.createGraphics();
		g2d = GraphicsUtil.prepareGraphics(g2d);
		g2d.setColor(new Color(r-dim,g-dim,b-dim));
		drawContent(g2d,w,h,6);
		g2d.dispose();
		if (b1 != null) image = b1.filter(image, null);
		if (b2 != null) image = b2.filter(image, null);
		g2d = image.createGraphics();
		g2d = GraphicsUtil.prepareGraphics(g2d);
		g2d.setColor(new Color(r,g,b));
		drawContent(g2d,w,h,0);
		g2d.dispose();
		return image;
	}
	
	private void init() {
		fadeInPerMs = (float)(FADE_PER_MS * fadeInFactor);
		fadeOutPerMs = (float)(0.5 * FADE_PER_MS * 1/fadeOutFactor); 
		setContentAreaFilled(false);
		setupImages();
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setupImages();
			}
		});
		pulseTimer = new Timer(200,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onPulse();
		}});
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (mouseFollow) {
					MouseTrackingService.getInstance().mouseFollows(GlowButton.this);
				}
				onMousePressed();
				
			}
			public void mouseReleased(MouseEvent e) {
				onMouseReleased();
			}
		});
		initButtonHeldTimer();
		initFadeTimers();
	}
	
	private void initButtonHeldTimer() {
		buttonHoldTimer = new Timer(ButtonConstants.BUTTON_REPEAT_INTERVAL,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onButtonHeldTimerFired();
		}});
		buttonHoldTimer.setInitialDelay(ButtonConstants.DELAY_BEFORE_BUTTON_REPEAT);
	}

	private void drawTriangle(Graphics2D g2d,int w,int h,int z) {
		int gx = w / 12;
		int gy = h / 6;
		if (z > gx) z = gx;
		if (z > gy) z = gy;
		int px[] = new int[] { 3*gx - z, 3*gx - z, 8*gx + z, 8*gx + z };
		int py[] = new int[] { gy - z, h-gy+z, h/2 + z,  h/2 - z};
		g2d.fillPolygon(px,py,px.length);
	}
	
	private void drawRectangles(Graphics2D g2d,int w,int h,int z) {
		int gx = w / 12;
		int gy = h / 4;
		if (z > gx) z = gx;
		if (z > gy) z = gy;
		int px[] = new int[] { 3*gx, 3*gx, 5*gx, 5*gx};
		int py[] = new int[] { gy, h-gy, h-gy,  gy};
		g2d.fillPolygon(px,py,px.length);
		for (int i=0; i<px.length; i++) {
			px[i] += 3*gx;
		}
		g2d.fillPolygon(px,py,px.length);
	}

	private void drawPlus(Graphics2D g2d,int w,int h,int z) {
		int gx = w / 7;
		int gy = h / 7;
		if (z > gx) z = gx;
		if (z > gy) z = gy;
		int px[] = new int[] { 3*gx, 4*gx, 4*gx, 6*gx, 6*gx, 4*gx, 4*gx, 3*gx, 3*gx, gx,   gx,   3*gx };
		int py[] = new int[] { gy,   gy,   3*gy, 3*gy, 4*gy, 4*gy, 6*gy, 6*gy, 4*gy, 4*gy, 3*gy, 3*gy };
		g2d.fillPolygon(px,py,px.length);
	}
	
	private void drawMinus(Graphics2D g2d,int w,int h,int z) {
		int gx = w / 7;
		int gy = h / 7;
		if (z > gx) z = gx;
		if (z > gy) z = gy;
		int px[] = new int[] { gx,   6*gx, 6*gx, gx};
		int py[] = new int[] { 3*gy, 3*gy, 4*gy, 4*gy};
		g2d.fillPolygon(px,py,px.length);
	}
	

	private void drawCompact(Graphics2D g2d, int w, int h, int z) {
		int x1,x2;
		if (getHorizontalAlignment() == SwingConstants.RIGHT) {
			x1 = w - 17;
			x2 = w - 1;
		} else if (getHorizontalAlignment() == SwingConstants.CENTER) {
			x1 = w / 2 - 8;
			x2 = w / 2 + 8;
		} else {
			x1 = 2;
			x2 = 30;
		}
		int[] px = new int[] { x1, x2 };  
		int[] py = new int[] { 4, 4 };
		g2d.drawPolygon(px,py,px.length);
		
		px = new int[] { x1 + 3, x1,  x1 + 3};  
		py = new int[] { 1,      4,  7 };
		g2d.drawPolygon(px,py,px.length);
		
		px = new int[] { x2 - 3, x2,  x2 - 3};  
		py = new int[] { 1,      4,  7 };
		g2d.drawPolygon(px,py,px.length);
	}
	
	private void drawClose(Graphics2D g2d, int w, int h, int z) {
		int px[] = new int[] { 0, w-1, w-1, 0, 0 };
		int py[] = new int[] { 0, 0, h-1, h-1, 0};
		g2d.drawPolygon(px,py,px.length);
		g2d.drawLine(0,0,w-1,h-1);
		g2d.drawLine(0,h-1,w-1,0);
	}
	
	private void drawMinimize(Graphics2D g2d, int w, int h, int z) {
		int px[] = new int[] { 0, w-1, w-1, 0, 0 };
		int py[] = new int[] { 0, 0, h-1, h-1, 0};
		g2d.drawPolygon(px,py,px.length);
		g2d.drawLine(2,h-3,w-3,h-3);
	}	
	
	private void drawMaximize(Graphics2D g2d, int w, int h, int z) {
		int px[] = new int[] { 0, w-1, w-1, 0, 0 };
		int py[] = new int[] { 0, 0, h-1, h-1, 0};
		g2d.drawPolygon(px,py,px.length);
	}	
	
	private void drawRemoveTitlebar(Graphics2D g2d, int w, int h,boolean showTitlebar) {
		int x1,x2;
		int y = 6;
		if (getHorizontalAlignment() == SwingConstants.RIGHT) {
			x1 = w - 17;
			x2 = w - 1;
		} else if (getHorizontalAlignment() == SwingConstants.CENTER) {
			x1 = w / 2 - 8;
			x2 = w / 2 + 8;
		} else {
			x1 = 2;
			x2 = 30;
		}
		int px[] = new int[] { x1, x2, x2,  x1,  x1 };
		int py[] = new int[] { 0,  0,  y,   y, 0};
		g2d.drawPolygon(px,py,px.length);
		if (!showTitlebar) {
			g2d.drawLine(x1,y,x2,0);	
		}
	}	
	
	private void drawResize(Graphics2D g2d, int w, int h) {
		int px[] = new int[] { 3, 0, 0 };
		int py[] = new int[] { 0, 0, 3 };
		g2d.drawPolygon(px,py,px.length);
		
		px = new int[] { w-2,  w-2, w-5 };
		py = new int[] { h-5,  h-2, h-2 };
		g2d.drawPolygon(px,py,px.length);
		
		g2d.drawLine(0,0,w-2,h-2);
	}


	private void drawTweak(Graphics2D g2d, int w, int h) {
		//                     TOP                           RIGHT SQUARE BOTTOM                       LEFT SQUARE
		int px[] = new int[] { 0, 2, 4, 6, 10, 12, 14, 16,   13, 13, 16,  16, 14, 12, 10, 6, 4, 2, 0,  3, 3 }; 
		int py[] = new int[] { 2, 0, 0, 2,  2,  0,  0,  2,   2,  4,  4,   4,  6,  6,  4,  4, 6, 6, 4,  4, 2 };				
		g2d.drawPolygon(px,py,px.length);
	}
		
	private FontMetrics fm = null;
	private void drawText(Graphics2D g2d,int w,int h,int z) {
		g2d.setFont(getFont());
		int x = 0;
		String t = getText();
		int tw = getTextWidth(t);
		if (getHorizontalAlignment() != SwingConstants.LEFT) {
			if (getHorizontalAlignment() == SwingConstants.RIGHT) {
				x = w-tw;
			} else {
				x = (w-tw)/2;	
			}
		}
		if (inverted) {
			g2d.fillRect(x-1,0,tw+2, fm.getHeight()-1);
			g2d.setColor(Color.BLACK);
		}
		g2d.drawString(t,x,h/2);
	}
	
	private int getTextWidth(String x) {
		if (fm == null) {
			fm = getFontMetrics(getFont());
		}
		return fm.stringWidth(x);
	}
	
	private void drawContent(Graphics2D g2d,int w,int h,int z) {
		if (style == Style.PLAY) {
			drawTriangle(g2d, w, h, z);
		} else if (style == Style.PAUSE) {
			drawRectangles(g2d, w, h, z);
		} else if (style == Style.PLUS) {
			drawPlus(g2d, w, h, z);
		} else if (style == Style.MINUS) {
			drawMinus(g2d, w, h, z);
		} else if (style == Style.COMPACT_MODE) {
			drawCompact(g2d, w, h, z);
		} else if (style == Style.CLOSE) {
			drawClose(g2d, w, h, z);
		} else if (style == Style.MINIMIZE) {
			drawMinimize(g2d, w, h, z);
		} else if (style == Style.MAXIMIZE) {
			drawMaximize(g2d, w, h, z);
		} else if (style == Style.REMOVE_TITLEBAR) {
			drawRemoveTitlebar(g2d, w, h, false);
		} else if (style == Style.ADD_TITLEBAR) {
			drawRemoveTitlebar(g2d, w, h, true);
		} else if (style == Style.RESIZE) {
			drawResize(g2d, w, h);
		} else if (style == Style.TWEAK) {
			drawTweak(g2d, w, h);
		} else {
			drawText(g2d,w,h,z);
		}
	}


}
